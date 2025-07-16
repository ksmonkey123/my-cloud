package ch.awae.mycloud.module.bookkeping.service

import ch.awae.mycloud.common.*
import ch.awae.mycloud.module.bookkeping.dto.*
import ch.awae.mycloud.module.bookkeping.model.*
import org.springframework.data.domain.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Transactional
@Service
class BookingRecordService(
    val bookService: BookService,
    val bookingRecordRepository: BookingRecordRepository,
) {

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

        val lastLocalId = bookingRecordRepository.findMaxLocalId(book) ?: 0

        val record = BookingRecord(
            book, lastLocalId + 1, request.text, request.description, request.bookingDate, request.tag
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
        return PageDto(
            records.content.map { BookingRecordDto.of(it) },
            records.totalElements,
        )
    }

    fun getRecord(bookId: Long, localBookingId: Long): BookingRecord {
        val book = bookService.getBook(bookId)
        return bookingRecordRepository.findByLocalIdAndBook(localBookingId, book)
            ?: throw ResourceNotFoundException("/books/$bookId/records/$localBookingId")
    }

    fun editRecord(bookId: Long, localBookingId: Long, request: BookingRecordEditRequest): BookingRecordDto {
        val record = getRecord(bookId, localBookingId)

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

    fun deleteRecord(bookId: Long, localBookingId: Long) {
        val record = getRecord(bookId, localBookingId)

        // validate that no account is locked or in a locked group
        record.movements.keys.forEach {
            if (it.locked || it.accountGroup.locked) {
                throw InvalidRequestException("account or account group locked: ${AccountId.of(it)}")
            }
        }

        bookingRecordRepository.delete(record)
    }

    fun listTags(bookId: Long): List<String> {
        val book = bookService.getBook(bookId)
        return bookingRecordRepository.listTags(book)
    }

}