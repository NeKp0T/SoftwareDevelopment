package ru.spb.hse.nesh.interpreter.commands.builtins

import ru.spb.hse.nesh.interpreter.commands.Command
import ru.spb.hse.nesh.interpreter.commands.ExternalCommandFactory
import ru.spb.hse.nesh.interpreter.commands.io.Sink
import ru.spb.hse.nesh.interpreter.interfaces.Environment
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.toList


/**
 * Prints all files (excluding hidden one) in the given directory or in current directory if no argument was given
 */
class Ls(private val output: Sink, private val arguments: List<String>, private val environment: Environment, private val expand: PathExpand) :
    Command {
    override fun runWait(): Int {
        try {
            if (!checkNumberOfArguments()) {
                System.err.println("wrong number of arguments")
            }
            val dir = arguments.getOrElse(0) {""}
            val path = Paths.get(expand.expand(dir))
            if (!Files.isDirectory(path)) {
                System.err.println("$dir is not a directory")
                return 1
            }
            val allEntries = Files.list(path).filter{ !Files.isHidden(it) }.map { it.fileName }.toList()
            output.getSinkStream().writer().apply {
                appendln(allEntries.joinToString(" "))
                flush()
            }
        } catch (ex: IOException) {
            System.err.println("""
                ls can't print for some reason.
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
