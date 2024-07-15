package ch.awae.mycloud.service.auth.domain

import ch.awae.mycloud.db.*
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query

@Entity
class Role(
    @Column(updatable = false, unique = true)
    val name: String,
    var enabled: Boolean = true,
    var description: String?,
) : IdBaseEntity() {
    @ManyToMany
    @JoinTable(
        name = "account_role",
        joinColumns = [JoinColumn(name = "role_id")],
        inverseJoinColumns = [JoinColumn(name = "account_id")]
    )
    val accounts: MutableSet<Account> = mutableSetOf()
}

interface RoleRepository : JpaRepository<Role, Long> {

    fun findByName(name: String): Role?
    fun deleteByName(role: String)

    @Query("select r from Role r where r.name in :names")
    fun findRolesByName(names : List<String>) : List<Role>

}