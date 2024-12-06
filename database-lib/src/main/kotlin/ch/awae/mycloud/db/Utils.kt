package ch.awae.mycloud.db

import java.util.*

inline fun <reified T : Any> T?.equalByField(other: Any?, fieldExtractor: (T) -> Any?): Boolean {
    if (this === other) {
        return true
    }
    if (this === null || other === null) {
        return false
    }
    if (other is T) {
        return Objects.equals(fieldExtractor(this), fieldExtractor(other))
    }
    return false
}
