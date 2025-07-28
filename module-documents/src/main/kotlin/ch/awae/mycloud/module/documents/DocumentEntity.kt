package ch.awae.mycloud.module.documents

import ch.awae.mycloud.common.db.*
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query
import java.time.*

@Entity(name = "DOCS_Document")
@Table(schema = "documents", name = "document")
class DocumentEntity(
    @Column(updatable = false)
    val type: String,
    @Column(updatable = false)
    val filename: String,
    @Column(updatable = false, unique = true)
    val token: String,
    @Column(updatable = false)
    val validUntil: LocalDateTime,
    @Column(updatable = false)
    val content: ByteArray,
) : IdBaseEntity()

interface DocumentRepository : JpaRepository<DocumentEntity, Long> {


    @Modifying(flushAutomatically = true)
    @Query("delete from DOCS_Document where validUntil < current_timestamp")
    fun deleteExpired()

    @Query("select d from DOCS_Document d where d.token = :token and d.validUntil >= current_timestamp")
    fun findValidByToken(token: String): DocumentEntity?

}
