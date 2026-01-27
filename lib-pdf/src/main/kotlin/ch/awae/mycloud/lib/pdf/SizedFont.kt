package ch.awae.mycloud.lib.pdf

import org.apache.pdfbox.pdmodel.font.PDFont

data class SizedFont(val pdFont: PDFont, val size: Float) {

    fun getStringWidth(text: String): Float {
        return (size * pdFont.getStringWidth(text) / 1000f) / POINTS_PER_MM
    }

    val height: Float
        get() = size / POINTS_PER_MM

}