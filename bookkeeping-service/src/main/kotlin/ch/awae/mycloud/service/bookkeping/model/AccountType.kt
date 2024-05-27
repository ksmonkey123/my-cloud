package ch.awae.mycloud.service.bookkeping.model

enum class AccountType(val shortString: String) {
    ASSET("A"),
    LIABILITY("P"),
    EXPENSE("-"),
    INCOME("+"),
}
