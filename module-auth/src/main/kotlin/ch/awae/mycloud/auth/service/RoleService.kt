package ch.awae.mycloud.auth.service

import ch.awae.mycloud.auth.domain.*
import ch.awae.mycloud.auth.dto.*
import jakarta.transaction.*
import org.springframework.stereotype.*

@Transactional
@Service
class RoleService(private val roleRepository: RoleRepository) {
    fun getAllRoles(): List<RoleDto> {
        return roleRepository.findAll().map(::RoleDto)
    }
}