package ch.awae.mycloud.service.bookkeping.service

import ch.awae.mycloud.audit.*
import ch.awae.mycloud.service.bookkeping.dto.*
import ch.awae.mycloud.service.bookkeping.model.*
import org.springframework.data.domain.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class BookingRecordService(
    val bookService: BookService,
    val bookingRecordRepository: BookingRecordRepository,
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
            records.toList().map { BookingRecordDto.of(it) },
            records.totalElements,
        )
    }
}