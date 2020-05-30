package ru.spb.hse.nesh.parsing

import ru.spb.hse.nesh.interpreter.interfaces.ExpressionExecutor

abstract class Expression {
    abstract fun accept(executor: ExpressionExecutor)
}

data class CommandExpr(val words: List<String>)

data class PipedExpression(val commands: List<CommandExpr>) : Expression() {
    override fun accept(executor: ExpressionExecutor) {
        executor.execute(this)
    }
}

data class AssignmentExpr(val variable: String, val value: String): Expression() {
    override fun accept(executor: ExpressionExecutor) {
        executor.assign(this)
    }
}