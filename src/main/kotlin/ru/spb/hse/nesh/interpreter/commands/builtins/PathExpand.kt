package ru.spb.hse.nesh.interpreter.commands.builtins

import ru.spb.hse.nesh.interpreter.interfaces.Environment
import java.nio.file.Path
import java.nio.file.Paths

class PathExpand(private val environment: Environment) {
    fun expand(pathToExpand: String): String {
        val pwd = Paths.get(environment.getPwd())
        return pwd.resolve(Paths.get(pathToExpand)).normalize().toString()
    }
}