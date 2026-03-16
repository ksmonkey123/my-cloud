package ch.awae.mycloud.module.documents

import ch.awae.mycloud.common.util.GUID
import ch.awae.mycloud.common.util.equalByValue
import ch.awae.mycloud.documents.DocumentSource
import org.springframework.http.MediaType
import java.time.LocalDateTime

interface DocumentRepository {
    fun create(document: DocumentData, username: String)
    fun findValid(id: GUID): DocumentData?
    fun deleteExpired()
}

data class DocumentData(
    val id: GUID,
    val source: DocumentSource,
    val filename: String,
    val type: MediaType,
    val validUntil: LocalDateTime,
    val content: ByteArray,
) {
    override fun equals(other: Any?): Boolean = equalByValue(other) { DocumentData::id }
    override fun hashCode(): Int = id.hashCode()
}
