package ch.awae.mycloud.common.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Implements an equality check based on a specific field.
 * Two instances are considered equal if they have compatible types and the tested field is equal.
 *
 * Example use:
 *
 * ```kt
 * val a: DbEntity = ...
 * val b: DbEntity = ...
 * a.equalByField(b, DbEntity::id)
 * ```
 *
 * @param T the type to check
 * @param other the object to compare `this` instance to
 * @param fieldExtractor function extracting the field value to compare.
 */
inline fun <reified T : Any> T.equalByField(other: Any?, fieldExtractor: (T) -> Any?): Boolean {
    if (this === other) {
        return true
    }
    if (other === null) {
        return false
    }
    if (other is T) {
        return Objects.equals(fieldExtractor(this), fieldExtractor(other))
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
