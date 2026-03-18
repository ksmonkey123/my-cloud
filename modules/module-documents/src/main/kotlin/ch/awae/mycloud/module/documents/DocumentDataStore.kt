package ch.awae.mycloud.module.documents

import ch.awae.mycloud.common.util.GUID
import ch.awae.mycloud.documents.DocumentData
import ch.awae.mycloud.documents.DocumentIdentifier
import ch.awae.mycloud.documents.DocumentSource
import ch.awae.mycloud.documents.DocumentStore
import jakarta.transaction.Transactional
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.http.MediaType
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Transactional
@Service
class DocumentDataStore(private val sql: NamedParameterJdbcTemplate) : DocumentStore {

    override fun createDocument(document: DocumentData, username: String): DocumentIdentifier {
        val id = GUID.generateV7()
        sql.update(
            """
            insert into documents.document (id, username, source, filename, type, created_at, valid_until, content)
            values (:id, :username, :source, :filename, :type, :created_at, :valid_until, :content)
            """.trimIndent(), mapOf(
                "id" to id,
                "username" to username,
                "source" to document.source.name,
                "filename" to document.filename,
                "type" to document.type.toString(),
                "created_at" to LocalDateTime.now(),
                "valid_until" to document.validUntil,
                "content" to document.content,
            )
        )
        return DocumentIdentifier(id, document.type.toString())
    }

    fun retrieveById(id: UUID): DocumentData? {
        return sql.query(
            "select source, filename, type, valid_until, content from documents.document where id = :id and valid_until > current_timestamp",
            mapOf("id" to id)
        ) { rs, _ ->
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
        sql.update(
            "delete from documents.document where valid_until < current_timestamp",
            emptyMap<String, Any>()
        )
    }

}