package ch.awae.mycloud.email.outbox

import ch.awae.mycloud.common.db.IdBaseEntity
import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

@Entity(name = "email_EmailOutbox")
@Table(schema = "email", name = "outbox")
class EmailOutbox(
    @Column(updatable = false, nullable = false)
    val recipient: String,
    @Column(updatable = false, nullable = false)
    val subject: String,
    @Column(updatable = false, nullable = false)
    val bodyContent: String,
    @Column(updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    val bodyFormat: EmailBodyFormat,
    @Column(updatable = false, nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(updatable = false, unique = true, nullable = true)
    val messageUid: String? = null,
) : IdBaseEntity() {
    override fun toString(): String {
        return "EmailOutbox(id=$id, bodyFormat=$bodyFormat, recipient='$recipient', subject='$subject', bodyContent=[${bodyContent.length} chars], createdAt=$createdAt, messageUid=$messageUid)"
    }

    var sentAt: LocalDateTime? = null

    fun markAsSent() {
        sentAt = LocalDateTime.now()
    }

}

enum class EmailBodyFormat {
    HTML, TEXT, MARKDOWN
}

interface EmailOutboxRepository : JpaRepository<EmailOutbox, Long> {
    @Query("select e from email_EmailOutbox e where e.id = :id and e.sentAt is null")
    fun findToSend(id: Long): EmailOutbox?

    @Query("select e.id from email_EmailOutbox e where e.sentAt is null order by e.createdAt asc limit :count")
    fun listToSend(count: Int): List<Long>

    fun existsByMessageUid(messageUid: String): Boolean

}