package ru.spb.hse.nesh.interpreter.commands.builtins

import ru.spb.hse.nesh.interpreter.commands.io.Sink
import ru.spb.hse.nesh.interpreter.commands.io.Source
import java.io.IOException
import java.io.InputStream
import java.lang.Exception

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

        try {
            val buffer = ByteArray(bufferSize)
            var readSize: Int
            while (inputStream.read(buffer).also { readSize = it } != -1) {
                val read = buffer.take(readSize)

                byteCount += readSize

                newlineCount += read.count { it == '\n'.toByte() }

                for (char in read.map(Byte::toChar)) {
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