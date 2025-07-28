package ch.awae.mycloud.module.bookkeping.service.document

import ch.awae.mycloud.api.documents.*
import ch.awae.mycloud.module.bookkeping.dto.*
import ch.awae.mycloud.module.bookkeping.model.*
import ch.awae.mycloud.module.bookkeping.pdf.*
import ch.awae.mycloud.module.bookkeping.service.*
import jakarta.transaction.*
import org.springframework.http.*
import org.springframework.stereotype.*
import java.time.*

@Service
@Transactional
class AccountLedgersService(
    private val bookService: BookService,
    private val accountTransactionRepository: AccountTransactionRepository,
    private val documentStore: DocumentStore,
) {

    fun generateAccountLegderBundle(bookId: Long): DocumentIdentifier {
        val book = bookService.getBook(bookId)

        val accounts = book.accountGroups
            .sortedBy { it.groupNumber }
            .flatMap { it.accounts.sortedBy { a -> a.accountNumber } }

        val content = PdfDocument {

            for (account in accounts) {
                generateAccountLedger(this, book, account)
            }

        }.toByteArray()

        return documentStore.createDocument("ledgers.pdf", MediaType.APPLICATION_PDF, content, Duration.ofHours(1))
    }

    fun generateAccountLedger(pdf: PdfDocument, book: Book, account: Account) {

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