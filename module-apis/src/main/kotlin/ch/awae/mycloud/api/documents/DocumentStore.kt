package ch.awae.mycloud.api.documents

import org.springframework.http.*
import java.time.*

interface DocumentStore {

    fun createDocument(
        filename: String,
        type: MediaType,
        content: ByteArray,
        lifetime: Duration,
    ): DocumentIdentifier

}

