package ch.awae.mycloud.module.bookkeping.service.document

import ch.awae.mycloud.api.auth.AuthInfo
import ch.awae.mycloud.api.documents.DocumentIdentifier
import ch.awae.mycloud.api.documents.DocumentSource
import ch.awae.mycloud.api.documents.DocumentStore
import ch.awae.mycloud.lib.pdf.Document
import ch.awae.mycloud.module.bookkeping.dto.AccountId
import ch.awae.mycloud.module.bookkeping.model.Account
import ch.awae.mycloud.module.bookkeping.model.AccountTransactionRepository
import ch.awae.mycloud.module.bookkeping.model.Book
import ch.awae.mycloud.module.bookkeping.service.BookService
import jakarta.transaction.Transactional
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.time.Duration

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

        val content = Document {

            for (account in accounts) {
                generateAccountLedger(this, book, account)
            }

        }.toByteArray()

        return documentStore.createDocument(
            source = DocumentSource.BOOKKEEPING,
            filename = "ledgers.pdf",
            type = MediaType.APPLICATION_PDF,
            content = content,
            lifetime = Duration.ofHours(1),
            username = AuthInfo.username,
        )
    }

    fun generateAccountLedger(pdf: Document, book: Book, account: Account) {

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