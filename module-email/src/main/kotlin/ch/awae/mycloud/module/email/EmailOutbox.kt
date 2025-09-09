package ch.awae.mycloud.module.email

import ch.awae.mycloud.common.db.*
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query
import java.time.*

@Entity
@Table(schema = "email", name = "outbox")
class EmailOutbox(
    @Column(updatable = false)
    val sender: String,
    @Column(updatable = false)
    val recipient: String,
    @Column(updatable = false)
    val subject: String,
    @Column(updatable = false)
    val bodyContent: String,
    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    val bodyFormat: EmailBodyFormat,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) : IdBaseEntity() {
    override fun toString(): String {
        return "EmailOutbox(id=$id, bodyFormat=$bodyFormat, sender='$sender', recipient='$recipient', subject='$subject', bodyContent=[${bodyContent.length} chars])"
    }

    var sent: Boolean = false
        private set

    var sentAt: LocalDateTime? = null
        private set

    fun markAsSent() {
        sent = true
        sentAt = LocalDateTime.now()
    }

}

enum class EmailBodyFormat {
    HTML, TEXT, MARKDOWN
}

interface EmailOutboxRepository : JpaRepository<EmailOutbox, Long> {
    @Query("select e from EmailOutbox e where e.id = :id and not e.sent")
    fun findToSend(id: Long): EmailOutbox?

    @Query("select e.id from EmailOutbox e where not e.sent order by e.createdAt asc limit :count")
    fun listToSend(count: Int): List<Long>
}