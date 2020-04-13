package ru.spb.hse.nesh.interpreter.interfaces

import ru.spb.hse.nesh.parsing.AssignmentExpr
import ru.spb.hse.nesh.parsing.PipedExpression

/**
 * An interface that can execute both types of expressions.
 */
interface ExpressionExecutor {
    fun assign(assignment: AssignmentExpr)
    fun execute(pipedExpression: PipedExpression)
}