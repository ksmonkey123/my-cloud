package ch.awae.mycloud.pdf

import java.io.InputStream

enum class StandardFont(val resourcePath: String) : FontSource {
    LIBERATION_SERIF_REGULAR("/lib-pdf/fonts/LiberationSerif-Regular.ttf"),
    LIBERATION_SERIF_BOLD("/lib-pdf/fonts/LiberationSerif-Bold.ttf"),
    LIBERATION_SERIF_ITALIC("/lib-pdf/fonts/LiberationSerif-Italic.ttf"),
    LIBERATION_SERIF_BOLD_ITALIC("/lib-pdf/fonts/LiberationSerif-BoldItalic.ttf"),
    LIBERATION_MONO_REGULAR("/lib-pdf/fonts/LiberationMono-Regular.ttf"),
    LIBERATION_MONO_BOLD("/lib-pdf/fonts/LiberationMono-Bold.ttf"),
    LIBERATION_MONO_ITALIC("/lib-pdf/fonts/LiberationMono-Italic.ttf"),
    LIBERATION_MONO_BOLD_ITALIC("/lib-pdf/fonts/LiberationMono-BoldItalic.ttf"),
    ;

    override fun getFontFile(): InputStream {
        return javaClass.getResourceAsStream(resourcePath) ?: error("Resource not found: $resourcePath")
    }

}
