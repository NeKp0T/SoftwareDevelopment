package ru.spb.hse.nesh.parsing.parsing

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.spb.hse.nesh.parsing.WordResolution
import ru.spb.hse.nesh.parsing.WordResolution.Companion.concatWordResolutions

class WordResolutionConcatTest {

    private fun assertConcatResut(results: List<String>, vararg resolutions: WordResolution) {
        assertEquals(results, concatWordResolutions(resolutions.asList()))
    }

    @Test
    fun `one word with brakeAfter`() {
        assertConcatResut(listOf("x"), "x" breaks true)
    }

    @Test
    fun `one word without brakeAfter`() {
        assertConcatResut(listOf("x"), "x" breaks false)
    }

    @Test
    fun `several words`() {
        val words = listOf("x", "y", "z")
        assertConcatResut(words, words breaks true)
    }

    @Test
    fun `with breakAfter`() {
        val words1 = listOf("x", "y")
        val words2 = listOf("z", "!")
        assertConcatResut(words1 + words2, words1 breaks true, words2 breaks true)
    }

    @Test
    fun `without breakAfter`() {
        val words1 = listOf("x", "y")
        val words2 = listOf("z", "!")
        assertConcatResut(listOf("x", "yz", "!"), words1 breaks false, words2 breaks true)
    }

    @Test
    fun `empty WordResolution with breakAfter`() {
        val words1 = listOf("x", "y")
        val words2 = listOf("z", "!")
        assertConcatResut(words1 + words2, words1 breaks false, emptyList<String>() breaks true, words2 breaks true)
    }

    @Test
    fun `concats several words`() {
        assertConcatResut(listOf("xyz"), "x" breaks false, "y" breaks false, "z" breaks true)
    }

    private infix fun String.breaks(breakAfter: Boolean) = WordResolution(listOf(this), breakAfter)
    private infix fun List<String>.breaks(breakAfter: Boolean) = WordResolution(this, breakAfter)
}