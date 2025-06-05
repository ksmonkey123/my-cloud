package ch.awae.mycloud.service.bookkeping.dto

data class BookDto(
    val id: Long,
    val summary: BookSummaryDto,
    val groups: List<AccountGroupDto>
) {
    constructor(book: BookSummaryDto, groups: List<AccountGroupDto>) : this(book.id, book, groups)
}
