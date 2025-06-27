package ch.awae.mycloud.module.bookkeping.facade.rest

import ch.awae.mycloud.module.bookkeping.model.*
import ch.awae.mycloud.module.bookkeping.service.*
import org.springframework.security.access.prepost.*
import org.springframework.web.bind.annotation.*

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