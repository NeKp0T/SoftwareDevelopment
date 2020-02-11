package ru.spb.hse.nesh.interpreter.commands.builtins.grep

import ru.spb.hse.nesh.interpreter.commands.Command
import ru.spb.hse.nesh.interpreter.commands.builtins.AbstractCommandFactory
import ru.spb.hse.nesh.interpreter.commands.io.Sink
import ru.spb.hse.nesh.interpreter.commands.io.Source
import ru.spb.hse.nesh.interpreter.interfaces.Environment

/** Command for `grep`, uses [GrepCLI]. */
class GrepCommand(private val source: Source, private val sink: Sink, private val argv: List<String>) : Command {
    override fun runWait(): Int = GrepCLI(source, sink).mainReturningCode(argv)
}

/** Factory that builds a [GrepCommand] if command name is "grep". */
object GrepFactory : AbstractCommandFactory() {
    override fun createCommandByName(
        programName: String,
        arguments: List<String>,
        input: Source,
        output: Sink,
        env: Environment
    ): Command? = when (programName) {
        "grep" -> GrepCommand(input, output, arguments)
        else -> null
    }
}