package ru.spb.hse.nesh.interpreter.commands.builtins

import ru.spb.hse.nesh.interpreter.commands.Command
import java.io.IOException
import java.io.OutputStream

class Echo(private val output: OutputStream, private val arguments: List<String>) :
    Command {
    override fun runWait(): Int = try {
        val writer = output.writer()
        writer.appendln(arguments.joinToString(" "))
        writer.flush()
        0
    } catch (ex: IOException) {
        System.err.println(
            """
            echo can't write:
            $ex
        """.trimIndent()
        )
        1
    }
}