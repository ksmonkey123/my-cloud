package ch.awae.mycloud.module.auth.domain

import ch.awae.mycloud.common.db.IdBaseEntity
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.*
import java.security.*
import java.time.*
import java.util.*

@Table(name = "auth_token", schema="auth")
@Entity
class AuthToken private constructor(
    @Column(updatable = false, unique = true)
    val tokenString: String,
    @ManyToOne
    @JoinColumn(name = "account_id", updatable = false)
    val account: Account,
) : IdBaseEntity() {

    companion object {
        fun buildToken(account: Account): AuthToken {
            val tokenString = ByteArray(64)
                .also { SecureRandom().nextBytes(it) }
                .let { Base64.getEncoder().encodeToString(it) }
            return AuthToken(tokenString, account)
        }
    }

}

@Repository
interface AuthTokenRepository : JpaRepository<AuthToken, Long> {

    @Modifying(flushAutomatically = true)
    @Query("delete from AuthToken where tokenString = :tokenString")
    fun deleteByTokenString(tokenString: String)

    @Modifying(flushAutomatically = true)
    @Query("delete from AuthToken where _creationTimestamp < :timestamp")
    fun deleteExpiredTokens(timestamp: LocalDateTime)

}
