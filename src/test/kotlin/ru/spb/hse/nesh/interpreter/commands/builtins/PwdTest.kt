package ru.spb.hse.nesh.interpreter.commands.builtins

import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.spb.hse.nesh.interpreter.commands.ExternalCommandFactory
import ru.spb.hse.nesh.interpreter.commands.io.StringSink
import ru.spb.hse.nesh.interpreter.interfaces.Environment

internal class PwdTest {

    @Test
    fun `pwd gets $PWD and returns it`() {
        val envMock = mockk<Environment>(relaxed = true)
        val pwdReturn = "/well/lets/return/this"
        val sink = StringSink()

        every { envMock[ExternalCommandFactory.PWD_VARIABLE] } returns pwdReturn

        Pwd(sink, envMock).runWait()

        verifySequence { envMock[ExternalCommandFactory.PWD_VARIABLE] }
        assertEquals(pwdReturn + "\n", sink.getOutput())
    }
}