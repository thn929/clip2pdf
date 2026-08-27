package com.example.clip2pdf

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import java.io.OutputStream

class PdfWriter(
    private val pageSpec: PageSpec = PageSpec()
) {
    fun write(text: String, outputStream: OutputStream) {
        val document = PdfDocument()
        try {
            val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
                textSize = pageSpec.textSizePoints
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            }

            val layout = createLayout(text, paint)
            val pageHeight = pageSpec.contentHeightPoints
            val pageCount = PdfLayout.pageCountForLayoutHeight(layout.height, pageSpec)
            var pageTop = 0
            var pageNumber = 1

            while (pageNumber <= pageCount) {
                val pageInfo = PdfDocument.PageInfo.Builder(
                    pageSpec.widthPoints,
                    pageSpec.heightPoints,
                    pageNumber
                ).create()
                val page = document.startPage(pageInfo)
                drawPage(page.canvas, layout, pageTop)
                document.finishPage(page)

                pageTop += pageHeight
                pageNumber += 1
            }

            document.writeTo(outputStream)
        } finally {
            document.close()
        }
    }

    private fun drawPage(canvas: Canvas, layout: StaticLayout, pageTop: Int) {
        canvas.drawColor(Color.WHITE)
        canvas.save()
        canvas.translate(pageSpec.marginPoints.toFloat(), pageSpec.marginPoints.toFloat() - pageTop)
        canvas.clipRect(
            0,
            pageTop,
            pageSpec.contentWidthPoints,
            pageTop + pageSpec.contentHeightPoints
        )
        layout.draw(canvas)
        canvas.restore()
    }

    private fun createLayout(text: String, paint: TextPaint): StaticLayout {
        val normalizedText = text.replace("\r\n", "\n").replace('\r', '\n')
        val spacingAdd = pageSpec.lineSpacingPoints

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(
                normalizedText,
                0,
                normalizedText.length,
                paint,
                pageSpec.contentWidthPoints
            )
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(spacingAdd, 1.0f)
                .setIncludePad(false)
                .setBreakStrategy(Layout.BREAK_STRATEGY_HIGH_QUALITY)
                .setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NORMAL)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(
                normalizedText,
                paint,
                pageSpec.contentWidthPoints,
                Layout.Alignment.ALIGN_NORMAL,
                1.0f,
                spacingAdd,
                false
            )
        }
    }
}
