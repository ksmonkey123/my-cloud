package ch.awae.mycloud.common.db

import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Version

/**
 * Base class for all JPA Database Entities.
 *
 * Includes a Version field for optimistic locking and pre-update and pre-persist validation hooks.
 */
@MappedSuperclass
abstract class BaseEntity {

    /**
     * The JPA Version field used for optimistic locking.
     */
    @Version
    val version: Int = 0

    /**
     * Pre-Update Hook. Executed when updating an existing entity.
     *
     * The default implementation calls [validate].
     * It is recommended to include a `super`-call when overriding the default implementation.
     */
    @PreUpdate
    open fun preUpdate() {
        validate()
    }

    /**
     * Pre-Persist Hook. Executed when inserting a new entity.
     *
     * The default implementation calls [validate].
     * It is recommended to include a `super`-call when overriding the default implementation.
     */
    @PrePersist
    open fun prePersist() {
        validate()
    }

    /**
     * Validation function used by [prePersist] and [preUpdate].
     *
     * The default implementation does nothing.
     * This function can be overridden to add invariants that should be validated before persisting an entity.
     */
    open fun validate() {
        // by default: no validation
    }

}