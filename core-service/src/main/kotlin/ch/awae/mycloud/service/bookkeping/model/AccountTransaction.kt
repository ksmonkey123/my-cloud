package ch.awae.mycloud.service.bookkeping.model

import jakarta.persistence.*
import jakarta.persistence.Table
import org.hibernate.annotations.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query
import java.math.*
import java.time.*

@Immutable
@Entity
@Table(name = "v_bookkeeping_account_transaction")
@IdClass(AccountTransactionPK::class)
class AccountTransaction(
    @Id
    val recordId: Long,
    @Id @Column(name = "account_id")
    val accountId: Long,
    val bookingDate: LocalDate,
    val bookingText: String,
    val description: String?,
    val tag: String?,
    val amount: BigDecimal,
    @ManyToOne
    val account: BookAccount,
    val creTime: LocalDateTime,
)

@Embeddable
data class AccountTransactionPK(val recordId: Long, val accountId: Long)

interface AccountTransactionRepository : JpaRepository<AccountTransaction, AccountTransactionPK> {

    @Query("select t from AccountTransaction t where t.account = :account order by t.bookingDate asc, t.creTime asc")
    fun findByAccount(account: BookAccount): List<AccountTransaction>

}