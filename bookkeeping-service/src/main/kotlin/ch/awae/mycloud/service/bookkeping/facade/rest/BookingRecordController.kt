package ch.awae.mycloud.service.bookkeping.facade.rest

import ch.awae.mycloud.service.bookkeping.dto.*
import ch.awae.mycloud.service.bookkeping.service.*
import ch.awae.mycloud.service.bookkeping.service.export.*
import org.springframework.core.io.*
import org.springframework.http.*
import org.springframework.security.access.prepost.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books/{bookId}/records")
@PreAuthorize("hasAuthority('bookkeeping')")
class BookingRecordController(
    val service: BookingRecordService,
    val exportService: BookingRecordExportService,
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

    @GetMapping("/export")
    fun export(
        @PathVariable bookId: Long
    ): ResponseEntity<Resource> {
        val blob = exportService.createExport(bookId)
        return ResponseEntity.ok()
            .headers {
                it.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=journal.xlsx")
            }
            .contentLength(blob.size.toLong())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(ByteArrayResource(blob))
    }

}