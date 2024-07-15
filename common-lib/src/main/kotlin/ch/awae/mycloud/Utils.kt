package ch.awae.mycloud

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Objects

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

inline fun <reified T : Any> T.createLogger(): Logger = LoggerFactory.getLogger(T::class.java)

inline fun <T> ThreadLocal<T>.update(f: (T) -> T) {
    set(f(get()))
}
