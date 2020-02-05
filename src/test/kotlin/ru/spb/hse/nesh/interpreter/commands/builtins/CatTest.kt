package ru.spb.hse.nesh.interpreter.commands.builtins

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import ru.spb.hse.nesh.interpreter.commands.io.StringSink
import ru.spb.hse.nesh.interpreter.commands.io.StringSource
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.random.Random

internal class CatTest {

    @Test
    fun `cats stdin if no arguments passed`() {
        val inputString = "abacaba test test test"
        val outputSink = StringSink()
        Cat(StringSource(inputString), outputSink, emptyList()).runWait()
        assertEquals(inputString, outputSink.getOutput())
    }

    @Test
    fun `cats a file`() {
        val inputString = "fsjldfsjflkdfsf  sfdsfs dfsf f"
        val inputFile = Files.createTempFile("test_cat", "suffix").toFile()
        inputFile.writeText(inputString)

        val outputSink = StringSink()
        Cat(StringSource("stdin, should be ignored"), outputSink, listOf(inputFile.absolutePath)).runWait()
        assertEquals(inputString, outputSink.getOutput())

        assertTrue(inputFile.delete(), "delete file, from which cat has read")
    }

    @Test
    fun `cats multiple files`() {
        val inputStrings = (1 .. 10).map { "string №$it" }
        val inputFiles = inputStrings.map { string ->
            Files.createTempFile("test_cat", "suffix").toFile().also { it.writeText(string) }
        }

        val outputSink = StringSink()
        Cat(StringSource("stdin, should be ignored"), outputSink, inputFiles.map(File::getAbsolutePath)).runWait()
        assertEquals(inputStrings.joinToString(""), outputSink.getOutput())

        inputFiles.forEach {
            assertTrue(it.delete())
        }
    }

    @Test
    fun `cat does not throw if file does not exist`() {
        assertDoesNotThrow {
            val nonExistantFilename = Random.Default.nextLong().toString()
            if (Paths.get(nonExistantFilename).toFile().exists()) {
                fail<Nothing>("What are the odds of a random file existing? Time to buy lottery tickets")
            }
            Cat(StringSource(""), StringSink(), listOf(nonExistantFilename)).runWait()
        }
    }
}