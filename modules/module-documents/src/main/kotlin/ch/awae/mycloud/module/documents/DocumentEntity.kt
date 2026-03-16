package ch.awae.mycloud.module.documents

import ch.awae.mycloud.common.util.GUID
import ch.awae.mycloud.documents.DocumentSource
import java.time.LocalDateTime

data class Document(
    val id: GUID,
    val type: String,
    val filename: String,
    val source: DocumentSource,
    val username: String,
    val createdAt: LocalDateTime,
    val validUntil: LocalDateTime,
    val content: ByteArray,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Document

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

