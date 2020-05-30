package ru.spb.hse.nesh.interpreter.commands.io

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

class StringSource(content: String) : Source {
    private val stream = content.byteInputStream()

    override fun getSourceStream(): InputStream = stream

    override fun getSourceRedirect(): ProcessBuilder.Redirect = throw NotImplementedError("SourceStream usage expected")

    override fun close() {
        stream.close()
    }
}

class StringSink : Sink {
    private val stream = ByteArrayOutputStream()

    fun getOutput(): String = stream.toString()

    override fun getSinkStream(): OutputStream = stream

    override fun getSinkRedirect(): ProcessBuilder.Redirect = throw NotImplementedError("SinkStream usage expected")

    override fun close() {
        stream.close()
    }
}