package ch.awae.mycloud.service.canary.dockerhub.model

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class TagListConverter : AttributeConverter<List<String>, String> {
    override fun convertToDatabaseColumn(attribute: List<String>?): String {
        return attribute?.joinToString(";") ?: ""
    }

    override fun convertToEntityAttribute(dbData: String?): List<String> {
        return dbData?.split(";") ?: emptyList()
    }
}