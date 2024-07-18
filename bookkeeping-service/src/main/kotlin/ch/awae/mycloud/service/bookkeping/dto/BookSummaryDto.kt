package ch.awae.mycloud.service.bookkeping.dto

import ch.awae.mycloud.service.bookkeping.model.*
import java.time.*

data class BookSummaryDto(
    val id: Long,
    val title: String,
    val description: String?,
    val openingDate: LocalDate,
    val closingDate: LocalDate,
) {

    constructor(book: Book) : this(book.id, book.title, book.description, book.openingDate, book.closingDate)

}
