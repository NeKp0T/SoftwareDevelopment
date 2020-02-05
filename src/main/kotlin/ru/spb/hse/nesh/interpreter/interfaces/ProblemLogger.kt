package ru.spb.hse.nesh.interpreter.interfaces

/**
 * Interface for logging errors in [ru.spb.hse.nesh.interpreter.Interpreter].
 *
 * Commands still use their error stream to print error information.
 */
interface ProblemLogger {
    /** Logs a problem */
    fun logProblem(message: String)
    /** Logs a problem caused by [cause] */
    fun logProblem(message: String, cause: Throwable)
}