package ru.spb.hse.nesh.interpreter.commands.builtins.grep

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import ru.spb.hse.nesh.interpreter.commands.builtins.CliktCommandBuiltin
import ru.spb.hse.nesh.interpreter.commands.io.Sink
import ru.spb.hse.nesh.interpreter.commands.io.Source
import java.io.File
import java.io.IOException

/**
 * [ClicktCommandBuiltin] for grep. Uses [Grepper].
 *
 * Does not close [source]/[sink].
 *
 * Error code is 0 unless command line arguments cannot be parsed.
 *
 * Usage: `grep --help` to see help.
 *
 * @throws IOException  if encounters problems with [source] or [sink]
 */
class GrepCLI(private val source: Source, private val sink: Sink) : CliktCommandBuiltin(
    source,
    sink,
    name = "grep",
    help = GENERAL_HELP
) {

    private val ignoreCase: Boolean by option("--ignore-case", "-i", help = IGNORE_CASE_HELP).flag()
    private val wholeWord: Boolean by option("--word-regexp", "-w", help = WHOLE_WORD_HELP).flag()
    private val afterContext: Int by option("--after-context", "-A", metavar = "NUM", help = AFTER_CONTEXT_HELP).int().default(0)

    private val grepper: Grepper by argument("PATTERN").convert { Grepper(it, ignoreCase, wholeWord, afterContext) }
    private val files: List<File> by argument("FILE").file().multiple()

    override fun run() {
        val writer = sink.getSinkStream().bufferedWriter()
        if (files.isEmpty()) {
            grepper.grep(source.getSourceStream().bufferedReader(), writer)
        } else {
            grepper.grep(writer, files)
        }
        writer.flush()
    }

    companion object {
        private const val IGNORE_CASE_HELP: String = "Ignore case distinctions, so that characters that differ only in case match each other."
        private const val WHOLE_WORD_HELP: String = "Select only those lines containing matches that form whole words. A word is a sequence of characters that does not have word characters before nor after."
        private const val AFTER_CONTEXT_HELP: String = "Print NUM lines of trailing context after matching lines. Places a line containing a group separator (--) between contiguous groups of matches."
        private const val GENERAL_HELP: String = "grep searches for a PATTERN in each FILE or in standard input if no files provided. It prints each line containing a match to standart output."
    }
}