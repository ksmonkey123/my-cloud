package ch.awae.mycloud.module.email

import ch.awae.mycloud.common.db.*
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query

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
    var sent: Boolean = false,
) : IdBaseEntity() {
    override fun toString(): String {
        return "EmailOutbox(id=$id, bodyFormat=$bodyFormat, sender='$sender', recipient='$recipient', subject='$subject', bodyContent=[${bodyContent.length} chars])"
    }
}

enum class EmailBodyFormat {
    HTML, TEXT, MARKDOWN
}

interface EmailOutboxRepository : JpaRepository<EmailOutbox, Long> {
    @Query("select e from EmailOutbox e where e.id = :id and not e.sent")
    fun findToSend(id: Long): EmailOutbox?

    @Query("select e.id from EmailOutbox e where not e.sent order by e._creationTimestamp asc limit :count")
    fun listToSend(count: Int): List<Long>
}