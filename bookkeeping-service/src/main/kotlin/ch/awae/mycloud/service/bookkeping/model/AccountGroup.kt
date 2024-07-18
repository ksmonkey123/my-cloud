package ch.awae.mycloud.service.bookkeping.model

import ch.awae.mycloud.db.*
import jakarta.persistence.*
import jakarta.validation.*
import org.springframework.data.jpa.repository.*

@Entity
class AccountGroup(
    @ManyToOne @JoinColumn(updatable = false)
    val book: Book,
    @Column(updatable = false)
    val groupNumber: Int,
    var title: String,
) : IdBaseEntity() {

    override fun validate() {
        if (groupNumber !in 1..9) {
            throw ValidationException("invalid group number")
        }
    }

    @OneToMany(mappedBy = "accountGroup", orphanRemoval = true)
    val accounts: List<Account> = emptyList()

    data class PK(
        val bookId: Long,
        val groupId: Int,
    )
}

interface AccountGroupRepository : JpaRepository<AccountGroup, Long> {

    fun getByBookAndGroupNumber(book: Book, groupNumber: Int): AccountGroup?

}
