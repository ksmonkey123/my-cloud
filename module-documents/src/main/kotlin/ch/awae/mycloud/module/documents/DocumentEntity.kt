package ch.awae.mycloud.module.documents

import ch.awae.mycloud.documents.DocumentSource
import ch.awae.mycloud.common.db.IdBaseEntity
import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

@Entity(name = "documents_Document")
@Table(schema = "documents", name = "document")
class DocumentEntity(
    @Column(updatable = false, nullable = false)
    val type: String,
    @Column(updatable = false, nullable = false)
    val filename: String,
    @Column(updatable = false, unique = true, nullable = false)
    val token: String,
    @Column(updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    val source: DocumentSource,
    @Column(updatable = false)
    val username: String?,
    @Column(updatable = false, nullable = false)
    val createdAt: LocalDateTime,
    @Column(updatable = false, nullable = false)
    val validUntil: LocalDateTime,
    @Column(updatable = false, nullable = false)
    val content: ByteArray,
) : IdBaseEntity()

interface DocumentRepository : JpaRepository<DocumentEntity, Long> {

    @Modifying(flushAutomatically = true)
    @Query("delete from documents_Document where validUntil < current_timestamp")
    fun deleteExpired()

    @Query("select d from documents_Document d where d.token = :token and d.validUntil >= current_timestamp")
    fun findValidByToken(token: String): DocumentEntity?

}
