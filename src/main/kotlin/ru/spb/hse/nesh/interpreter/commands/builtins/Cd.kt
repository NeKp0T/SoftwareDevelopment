package ru.spb.hse.nesh.interpreter.commands.builtins

import ru.spb.hse.nesh.interpreter.commands.Command
import ru.spb.hse.nesh.interpreter.commands.ExternalCommandFactory
import ru.spb.hse.nesh.interpreter.interfaces.Environment
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

class Cd(private val arguments: List<String>, private val environment: Environment) :
    Command {
    override fun runWait(): Int {
        try {
            val dir = arguments.getOrElse(0) {environment["HOME"]}
            val pwd = Paths.get(environment[ExternalCommandFactory.PWD_VARIABLE])
            val path = pwd.resolve(Paths.get(dir))
            if (!Files.isDirectory(path)) {
                System.err.println("$dir is not a directory")
                return 1
            }
            environment[ExternalCommandFactory.PWD_VARIABLE] = path.normalize().toString()
        } catch (ex: IOException) {
            System.err.println("""
                cd encountered an error.
                $ex
            """.trimIndent())
            return 1
        }
        return 0
    }
}
