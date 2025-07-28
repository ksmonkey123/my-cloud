package ch.awae.mycloud.module.bookkeping.service.document

import ch.awae.mycloud.api.documents.*
import ch.awae.mycloud.common.*
import ch.awae.mycloud.module.bookkeping.dto.*
import ch.awae.mycloud.module.bookkeping.model.*
import ch.awae.mycloud.module.bookkeping.pdf.*
import ch.awae.mycloud.module.bookkeping.service.*
import jakarta.transaction.*
import org.springframework.http.*
import org.springframework.stereotype.*
import java.math.*
import java.time.*

@Service
@Transactional
class EarningsReportService(
    private val bookService: BookService,
    private val accountTagBalanceRepository: AccountTagBalanceRepository,
    private val bookingRecordRepository: BookingRecordRepository,
    private val documentStore: DocumentStore,
) {

    fun generateReportBundle(bookId: Long): DocumentIdentifier {
        val book = bookService.getBook(bookId)

        val earningGroups = book.accountGroups
            .filter { ag -> ag.accounts.any { a -> a.accountType.earningsAccount } }
            .sortedBy { it.groupNumber }

        val content = PdfDocument {
            generateInitialBalance(this, book)
            generateReport(this, book, "Schlussbilanz", book.accountGroups)
            if (earningGroups.isNotEmpty()) {
                generateReport(this, book, "Erfolgsrechnung", earningGroups, earnings = true)
                for (group in earningGroups) {
                    generateDetailedEarningsReport(this, book, "Erfolgrechnung " + group.title, group)
                }
            }
        }.toByteArray()

        return documentStore.createDocument("report.pdf", MediaType.APPLICATION_PDF, content, Duration.ofHours(1))
    }

    fun generatePartialEarningsReport(bookId: Long, groupNumbers: List<Int>, title: String?): DocumentIdentifier {
        val book = bookService.getBook(bookId)

        val earningGroups = book.accountGroups
            .filter { ag -> ag.groupNumber in groupNumbers }
            .filter { ag -> ag.accounts.any { a -> a.accountType.earningsAccount } }
            .sortedBy { it.groupNumber }

        if (earningGroups.isEmpty()) {
            throw InvalidRequestException("no earnings account groups selected")
        }

        val content = PdfDocument {
            generateReport(this, book, "Erfolgsrechnung " + (title ?: "(partiell)"), earningGroups, earnings = true)
            for (group in earningGroups) {
                generateDetailedEarningsReport(this, book, "Erfolgrechnung " + group.title, group)
            }
        }.toByteArray()

        return documentStore.createDocument("partial_report.pdf", MediaType.APPLICATION_PDF, content, Duration.ofHours(1))
    }

    fun generateInitialBalance(pdf: PdfDocument, book: Book) {
        val openingRecord = bookingRecordRepository.findFirstInBook(book) ?: return

        val accountGroups = openingRecord.movements.toList().groupBy { (account, _) -> account.accountGroup }.toList()

        val groups = accountGroups
            .sortedBy { (group, _) -> group.groupNumber }
            .map { (group, accounts) ->
                ReportRenderer.Group(
                    label = group.title,
                    items = accounts
                        .sortedBy { (account, _) -> account.accountNumber }
                        .map { (account, balance) ->
                            ReportRenderer.Item(
                                label = AccountId.of(account).toString() + " " + account.title,
                                amount = balance.let { if (account.accountType.invertedPresentation) it.negate() else it },
                                type = ReportRenderer.ItemType.getType(account.accountType.invertedPresentation),
                                tag = false,
                            )
                        },
                    valueOverride = null,
                )
            }

        ReportRenderer.generateReport(
            pdf,
            mode = ReportRenderer.Mode.BALANCE,
            title = book.title,
            subtitle = "Eröffnungsbilanz",
            groups = groups,
            hideZeroProfitLine = true,
        )
    }

    fun generateReport(
        pdf: PdfDocument,
        book: Book,
        title: String,
        accountGroups: Collection<AccountGroup>,
        earnings: Boolean = false,
    ) {
        val groups = accountGroups.sortedBy { it.groupNumber }
            // convert to Group DTO structure
            .map { group ->
                ReportRenderer.Group(
                    label = group.title,
                    items = group.accounts
                        .filter { it.accountType.earningsAccount == earnings }
                        .sortedBy { it.accountNumber }
                        .map { account ->
                            ReportRenderer.Item(
                                label = AccountId.of(account).toString() + " " + account.title,
                                amount = account.balance?.balance
                                    ?.let { if (account.accountType.invertedPresentation) it.negate() else it }
                                    ?: BigDecimal.ZERO,
                                type = ReportRenderer.ItemType.getType(account.accountType.invertedPresentation),
                                tag = false,
                            )
                        },
                    valueOverride = null,
                )
            }
        ReportRenderer.generateReport(
            pdf,
            mode = if (earnings) ReportRenderer.Mode.EARNINGS else ReportRenderer.Mode.BALANCE,
            title = book.title,
            subtitle = title,
            groups = groups,
        )
    }

    fun generateDetailedEarningsReport(
        pdf: PdfDocument,
        book: Book,
        title: String,
        accountGroup: AccountGroup,
    ) {
        val accounts = accountGroup.accounts.filter { it.accountType.earningsAccount }.sortedBy { it.accountNumber }

        val groups = accounts.map { account ->
            val tags = accountTagBalanceRepository.findByAccountId(account.id)
                .takeUnless { it.size == 1 && it[0].tag.isEmpty() }
                ?.sortedWith(
                    Comparator.comparing<AccountTagBalance, BigDecimal> { it.balance }
                        .let { if (account.accountType.invertedPresentation) it else it.reversed() }
                        .thenComparing<String> { it.tag.takeUnless(String::isEmpty) ?: "}" }
                )
                ?.map { tag ->
                    ReportRenderer.Item(
                        label = tag.tag.takeUnless(String::isEmpty),
                        amount = tag.balance.let { if (account.accountType.invertedPresentation) it.negate() else it },
                        type = ReportRenderer.ItemType.getType(account.accountType.invertedPresentation),
                        tag = true,
                    )
                }
                ?.filter { it.amount.compareTo(BigDecimal.ZERO) != 0 }
                ?.takeUnless { it.size == 1 && it[0].label == null }

            ReportRenderer.Group(
                label = AccountId.of(account).toString() + " " + account.title,
                valueOverride = ReportRenderer.Group.ValueOverride(
                    credit = (account.balance?.balance
                        ?: BigDecimal.ZERO)?.takeIf { !account.accountType.invertedPresentation },
                    debit = (account.balance?.balance?.negate()
                        ?: BigDecimal.ZERO)?.takeIf { account.accountType.invertedPresentation },
                ),
                items = tags ?: emptyList(),
            )
        }

        ReportRenderer.generateReport(
            pdf,
            mode = ReportRenderer.Mode.EARNINGS,
            title = book.title,
            subtitle = title,
            groups = groups,
        )
    }


}