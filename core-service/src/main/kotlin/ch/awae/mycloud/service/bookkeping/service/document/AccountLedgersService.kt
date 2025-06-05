package ch.awae.mycloud.service.bookkeping.service.document

import ch.awae.mycloud.common.pdf.PdfDocument
import ch.awae.mycloud.service.bookkeping.dto.*
import ch.awae.mycloud.service.bookkeping.model.*
import ch.awae.mycloud.service.bookkeping.service.*
import jakarta.transaction.*
import org.springframework.stereotype.*

@Service
@Transactional
class AccountLedgersService(
    private val bookService: BookService,
    private val accountTransactionRepository: AccountTransactionRepository,
) {

    fun generateAccountLegderBundle(bookId: Long): ByteArray {
        val book = bookService.getBook(bookId)

        val accounts = book.accountGroups
            .sortedBy { it.groupNumber }
            .flatMap { it.accounts.sortedBy { a -> a.accountNumber } }

        return PdfDocument {

            for (account in accounts) {
                generateAccountLedger(this, book, account)
            }

        }.toByteArray()

    }

    fun generateAccountLedger(pdf: PdfDocument, book: Book, account: BookAccount) {

        val transactions: List<AccountLedgerRenderer.TransactionRecord> =
            accountTransactionRepository.findByAccount(account)
                .map {
                    AccountLedgerRenderer.TransactionRecord(
                        it.bookingDate,
                        it.recordId,
                        it.bookingText,
                        it.amount,
                        it.description != null,
                    )
                }

        AccountLedgerRenderer.generateLedger(
            pdf,
            book.title,
            "${AccountId.of(account)} [${account.accountType.shortString}] ${account.title}",
            transactions,
            account.accountType.invertedPresentation
        )
    }
}