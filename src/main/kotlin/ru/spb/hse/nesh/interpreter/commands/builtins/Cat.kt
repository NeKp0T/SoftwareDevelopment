package ru.spb.hse.nesh.interpreter.commands.builtins

import ru.spb.hse.nesh.interpreter.commands.io.Sink
import ru.spb.hse.nesh.interpreter.commands.io.Source
import ru.spb.hse.nesh.interpreter.interfaces.Environment
import java.io.IOException
import java.io.InputStream

// =^.^=
/**
 * Iterates through files from arguemnts and copies them to its output.
 *
 * If no arguments given, uses standart input instead.
 *
 * Skips files that can't read.
 */
class Cat(
    input: Source,
    output: Sink,
    arguments: List<String>,
    env: Environment
) : FileIteratingBuiltin(input, output, arguments, env) {
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