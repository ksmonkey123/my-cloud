package ch.awae.mycloud.module.documents.jdbc

import ch.awae.mycloud.common.util.GUID
import ch.awae.mycloud.documents.DocumentSource
import ch.awae.mycloud.module.documents.DocumentData
import ch.awae.mycloud.module.documents.DocumentRepository
import org.springframework.http.MediaType
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class DocumentRepositoryJdbcImpl(private val sql: NamedParameterJdbcTemplate) : DocumentRepository {

    override fun create(document: DocumentData, username: String) {
        sql.update(
            """
            insert into documents.document (id, username, source, filename, type, created_at, valid_until, content)
            values (:id, :username, :source, :filename, :type, :created_at, :valid_until, :content)
            """.trimIndent(), mapOf(
                "id" to document.id.uuid,
                "username" to username,
                "source" to document.source.name,
                "filename" to document.filename,
                "type" to document.type.toString(),
                "created_at" to LocalDateTime.now(),
                "valid_until" to document.validUntil,
                "content" to document.content,
            )
        )
    }

    override fun findValid(id: GUID): DocumentData? {
        return sql.query(
            "select source, filename, type, valid_until, content from documents.document where id = :id and valid_until > current_timestamp",
            mapOf("id" to id.uuid)
        ) { rs, _ ->
            DocumentData(
                id = id,
                source = DocumentSource.valueOf(rs.getString("source")),
                type = MediaType.valueOf(rs.getString("type")),
                filename = rs.getString("filename"),
                validUntil = rs.getTimestamp("valid_until").toLocalDateTime(),
                content = rs.getBytes("content")
            )
        }.singleOrNull()
    }

    override fun deleteExpired() {
        sql.update(
            "delete from documents.document where valid_until < current_timestamp",
            emptyMap<String, Any>()
        )
    }

}