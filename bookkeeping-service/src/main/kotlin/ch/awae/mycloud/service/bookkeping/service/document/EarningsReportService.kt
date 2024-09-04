package ch.awae.mycloud.service.bookkeping.service.document

import ch.awae.mycloud.*
import ch.awae.mycloud.pdf.*
import ch.awae.mycloud.service.bookkeping.dto.*
import ch.awae.mycloud.service.bookkeping.model.*
import ch.awae.mycloud.service.bookkeping.service.*
import jakarta.transaction.*
import org.springframework.stereotype.*
import java.math.*

@Service
@Transactional
class EarningsReportService(
    private val bookService: BookService,
    private val accountTagBalanceRepository: AccountTagBalanceRepository,
) {

    fun generateEarningsReport(bookId: Long, groupNumbers: List<Int>): ByteArray {
        val book = bookService.getBook(bookId)

        val groups = book.accountGroups
            .filter { groupNumbers.isEmpty() || it.groupNumber in groupNumbers }
            .filter { it.accounts.any { it.accountType.earningsAccount } }

        return PdfDocument {
            generateReport(this, book, "Bilanz", book.accountGroups, hideEmptyAccounts = true)
            generateReport(this, book, "Detaillierte Bilanz", book.accountGroups)
            if (groups.isNotEmpty()) {
                generateReport(this, book, "Erfolgsrechnung", groups, earnings = true)
                for (group in groups) {
                    generateDetailedEarningsReport(this, book, "Erfolgrechnung " + group.title, group)
                }
            }
        }.toByteArray()
    }

    fun generateReport(
        pdf: PdfDocument,
        book: Book,
        title: String,
        accountGroups: Collection<AccountGroup>,
        earnings: Boolean = false,
        hideEmptyAccounts: Boolean = false,
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
            hideZeroItems = hideEmptyAccounts,
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
                ?.sortedBy { it.tag.takeUnless(String::isEmpty) ?: "}" }
                ?.map { tag ->
                    ReportRenderer.Item(
                        label = tag.tag.takeUnless(String::isEmpty) ?: "(null)",
                        amount = tag.balance.let { if (account.accountType.invertedPresentation) it.negate() else it },
                        type = ReportRenderer.ItemType.getType(account.accountType.invertedPresentation),
                        tag = true,
                    )
                }

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