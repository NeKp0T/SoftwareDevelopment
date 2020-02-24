package ru.spb.hse.nesh.interpreter.interfaces

/** Holds all information abut interpreter context. For now it means environmental variables */
interface Environment {
    /** Gets variable named [variable] from environment, or returns an empty string */
    operator fun get(variable: String): String
    /** Sets [variable] to [value] */
    operator fun set(variable: String, value: String)
    fun getRedefinedVariables(): Map<String, String>

    fun pwdVariable(): String = "PWD"
}