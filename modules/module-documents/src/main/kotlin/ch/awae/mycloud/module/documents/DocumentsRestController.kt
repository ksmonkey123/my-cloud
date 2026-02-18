package ch.awae.mycloud.module.documents

import org.springframework.http.*
import org.springframework.web.bind.annotation.*

@RestController
class DocumentsRestController(
    private val documentRepository: DocumentRepository,
) {

    @GetMapping("/documents/{token}")
    fun getDocument(
        @PathVariable token: String,
    ): ResponseEntity<Any> {
        val document = documentRepository.findValidByToken(token)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok()
            .contentType(MediaType.valueOf(document.type))
            .contentLength(document.content.size.toLong())
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=${document.filename}")
            .body(document.content)
    }

}
