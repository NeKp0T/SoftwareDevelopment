package ru.spb.hse.nesh.interpreter.commands.builtins

import ru.spb.hse.nesh.interpreter.commands.Command
import ru.spb.hse.nesh.interpreter.commands.io.Sink
import ru.spb.hse.nesh.interpreter.commands.io.Source
import ru.spb.hse.nesh.interpreter.interfaces.Environment
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Paths

// lacks a better name
abstract class FileIteratingBuiltin(
    private val input: Source,
    protected val output: Sink,
    private val arguments: List<String>,
    private val environment: Environment,
    private val expand: PathExpand
) :
    Command {
    override fun runWait(): Int {
        arguments.forEach { pathname ->
            try {
                File(expand.expand(pathname)).inputStream().use { dealWithInput(it) }
            } catch (ex: IOException) {
                reportIOException(ex)
            } catch (ex: SecurityException) {
                reportSecurityException(ex)
            }
        }
        if (arguments.isEmpty()) {
            try {
                dealWithInput(input.getSourceStream())
            } catch (ex: IOException) {
                reportIOException(ex)
            }
        }
        afterInputs()
        return 0
    }

    abstract fun dealWithInput(inputStream: InputStream)

    open fun afterInputs() {}

    abstract fun reportIOException(ex: IOException)

    abstract fun reportSecurityException(ex: SecurityException)
}
