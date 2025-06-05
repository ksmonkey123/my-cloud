package ch.awae.mycloud.service.bookkeping.model

import jakarta.persistence.*
import jakarta.persistence.Table
import org.hibernate.annotations.*
import java.math.*

@Entity
@Table(name = "v_bookkeeping_account_balance")
@Immutable
data class AccountBalance(
    @Id
    @OneToOne
    val account: BookAccount,
    val balance: BigDecimal,
)
