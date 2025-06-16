package ch.awae.mycloud.module.email

import com.vladsch.flexmark.html.*
import com.vladsch.flexmark.parser.*

object MarkdownParser {

    private val parser = Parser.builder().build()
    private val renderer = HtmlRenderer.builder().build()

    fun toHtml(markdown: String): String {
        return renderer.render(parser.parse(markdown))
    }
    
}

