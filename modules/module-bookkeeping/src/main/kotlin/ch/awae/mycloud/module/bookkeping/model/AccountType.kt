package ch.awae.mycloud.module.bookkeping.model

enum class AccountType(val shortString: String, val invertedPresentation: Boolean?) {
    ASSET("A", false),
    LIABILITY("P", true),
    TRANSFER("X", null),
    EXPENSE("-", false),
    INCOME("+", true),
    ;

    val earningsAccount: Boolean
        get() = this == EXPENSE || this == INCOME

}
