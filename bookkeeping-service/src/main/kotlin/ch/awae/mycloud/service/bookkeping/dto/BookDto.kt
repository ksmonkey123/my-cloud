package ch.awae.mycloud.service.bookkeping.dto

import ch.awae.mycloud.service.bookkeping.model.*
import java.time.*

data class BookDto(
    val id: Long,
    val summary: BookSummaryDto,
    val groups: List<AccountGroupDto>
) {
    constructor(book: BookSummaryDto, groups: List<AccountGroupDto>) : this(book.id, book, groups)
}
