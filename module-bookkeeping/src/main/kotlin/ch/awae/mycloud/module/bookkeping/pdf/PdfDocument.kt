package ch.awae.mycloud.module.bookkeping.pdf

import org.apache.pdfbox.pdmodel.*
import org.apache.pdfbox.pdmodel.common.*
import org.apache.pdfbox.pdmodel.font.*
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.*
import java.io.*

class PdfDocument(private val generator: PdfDocument.() -> Unit) {

    val document = PDDocument()

    private fun render() {
        generator(this)
    }

    fun toByteArray(): ByteArray {
        return document.use {
            render()
            ByteArrayOutputStream().also {
                document.save(it)
            }.toByteArray()
        }

    }

    private val loadedFonts = mutableMapOf<Pair<FontName, Float>, PdfFont>()

    fun loadFont(fontType: FontName, size: Float): PdfFont {
        return loadedFonts.computeIfAbsent(Pair(fontType, size)) { (f, s) ->
            PdfFont(PDType1Font(f), s)
        }
    }

    fun addPage(pageGenerator: PdfPage.() -> Unit) {
        PdfPage(document, PDPage(PDRectangle.A4).also { document.addPage(it) }, pageGenerator).render()
    }

}

data class PdfFont(val pdFont: PDType1Font, val size: Float) {

    fun getStringWidth(text: String): Float {
        return (size * pdFont.getStringWidth(text) / 1000f) / POINTS_PER_MM
    }

    val height: Float
        get() = size / POINTS_PER_MM

}

const val POINTS_PER_MM = 1 / (10 * 2.54f) * 72f

class PdfPage(val document: PDDocument, val page: PDPage, val generator: PdfPage.() -> Unit) {

    val height = page.mediaBox.height / POINTS_PER_MM
    val width = page.mediaBox.width / POINTS_PER_MM
    val stream = PDPageContentStream(document, page)

    fun writeText(x: Float, y: Float, text: String, font: PdfFont, rightJustified: Boolean = false) {
        stream.beginText()
        stream.setFont(font.pdFont, font.size)

        if (rightJustified) {
            stream.newLineAtOffset(
                (x + 30f - font.getStringWidth(text)) * POINTS_PER_MM,
                (height - y - 20f) * POINTS_PER_MM
            )
        } else {
            stream.newLineAtOffset((x + 30f) * POINTS_PER_MM, (height - y - 20f) * POINTS_PER_MM)
        }
        stream.showText(text)
        stream.endText()
    }

    fun writeText(x: Float, y: Float, text: Textblock) {
        stream.beginText()
        stream.setFont(text.font.pdFont, text.font.size)
        stream.setLeading(text.font.size * 1.2f)
        stream.newLineAtOffset((x + 30f) * POINTS_PER_MM, (height - y - 20f) * POINTS_PER_MM)
        text.lines.forEachIndexed { idx, line ->
            if (idx > 0) {
                stream.newLine()
            }
            stream.showText(line)
        }
        stream.endText()
    }

    fun render() {
        stream.use {
            generator()
        }
    }

}

class Textblock(text: String, val font: PdfFont, lineWidth: Float = 170f) {
    val lines: List<String>

    init {
        val words = text.split(' ')

        val fullLines = mutableListOf<String>()
        var currentLine: String? = null
        for (word in words)
            if (currentLine == null) {
                // first word always goes on the line
                currentLine = word
            } else {
                val newCurrentLine = "$currentLine $word"
                if (font.getStringWidth(newCurrentLine) > lineWidth) {
                    // next word did not fit onto line. break before word.
                    fullLines.add(currentLine)
                    currentLine = word
                } else {
                    // still got space. add word to line.
                    currentLine = newCurrentLine
                }
            }
        // we are done. if we have a non-empty line, we should complete that too
        if (!currentLine.isNullOrEmpty()) {
            fullLines.add(currentLine)
        }

        this.lines = fullLines
    }

}