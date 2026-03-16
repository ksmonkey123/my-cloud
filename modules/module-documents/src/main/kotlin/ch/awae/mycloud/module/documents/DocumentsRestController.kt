package ch.awae.mycloud.module.documents

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class DocumentsRestController(
    private val documentRepository: DocumentDataStore,
) {

    @GetMapping("/documents/{link}")
    fun getDocument(
        @PathVariable link: String,
    ): ResponseEntity<Any> {
        val document = documentRepository.retrieveByLink(link)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok()
            .contentType(document.type)
            .contentLength(document.content.size.toLong())
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=${document.filename}")
            .body(document.content)
    }

}
