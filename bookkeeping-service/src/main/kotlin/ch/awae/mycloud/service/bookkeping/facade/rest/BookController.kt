package ch.awae.mycloud.service.bookkeping.facade.rest

import ch.awae.mycloud.service.bookkeping.dto.*
import ch.awae.mycloud.service.bookkeping.service.*
import org.springframework.security.access.prepost.*
import org.springframework.web.bind.annotation.*
import java.time.*

@RestController
@RequestMapping("/books")
@PreAuthorize("hasAuthority('bookkeeping')")
class BookController(private val service: BookService) {

    @GetMapping
    fun listAll(): List<BookSummaryDto> {
        return service.getBooks()
    }

    @PostMapping
    fun create(@RequestBody request: CreateBookRequest): BookSummaryDto {
        return service.createBook(request)
    }

    @GetMapping("/{bookId}")
    fun get(@PathVariable bookId: Long): BookDto {
        return service.getBookDetails(bookId)
    }

    @PutMapping("/{bookId}")
    fun edit(@PathVariable bookId: Long, @RequestBody request: EditBookRequest): BookSummaryDto {
        return service.editBook(bookId, request)
    }

}

data class CreateBookRequest(
    val title: String,
    val description: String?,
    val openingDate: LocalDate,
    val closingDate: LocalDate,
)

data class EditBookRequest(
    val title: String,
    val description: String?
)