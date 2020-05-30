package ru.spb.hse.nesh.interpreter.implementations

import ru.spb.hse.nesh.interpreter.interfaces.Environment

private const val PWD_NAME = "PWD"
private const val HOME_NAME = "HOME"

/**
 * An [Environment] that simply uses a [MutableMap] to store variables.
 *
 * Deletes empty variables.
 */
class LocalEnvironment : Environment {
    private val values = mutableMapOf<String, String>()

    override fun get(variable: String): String = values[variable] ?: ""
    /** Sets [variable] to [value], or deletes it if [value] is empty string] */
    override fun set(variable: String, value: String) {
        if (value == "")
            values.remove(variable)
        else
            values[variable] = value
    }

    override fun getRedefinedVariables(): Map<String, String> = values

    override fun getPwd() = get(PWD_NAME)

    override fun setPwd(new: String) {
        set(PWD_NAME, new)
    }

    override fun getHome() = get(HOME_NAME)
}