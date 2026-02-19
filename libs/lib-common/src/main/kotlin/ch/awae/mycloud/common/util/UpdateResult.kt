package ch.awae.mycloud.common.util

/**
 * Represents the result of an "insert-or-update" operation.
 *
 * @param T the result type
 */
sealed interface UpdateResult<T> {

    /**
     * the result of the update
     */
    val value: T

    /**
     * `true` iff instance of [UpdateResult.Created]
     */
    val created: Boolean

    /**
     * `true` iff instance of [UpdateResult.Updated]
     */
    val updated: Boolean

    /**
     * Represents the result of an "insert" operation.
     *
     * @param T the result type
     * @param value the result
     */
    data class Created<T>(override val value: T) : UpdateResult<T> {
        override val created = true
        override val updated = false
    }

    /**
     * Represents the result of an "update" operation.
     *
     * @param T the result type
     * @param value the result
     */
    data class Updated<T>(override val value: T) : UpdateResult<T> {
        override val created = false
        override val updated = true
    }
}