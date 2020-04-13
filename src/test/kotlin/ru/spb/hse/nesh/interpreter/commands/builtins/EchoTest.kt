package ru.spb.hse.nesh.interpreter.commands.builtins

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import ru.spb.hse.nesh.interpreter.commands.io.StringSink

internal class EchoTest {
    @Test
    fun `prints single parameter`() {
        val sink = StringSink()
        val string = "qweqwe"
        Echo(sink.getSinkStream(), listOf(string)).runWait()
        assertEquals(string + "\n", sink.getOutput())
    }

    @Test
    fun `prints multiple parameters`() {
        val sink = StringSink()
        val strings = listOf("qweqwe", "eweq", "ew")
        Echo(sink.getSinkStream(), strings).runWait()
        assertEquals(strings.joinToString(" ") + "\n", sink.getOutput())
    }
}