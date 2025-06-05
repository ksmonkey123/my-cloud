package ch.awae.mycloud.service.bookkeping.facade.rest

import ch.awae.mycloud.service.bookkeping.service.document.*
import org.springframework.core.io.*
import org.springframework.http.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/bookkeeping/books/{bookId}/documents")
class DocumentsController(
    private val earningsReportService: EarningsReportService,
    private val accountLedgersService: AccountLedgersService,
) {

    @GetMapping("/earnings.pdf")
    fun getEarningsDocument(
        @PathVariable("bookId") bookId: Long,
        @RequestParam("groupNumber", required = false) groups: List<Int>?,
        @RequestParam("title", required = false) title: String?,
    ): ResponseEntity<Resource> {
        val pdf =
            if (groups.isNullOrEmpty()) {
                earningsReportService.generateReportBundle(bookId)
            } else {
                earningsReportService.generatePartialEarningsReport(bookId, groups, title?.takeIf { it.isNotBlank() })
            }


        return ResponseEntity.ok()
            .headers {
                it.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=earnings.pdf")
            }
            .contentLength(pdf.size.toLong())
            .contentType(MediaType.APPLICATION_PDF)
            .body(ByteArrayResource(pdf))
    }


    @GetMapping("/ledgers.pdf")
    fun getEarningsDocument(
        @PathVariable("bookId") bookId: Long,
    ): ResponseEntity<Resource> {
        val pdf = accountLedgersService.generateAccountLegderBundle(bookId)

        return ResponseEntity.ok()
            .headers {
                it.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ledgers.pdf")
            }
            .contentLength(pdf.size.toLong())
            .contentType(MediaType.APPLICATION_PDF)
            .body(ByteArrayResource(pdf))
    }



}