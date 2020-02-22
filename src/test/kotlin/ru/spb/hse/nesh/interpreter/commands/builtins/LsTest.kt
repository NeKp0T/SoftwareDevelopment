package ru.spb.hse.nesh.interpreter.commands.builtins

import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import ru.spb.hse.nesh.interpreter.commands.ExternalCommandFactory
import ru.spb.hse.nesh.interpreter.commands.io.StringSink
import ru.spb.hse.nesh.interpreter.implementations.SystemBasedEnvironment
import ru.spb.hse.nesh.interpreter.interfaces.Environment
import java.nio.file.Files
import java.nio.file.Path

internal class LsTest {

    private fun createTestFS(): Path {
        val root = Files.createTempDirectory("root")
        val dir1 = Files.createDirectory(root.resolve("dir1"))
        val dir2 = Files.createDirectory(root.resolve("dir2"))
        val file1 = Files.createFile(root.resolve("file1"))
        val file2 = Files.createFile(root.resolve("file2"))
        val innerDir1 = Files.createDirectory(dir1.resolve("innerDir1"))
        val innerDir2 = Files.createDirectory(dir1.resolve("innerDir2"))
        val innerFile1 = Files.createFile(dir1.resolve("innerFile1"))
        return root
    }

    @Test
    fun `ls prints content of pwd when no argument given`() {
        val env = SystemBasedEnvironment()
        val testFS = createTestFS()
        env[ExternalCommandFactory.PWD_VARIABLE] = testFS.toString()
        val sink = StringSink()
        Ls(sink, listOf(), env).runWait()
        assertEquals(setOf("dir1", "dir2", "file1", "file2"), sink.getOutput().split(" ").map{it.trim()}.toSet())
    }

    @Test
    fun `ls prints content of folder when folder is given`() {
        val env = SystemBasedEnvironment()
        val testFS = createTestFS()
        val sink = StringSink()
        Ls(sink, listOf(testFS.toString()), env).runWait()
        assertEquals(setOf("dir1", "dir2", "file1", "file2"), sink.getOutput().split(" ").map{it.trim()}.toSet())
    }

    @Test
    fun `ls handles relative paths`() {
        val env = SystemBasedEnvironment()
        val testFS = createTestFS()
        env[ExternalCommandFactory.PWD_VARIABLE] = testFS.toString()
        val sink = StringSink()
        Ls(sink, listOf("dir1"), env).runWait()
        assertEquals(setOf("innerDir1", "innerDir2", "innerFile1"), sink.getOutput().split(" ").map{it.trim()}.toSet())
    }

    @Test
    fun `ls does not throw when file is given and produces no output`() {
        val env = SystemBasedEnvironment()
        val testFS = createTestFS()
        env[ExternalCommandFactory.PWD_VARIABLE] = testFS.toString()
        val sink = StringSink()
        assertDoesNotThrow{Ls(sink, listOf("file1"), env).runWait()}
        assertEquals("", sink.getOutput())
    }

}