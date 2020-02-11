package ru.spb.hse.nesh.interpreter

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.parser.ParseException
import ru.spb.hse.nesh.interpreter.commands.builtins.BuiltinCommandFactory
import ru.spb.hse.nesh.interpreter.commands.CommandFactory
import ru.spb.hse.nesh.interpreter.commands.ExternalCommandFactory
import ru.spb.hse.nesh.interpreter.commands.FirstAcceptedFactory
import ru.spb.hse.nesh.interpreter.commands.builtins.grep.GrepFactory
import ru.spb.hse.nesh.interpreter.commands.io.*
import ru.spb.hse.nesh.interpreter.implementations.SerrLogger
import ru.spb.hse.nesh.interpreter.implementations.SystemBasedEnvironment
import ru.spb.hse.nesh.interpreter.interfaces.Environment
import ru.spb.hse.nesh.interpreter.interfaces.ExpressionExecutor
import ru.spb.hse.nesh.interpreter.interfaces.ProblemLogger
import ru.spb.hse.nesh.parsing.*
import java.io.Closeable
import java.io.File
import java.io.IOException

/**
 * An instance of nesh interpreter.
 *
 * Interpreter is capable of running nesh commands and allows access to it's environment.
 *
 * One process can have multiple interpreters. However if they use the same input, it might lead to weird consequences.
 *
 * @param env               environment to store environment variables.
 * @param commandFactory    used to create commands for interpretation.
 * @param input             input source for the first command in a piped chain of commands.
 * @param output            where to put output of the last command in a piped chain of commands.
 * @param logger            used to log errors that happen in [Interpreter] itself.
 */
class Interpreter(
    val env: Environment = SystemBasedEnvironment(),
    private val commandFactory: CommandFactory = FirstAcceptedFactory(BuiltinCommandFactory, GrepFactory, ExternalCommandFactory),
    private val input: Source = ShellInputSource,
    private val output: Sink = ShellOutputSink,
    private val logger: ProblemLogger = SerrLogger()
) : ExpressionExecutor {

    private val grammar = NeshGrammar(env)

    /**
     * Executes [line] as a nesh expression.
     */
    fun interpret(line: String) {
        var expression: Expression? = null
        try {
            expression = grammar.parseToEnd(line)
        } catch (parseException: ParseException) {
            logger.logProblem(parseErrorMessage(parseException))
        }
        try {
            expression?.accept(this)
        } catch (ex: Throwable) {
            logger.logProblem("Uncaught exception while executing a command", ex)
        }
    }

    /** Returns [variable] from [env]. */
    fun getEnvVariable(variable: String): String = env[variable]

    /** Executes assignment expression. */
    override fun assign(assignment: AssignmentExpr) {
        env[assignment.variable] = assignment.value
    }

    /** Executes [pipedExpression] (tries at least). */
    override fun execute(pipedExpression: PipedExpression) {

        // don't close input or output of interpreter on accident
        fun <T : Closeable> T.useSmartClosing(block: (T) -> Unit) {
            when (this) {
                input -> block(this)
                output -> block(this)
                else -> this.use(block)
            }
        }

        var source = input
        var lastPipeFile: File? = null
        for (commandEx in pipedExpression.commands.dropLast(1)) {
            val pipeFile: File? = constructPipeFile(commandEx)
            if (pipeFile === null) {
                break
            }

            try {
                source.useSmartClosing {sourceIt ->
                    try {
                        PipeOutputSink(pipeFile).use { pipeSink ->
                            handleCommand(commandEx, sourceIt, pipeSink)
                        }
                    } catch (ex: IOException) {
                        logger.logProblem("IO error while closing pipe output file stream", ex)
                    }
                }
            } catch (ex: IOException) {
                logger.logProblem("IO error while closing pipe input file stream", ex)
            } finally {
                deletePipeFile(lastPipeFile)
            }

            lastPipeFile = pipeFile
            source = PipeInputSource(pipeFile)
        }

        try {
            pipedExpression.commands.lastOrNull()?.also { commandExpr ->
                source.useSmartClosing { sourceIt -> handleCommand(commandExpr, sourceIt, output) }
            }
        } finally {
            deletePipeFile(lastPipeFile)
        }
    }

    private fun constructPipeFile(commandEx: CommandExpr): File? {
        return try {
            createPipeFile()
        } catch (ex: IOException) {
            logger.logProblem("Can't create temporary file for command $commandEx", ex)
            null
        }
    }

    private fun deletePipeFile(oldPipeFile: File?) {
        try {
            if (oldPipeFile?.delete() == false) {
                logger.logProblem("Unable to delete pipe file $oldPipeFile for unknown reason")
            }
        } catch (ex: SecurityException) {
            logger.logProblem("Unable to delete pipe file $oldPipeFile: permission denied", ex)
        }
    }

    // does not catch InterruptedException
    private fun handleCommand(commandExpr: CommandExpr, source: Source, sink: Sink) {
        val command = commandFactory.createCommand(commandExpr, source, sink, env)
        if (command === null) {
            logger.logProblem("Can't understand command $commandExpr")
        } else {
            try {
                env["?"] = command.runWait().toString()
            } catch (ex: IOException) {
                logger.logProblem("IO error in a command", ex)
            }
        }
    }

    private fun createPipeFile(): File = File.createTempFile("neshTempIO_", "_top_secret")

    private fun parseErrorMessage(parseException: ParseException) = """
        Can't parse that. Sorry, no meaningful message here.
        Parser message:
        ${parseException.message}
            
        """.trimIndent()
}