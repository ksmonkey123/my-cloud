package ch.awae.mycloud.module.bookkeping.model

import jakarta.persistence.*
import jakarta.persistence.Table
import org.hibernate.annotations.*
import java.math.*

@Entity(name = "bookkeeping_AccountBalance")
@Table(name = "v_account_balance", schema = "bookkeeping")
@Immutable
data class AccountBalance(
    @Id
    @OneToOne
    val account: Account,
    val balance: BigDecimal,
)
