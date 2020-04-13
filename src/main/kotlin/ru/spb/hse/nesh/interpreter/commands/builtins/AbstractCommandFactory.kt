package ru.spb.hse.nesh.interpreter.commands.builtins

import ru.spb.hse.nesh.interpreter.commands.Command
import ru.spb.hse.nesh.interpreter.commands.CommandFactory
import ru.spb.hse.nesh.interpreter.commands.io.Sink
import ru.spb.hse.nesh.interpreter.commands.io.Source
import ru.spb.hse.nesh.interpreter.interfaces.Environment
import ru.spb.hse.nesh.parsing.CommandExpr

/**
 * Helps implement [CommandFactory]es by skipping empty [CommandExpr]s and providing command name
 * separately from arguments
 */
abstract class AbstractCommandFactory : CommandFactory {
    override fun createCommand(
        expression: CommandExpr,
        input: Source,
        output: Sink,
        env: Environment
    ): Command? = expression.words.firstOrNull()?.let { programName ->
        val arguments = expression.words.drop(1)
        createCommandByName(programName, arguments, input, output, env)
    }

    /**
     * Created a command with name [programName] and [arguments]
     */
    abstract fun createCommandByName(
        programName: String,
        arguments: List<String>,
        input: Source,
        output: Sink,
        env: Environment
    ): Command?
}