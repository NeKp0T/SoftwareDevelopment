package ru.spb.hse.nesh.interpreter.commands.builtins

import ru.spb.hse.nesh.interpreter.commands.io.Sink
import ru.spb.hse.nesh.interpreter.commands.io.Source
import java.io.IOException
import java.io.InputStream
import java.lang.Exception

/**
 * Iterates through files given as arguments and prints number of newlines, words and bytes in each.
 *
 * If no arguments provided uses standart input instead.
 *
 * Skips files that can't read.
 *
 * Assumes line separator is suffix-free.
 */
class WordCount(
    input: Source,
    output: Sink,
    arguments: List<String>
) : FileIteratingBuiltin(input, output, arguments) {

    private val outputWriter = output.getSinkStream().bufferedWriter()

    override fun dealWithInput(inputStream: InputStream) {
        var newlineCount = 0
        var wordCount = 0
        var byteCount = 0
        var lastWasWord = false
        var delimPrefix = 0

        try {
            val buffer = ByteArray(bufferSize)
            var readSize: Int
            while (inputStream.read(buffer).also { readSize = it } != -1) {
                val read = buffer.take(readSize)

                byteCount += readSize

                for (char in read.map(Byte::toChar)) {
                    if (char == System.lineSeparator()[delimPrefix]) {
                        delimPrefix += 1
                        if (delimPrefix == System.lineSeparator().length) {
                            delimPrefix = 0
                            newlineCount += 1
                        }
                    }

                    if (!lastWasWord && !char.isWhitespace())
                        wordCount++
                    lastWasWord = !char.isWhitespace()
                }
            }
        } finally {
            outputWriter.appendln("$newlineCount $wordCount $byteCount")
        }
    }

    override fun afterInputs() {
        outputWriter.flush()
    }

    override fun reportIOException(ex: IOException) = reportException(ex)

    override fun reportSecurityException(ex: SecurityException) = reportException(ex)

    private fun reportException(ex: Exception) {
        System.err.println("""
                wc encountered an exception
                $ex
            """.trimIndent())
    }

    private val bufferSize = DEFAULT_BUFFER_SIZE
}