package ch.awae.mycloud.lib.pdf

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream

class Page(document: PDDocument, page: PDPage, val generator: Page.() -> Unit) {

    val height = page.mediaBox.height / POINTS_PER_MM
    val width = page.mediaBox.width / POINTS_PER_MM
    val stream = PDPageContentStream(document, page)

    fun writeText(x: Float, y: Float, text: String, font: SizedFont, rightJustified: Boolean = false) {
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
