package ch.awae.mycloud.lib.pdf

import java.io.InputStream

sealed interface FontSource {
    fun getFontFile(): InputStream
}