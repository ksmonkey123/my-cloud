package ch.awae.mycloud.documents

import org.springframework.http.MediaType
import java.time.Duration
import java.time.LocalDateTime

class DocumentData(
    val source: DocumentSource,
    val filename: String,
    val type: MediaType,
    val validUntil: LocalDateTime,
    val content: ByteArray,
) {
    constructor(
        source: DocumentSource,
        filename: String,
        type: MediaType,
        lifetime: Duration,
        content: ByteArray,
    ) : this(
        source = source,
        filename = filename,
        type = type,
        validUntil = LocalDateTime.now().plus(lifetime),
        content = content,
    )
}