package ch.awae.mycloud.module.auth.domain

import ch.awae.mycloud.common.db.*
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query
import java.time.*

@Table(name = "api_key", schema = "auth")
@Entity(name = "AUTH_API_KEY")
class ApiKey(
    @Column(updatable = false)
    val name: String,
    @Column(updatable = false, unique = true)
    val tokenString: String,
    @ManyToOne
    @JoinColumn(updatable = false)
    val owner: Account,
    @Convert(converter = AuthoritiesConverter::class)
    val authorities: Set<String>,
    val creationTime: LocalDateTime = LocalDateTime.now(),
) : IdBaseEntity()

interface ApiKeyRepository : JpaRepository<ApiKey, Long> {

    @Query("select a from AUTH_API_KEY a where a.owner.username = :username order by a.creationTime desc")
    fun listByOwnerUsername(username: String): List<ApiKey>

    @Query("select a from AUTH_API_KEY a where a.owner.username = :username and a.name = :name")
    fun findByOwnerAndName(username: String, name: String): ApiKey?

}

