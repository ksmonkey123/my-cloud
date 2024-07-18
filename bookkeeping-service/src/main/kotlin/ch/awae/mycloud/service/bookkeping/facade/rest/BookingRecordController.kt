package ch.awae.mycloud.service.bookkeping.facade.rest

import ch.awae.mycloud.service.bookkeping.dto.*
import ch.awae.mycloud.service.bookkeping.service.*
import org.springframework.http.*
import org.springframework.security.access.prepost.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books/{bookId}/records")
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

}