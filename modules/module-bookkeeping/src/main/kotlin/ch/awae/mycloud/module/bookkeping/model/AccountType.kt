package ch.awae.mycloud.module.bookkeping.model

enum class AccountType(val shortString: String) {
    ASSET("A"),
    LIABILITY("P"),
    EXPENSE("-"),
    INCOME("+"),
    ;

    val invertedPresentation: Boolean
        get() = this == LIABILITY || this == INCOME

    val earningsAccount: Boolean
        get() = this == EXPENSE || this == INCOME

}
