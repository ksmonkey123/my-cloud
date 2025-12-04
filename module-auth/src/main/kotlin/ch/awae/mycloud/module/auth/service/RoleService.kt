package ch.awae.mycloud.module.auth.service

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
}