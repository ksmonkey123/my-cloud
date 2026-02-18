package ch.awae.mycloud.auth.domain

import ch.awae.mycloud.auth.Language
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class LanguageConverter : AttributeConverter<Language, String> {
    override fun convertToDatabaseColumn(attribute: Language?): String? {
        return attribute?.code
    }

    override fun convertToEntityAttribute(dbData: String?): Language? {
        return Language.entries.find { it.code == dbData }
    }
}