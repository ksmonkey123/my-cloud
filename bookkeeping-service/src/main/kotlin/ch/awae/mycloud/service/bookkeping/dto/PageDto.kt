package ch.awae.mycloud.service.bookkeping.dto

data class PageDto<T>(
    val items: List<T>,
    val totalElements: Long,
)
