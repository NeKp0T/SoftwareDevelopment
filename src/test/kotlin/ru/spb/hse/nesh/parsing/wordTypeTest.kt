package ru.spb.hse.nesh.parsing

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import ru.spb.hse.nesh.language.dummyEnv
import ru.spb.hse.nesh.parsing.*

abstract class WordTypeTest {
    abstract val wordType: WordType

    fun assertResolve(s: String, vararg resultWords: String, breakAfter: Boolean = false) {
        assertEquals(WordResolution(resultWords.asList(), breakAfter), wordType.resolve(s, dummyEnv))
    }
}

class WhitespaceWordTypeTest : WordTypeTest() {
    override val wordType = WhitespaceWord

    @Test
    fun `whitespace resolve returns right value`() {
        assertResolve(" ", breakAfter = true)
    }
}

class RegularWordTypeTest : WordTypeTest() {
    override val wordType = RegularWord

    @Test
    fun `regular resolve splits`() {
        assertResolve("one two three", "one", "two", "three")
    }

    @Test
    fun `regular resolve splits by multiple spaces`() {
        assertResolve("one   two \tthree", "one", "two", "three")
    }

    @Test
    fun `regular resolve substitutes variables`() {
        assertResolve("\$x", "x")
    }

    @Test
    fun `regular resolve splits substituted variables`() {
        assertResolve("\$x__x", "x", "x")
    }

    @Test
    fun `regular resolve with leading whitespace`() {
        assertResolve(" x", "x")
    }

    @Test
    fun `regular resolve sets breakAfter`() {
        assertResolve("x ", "x", breakAfter = true)
    }

    @Test
    fun `regular resolve sets breakAfter space from variable`() {
        assertResolve("\$x_", "x", breakAfter = true)
    }
}

class SingleQuotedWordTest : WordTypeTest() {
    override val wordType = SingleQuotedWord

    @Test
    fun `single quoted does not split on whitespaces`() {
        val s = "wqeqew dsadsa  ad ds dsss    dd"
        assertResolve(s, s)
    }

    @Test
    fun `single quoted does not trim`() {
        val s = "  x  "
        assertResolve(s, s)
    }

    @Test
    fun `single quoted does not resolve variables`() {
        val s = "  \$x  "
        assertResolve(s, s)
    }
}

class DoubleQuotedWordTest : WordTypeTest() {
    override val wordType = DoubleQuotedWord

    @Test
    fun `double quoted does not split on whitespaces`() {
        val s = "wqeqew dsadsa  ad ds dsss    dd"
        assertResolve(s, s)
    }

    @Test
    fun `double quoted does not trim`() {
        val s = "  x  "
        assertResolve(s, s)
    }

    @Test
    fun `double quoted resolves variables`() {
        assertResolve("\$x", "x")
    }

    @Test
    fun `double quoted does not split on whitespaces from variables`() {
        assertResolve("\$x__x", "x  x")
    }
}
