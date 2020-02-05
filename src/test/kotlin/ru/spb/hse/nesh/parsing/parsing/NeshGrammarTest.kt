package ru.spb.hse.nesh.parsing.parsing

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.spb.hse.nesh.parsing.AssignmentExpr
import ru.spb.hse.nesh.parsing.NeshGrammar
import ru.spb.hse.nesh.parsing.PipedExpression
import ru.spb.hse.nesh.language.dummyEnv
import ru.spb.hse.nesh.parsing.CommandExpr

class NeshGrammarTest {
    private val grammar = NeshGrammar(dummyEnv)

    private fun assertPipedExpression(input: String, vararg commands: List<String>) {
        assertEquals(PipedExpression(commands.map(::CommandExpr)), grammar.parseToEnd(input))
    }

    private fun assertAssignments(input: String, variable: String, value: String) {
        assertEquals(AssignmentExpr(variable, value), grammar.parseToEnd(input))
    }

    @Test
    fun `one word`() {
        assertPipedExpression("qwe", listOf("qwe"))
    }

    @Test
    fun `several words`() {
        assertPipedExpression("qwe ewq www", listOf("qwe", "ewq", "www"))
    }

    @Test
    fun `different types of word without spaces`() {
        assertPipedExpression("q'w'\"e\"", listOf("qwe"))
    }

    @Test
    fun pipe() {
        assertPipedExpression("qwe | www", listOf("qwe"), listOf("www"))
    }

    @Test
    fun pipes() {
        assertPipedExpression("qwe | www | ewq", listOf("qwe"), listOf("www"), listOf("ewq"))
    }

    @Test
    fun `empty space between pipes`() {
        assertPipedExpression("qwe || ewq", listOf("qwe"), emptyList(), listOf("ewq"))
    }

    @Test
    fun `pipe character in quotes`() {
        assertPipedExpression("\"|\"", listOf("|"))
    }

    @Test
    fun `= sign does not break parser`() {
        assertPipedExpression("echo = qwe", listOf("echo", "=", "qwe"))
    }

    @Test
    fun `empty variable doesn't count as argument`() {
        assertPipedExpression("\$empty \$empty", emptyList())
    }

    @Test
    fun `assignments are parsed`() {
        assertAssignments("x=y", "x", "y")
    }

    @Test
    fun `assignments with multiple word types`() {
        assertAssignments("x=' a 'y", "x", " a y")
    }

    @Test
    fun `assignments with spaces`() {
        assertAssignments("x=a b c", "x", "a b c")
    }

    @Test
    fun `assignments multiple spaces in a row`() {
        assertAssignments("x=a   c", "x", "a c")
    }
}