package ch.awae.mycloud.module.auth.service

import ch.awae.mycloud.common.ResourceAlreadyExistsException
import ch.awae.mycloud.common.ResourceNotFoundException
import ch.awae.mycloud.module.auth.domain.*
import ch.awae.mycloud.module.auth.dto.*
import jakarta.transaction.*
import org.springframework.stereotype.*

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