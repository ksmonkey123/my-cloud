package ch.awae.mycloud.db

import jakarta.persistence.*

@MappedSuperclass
abstract class IdBaseEntity : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GEN_SEQ_GEN")
    @SequenceGenerator(name = "GEN_SEQ_GEN", sequenceName = "HIBERNATE_SEQ", allocationSize = 1)
    val id: Long = 0

    override fun equals(other: Any?) = equalByField(other, IdBaseEntity::id)

    override fun hashCode() = id.hashCode()

}