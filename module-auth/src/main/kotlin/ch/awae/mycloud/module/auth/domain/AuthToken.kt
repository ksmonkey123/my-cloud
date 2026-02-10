package ch.awae.mycloud.module.auth.domain

import ch.awae.mycloud.common.TokenGenerator
import ch.awae.mycloud.common.db.*
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query
import java.time.*

@Table(name = "auth_token", schema = "auth")
@Entity(name = "auth_AuthToken")
class AuthToken private constructor(
    @Column(updatable = false, unique = true, nullable = false)
    val tokenString: String,
    @ManyToOne
    @JoinColumn(updatable = false, nullable = false)
    val account: Account,
    @Column(updatable = false, nullable = false)
    val validUntil: LocalDateTime,
) : IdBaseEntity() {

    companion object {
        fun buildToken(account: Account, tokenGenerator: TokenGenerator, validUntil: LocalDateTime): AuthToken {
            val tokenString = tokenGenerator.generate(32, TokenGenerator.EncoderType.BASIC)
            return AuthToken(tokenString, account, validUntil)
        }
    }

}

interface AuthTokenRepository : JpaRepository<AuthToken, Long> {

    @Modifying(flushAutomatically = true)
    @Query("delete from auth_AuthToken where tokenString = :tokenString")
    fun deleteByTokenString(tokenString: String)

    @Modifying(flushAutomatically = true)
    @Query("delete from auth_AuthToken where validUntil < current_timestamp")
    fun deleteExpiredTokens()

}
