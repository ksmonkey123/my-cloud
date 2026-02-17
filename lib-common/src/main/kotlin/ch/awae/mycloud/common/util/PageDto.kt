package ch.awae.mycloud.common.util

data class PageDto<T>(
    val items: List<T>,
    val totalElements: Long,
)