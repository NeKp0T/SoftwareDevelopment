package ru.spb.hse.nesh.language

import ru.spb.hse.nesh.interpreter.interfaces.Environment

class EnvironmentMapDummy(private val values: Map<String, String>):
    Environment {
    override fun get(variable: String): String = values[variable] ?: ""
    override fun set(variable: String, value: String) {}
    override fun getRedefinedVariables(): Map<String, String> = values
    override fun getPwd(): String {
        return "pwd"
    }

    override fun setPwd(new: String) {
    }

    override fun getHome(): String {
        return "home"
    }


}

val dummyEnv = EnvironmentMapDummy(mapOf("x" to "x", "y" to "y", "x_x" to "x x", "x__x" to "x  x", "x_" to "x "))