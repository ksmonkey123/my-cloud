package ch.awae.mycloud.module.bookkeping.model

import ch.awae.mycloud.common.db.*
import jakarta.persistence.*
import jakarta.validation.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query
import java.time.*

@Table(name = "book", schema = "bookkeeping")
@Entity(name = "bookkeeping_Book")
class Book(
    @Column(updatable = false, nullable = false)
    val username: String,
    @Column(nullable = false)
    var title: String,
    @Column(updatable = false, nullable = false)
    val openingDate: LocalDate,
    @Column(updatable = false, nullable = false)
    val closingDate: LocalDate,
    var description: String?,
    var closed: Boolean,
) : IdBaseEntity() {
    @OneToMany(mappedBy = "book")
    val accountGroups: MutableSet<AccountGroup> = mutableSetOf()

    override fun validate() {
        if (!closingDate.isAfter(openingDate)) {
            throw ValidationException("book cannot close before opening")
        }
    }

}

interface BookRepository : JpaRepository<Book, Long> {

    @Query("select b from bookkeeping_Book b where b.username = :username and not b.closed")
    fun findOpenByUsername(username: String): List<Book>

    @Query("select b from bookkeeping_Book b where b.username = :username")
    fun findAllByUsername(username: String): List<Book>

    fun findByIdAndUsername(id: Long, username: String): Book?

}