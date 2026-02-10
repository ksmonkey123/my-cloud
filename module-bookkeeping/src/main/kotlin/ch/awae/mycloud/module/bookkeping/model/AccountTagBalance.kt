package ch.awae.mycloud.module.bookkeping.model

import jakarta.persistence.*
import jakarta.persistence.Table
import org.hibernate.annotations.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.jpa.repository.Query
import java.math.*

@Immutable
@Entity(name = "bookkeeping_AccountTagBalance")
@Table(name = "v_account_tag_balance", schema = "bookkeeping")
@IdClass(AccountTagBalancePK::class)
class AccountTagBalance(
    @Id
    val accountId: Long,
    @Id
    val tag: String,
    val balance: BigDecimal
)

@Embeddable
data class AccountTagBalancePK(val accountId: Long, val tag: String)

interface AccountTagBalanceRepository : JpaRepository<AccountTagBalance, AccountTagBalancePK> {

    @Query("select t from bookkeeping_AccountTagBalance t where t.accountId = :accountId")
    fun findByAccountId(accountId: Long): List<AccountTagBalance>

}