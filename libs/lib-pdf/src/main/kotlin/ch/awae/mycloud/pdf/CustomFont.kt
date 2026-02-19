package ch.awae.mycloud.pdf

import java.io.InputStream

data class CustomFont(val resourcePath: String) : FontSource {
    override fun getFontFile(): InputStream {
        return javaClass.getResourceAsStream(resourcePath) ?: error("Resource not found: $resourcePath")
    }
}