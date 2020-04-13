package ru.spb.hse.nesh.language

import ru.spb.hse.nesh.interpreter.interfaces.Environment

class EnvironmentMapDummy(private val values: Map<String, String>):
    Environment {
    override fun get(variable: String): String = values[variable] ?: ""
    override fun set(variable: String, value: String) {}
    override fun getRedefinedVariables(): Map<String, String> = values
}

val dummyEnv = EnvironmentMapDummy(mapOf("x" to "x", "y" to "y", "x_x" to "x x", "x__x" to "x  x", "x_" to "x ", "?" to "0"))