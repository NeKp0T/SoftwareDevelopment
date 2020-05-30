package ru.spb.hse.nesh.parsing

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.parser.Parser
import ru.spb.hse.nesh.interpreter.interfaces.Environment

/**
 * A grammar that parses lines to complete [Expression]s of nesh language
 *
 * Supports single and double quoted strings and variable substitution.
 * These things work like in bash.
 *
 * @param env used to determine values of substituted variables
 */
class NeshGrammar(env: Environment) : Grammar<Expression>() {
    private val pipe by token("[|]")
    private val ws by token("[\\s]+")
    private val assignmentStart by token("^[\\w]+=")
    private val quoteless by token("[^\\s'\"|]+")
    private val singleQuoted by token("'[^']*'")
    private val doubleQuoted by token("\"[^\"]*\"")

    private val whitespaceType = WhitespaceWord
    private val whitespaceParser = ws map { whitespaceType.resolve(it.text, env) }

    private val quotelessType = RegularWord
    private val quotelessParser = quoteless map { quotelessType.resolve(it.text, env) }

    private val singleQuotedType = SingleQuotedWord
    private val singleQuotedParser = singleQuoted map {
        singleQuotedType.resolve(
            it.text.drop(
                1
            ).dropLast(1), env
        )
    }

    private val doubleQuotedType = DoubleQuotedWord
    private val doubleQuotedParser = doubleQuoted map { doubleQuotedType.resolve(it.text.drop(1).dropLast(1), env) }

    private val word: Parser<WordResolution> = whitespaceParser or quotelessParser or singleQuotedParser or doubleQuotedParser
    private val words: Parser<List<String>> = zeroOrMore(word) map WordResolution.Companion::concatWordResolutions
    private val pipeSeparated: Parser<PipedExpression> = separated(words, pipe, acceptZero = true) map {
        PipedExpression(
            it.terms.map(::CommandExpr)
        )
    }

    private val assignmentVariable = assignmentStart map { it.text.dropLast(1) }
    private val assignment = assignmentVariable * words map { (variable, words) ->
        AssignmentExpr(variable, words.joinToString(" "))
    }

    override val rootParser = assignment or pipeSeparated
}