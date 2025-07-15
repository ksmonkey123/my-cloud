package ch.awae.mycloud.module.auth.domain

import ch.awae.mycloud.api.auth.*
import ch.awae.mycloud.common.db.*
import com.fasterxml.jackson.annotation.*
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query

@Table(name = "account", schema = "auth")
@Entity
class Account(
    @Column(updatable = false, unique = true)
    val username: String,
    @JsonIgnore
    var password: String,
    @Email
    var email: String?,
    var enabled: Boolean = true,
    var admin: Boolean = false,
    @Convert(converter = LanguageConverter::class)
    var language: Language,
    @JsonIgnore
    @ManyToMany
    @JoinTable(
        schema = "auth",
        name = "account_role",
        joinColumns = [JoinColumn(name = "account_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: MutableSet<Role> = mutableSetOf(),
) : IdBaseEntity()

interface AccountRepository : JpaRepository<Account, Long> {

    @Query("select count(*) from Account a where a.admin")
    fun countAdmins(): Long

    @Query("select a from Account a where a.username = :username and a.enabled")
    fun findActiveByUsername(username: String): Account?

    @Query("select t.account from AuthToken t where t.tokenString = :tokenString and t.validUntil >= current_timestamp and t.account.enabled")
    fun findActiveByValidTokenString(tokenString: String): Account?

    @Query("select a from Account a order by a.username asc")
    fun listAll(): List<Account>

    @Query("select a from Account a where a.enabled and a.admin order by a.username asc")
    fun listActiveAdmins(): List<Account>

    fun findByUsername(username: String): Account?

    fun existsByUsername(username: String): Boolean

}