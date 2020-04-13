package ru.spb.hse.nesh.parsing

import ru.spb.hse.nesh.interpreter.interfaces.Environment

/**
 * A type of word that can resolve strings into actual space-separated words.
 *
 * Word in this context means something like "class of text escapement", not words as in separated by spaces.
 * Resolution might contain many (or none) space-separated words.
 */
interface WordType {
    /**
     * Resolves [s] into a list of words described by [WordResolution] using variables from [env].
     */
    fun resolve(s: String, env: Environment): WordResolution
}

/**
 * Describes a separation of single part of a line into words.
 *
 * @property words actual space-separated words
 * @property breakAfter whether next encountered word in word resolutions might not be attached to the last one
 * @constructor creates [WordResolution] of [words] with flag [breakAfter]
 */
data class WordResolution(val words: List<String>, val breakAfter: Boolean) {
    companion object {
        /**
         * Concatenates a list of [WordResolution]s into a single list of space-separated words.
         *
         * All words from [resolutions] are arranged in a single list and then
         * two adjacent words are concatenated iff
         * * they come from different [WordResolution]s
         * * no [WordResolution] form first word's until second's has [breakAfter] set to `true`
         */
        fun concatWordResolutions(resolutions: List<WordResolution>): List<String> {
            val builder = object {
                private val result: MutableList<String> = mutableListOf()
                private var flushed = true
                private val previousBuilder = StringBuilder()

                fun push(s: String) {
                    previousBuilder.append(s)
                    flushed = false
                }

                fun flush() {
                    if (!flushed) {
                        result.add(previousBuilder.toString())
                        previousBuilder.clear()
                        flushed = true
                    }
                }

                fun getResult(): List<String> = result
            }

            for (res in resolutions) {
                res.words.forEachIndexed { index, word ->
                    builder.push(word)
                    if (index != res.words.size - 1) {
                        builder.flush()
                    }
                }
                if (res.breakAfter) {
                    builder.flush()
                }
            }

            builder.flush()

            return builder.getResult()
        }
    }
}

