package ch.awae.mycloud.module.auth.domain

import ch.awae.mycloud.common.db.*
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query

@Table(name = "role", schema = "auth")
@Entity
class Role(
    @Column(updatable = false, unique = true)
    val name: String,
    @Convert(converter = AuthoritiesConverter::class)
    var authorities: Set<String>,
    var description: String? = null,
) : IdBaseEntity() {
    @ManyToMany
    @JoinTable(
        schema = "auth",
        name = "account_role",
        joinColumns = [JoinColumn(name = "role_id")],
        inverseJoinColumns = [JoinColumn(name = "account_id")]
    )
    val accounts: MutableSet<Account> = mutableSetOf()
}

interface RoleRepository : JpaRepository<Role, Long> {

    @Query("select r from Role r where r.name in :names")
    fun findRolesByName(names: List<String>): List<Role>

}