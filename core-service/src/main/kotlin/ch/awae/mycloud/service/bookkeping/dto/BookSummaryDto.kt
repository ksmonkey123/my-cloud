package ch.awae.mycloud.service.bookkeping.dto

import ch.awae.mycloud.service.bookkeping.model.*
import java.time.*

data class BookSummaryDto(
    val id: Long,
    val title: String,
    val description: String?,
    val openingDate: LocalDate,
    val closingDate: LocalDate,
    val closed: Boolean,
) {

    constructor(book: Book) : this(
        id = book.id,
        title = book.title,
        description = book.description,
        openingDate = book.openingDate,
        closingDate = book.closingDate,
        closed = book.closed,
    )

}
