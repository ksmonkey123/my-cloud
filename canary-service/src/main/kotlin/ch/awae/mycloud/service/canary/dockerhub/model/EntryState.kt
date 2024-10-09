package ch.awae.mycloud.service.canary.dockerhub.model

import ch.awae.mycloud.db.*
import jakarta.persistence.*
import org.springframework.data.jpa.repository.*

@Entity
@Table(name = "docker_entry_state")
class EntryState(
    @ManyToOne
    val monitoredEntry: MonitoredEntry,
    val digest: String,
    @Convert(converter = TagListConverter::class)
    val tags: List<String>,
) : IdBaseEntity()

@Converter
class TagListConverter : AttributeConverter<List<String>, String> {
    override fun convertToDatabaseColumn(attribute: List<String>?): String {
        return attribute?.joinToString(";") ?: ""
    }

    override fun convertToEntityAttribute(dbData: String?): List<String> {
        return dbData?.split(";") ?: emptyList()
    }
}

interface EntryStateRepository : JpaRepository<EntryState, Long>
