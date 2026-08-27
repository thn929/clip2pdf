package com.example.clip2pdf

data class PageSpec(
    val widthPoints: Int = 612,
    val heightPoints: Int = 792,
    val marginPoints: Int = 54,
    val textSizePoints: Float = 12f,
    val lineSpacingPoints: Float = 4f
) {
    val contentWidthPoints: Int
        get() = widthPoints - marginPoints * 2

    val contentHeightPoints: Int
        get() = heightPoints - marginPoints * 2
}

object PdfLayout {
    fun maxLinesPerPage(spec: PageSpec): Int {
        val lineHeight = spec.textSizePoints + spec.lineSpacingPoints
        return (spec.contentHeightPoints / lineHeight).toInt().coerceAtLeast(1)
    }

    fun pageCountForLayoutHeight(layoutHeightPoints: Int, spec: PageSpec): Int {
        val contentHeight = spec.contentHeightPoints.coerceAtLeast(1)
        return ((layoutHeightPoints.coerceAtLeast(1) + contentHeight - 1) / contentHeight)
            .coerceAtLeast(1)
    }
}
