package ru.spb.hse.nesh.interpreter.commands

import ru.spb.hse.nesh.interpreter.commands.io.Sink
import ru.spb.hse.nesh.interpreter.commands.io.Source
import ru.spb.hse.nesh.interpreter.interfaces.Environment
import ru.spb.hse.nesh.parsing.CommandExpr

/**
 * An abstraction over various commands a shell can run.
 *
 * A command should not start working until [runWait] is invoked.
 */
interface Command {
    /**
     * Starts command execution and waits until it is done or throws an exception.
     *
     * Be mindful of possible exception thrown by a command.
     *
     * @return return code of a command
     */
    fun runWait(): Int
}

/**
 * A factory that might create a command out of provided [CommandExpr].
 *
 * If factory chooses not to create a command, it will return `null`.
 */
interface CommandFactory {
    /**
     * Creates a command or returns `null`.
     *
     * Returned command should not be running yet.
     *
     * Command should not close it's sink and source.
     *
     * @param expression from which a command may be created
     * @param input source of created command's input stream
     * @param output source of created command's output stream
     *
     * @return created command or null if arguments do not satisfy this factory
     */
    fun createCommand(expression: CommandExpr, input: Source, output: Sink, env: Environment): Command?
}