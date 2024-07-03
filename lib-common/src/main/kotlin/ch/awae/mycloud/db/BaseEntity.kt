package ch.awae.mycloud.db

import ch.awae.mycloud.auth.*
import jakarta.persistence.*
import org.slf4j.*
import java.time.*

private val log = LoggerFactory.getLogger(BaseEntity::class.java)

@MappedSuperclass
abstract class BaseEntity {

    @Version
    val version: Int = 0

    @Column(name = "cre_user", updatable = false)
    private var _creationUser: String? = null

    @Column(name = "mut_user")
    private var _mutationUser: String? = null

    @Column(name = "cre_time", updatable = false)
    private var _creationTimestamp: LocalDateTime? = null

    @Column(name = "mut_time")
    private var _mutationTimestamp: LocalDateTime? = null

    val creationUser: String
        get() = _creationUser!!

    val mutationUser: String
        get() = _mutationUser!!

    val creationTimestamp: LocalDateTime
        get() = _creationTimestamp!!

    val mutationTimestamp: LocalDateTime
        get() = _mutationTimestamp!!

    @PreUpdate
    @PrePersist
    open fun prePersist() {
        updateManagementFields()
    }

    private fun updateManagementFields() {
        val user = AuthInfo.username ?: "dummy".also {
            log.warn("persisting ${this::class.simpleName} without auth context")
        }
        val time = LocalDateTime.now()

        if (_creationTimestamp == null) {
            // on create
            _creationUser = user
            _creationTimestamp = time
        }
        // on update
        _mutationUser = user
        _mutationTimestamp = time
    }

}