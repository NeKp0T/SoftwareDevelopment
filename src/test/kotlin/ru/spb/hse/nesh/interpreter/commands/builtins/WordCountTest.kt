package ru.spb.hse.nesh.interpreter.commands.builtins

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import ru.spb.hse.nesh.interpreter.commands.io.StringSink
import ru.spb.hse.nesh.interpreter.commands.io.StringSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class WordCountTest {

    @ParameterizedTest
    @MethodSource("listsOfArguments")
    fun `wc counts from stdin correctly`(contents: String) {
        val sink = StringSink()
        WordCount(StringSource(contents), sink, emptyList()).runWait()
        assertEquals(correctAnswer(contents).expectedAnswer(), sink.getOutput())
    }

    fun listsOfArguments(): Stream<String> = Stream.of(
        "qwe",
        "w1 w2",
        " space_in_front",
        "space_in_end ",
        "many_spaces_in_a   row",
        "so\nmany\nlines\n!",
        "long string\n".repeat(100)
    )

    private fun correctAnswer(contents: String) = WordCountResult(
        contents.count { it == '\n' },
        contents.trim().split("\\s+".toRegex()).size,
        contents.length
    )

    internal class WordCountResult(
        private val newlineCount: Int,
        val wordCount: Int,
        val byteCount: Int
    ) {
        fun expectedAnswer() = "$newlineCount $wordCount $byteCount\n"
    }
}

