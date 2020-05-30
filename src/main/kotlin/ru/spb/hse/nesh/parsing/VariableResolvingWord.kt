package ru.spb.hse.nesh.parsing

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.parser.Parser
import ru.spb.hse.nesh.interpreter.interfaces.Environment

/**
 * A [WordType] that resolves all variables in a word before modifying it more.
 */
interface VariableResolvingWord: WordType {
    override fun resolve(s: String, env: Environment) = modifyResolved(
        VariableResolvingGrammar(env).parseToEnd(s))

    /** Describes how line should be modified after variable resolution. */
    fun modifyResolved(resolved: String): WordResolution
}

/**
 * A grammar that resolves variables in a line.
 *
 * @constructor creates a grammar that takes variables from provided [Environment]
 * @param env [Environment] to get variables' values from
 */
class VariableResolvingGrammar(env: Environment): Grammar<String>() {
    private val variableSigns = "$"

    private val dollar by token("[$variableSigns]")
    private val variableText by token("[a-zA-Z][\\w]*")
    private val nonDollarText by token("[^$variableSigns]+")

    private val variableParser = -dollar * variableText map { env[it.text] }
    private val regularTextParser = variableText or nonDollarText map { it.text }
    private val dollarNotVariable = dollar map { it.text }

    override val rootParser: Parser<String> = zeroOrMore(variableParser or regularTextParser or dollarNotVariable) map
            { it.joinToString("") }
}