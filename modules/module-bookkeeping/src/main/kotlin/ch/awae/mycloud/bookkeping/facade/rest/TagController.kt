package ch.awae.mycloud.bookkeping.facade.rest

import ch.awae.mycloud.bookkeping.service.BookingRecordService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/bookkeeping/books/{bookId}/tags")
@PreAuthorize("hasAuthority('bookkeeping')")
class TagController(val service: BookingRecordService) {

    @GetMapping
    fun listTags(
        @PathVariable bookId: Long,
    ): List<String> {
        return service.listTags(bookId)
    }

}