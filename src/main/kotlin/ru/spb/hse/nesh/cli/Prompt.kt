package ru.spb.hse.nesh.cli

import ru.spb.hse.nesh.interpreter.commands.ExternalCommandFactory
import ru.spb.hse.nesh.interpreter.interfaces.Environment

/** Describes what prompt should CLI show to a user */
interface Prompt {
    /** Returns prompt line that will be shown to a user */
    fun getPrompt(environment: Environment): String
}

/** A prompt that always returns an empty string. */
object EmptyPrompt : Prompt {
    /** Always returns an empty string */
    override fun getPrompt(environment: Environment): String = ""
}

/**
 * A prompt that prints working directory.
 *
 * Prompt directory is taken from environment variable `$PWD`
 * (actual variable is defined as a constant [ExternalCommandFactory.PWD_VARIABLE])
 */
object PWDPrompt : Prompt {
    /** Returns a prompt line with working directory and a dollar at the end */
    override fun getPrompt(environment: Environment): String = "${environment[ExternalCommandFactory.PWD_VARIABLE]}$ "
}