package ru.spb.hse.nesh.interpreter.commands.builtins.grep

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int

class TestCli : CliktCommand() {
    // TODO description for help

    private val ignoreCase: Boolean by option("--ignore-case", "-i", help = IGNORE_CASE_HELP).flag()
    private val wholeWord: Boolean by option("--word-regexp", "-w", help = WHOLE_WORD_HELP).flag()
    private val afterContext: Int by option("--after-context", "-A", metavar = "NUM", help = AFTER_CONTEXT_HELP).int().default(0)

    private val pattern: Regex by argument("PATTERN").convert { it.toRegex() }
    private val files: List<String> by argument("FILE").multiple()

    override fun run() {
        echo(pattern)
        echo(files)
    }

    companion object {
        private const val IGNORE_CASE_HELP: String = "Ignore case distinctions, so that characters that differ only in case match each other."
        private const val WHOLE_WORD_HELP: String = "Select only those lines containing matches that form whole words."
        private const val AFTER_CONTEXT_HELP: String = "Print NUM lines of trailing context after matching lines. Places a line containing a group separator (--) between contiguous groups of matches."
    }

}

fun main() {
    val cli = TestCli()
    cli.main(listOf("[abc].*", "sdasd", "kek.txt"))
    println(cli.getFormattedHelp())
}