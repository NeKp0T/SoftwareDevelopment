package ru.spb.hse.nesh.interpreter.commands.io

import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * Provides [Source] out of current process' stdin.
 */
object ShellInputSource : Source {
    override fun getSourceStream(): InputStream = System.`in`
    override fun getSourceRedirect(): ProcessBuilder.Redirect = ProcessBuilder.Redirect.INHERIT
    override fun close() {} // don't close System.in
}

/**
 * Provides [Sink] out of current process' stdout.
 */
object ShellOutputSink : Sink {
    override fun getSinkStream(): OutputStream = System.out
    override fun getSinkRedirect(): ProcessBuilder.Redirect = ProcessBuilder.Redirect.INHERIT
    override fun close() {} // don't close System.out
}

/**
 * Makes a [Source] out of a file.
 */
class PipeInputSource(pipe: File) : Source {
    private val sourceStreamHolder: InputStream = pipe.inputStream()
    private val sourceRedirectHolder: ProcessBuilder.Redirect = ProcessBuilder.Redirect.from(pipe)

    override fun getSourceStream(): InputStream = sourceStreamHolder
    override fun getSourceRedirect(): ProcessBuilder.Redirect = sourceRedirectHolder

    override fun close() {
        sourceStreamHolder.close()
    }
}

/**
 * Makes a [Sink] out of a file.
 */
class PipeOutputSink(pipe: File) : Sink {
    private val sinkStreamHolder: OutputStream = pipe.outputStream()
    private val sinkRedirectHolder: ProcessBuilder.Redirect = ProcessBuilder.Redirect.to(pipe)

    override fun getSinkStream(): OutputStream = sinkStreamHolder
    override fun getSinkRedirect(): ProcessBuilder.Redirect = sinkRedirectHolder

    override fun close() {
        sinkStreamHolder.close()
    }
}