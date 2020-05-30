package ru.spb.hse.nesh.interpreter.commands.builtins

import ru.spb.hse.nesh.interpreter.commands.Command
import ru.spb.hse.nesh.interpreter.commands.CommandFactory
import ru.spb.hse.nesh.interpreter.commands.ExternalCommandFactory
import ru.spb.hse.nesh.interpreter.interfaces.Environment
import ru.spb.hse.nesh.parsing.CommandExpr
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Changes the current directory with the given one
 */
class Cd(private val arguments: List<String>, private val environment: Environment, private val expand: PathExpand) :
    Command {
    override fun runWait(): Int {
        try {
            if (!checkNumberOfArguments()) {
                System.err.println("invalid number of arguments")
                return 1;
            }
            val dir = arguments.getOrElse(0) {environment.getHome()}
            val path = Paths.get(expand.expand(dir))
            if (!Files.isDirectory(path)) {
                System.err.println("$dir is not a directory")
                return 1
            }
            environment.setPwd(path.normalize().toString())
        } catch (ex: IOException) {
            System.err.println("""
                cd encountered an error.
                $ex
            """.trimIndent())
            return 1
        }
        return 0
    }

    private fun checkNumberOfArguments() : Boolean {
        return arguments.size <= 1
    }
}