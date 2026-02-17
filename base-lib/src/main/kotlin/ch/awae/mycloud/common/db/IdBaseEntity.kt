package ch.awae.mycloud.common.db

import ch.awae.mycloud.common.util.equalByField
import jakarta.persistence.*

/**
 * Base class for all JPA database entities using a numerical ID.
 *
 * The numerical ID is generated from the central database sequence `public.HIBERNATE_SEQ`.
 *
 * Includes [equals] and [hashCode] implementations based solely on the [id] field.
 */
@MappedSuperclass
abstract class IdBaseEntity : BaseEntity() {

    /**
     * The numerical ID of the entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GEN_SEQ_GEN")
    @SequenceGenerator(name = "GEN_SEQ_GEN", sequenceName = "HIBERNATE_SEQ", allocationSize = 1)
    val id: Long = 0

    override fun equals(other: Any?) = equalByField(other, IdBaseEntity::id)

    override fun hashCode() = id.hashCode()

}