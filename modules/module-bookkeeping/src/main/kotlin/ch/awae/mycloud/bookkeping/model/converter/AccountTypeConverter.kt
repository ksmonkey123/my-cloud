package ch.awae.mycloud.bookkeping.model.converter

import ch.awae.mycloud.bookkeping.model.*
import jakarta.persistence.*

@Converter
class AccountTypeConverter : AttributeConverter<AccountType, String> {

    override fun convertToDatabaseColumn(accountType: AccountType?): String? {
        return accountType?.shortString
    }

    override fun convertToEntityAttribute(shortString: String?): AccountType? {
        return AccountType.entries.find { it.shortString == shortString }
    }
}
