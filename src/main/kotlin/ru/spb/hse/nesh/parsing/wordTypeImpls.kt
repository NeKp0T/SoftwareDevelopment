package ru.spb.hse.nesh.parsing

import ru.spb.hse.nesh.interpreter.interfaces.Environment

/** A [WordType] for words consisting entirely of whitespace symbols */
object WhitespaceWord: WordType {
    override fun resolve(s: String, env: Environment): WordResolution =
        WordResolution(emptyList(), true)
}

/** A [WordType] for regular words without whitespaces or quotes */
object RegularWord: VariableResolvingWord {
    override fun modifyResolved(resolved: String): WordResolution = WordResolution(
        resolved.trim().run {
            if (isEmpty())
                emptyList()
            else
                split(Regex("[\\s]+"))
        },
        resolved.lastOrNull()?.isWhitespace() ?: false
        )
}

/** A [WordType] for substrings enclosed in single quotes */
object SingleQuotedWord: WordType {
    override fun resolve(s: String, env: Environment): WordResolution =
        WordResolution(listOf(s), false)
}

/** A [WordType] for substrings enclosed in double quotes */
object DoubleQuotedWord: VariableResolvingWord {
    override fun modifyResolved(resolved: String): WordResolution =
        WordResolution(listOf(resolved), false)
}
