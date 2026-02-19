package ch.awae.mycloud.pdf

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType0Font
import java.io.ByteArrayOutputStream

class Document(private val generator: Document.() -> Unit) {

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

    private val embeddedFontCache = mutableMapOf<FontSource, PDFont>()

    fun loadFont(font: FontSource, size: Float): SizedFont {
        val font = embeddedFontCache.computeIfAbsent(font) {
            PDType0Font.load(document, font.getFontFile(), true)
        }
        return SizedFont(font, size)
    }

    fun addPage(pageGenerator: Page.() -> Unit) {
        Page(document, PDPage(PDRectangle.A4).also { document.addPage(it) }, pageGenerator).render()
    }

}