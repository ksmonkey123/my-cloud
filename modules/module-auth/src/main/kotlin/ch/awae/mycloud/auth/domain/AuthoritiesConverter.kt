package ch.awae.mycloud.auth.domain

import com.fasterxml.jackson.module.kotlin.*
import jakarta.persistence.*

@Converter
class AuthoritiesConverter : AttributeConverter<Set<String>, String> {

    private val mapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: Set<String>?): String {
        return mapper.writeValueAsString(attribute ?: emptySet<String>())
    }

    override fun convertToEntityAttribute(dbData: String?): Set<String> {
        return mapper.readValue<Set<String>>(dbData ?: return emptySet())
    }
}