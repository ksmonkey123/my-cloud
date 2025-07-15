package ch.awae.mycloud.module.auth.config

import ch.awae.mycloud.api.auth.AuthInfo
import ch.awae.mycloud.common.ModuleConfiguration
import ch.awae.mycloud.common.createLogger
import ch.awae.mycloud.module.auth.domain.AccountRepository
import ch.awae.mycloud.module.auth.domain.Role
import ch.awae.mycloud.module.auth.domain.RoleRepository
import jakarta.transaction.Transactional
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

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
        AuthInfo.impersonate("init") {
            val addedRoles = moduleConfigurations.flatMap { module ->
                if (module.roles.isNotEmpty()) {
                    log.info("initializing ${module.roles.size} role(s) for module ${module.name}")
                    module.roles.mapNotNull { role -> initializeRole(module, role) }
                } else {
                    emptyList()
                }
            }

            if (addedRoles.isNotEmpty()) {
                grantRoles(addedRoles)
            }
        }
    }

    private fun initializeRole(module: ModuleConfiguration, name: String): Role? {
        val role = roleRepository.findByName(name)
        if (role != null) {
            return null
        }
        log.info("initializing role $name for module ${module.name}")
        return roleRepository.save(Role(name, true, "role for module '${module.name}'"))
    }

    private fun grantRoles(addedRoles: List<Role>) {
        val admins = accountRepository.listActiveAdmins()
        if (admins.isEmpty()) {
            return
        }
        log.info("grant ${addedRoles.size} added roles to ${admins.size} active admin user(s)")
        admins.forEach { it.roles.addAll(addedRoles) }
        accountRepository.saveAll(admins)
    }

}
