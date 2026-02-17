package ch.awae.mycloud.common.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Implements an equality check based on a derived value. (Usually a field)
 * Two instances are considered equal if they are the same instance,
 * or if they have compatible types and the derived values are equal.
 *
 * Example use:
 *
 * ```kt
 * val a: DbEntity = ...
 * val b: DbEntity = ...
 * a.equalByValue(b, DbEntity::id)
 * ```
 *
 * @param T the type to check
 * @param other the object to compare `this` instance to
 * @param mapper function producing the derived value to compare.
 */
inline fun <reified T : Any> T.equalByValue(other: Any?, mapper: (T) -> Any?): Boolean {
    if (this === other) {
        return true
    }
    if (other === null) {
        return false
    }
    if (other is T) {
        return Objects.equals(mapper(this), mapper(other))
    }
    return false
}

/**
 * Creates a logger instance for the class this function is called from.
 *
 * Example usage:
 *
 * ```kt
 * class MyClass {
 *   private val logger = createLogger()
 * }
 * ```
 */
inline fun <reified T : Any> T.createLogger(): Logger = LoggerFactory.getLogger(T::class.java)
