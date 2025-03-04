package ch.awae.mycloud.db

import ch.awae.mycloud.auth.*
import jakarta.persistence.*

@Converter
class LanguageConverter : AttributeConverter<Language, String> {
    override fun convertToDatabaseColumn(attribute: Language?): String? {
        return attribute?.code
    }

    override fun convertToEntityAttribute(dbData: String?): Language? {
        return Language.entries.find { it.code == dbData }
    }
}