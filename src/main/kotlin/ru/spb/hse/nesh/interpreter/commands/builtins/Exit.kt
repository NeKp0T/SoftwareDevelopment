package ru.spb.hse.nesh.interpreter.commands.builtins

import ru.spb.hse.nesh.interpreter.commands.Command
import ru.spb.hse.nesh.interpreter.commands.io.Sink
import ru.spb.hse.nesh.interpreter.interfaces.Environment
import java.io.IOException

class Exit(private val environment: Environment, args: List<String>, private val output: Sink) : Command {
    private val argumentsProvided = args.isNotEmpty()

    override fun runWait(): Int {
        if (argumentsProvided) {
            try {
                output.getSinkStream().writer().appendln(TOO_MUCH_ARGUMENTS)
                return 1
            } catch (ex: IOException) {
                System.err.println("exit can't report problem ($TOO_MUCH_ARGUMENTS)\n$ex")
            }
        }
        environment[EXIT_VARIABLE] = "exit"
        return 0
    }

    companion object {
        const val EXIT_VARIABLE: String = "NEXIT"
        private const val TOO_MUCH_ARGUMENTS: String = "exit does not support arguments"
    }

}