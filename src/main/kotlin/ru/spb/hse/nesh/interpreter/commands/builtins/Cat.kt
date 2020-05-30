package ru.spb.hse.nesh.interpreter.commands.builtins

import ru.spb.hse.nesh.interpreter.commands.io.Sink
import ru.spb.hse.nesh.interpreter.commands.io.Source
import ru.spb.hse.nesh.interpreter.interfaces.Environment
import java.io.IOException
import java.io.InputStream

// =^.^=
class Cat(
    input: Source,
    output: Sink,
    arguments: List<String>,
    env: Environment,
    expand: PathExpand
) : FileIteratingBuiltin(input, output, arguments, env, expand) {
    override fun dealWithInput(inputStream: InputStream) {
        inputStream.copyTo(output.getSinkStream())
    }

    override fun reportIOException(ex: IOException) = reportException(ex)

    override fun reportSecurityException(ex: SecurityException) = reportException(ex)

    private fun reportException(ex: Exception) {
        System.err.println("""
                cat encountered an exception
                $ex
            """.trimIndent())
    }
}