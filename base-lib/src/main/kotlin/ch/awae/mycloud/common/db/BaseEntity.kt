package ch.awae.mycloud.common.db

import jakarta.persistence.*

@MappedSuperclass
abstract class BaseEntity {

    @Version
    val version: Int = 0

    @PreUpdate
    open fun preUpdate() {
        validate()
    }

    @PrePersist
    open fun prePersist() {
        validate()
    }

    open fun validate() {
        // by default: no validation
    }

}