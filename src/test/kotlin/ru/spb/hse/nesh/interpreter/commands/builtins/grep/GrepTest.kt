package ru.spb.hse.nesh.interpreter.commands.builtins.grep

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.StringWriter

internal class GrepTest {

    private fun assertPrintsThis(grepper: Grepper, lines: List<String>, correct: List<String>) {
        val writer = StringWriter()
        grepper.grep(
            lines.joinToString(System.lineSeparator()).reader().buffered(),
            writer
        )
        assertEquals(
            correct.joinToString(System.lineSeparator(), postfix = System.lineSeparator()),
            writer.toString()
        )
    }

    @Test
    fun `grep prints lines with a match (only)`() {
        val lines = listOf("a", "b", "ax", "xa", "xax", "A")
        assertPrintsThis(Grepper("a"), lines, lines.filter { it.contains("a") })
    }

    @Test
    fun `grep uses regexps`() {
        val linesCorect = listOf("ad", "abd", "abcbcd", "abbd", "addd", "bdbdbdadbdb")
        val linesIncorrect = listOf("a d", "acc", "abc", "da")
        assertPrintsThis(Grepper("a[bc]*d"), linesCorect + linesIncorrect, linesCorect)
    }

    @Test
    fun `grep prints lines once`() {
        val lines = listOf("aa aa a a aa a")
        assertPrintsThis(Grepper("a"), lines, lines)
    }

    @Test
    fun `grep ignores case with ignoreCase`() {
        val lines = listOf("a", "A", "aA", "b")
        assertPrintsThis(Grepper("a", ignoreCase = true), lines, lines.dropLast(1))
    }

    @Test
    fun `grep matches only whole words with wholeWord`() {
        val lines = listOf("a", " a ", "a ", " a", "\\a/", " ab", " ba", "")
        assertPrintsThis(Grepper("a", wholeWord = true), lines, lines.dropLast(3))
    }

    @Test
    fun `grep with wholeWord matches even if the match itself is not a word`() {
        val lines1 = listOf(" a ", "  a  ", "| a |")
        assertPrintsThis(Grepper(" a ", wholeWord = true), lines1, lines1)

        val lines2 = listOf("a a", " a a ")
        assertPrintsThis(Grepper("a a", wholeWord = true), lines2, lines2)
    }

    @Test
    fun `grep with afterContext prints context`() {
        val lines = listOf("a", "qwe", "ewq", "bbb")
        assertPrintsThis(Grepper("a", afterContext = 2), lines, lines.take(3))
    }

    @Test
    fun `grep with afterContext prints separator between groups`() {
        val lines = listOf("a", "qwe", "ewq", "bbb", "a")
        assertPrintsThis(Grepper("a", afterContext = 1), lines, lines.take(2) + listOf("--") + lines.takeLast(1))
    }

    @Test
    fun `grep with afterContext doesn't print separator if groups collide`() {
        val lines = listOf("a", "qwe", "a", "bbb", "lll")
        assertPrintsThis(Grepper("a", afterContext = 1), lines, lines.take(4))
    }

    @Test
    fun `grep with afterContext doesn't print separator if groups overlap`() {
        val lines = listOf("a", "qwe", "a", "bbb", "lll")
        assertPrintsThis(Grepper("a", afterContext = 2), lines, lines)
    }
}