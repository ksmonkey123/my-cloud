package ch.awae.mycloud.module.bookkeping.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable
import java.math.BigDecimal

@Entity(name = "bookkeeping_AccountBalance")
@Table(name = "v_account_balance", schema = "bookkeeping")
@Immutable
class AccountBalance(
    @Id
    @OneToOne
    val account: Account,
    val balance: BigDecimal,
)
