package ch.awae.mycloud.module.bookkeping.dto

import ch.awae.mycloud.module.bookkeping.model.*

data class AccountId(val groupNumber: Int, val accountNumber: Int) {

    override fun toString(): String {
        return "%01d.%03d".format(groupNumber, accountNumber)
    }

    companion object {
        fun of(id: String): AccountId {
            val (groupNumber, accountNumber) = id.split(".", limit = 2).map { it.toInt() }
            return AccountId(groupNumber, accountNumber)
        }

        fun of(account: Account): AccountId {
            return AccountId(account.accountGroup.groupNumber, account.accountNumber)
        }
    }

}