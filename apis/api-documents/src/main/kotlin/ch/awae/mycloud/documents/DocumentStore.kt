package ch.awae.mycloud.documents

import org.springframework.http.MediaType
import java.time.Duration
import java.time.LocalDateTime

interface DocumentStore {

    fun createDocument(
        source: DocumentSource,
        filename: String,
        type: MediaType,
        content: ByteArray,
        validUntil: LocalDateTime,
        username: String? = null,
    ): DocumentIdentifier

    fun createDocument(
        source: DocumentSource,
        filename: String,
        type: MediaType,
        content: ByteArray,
        lifetime: Duration,
        username: String? = null,
    ): DocumentIdentifier

}

