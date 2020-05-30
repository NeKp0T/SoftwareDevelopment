package ru.spb.hse.nesh.interpreter.commands

import ru.spb.hse.nesh.interpreter.commands.io.Sink
import ru.spb.hse.nesh.interpreter.commands.io.Source
import ru.spb.hse.nesh.interpreter.interfaces.Environment
import ru.spb.hse.nesh.parsing.CommandExpr

/**
 * A factory that returns a first successfully constructed by [factories] command.
 *
 * All factories are called in order until one of them succeeds or all fail.
 */
class FirstAcceptedFactory(private val factories: List<CommandFactory>) : CommandFactory {

    /** @see FirstAcceptedFactory */
    constructor(vararg factories: CommandFactory) : this(factories.toList())

    /**
     * Tries all [factories] until first success.
     *
     * @return first successfully constructed command or null if all fail
     */
    override fun createCommand(expression: CommandExpr, input: Source, output: Sink, env: Environment): Command? {
        for (factory in factories) {
            factory.createCommand(expression, input, output, env)?.also { return it }
        }
        return null
    }
}