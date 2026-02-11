package ch.awae.mycloud.common.util

sealed interface UpdateResult<T> {

    val value: T
    val created: Boolean
    val updated: Boolean

    data class Created<T>(override val value: T) : UpdateResult<T> {
        override val created = true
        override val updated = false
    }

    data class Updated<T>(override val value: T) : UpdateResult<T> {
        override val created = false
        override val updated = true
    }
}