package ch.awae.mycloud.module.docker.dockerhub.model

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kotlin.collections.joinToString
import kotlin.text.split

@Converter
class TagListConverter : AttributeConverter<List<String>, String> {
    override fun convertToDatabaseColumn(attribute: List<String>?): String {
        return attribute?.joinToString(";") ?: ""
    }

    override fun convertToEntityAttribute(dbData: String?): List<String> {
        return dbData?.split(";") ?: emptyList()
    }
}