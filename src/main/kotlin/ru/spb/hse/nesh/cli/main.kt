package ru.spb.hse.nesh.cli

import ru.spb.hse.nesh.interpreter.Interpreter
import ru.spb.hse.nesh.interpreter.commands.builtins.Exit

/** Runs a command line interpreter of nesh with default parameters and default prompt. */
fun main() {
    val inter = Interpreter()
    runCLI(inter)
    println("Stopped, exit message: ${inter.getEnvVariable(Exit.EXIT_VARIABLE)}")
}