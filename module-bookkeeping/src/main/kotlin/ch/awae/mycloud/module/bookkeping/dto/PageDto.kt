package ch.awae.mycloud.module.bookkeping.dto

data class PageDto<T>(
    val items: List<T>,
    val totalElements: Long,
)
