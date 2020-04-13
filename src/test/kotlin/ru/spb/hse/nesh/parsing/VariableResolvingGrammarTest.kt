package ru.spb.hse.nesh.parsing

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import ru.spb.hse.nesh.parsing.VariableResolvingGrammar
import ru.spb.hse.nesh.language.dummyEnv

class VariableResolvingGrammarTest {
    private val grammar: VariableResolvingGrammar
        get() = VariableResolvingGrammar(dummyEnv)

    @Test
    fun `one variable`() {
        assertEquals("x", grammar.parseToEnd("\$x"))
    }

    @Test
    fun `no variables`() {
        assertEquals("asd", grammar.parseToEnd("asd"))
    }

    @Test
    fun `variable with single spaces`() {
        assertEquals(" x ", grammar.parseToEnd(" \$x "))
    }

    @Test
    fun `variable with multiple spaces`() {
        assertEquals("  x  ", grammar.parseToEnd("  \$x  "))
    }

    @Test
    fun `variable with a name "?"`() {
        assertEquals("0", grammar.parseToEnd("\$?"))
    }

    @Test
    fun `dollar without variable`() {
        val s = " \$. \$_ \$ "
        assertEquals(s, grammar.parseToEnd(s))
    }

    @Test
    fun `variable before punctuation`() {
        assertEquals("x;", grammar.parseToEnd("\$x;"))
    }

    @Test
    fun `mixed case`() {
        assertEquals(" x x qwe x  xx;", grammar.parseToEnd(" \$x x qwe \$x__x\$x;"))
    }

    @Test
    fun `nonexistent variable goes to empty string`() {
        assertEquals("", grammar.parseToEnd("\$nonexistent"))
    }
}