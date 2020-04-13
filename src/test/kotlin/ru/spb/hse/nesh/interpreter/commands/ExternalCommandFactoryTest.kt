package ru.spb.hse.nesh.interpreter.commands

import org.junit.jupiter.api.Test
import ru.spb.hse.nesh.interpreter.commands.io.PipeInputSource
import ru.spb.hse.nesh.interpreter.commands.io.PipeOutputSink
import ru.spb.hse.nesh.interpreter.implementations.LocalEnvironment
import ru.spb.hse.nesh.parsing.CommandExpr
import java.io.File
import org.junit.jupiter.api.Assertions.*
import ru.spb.hse.nesh.interpreter.interfaces.Environment

internal class ExternalCommandFactoryTest {
    private fun getOutput(
        input: String,
        vararg commandWords: String,
        env: Environment = LocalEnvironment().apply { set(ExternalCommandFactory.PWD_VARIABLE, System.getProperty("user.home")) }
    ): String {
        val inputFile = newTempFile()
        inputFile.writeText(input)
        val outputFile = newTempFile()

        val inputSource = PipeInputSource(inputFile)
        val outputSink = PipeOutputSink(outputFile)

        inputSource.use { outputSink.use {
            ExternalCommandFactory.createCommand(
                CommandExpr(commandWords.toList()),
                inputSource,
                outputSink,
                env
            )!!.runWait()
        } }

        return outputFile.readText()
    }

    @Test
    fun `runs command (git) and gets it's output`() {
        val pingOutput = getOutput("", "git")
        assertTrue(pingOutput.contains("git ["), "if git outputs it's usage")
    }

    @Test
    fun `provides stdin to commands`() {
        val env = LocalEnvironment()
        env[ExternalCommandFactory.PWD_VARIABLE] = System.getProperty("user.home")

        val input = "asdadasd"
        val output = getOutput(input, "more", env = env)
        assertTrue(output.contains(input), "output = $output") // contains because I'm afraid of windows
    }

    @Test
    fun `commands inherit environment & (test skipped on windows)`() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            return
        }

        val env = LocalEnvironment()
        env[ExternalCommandFactory.PWD_VARIABLE] = System.getProperty("user.home")

        val varName = "x"
        val varValue = "vevevvr"
        env[varName] = varValue
        val output = getOutput("echo \$x", "sh", env = env)
        assertEquals(varValue + System.lineSeparator(), output)
    }

    @Test
    fun `commands run in $PWD`() {
        val env = LocalEnvironment()
        env[ExternalCommandFactory.PWD_VARIABLE] = System.getProperty("user.home")

        val output = getOutput("", "pwd", env = env)
        assertEquals(env[ExternalCommandFactory.PWD_VARIABLE] + System.lineSeparator(), output)
    }

    private fun newTempFile(): File = File.createTempFile("test", "aaaa").apply { deleteOnExit() }
}