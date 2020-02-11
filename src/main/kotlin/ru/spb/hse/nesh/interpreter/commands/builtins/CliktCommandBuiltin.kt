package ru.spb.hse.nesh.interpreter.commands.builtins

import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.output.CliktConsole
import ru.spb.hse.nesh.interpreter.commands.io.Sink
import ru.spb.hse.nesh.interpreter.commands.io.Source
import java.io.BufferedReader
import java.io.IOError
import java.io.Writer

/**
 * A [CliktCommand] with I/O redirected to provided [sink] and [source].
 *
 * It also provides a method to run [main], but in case of error it returns error code
 * instead of calling [System.exit].
 */
abstract class CliktCommandBuiltin(
    source: Source,
    sink: Sink,
    help: String = "",
    epilog: String = "",
    name: String? = null,
    invokeWithoutSubcommand: Boolean = false,
    printHelpOnEmptyArgs: Boolean = false,
    helpTags: Map<String, String> = emptyMap(),
    autoCompleteEnvvar: String? = ""
) : CliktCommand(help, epilog, name, invokeWithoutSubcommand, printHelpOnEmptyArgs, helpTags, autoCompleteEnvvar) {

    private val writer = sink.getSinkStream().writer()
    private val reader = source.getSourceStream().bufferedReader()

    init {
        context {
            this.console = BuiltinCliktConsole(reader, writer)
        }
    }

    /**
     *  Parse arguments and print a message if an error occurs.
     *  Does not stop process in case of error.
     *
     *  @return error code
     *  @see    [main]
     */
    fun mainReturningCode(argv: List<String>): Int {
        try {
            parse(argv)
        } catch (e: PrintHelpMessage) {
            echo(e.command.getFormattedHelp())
            return 0
        } catch (e: PrintCompletionMessage) {
            val s = if (e.forceUnixLineEndings) "\n" else context.console.lineSeparator
            echo(e.message, lineSeparator = s)
            return 0
        } catch (e: PrintMessage) {
            echo(e.message)
            return 0
        } catch (e: UsageError) {
            echo(e.helpMessage(), err = true)
            return e.statusCode
        } catch (e: CliktError) {
            echo(e.message, err = true)
            return 1
        } catch (e: Abort) {
            echo("Aborted!", err = true)
            return if (e.error) 1 else 0
        }
        return 0
    }
}

/**
 * An implementation of [CliktConsole] that uses provided writer and reader.
 */
class BuiltinCliktConsole(private val reader: BufferedReader, private val writer: Writer) : CliktConsole {
    override val lineSeparator: String get() = System.lineSeparator()

    override fun print(text: String, error: Boolean) = when(error) {
        false -> {
            writer.write(text)
            writer.flush()
        }
        true -> System.err.println(text)
    }

    /**
     * Does not support hiding input.
     */
    override fun promptForLine(prompt: String, hideInput: Boolean): String? {
        return try {
            print(prompt)
            reader.readLine()
        } catch (ex: IOError) {
            null
        }
    }
}