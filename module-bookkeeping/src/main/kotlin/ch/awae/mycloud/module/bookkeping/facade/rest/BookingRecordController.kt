package ch.awae.mycloud.module.bookkeping.facade.rest

import ch.awae.mycloud.common.*
import ch.awae.mycloud.module.bookkeping.dto.*
import ch.awae.mycloud.module.bookkeping.service.*
import org.springframework.http.*
import org.springframework.security.access.prepost.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/bookkeeping/books/{bookId}/records")
@PreAuthorize("hasAuthority('bookkeeping')")
class BookingRecordController(
    val service: BookingRecordService,
) {

    @GetMapping
    fun list(
        @PathVariable bookId: Long,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") pageSize: Int,
    ): PageDto<BookingRecordDto> {
        return service.listRecordsOfBook(bookId, page, pageSize)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable bookId: Long,
        @RequestBody request: CreateBookingRecordRequest
    ): BookingRecordDto {
        return service.createRecord(bookId, request)
    }

    @PutMapping("/{bookingId}")
    fun edit(
        @PathVariable bookId: Long,
        @PathVariable bookingId: Long,
        @RequestBody request: BookingRecordEditRequest,
    ): BookingRecordDto {
        return service.editRecord(bookId, bookingId, request)
    }

    @DeleteMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable bookId: Long,
        @PathVariable bookingId: Long,
    ) {
        service.deleteRecord(bookId, bookingId)
    }

}