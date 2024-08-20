package ch.awae.mycloud.service.bookkeping.model

import jakarta.persistence.*
import jakarta.persistence.Table
import org.hibernate.annotations.*
import java.math.*

@Entity
@Table(name = "v_account_balance")
@Immutable
class AccountBalance(
    @Id
    @OneToOne
    val account: Account,
    val balance: BigDecimal,
)
