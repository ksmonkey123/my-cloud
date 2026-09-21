package ch.awae.mycloud.module.documents

import ch.awae.mycloud.common.db.singleOrNull
import ch.awae.mycloud.common.util.GUID
import ch.awae.mycloud.documents.DocumentData
import ch.awae.mycloud.documents.DocumentIdentifier
import ch.awae.mycloud.documents.DocumentSource
import ch.awae.mycloud.documents.DocumentStore
import jakarta.transaction.Transactional
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.http.MediaType
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Transactional
@Service
class DocumentDataStore(private val db: JdbcClient) : DocumentStore {

    override fun createDocument(document: DocumentData, username: String): DocumentIdentifier {
        val id = GUID.generateV7()
        db.sql(
            """
            insert into documents.document (id, username, source, filename, type, created_at, valid_until, content)
            values (:id, :username, :source, :filename, :type, :created_at, :valid_until, :content)
            """.trimIndent()
        )
            .param("id", id)
            .param("username", username)
            .param("source", document.source.name)
            .param("filename", document.filename)
            .param("type", document.type.toString())
            .param("created_at", LocalDateTime.now())
            .param("valid_until", document.validUntil)
            .param("content", document.content)
            .update()
        return DocumentIdentifier(id, document.type.toString())
    }

    fun retrieveById(id: UUID): DocumentData? {
        return db.sql("select source, filename, type, valid_until, content from documents.document where id = :id and valid_until > current_timestamp")
            .param("id", id)
            .query { rs, _ ->
                DocumentData(
                    source = DocumentSource.valueOf(rs.getString("source")),
                    type = MediaType.valueOf(rs.getString("type")),
                    filename = rs.getString("filename"),
                    validUntil = rs.getTimestamp("valid_until").toLocalDateTime(),
                    content = rs.getBytes("content")
                )
            }.singleOrNull()
    }

    @SchedulerLock(name = "documents:expired-documents-cleaner")
    @Scheduled(cron = "\${documents.clean-timer.schedule}")
    fun deleteExpired() {
        db.sql("delete from documents.document where valid_until < current_timestamp")
            .update()
    }

}