package ch.awae.mycloud.module.documents

import ch.awae.mycloud.common.util.GUID
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class DocumentsRestController(
    private val documentRepository: DocumentRepository,
) {

    @GetMapping("/documents/{id}")
    fun getDocument(
        @PathVariable id: String,
    ): ResponseEntity<Any> {
        val document = documentRepository.findValid(GUID.decodeShortString(id))
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok()
            .contentType(document.type)
            .contentLength(document.content.size.toLong())
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=${document.filename}")
            .body(document.content)
    }

}
