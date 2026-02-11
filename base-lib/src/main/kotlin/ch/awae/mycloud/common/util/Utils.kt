package ch.awae.mycloud.common.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

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
