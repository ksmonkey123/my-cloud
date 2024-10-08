package ch.awae.mycloud.service.bookkeping.service

import ch.awae.mycloud.*
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

        // validate that no account is locked or in a locked group
        (credits.keys union credits.keys).forEach {
            if (it.locked || it.accountGroup.locked) {
                throw InvalidRequestException("account or account group locked: ${AccountId.of(it)}")
            }
        }

        val record = BookingRecord(
            book, request.text, request.description, request.bookingDate, request.tag
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

    fun getRecord(bookId: Long, bookingId: Long): BookingRecord {
        val book = bookService.getBook(bookId)
        return bookingRecordRepository.findByIdAndBook(bookingId, book)
            ?: throw ResourceNotFoundException("/books/$bookId/records/$bookingId")
    }

    fun editRecord(bookId: Long, bookingId: Long, request: BookingRecordEditRequest): BookingRecordDto {
        val record = getRecord(bookId, bookingId)

        // validate that no account is locked or in a locked group
        record.movements.keys.forEach {
            if (it.locked || it.accountGroup.locked) {
                throw InvalidRequestException("account or account group locked: ${AccountId.of(it)}")
            }
        }

        record.bookingText = request.text
        record.description = request.description
        record.tag = request.tag

        return BookingRecordDto.of(record)
    }

    fun deleteRecord(bookId: Long, bookingId: Long) {
        val record = getRecord(bookId, bookingId)

        // validate that no account is locked or in a locked group
        record.movements.keys.forEach {
            if (it.locked || it.accountGroup.locked) {
                throw InvalidRequestException("account or account group locked: ${AccountId.of(it)}")
            }
        }

        bookingRecordRepository.delete(record)
    }

}