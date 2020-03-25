package ru.spb.hse.nesh.interpreter.commands.builtins

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import ru.spb.hse.nesh.interpreter.commands.ExternalCommandFactory
import ru.spb.hse.nesh.interpreter.commands.io.StringSink
import ru.spb.hse.nesh.interpreter.implementations.SystemBasedEnvironment
import java.nio.file.Files
import java.nio.file.Path

internal class CdTest {

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
    fun `cd handles absolute paths`() {
        val env = SystemBasedEnvironment()
        val testFS = createTestFS()
        Cd(listOf(testFS.toString()), env).runWait()
        assertEquals(testFS.toString(), env[ExternalCommandFactory.PWD_VARIABLE])
    }

    @Test
    fun `cd handles relative paths`() {
        val env = SystemBasedEnvironment()
        val testFS = createTestFS()
        env[ExternalCommandFactory.PWD_VARIABLE] = testFS.toString()
        Cd(listOf("dir1"), env).runWait()
        assertEquals(testFS.resolve("dir1").toString(), env[ExternalCommandFactory.PWD_VARIABLE])
    }

    @Test
    fun `cd switches to HOME when no argument given`() {
        val env = SystemBasedEnvironment()
        val testFS = createTestFS()
        env[ExternalCommandFactory.PWD_VARIABLE] = testFS.toString()
        Cd(listOf(), env).runWait()
        assertEquals(env["HOME"], env[ExternalCommandFactory.PWD_VARIABLE])
    }

    @Test
    fun `cd switches to parent when dots is given`() {
        val env = SystemBasedEnvironment()
        val testFS = createTestFS()
        env[ExternalCommandFactory.PWD_VARIABLE] = testFS.resolve("dir1").toString()
        Cd(listOf(".."), env).runWait()
        assertEquals(testFS.toString(), env[ExternalCommandFactory.PWD_VARIABLE])
    }

}