package ch.awae.mycloud.service.bookkeping.model

import ch.awae.mycloud.db.*
import jakarta.persistence.*
import jakarta.validation.*
import org.springframework.data.jpa.repository.*
import java.time.*

@Entity
class Book(
    @Column(updatable = false)
    val username: String,
    var title: String,
    @Column(updatable = false)
    val openingDate: LocalDate,
    @Column(updatable = false)
    val closingDate: LocalDate,
    var description: String?,
    var closed: Boolean,
) : IdBaseEntity() {
    @OneToMany(mappedBy = "book")
    val accountGroups: Set<AccountGroup> = emptySet()

    override fun validate() {
        if (!closingDate.isAfter(openingDate)) {
            throw ValidationException("book cannot close before opening")
        }
    }

}

interface BookRepository : JpaRepository<Book, Long> {

    fun findByUsername(username: String): List<Book>

    fun findByIdAndUsername(id: Long, username: String): Book?

}