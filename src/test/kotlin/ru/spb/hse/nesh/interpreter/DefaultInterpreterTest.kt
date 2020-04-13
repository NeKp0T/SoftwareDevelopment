package ru.spb.hse.nesh.interpreter

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.spb.hse.nesh.interpreter.commands.io.PipeInputSource
import ru.spb.hse.nesh.interpreter.commands.io.PipeOutputSink
import java.io.File
import java.nio.file.Files
import org.junit.jupiter.api.Assertions.*

internal class DefaultInterpreterTest {

    private lateinit var defaultInterpreter: Interpreter
    private lateinit var inputFIle: File
    private lateinit var outputFile: File

    @BeforeEach
    fun initInterpreter() {
        inputFIle = createTempFile()
        outputFile = createTempFile()
        defaultInterpreter = Interpreter(input = PipeInputSource(inputFIle), output = PipeOutputSink(outputFile))
    }

    @Test
    fun `runs echo`() {
        val echoed = "qweewq"
        defaultInterpreter.interpret("echo '$echoed'")
        assertEquals(echoed + System.lineSeparator(), getOutput())
    }

    @Test
    fun `piped cats work`() {
        val input = "hi im a string!"
        setInput(input)
        defaultInterpreter.interpret("cat | cat | cat")
        assertEquals(input, getOutput())
    }

    @Test
    fun `variable setting works`() {
        val variable = "X"
        val value = "x_value"
        defaultInterpreter.interpret("$variable=$value")
        assertEquals(value, defaultInterpreter.getEnvVariable(variable))
    }

    @Test
    fun `pipe builtin | external command works`() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            // could not find anything that works on every windows system
            return
        }

        val value = "qweeee"
        defaultInterpreter.interpret("echo $value | more")
        val output = getOutput()
        assertTrue(output.contains(value), "actual output = $output") // contains because windows adds more newlines
    }

    @Test
    fun `builtin | external command | builtin works`() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            // could not find anything that works on every windows system
            return
        }

        val value = "qweeee"
        defaultInterpreter.interpret("echo $value | more | cat")
        val output = getOutput()
        assertTrue(output.contains(value), "actual output = $output") // contains because windows adds more newlines
    }

    private fun getOutput(): String = outputFile.readText()
    private fun setInput(input: String) = inputFIle.writeText(input)

    private fun createTempFile() = Files.createTempFile("interpretTest_", "suffix").toFile().apply { deleteOnExit() }
}