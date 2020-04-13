package ru.spb.hse.nesh.interpreter.commands.io

import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream

/**
 * Provides interface for a [ru.spb.hse.nesh.interpreter.commands.Command] to read from.
 */
interface Source : Closeable {
    /**
     * Returns an input stream for command to use. Should return the same stream if called multiple times.
     */
    fun getSourceStream(): InputStream
    /**
     * Returns a redirect for [ProcessBuilder] that may be used to get input from.
     */
    fun getSourceRedirect(): ProcessBuilder.Redirect
}

/**
 * Provides interface for a [ru.spb.hse.nesh.interpreter.commands.Command] to write to.
 */
interface Sink : Closeable {
    /**
     * Returns an output stream for command to use. Should return the same stream if called multiple times.
     */
    fun getSinkStream(): OutputStream
    /**
     * Returns a redirect for [ProcessBuilder] that may be used to put output.
     */
    fun getSinkRedirect(): ProcessBuilder.Redirect
}