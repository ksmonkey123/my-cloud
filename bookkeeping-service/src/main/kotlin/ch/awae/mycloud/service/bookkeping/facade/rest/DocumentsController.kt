package ch.awae.mycloud.service.bookkeping.facade.rest

import ch.awae.mycloud.service.bookkeping.service.document.*
import org.springframework.core.io.*
import org.springframework.http.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books/{bookId}/documents")
class DocumentsController(
    private val earningsReportService: EarningsReportService,
) {

    @GetMapping("/earnings.pdf")
    fun getEarningsDocument(
        @PathVariable("bookId") bookId: Long,
        @RequestParam("groupNumber", required = false) groups: List<Int>?
    ): ResponseEntity<Resource> {
        val pdf = earningsReportService.generateEarningsReport(bookId, groups ?: emptyList())

        return ResponseEntity.ok()
            .headers {
                it.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=journal.pdf")
            }
            .contentLength(pdf.size.toLong())
            .contentType(MediaType.APPLICATION_PDF)
            .body(ByteArrayResource(pdf))
    }

}