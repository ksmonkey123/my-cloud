package ch.awae.mycloud.common.db

import ch.awae.mycloud.common.auth.Language
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