package ru.spb.hse.nesh.interpreter.implementations

import ru.spb.hse.nesh.interpreter.interfaces.ProblemLogger

/** A [ProblemLogger] that simply attaches exception-cause to the message */
abstract class ExceptionAttacherLogger : ProblemLogger {
    override fun logProblem(message: String, cause: Throwable) = logProblem("message\n$cause")
}

/** A [ProblemLogger] that prints all logs to [System.err] */
class SerrLogger : ExceptionAttacherLogger() {
    override fun logProblem(message: String) = System.err.println(message)
}