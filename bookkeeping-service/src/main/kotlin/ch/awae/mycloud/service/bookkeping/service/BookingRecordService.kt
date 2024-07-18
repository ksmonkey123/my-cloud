package ch.awae.mycloud.service.bookkeping.service

import ch.awae.mycloud.audit.*
import ch.awae.mycloud.service.bookkeping.dto.*
import ch.awae.mycloud.service.bookkeping.model.*
import org.springframework.data.domain.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*
import java.math.*

@Transactional
@Service
class BookingRecordService(
    val bookService: BookService,
    val bookingRecordRepository: BookingRecordRepository,
    val accountTransactionRepository: AccountTransactionRepository,
) {

    @AuditLog
    fun createRecord(bookId: Long, request: CreateBookingRecordRequest): BookingRecordDto {
        val book = bookService.getBook(bookId)

        val credits = request.credits.associate {
            val account = bookService.getAccount(bookId, AccountId.of(it.accountId))
            Pair(account, it.amount)
        }
        val debits = request.debits.associate {
            val account = bookService.getAccount(bookId, AccountId.of(it.accountId))
            Pair(account, -it.amount)
        }

        val record = BookingRecord(
            book,
            request.text,
            request.description,
            request.bookingDate,
            request.tag
        ).apply {
            movements.putAll(credits)
            movements.putAll(debits)
        }

        return BookingRecordDto.of(
            bookingRecordRepository.save(record)
        )
    }

    fun listRecordsOfBook(bookId: Long, page: Int, pageSize: Int): PageDto<BookingRecordDto> {
        val book = bookService.getBook(bookId)
        val records = bookingRecordRepository.listAllInBook(book, Pageable.ofSize(pageSize).withPage(page))
        records.hasNext()
        return PageDto(
            records.content.map { BookingRecordDto.of(it) },
            records.totalElements,
        )
    }

    fun getLedgerPageForAccount(
        bookId: Long,
        accountId: AccountId,
        page: Int,
        pageSize: Int
    ): AccountLedgerDto {
        val account = bookService.getAccount(bookId, accountId)

        val accountBalance = accountTransactionRepository.getBalanceOfAccount(account)
        val transactionPage = accountTransactionRepository.findByAccount(
            account,
            Pageable.ofSize(pageSize).withPage(page)
        )

        val transactions = transactionPage.content.map {
            LedgerTransactionDto(
                id = it.recordId,
                bookingDate = it.bookingDate,
                text = it.bookingText,
                description = it.description,
                tag = it.tag,
                amount = it.amount,
            )
        }

        return AccountLedgerDto(
            AccountSummaryDto(account),
            PageDto(transactions, transactionPage.totalElements),
            accountBalance ?: BigDecimal.ZERO,
        )
    }

}