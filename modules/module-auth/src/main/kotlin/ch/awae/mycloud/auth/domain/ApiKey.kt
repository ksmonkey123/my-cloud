package ch.awae.mycloud.auth.domain

import ch.awae.mycloud.common.db.IdBaseEntity
import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

@Table(name = "api_key", schema = "auth")
@Entity(name = "auth_ApiKey")
class ApiKey(
    @Column(updatable = false, nullable = false)
    val name: String,
    @Column(updatable = false, unique = true, nullable = false)
    val tokenString: String,
    @ManyToOne
    @JoinColumn(updatable = false, nullable = false)
    val owner: Account,
    @Convert(converter = AuthoritiesConverter::class)
    val authorities: Set<String>,
    @Column(updatable = false, nullable = false)
    val creationTime: LocalDateTime = LocalDateTime.now(),
) : IdBaseEntity()

interface ApiKeyRepository : JpaRepository<ApiKey, Long> {

    @Query("select a from auth_ApiKey a where a.owner.username = :username order by a.creationTime desc")
    fun listByOwnerUsername(username: String): List<ApiKey>

    @Query("select a from auth_ApiKey a where a.owner.username = :username and a.name = :name")
    fun findByOwnerAndName(username: String, name: String): ApiKey?

    @Query("select a from auth_ApiKey a where a.tokenString = :tokenString and a.owner.enabled")
    fun findActiveByTokenString(tokenString: String): ApiKey?

}
