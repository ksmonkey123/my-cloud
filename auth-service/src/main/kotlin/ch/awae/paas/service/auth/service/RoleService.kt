package ch.awae.paas.service.auth.service

import ch.awae.paas.service.auth.*
import ch.awae.paas.service.auth.domain.*
import ch.awae.paas.service.auth.dto.*
import ch.awae.paas.service.auth.exception.*
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Transactional
@Service
class RoleService(private val roleRepository: RoleRepository) {
    fun getAllRoles(): List<RoleDto> {
        return roleRepository.findAll().map(::RoleDto)
    }

    fun createRole(name: String, description: String?): RoleDto {
        if (roleRepository.findByName(name) != null) {
            throw ResourceAlreadyExistsException("/roles/$name")
        }

        return RoleDto(roleRepository.save(Role(name, true, description?.takeIf { it.isNotEmpty() })))
    }

    fun editRole(
        name: String,
        description: String?,
        enabled: Boolean?
    ): RoleDto {
        val role = roleRepository.findByName(name) ?: throw ResourceNotFoundException("/roles/$name")

        if (enabled != null) role.enabled = enabled
        if (description != null) {
            role.description = description.ifEmpty { null }
        }

        return RoleDto(role)
    }

    fun deleteRole(role: String) {
        this.roleRepository.deleteByName(role)
    }


}