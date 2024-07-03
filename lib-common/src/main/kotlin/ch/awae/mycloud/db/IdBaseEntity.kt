package ch.awae.mycloud.db

import ch.awae.mycloud.*
import jakarta.persistence.*

@MappedSuperclass
abstract class IdBaseEntity : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long = 0

    override fun equals(other: Any?) = equalByField(other, IdBaseEntity::id)

    override fun hashCode() = id.hashCode()

}