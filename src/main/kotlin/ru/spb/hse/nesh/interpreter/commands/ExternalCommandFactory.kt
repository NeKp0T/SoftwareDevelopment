package ru.spb.hse.nesh.interpreter.commands

import ru.spb.hse.nesh.interpreter.commands.io.Sink
import ru.spb.hse.nesh.interpreter.commands.io.Source
import ru.spb.hse.nesh.interpreter.interfaces.Environment
import ru.spb.hse.nesh.parsing.CommandExpr
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.UnsupportedOperationException

/**
 * A command that executes an external process in operating system.
 *
 * @constructor creates an external command for executing [process]
 * @param process [ProcessBuilder] for a process to be executed
 */
class ExternalCommand(private val process: ProcessBuilder) : Command {
    /**
     * Tries to execute [process].
     *
     * Does not throw any exceptions. In case of error return a code and writes a message into error stream.
     *
     * @return return code of a command if it was executed, or one of predefined codes in case of error
     */
    override fun runWait(): Int {
        val proc: Process
        try {
            proc = process.start()
        } catch (ex: IOException) {
            System.err.println("""
                System refuses to start such process
                $ex
            """.trimIndent())
            return IO_ERROR_CODE
        } catch (ex: SecurityException) {
            System.err.println("""
                Security manager forbids running a command
                $ex
            """.trimIndent())
            return SECURITY_ERROR_CODE
        } catch (ex: UnsupportedOperationException) {
            System.err.println("""
                System does not support running external programs
                $ex
            """.trimIndent())
            return SECURITY_ERROR_CODE
        } catch (ex: ArrayIndexOutOfBoundsException) {
            System.err.println("""
                Empty command
            """.trimIndent())
            return IO_ERROR_CODE
        }
        return proc.waitFor()
    }

    companion object {
        /** Code to be returned if [IOException] prevents starting a process or no program were provided at all. */
        const val IO_ERROR_CODE: Int = 127
        /** Code to be returned if System does not support running external programs or
         * a [SecurityManager] prohibits running this particular one */
        const val SECURITY_ERROR_CODE: Int = 126
    }
}

/**
 * A Factory that constructs [Command]s for executing external programs.
 *
 * Always constructs a [Command], even if provided expression was empty
 */
object ExternalCommandFactory : CommandFactory {
    /** A name of variable that is used as current directory for external processes.
     *
     * Uses "cd" on windows and "PWD" on every other system.
     * */
    val PWD_VARIABLE: String
        get() = if (System.getProperty("os.name").contains("win")) "cd" else "PWD"

    /**
     * Constructs a command that will try to run a program specified by first word of [expression]
     * with arguments from other words.
     *
     * Always constructs a command.
     *
     * Process will use value of environmental variable [PWD_VARIABLE] as working directory.
     * All variables from [Environment.getRedefinedVariables] will be added to process' environment (if system allows it).
     */
    override fun createCommand(expression: CommandExpr, input: Source, output: Sink, env: Environment): Command? {
        val process = ProcessBuilder(expression.words).apply {
            directory(File(env[PWD_VARIABLE]))
            redirectInput(input.getSourceRedirect())
            redirectOutput(output.getSinkRedirect())
            env.getRedefinedVariables().forEach {(variable, value) ->
                try {
                    environment()[variable] = value
                } catch (ex: Exception) {
                    when(ex) {
                        is SecurityException -> {}
                        is UnsupportedOperationException -> {}
                        is IllegalArgumentException -> {}
                        else -> throw ex
                    }
                }
            }
        }
        return ExternalCommand(process)
    }
}