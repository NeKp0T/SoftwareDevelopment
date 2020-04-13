package ru.spb.hse.nesh.interpreter.commands

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

internal class FirstAcceptedFactoryTest {
    @Test
    fun `chooses first command out of many`() {
        val firstFactory = mockk<CommandFactory>()
        val secondFactory = mockk<CommandFactory>()
        val thirdFactory = mockk<CommandFactory>()

        val secondFactoryCommand = mockk<Command>()
        val thirdFactoryCommand = mockk<Command>()

        every { firstFactory.createCommand(any(), any(), any(), any()) } returns null
        every { secondFactory.createCommand(any(), any(), any(), any()) } returns secondFactoryCommand
        every { thirdFactory.createCommand(any(), any(), any(), any()) } returns thirdFactoryCommand

        FirstAcceptedFactory(firstFactory, secondFactory, thirdFactory).createCommand(
            mockk(), mockk(), mockk(), mockk()
        ).also { assertSame(secondFactoryCommand, it) }
    }

    @Test
    fun `returns null if none work`() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            // weird problems with mockito on windows
            return
        }
        val firstFactory = mockk<CommandFactory>()
        val secondFactory = mockk<CommandFactory>()
        val thirdFactory = mockk<CommandFactory>()

        every { firstFactory.createCommand(any(), any(), any(), any()) } returns null
        every { secondFactory.createCommand(any(), any(), any(), any()) } returns null
        every { thirdFactory.createCommand(any(), any(), any(), any()) } returns null

        FirstAcceptedFactory(firstFactory, secondFactory, thirdFactory).createCommand(
            mockk(), mockk(), mockk(), mockk()
        ).also { assertNull(it) }
    }
}