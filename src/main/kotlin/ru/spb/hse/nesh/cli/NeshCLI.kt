package ru.spb.hse.nesh.cli

import ru.spb.hse.nesh.interpreter.Interpreter
import ru.spb.hse.nesh.interpreter.commands.builtins.Exit

/** Runs CLI cycle, printing provided [prompt] and interpreting input with [interpreter]. */
fun runCLI(interpreter: Interpreter = Interpreter(), prompt: Prompt = PWDPrompt) {
    while (interpreter.getEnvVariable(Exit.EXIT_VARIABLE) == "") {
        print(prompt.getPrompt(interpreter.env))
        readLine()?.also { interpreter.interpret(it) } ?: break
    }
}