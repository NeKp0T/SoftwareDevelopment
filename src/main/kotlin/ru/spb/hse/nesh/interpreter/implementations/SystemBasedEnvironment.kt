package ru.spb.hse.nesh.interpreter.implementations

import ru.spb.hse.nesh.interpreter.interfaces.Environment

private const val PWD_NAME = "PWD"
private const val HOME_NAME_NIX = "HOME"
private const val HOME_NAME_WIN = "HOMEPATH"
/**
 * Environment that searches system environment if it does not hold required variable.
 *
 * It does not actually changes system environment of a process.
 */
class SystemBasedEnvironment : Environment {
    private val localEnv = mutableMapOf<String, String>()

    /** Gets [variable]'s value from this environment or the system one */
    override fun get(variable: String): String = localEnv.getOrElse(variable) { System.getenv(variable) ?: "" }

    /** Sets [variable]'s value to [value]. Does not change system environment. */
    override fun set(variable: String, value: String) {
        localEnv[variable] = value
    }

    /** Returns all variables that has been [set] for this environment */
    override fun getRedefinedVariables(): Map<String, String> = localEnv


    override fun getPwd() = get(PWD_NAME)

    override fun setPwd(new: String) {
        set(PWD_NAME, new)
    }

    override fun getHome(): String {
        val osName = System.getProperty("os.name").toLowerCase()
        return if(osName.contains("win")) {
            get(HOME_NAME_WIN)
        } else {
            get(HOME_NAME_NIX)
        }
    }
}