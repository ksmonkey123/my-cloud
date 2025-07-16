package ch.awae.mycloud.common

data class PageDto<T>(
    val items: List<T>,
    val totalElements: Long,
)