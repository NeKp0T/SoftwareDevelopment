package ru.spb.hse.nesh.interpreter.commands.builtins

import ru.spb.hse.nesh.interpreter.commands.Command
import ru.spb.hse.nesh.interpreter.commands.ExternalCommandFactory
import ru.spb.hse.nesh.interpreter.commands.io.Sink
import ru.spb.hse.nesh.interpreter.interfaces.Environment
import java.io.IOException

class Pwd(private val output: Sink, private val environment: Environment) :
    Command {
    override fun runWait(): Int {
        try {
            output.getSinkStream().writer().apply {
                appendln(environment[ExternalCommandFactory.PWD_VARIABLE])
                flush()
            }
        } catch (ex: IOException) {
            System.err.println("""
                pwd can't print for some reason.
                $ex
            """.trimIndent())
            return 1
        }
        return 0
    }

}