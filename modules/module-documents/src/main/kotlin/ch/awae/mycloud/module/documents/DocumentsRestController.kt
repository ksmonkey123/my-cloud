package ch.awae.mycloud.module.documents

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class DocumentsRestController(
    private val documentRepository: DocumentRepository,
) {

    @GetMapping("/documents/{id}")
    fun getDocument(
        @PathVariable id: String,
    ): ResponseEntity<Any> {
        val document = documentRepository.findValidById(UUID.fromString(id))
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok()
            .contentType(MediaType.valueOf(document.type))
            .contentLength(document.content.size.toLong())
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=${document.filename}")
            .body(document.content)
    }

}
