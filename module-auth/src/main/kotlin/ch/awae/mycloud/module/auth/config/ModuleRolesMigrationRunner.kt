package ch.awae.mycloud.module.auth.config

import ch.awae.mycloud.common.*
import ch.awae.mycloud.module.auth.domain.*
import jakarta.transaction.*
import org.springframework.boot.*
import org.springframework.core.annotation.*
import org.springframework.stereotype.*

@Component
@Order(2)
class ModuleRoleInitializer(
    val moduleConfigurations: List<ModuleConfiguration>,
    val roleRepository: RoleRepository,
    val accountRepository: AccountRepository,
) : CommandLineRunner {

    private val log = createLogger()

    @Transactional
    override fun run(vararg args: String?) {
        val requiredRoles = moduleConfigurations.flatMap { it.roles }.associateBy { it.name }
        val existingRoles = roleRepository.findAll().associateBy { it.name }

        val allNames = requiredRoles.keys + existingRoles.keys

        val joinedLists = allNames.map { name ->
            Pair(requiredRoles[name], existingRoles[name])
        }

        val addedRoles = mutableListOf<Role>()

        for ((required, existing) in joinedLists) {
            if (required != null && existing == null) {
                addedRoles += addRole(required)
            } else if (required != null && existing != null) {
                updateRole(existing, required)
            } else if (existing != null) {
                roleRepository.delete(existing)
            }
        }

        if (addedRoles.isNotEmpty()) {
            grantRoles(addedRoles)
        }
    }

    private fun addRole(roleConfig: RoleConfig): Role {
        return roleRepository.save(Role(roleConfig.name, true, roleConfig.authorities.toSet()))
    }

    private fun updateRole(role: Role, config: RoleConfig) {
        val requiredAuthorities = config.authorities.toSet()

        if (role.authorities != requiredAuthorities) {
            role.authorities = requiredAuthorities
        }

    }

    private fun grantRoles(addedRoles: List<Role>) {
        val admins = accountRepository.listActiveAdmins()
        if (admins.isEmpty()) {
            return
        }
        log.info("grant ${addedRoles.size} added roles to ${admins.size} active admin user(s)")
        admins.forEach { it.roles.addAll(addedRoles) }
        accountRepository.saveAllAndFlush(admins)
    }
}
