package ch.awae.mycloud.pdf

import java.io.InputStream

sealed interface FontSource {
    fun getFontFile(): InputStream
}