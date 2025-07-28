package ch.awae.mycloud.module.bookkeping.facade.rest

import ch.awae.mycloud.api.documents.*
import ch.awae.mycloud.module.bookkeping.service.document.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/bookkeeping/books/{bookId}/documents")
class DocumentsController(
    private val earningsReportService: EarningsReportService,
    private val accountLedgersService: AccountLedgersService,
) {

    @PostMapping("/report")
    fun getEarningsDocument(
        @PathVariable("bookId") bookId: Long,
        @RequestParam("groupNumber", required = false) groups: List<Int>?,
        @RequestParam("title", required = false) title: String?,
    ): DocumentIdentifier {
        return if (groups.isNullOrEmpty()) {
            earningsReportService.generateReportBundle(bookId)
        } else {
            earningsReportService.generatePartialEarningsReport(bookId, groups, title?.takeIf { it.isNotBlank() })
        }
    }

    @PostMapping("/ledgers")
    fun createLedgerDocument(
        @PathVariable("bookId") bookId: Long,
    ): DocumentIdentifier {
        return accountLedgersService.generateAccountLegderBundle(bookId)
    }
}