package ru.spb.hse.nesh.interpreter.commands.io

import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream

/**
 * Provides interface for a [ru.spb.hse.nesh.interpreter.commands.Command] to read from.
 */
interface Source : Closeable {
    fun getSourceStream(): InputStream
    fun getSourceRedirect(): ProcessBuilder.Redirect
}

/**
 * Provides interface for a [ru.spb.hse.nesh.interpreter.commands.Command] to write to.
 */
interface Sink : Closeable {
    fun getSinkStream(): OutputStream
    fun getSinkRedirect(): ProcessBuilder.Redirect
}