package ru.spb.hse.nesh.interpreter.commands.builtins.grep

import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.spb.hse.nesh.interpreter.commands.io.StringSink
import ru.spb.hse.nesh.interpreter.commands.io.StringSource
import java.io.File
import java.io.StringWriter
import java.nio.file.Files

internal class GrepTest {

    private fun assertPrintsThis(grepper: Grepper, lines: List<String>, correct: List<String>) {
        val writer = StringWriter()
        grepper.grep(
            lines.joinToString(System.lineSeparator()).reader().buffered(),
            writer
        )
        assertEquals(
            joinToFileContent(correct),
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

    @Test
    fun `grep from single file works`() {
        val lines = listOf("a", "b", "c")
        val grepper = Grepper("[ab]")

        val calls = mutableListOf<String>()
        val writer = mockk<StringWriter>()
        every { writer.append(capture(calls)) } answers { writer }

        usingTempFile(lines) { file ->
            grepper.grep(writer, file)

            verifySequence {
                // can't use appendln(string) because it is an extension function that does chained method calls
                lines.take(2).forEach { line ->
                    writer.append(line)
                    writer.appendln()
                }
            }
        }
    }

    @Test
    fun `grep from files prints filenames and works correctly`() {
        val lines = listOf("a", "b", "c")
        val grepper = Grepper("[ab]")

        val writer = mockk<StringWriter>()
        every { writer.append(any<String>()) } answers { writer }

        usingTempFile(lines) { file ->
            grepper.grep(writer, listOf(file, file))

            verifySequence {
                repeat(2) {
                    // can't use appendln(string) because it is an extension function that does chained method calls
                    writer.append(match { line: String ->
                        line.contains(file.name)
                    })
                    writer.appendln()
                    lines.take(2).forEach { line ->
                        writer.append(line)
                        writer.appendln()
                    }
                }
            }
        }
    }

    @Test
    fun `GrepCLI runs grep from source if no files provided`() {
        val lines = listOf("a", "ba", "c")
        val source = StringSource(joinToFileContent(lines))
        val sink = StringSink()
        GrepCLI(source, sink).mainReturningCode(listOf("a"))
        assertEquals(joinToFileContent(lines.take(2)), sink.getOutput())
    }

    @Test
    fun `GrepCLI runs grep from file`() {
        val lines = listOf("a", "ba", "c")
        usingTempFile(lines) { file ->
            val sink = StringSink()
            GrepCLI(StringSource(""), sink).mainReturningCode(listOf("a", file.absolutePath))
            assertEquals(joinToFileContent(lines.take(2)), sink.getOutput())
        }
    }

    @Test
    fun `GrepCLI runs grep from files`() {
        val lines = listOf("a", "b", "c")

        val source = StringSource(joinToFileContent(lines))
        val sink = StringSink()


        usingTempFile(lines) { file ->
            GrepCLI(source, sink).mainReturningCode(listOf("[ab]", file.absolutePath, file.absolutePath))
            val results = sink.getOutput().split(System.lineSeparator()).dropLastWhile { it.isEmpty() }

            assertTrue(results.first().contains(file.name))
            assertEquals(lines.take(2), results.drop(1).take(2))
            assertEquals(results.take(3), results.drop(3))
        }
    }

    private fun usingTempFile(lines: List<String>, block: (File) -> Unit) {
        Files.createTempFile("test_grep", "suffix").toFile().apply{
            deleteOnExit()
            writeText(joinToFileContent(lines))
            block(this)
        }
    }

    private fun joinToFileContent(lines: List<String>) = lines.joinToString(System.lineSeparator(), postfix = System.lineSeparator())
}