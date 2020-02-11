package ru.spb.hse.nesh.interpreter.commands.builtins.grep

import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.validate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import ru.spb.hse.nesh.interpreter.commands.builtins.CliktCommandBuiltin
import ru.spb.hse.nesh.interpreter.commands.io.ShellInputSource
import ru.spb.hse.nesh.interpreter.commands.io.ShellOutputSink
import ru.spb.hse.nesh.interpreter.commands.io.Sink
import ru.spb.hse.nesh.interpreter.commands.io.Source
import java.io.File

class GrepCLI(source: Source, sink: Sink) : CliktCommandBuiltin(source, sink, help = GENERAL_HELP) {

    private val ignoreCase: Boolean by option("--ignore-case", "-i", help = IGNORE_CASE_HELP).flag()
    private val wholeWord: Boolean by option("--word-regexp", "-w", help = WHOLE_WORD_HELP).flag()
    private val afterContext: Int by option("--after-context", "-A", metavar = "NUM", help = AFTER_CONTEXT_HELP).int().default(0)

    private val grepper: Grepper by argument("PATTERN").convert { Grepper(it, ignoreCase, wholeWord, afterContext) }
    private val files: List<File> by argument("FILE").file().multiple()

    override fun run() {
        echo(grepper)
        echo(afterContext)
    }

    companion object {
        private const val IGNORE_CASE_HELP: String = "Ignore case distinctions, so that characters that differ only in case match each other."
        private const val WHOLE_WORD_HELP: String = "Select only those lines containing matches that form whole words. A word is a sequence of characters that does not have word characters before nor after."
        private const val AFTER_CONTEXT_HELP: String = "Print NUM lines of trailing context after matching lines. Places a line containing a group separator (--) between contiguous groups of matches."
        private const val GENERAL_HELP: String = "grep searches for a PATTERN in each FILE or in standard input if no files provided. It prints each line containing a match to standart output."
    }
}

fun main() {
    val cli = GrepCLI(ShellInputSource, ShellOutputSink)
    try {
        cli.mainReturningCode(listOf("-iw", "-A", "123", "(asd)", "sdasd", "kek.txt"))
    } catch (ex: CliktError) {
        println(ex.message)
    }
    println(cli.getFormattedHelp())
}