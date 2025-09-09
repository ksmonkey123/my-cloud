package ch.awae.mycloud.module.auth.domain

import ch.awae.mycloud.common.db.*
import jakarta.persistence.*
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
    @ManyToMany
    @JoinTable(
        schema = "auth", name = "api_key_role",
        joinColumns = [JoinColumn(name = "api_key_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: MutableSet<Role> = mutableSetOf(),
    val creationTime: LocalDateTime = LocalDateTime.now(),
) : IdBaseEntity()