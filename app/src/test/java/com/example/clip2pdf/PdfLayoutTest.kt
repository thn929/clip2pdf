package com.example.clip2pdf

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PdfLayoutTest {
    @Test
    fun maxLinesPerPageUsesContentHeightAndLineSpacing() {
        val spec = PageSpec(
            widthPoints = 612,
            heightPoints = 792,
            marginPoints = 54,
            textSizePoints = 12f,
            lineSpacingPoints = 4f
        )

        assertEquals(42, PdfLayout.maxLinesPerPage(spec))
    }

    @Test
    fun maxLinesPerPageAlwaysAllowsAtLeastOneLine() {
        val spec = PageSpec(
            heightPoints = 100,
            marginPoints = 49,
            textSizePoints = 12f,
            lineSpacingPoints = 4f
        )

        assertTrue(PdfLayout.maxLinesPerPage(spec) >= 1)
    }

    @Test
    fun pageCountRoundsUpPartialPages() {
        val spec = PageSpec(heightPoints = 200, marginPoints = 50)

        assertEquals(1, PdfLayout.pageCountForLayoutHeight(100, spec))
        assertEquals(2, PdfLayout.pageCountForLayoutHeight(101, spec))
        assertEquals(3, PdfLayout.pageCountForLayoutHeight(201, spec))
    }

    @Test
    fun pageCountAlwaysCreatesAtLeastOnePage() {
        val spec = PageSpec(heightPoints = 200, marginPoints = 50)

        assertEquals(1, PdfLayout.pageCountForLayoutHeight(0, spec))
    }
}
