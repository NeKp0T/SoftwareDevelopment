package ru.spb.hse.nesh.interpreter.commands.builtins

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import ru.spb.hse.nesh.interpreter.commands.io.StringSink
import ru.spb.hse.nesh.interpreter.interfaces.Environment
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ExitTest {
    @Test
    fun `exit sets exit variable`() {
        val envMock = mockk<Environment>(relaxed = true)

        Exit(envMock, emptyList(), StringSink()).runWait()

        verify {
            envMock[Exit.EXIT_VARIABLE] = neq("")
        }
    }

    @ParameterizedTest
    @MethodSource("listsOfArguments")
    fun `exit does not change EXIT_VARIABLE if provided with arguments`(arguments: List<String>) {
        val envMock = mockk<Environment>(relaxed = true)

        Exit(envMock, arguments, StringSink()).runWait()

        verify(inverse = true) {
            envMock[Exit.EXIT_VARIABLE] = neq("")
        }
    }

    private fun listsOfArguments() = Stream.of(
        listOf("qwe"),
        listOf("qwe", "ewq"),
        (1 .. 10).map(Int::toString)
    )
}