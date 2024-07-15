package ch.awae.mycloud.service.shortener.model

import ch.awae.mycloud.db.*
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*
import java.time.*

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