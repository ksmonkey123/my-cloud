package ch.awae.mycloud.module.documents.jdbc

import ch.awae.mycloud.common.util.GUID
import ch.awae.mycloud.documents.DocumentSource
import ch.awae.mycloud.module.documents.Document
import ch.awae.mycloud.module.documents.DocumentRepository
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class DocumentRepositoryJdbcImpl(private val jdbcTemplate: NamedParameterJdbcTemplate) : DocumentRepository {

    override fun save(document: Document) {
        jdbcTemplate.update(
            """
            insert into documents.document (id, username, source, filename, type, created_at, valid_until, content)
            values (:id, :username, :source, :filename, :type, :created_at, :valid_until, :content)
            """.trimIndent(), mapOf(
                "id" to document.id.uuid,
                "username" to document.username,
                "source" to document.source.name,
                "filename" to document.filename,
                "type" to document.type,
                "created_at" to document.createdAt,
                "valid_until" to document.validUntil,
                "content" to document.content,
            )
        )
    }

    override fun findValidById(id: GUID): Document? {
        return jdbcTemplate.query(
            "select * from documents.document where id = :id and valid_until > current_timestamp",
            mapOf("id" to id.uuid)
        ) { rs, _ ->
            Document(
                id = GUID(rs.getObject("id", UUID::class.java)),
                type = rs.getString("type"),
                filename = rs.getString("filename"),
                source = DocumentSource.valueOf(rs.getString("source")),
                username = rs.getString("username"),
                createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
                validUntil = rs.getTimestamp("valid_until").toLocalDateTime(),
                content = rs.getBytes("content")
            )
        }.singleOrNull()
    }

    override fun deleteExpired() {
        jdbcTemplate.update(
            "delete from documents.document where valid_until < current_timestamp",
            emptyMap<String, Any>()
        )
    }

}