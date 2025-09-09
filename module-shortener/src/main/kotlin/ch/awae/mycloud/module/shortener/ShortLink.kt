package ch.awae.mycloud.module.shortener

import ch.awae.mycloud.common.db.*
import com.fasterxml.jackson.annotation.*
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*

@Table(name = "link", schema = "shortener")
@Entity
class ShortLink(
    @Id
    @Column(updatable = false)
    val id: String,
    @JsonIgnore
    @Column(updatable = false)
    val username: String,
    @Column(updatable = false)
    val targetUrl: String,
) : BaseEntity()

interface ShortLinkRepository : JpaRepository<ShortLink, String> {
    fun findByUsername(username: String): List<ShortLink>
    fun findByIdAndUsername(id: String, username: String): ShortLink?
}