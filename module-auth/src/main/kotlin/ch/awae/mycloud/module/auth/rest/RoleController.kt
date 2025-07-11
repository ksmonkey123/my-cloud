package ch.awae.mycloud.module.auth.rest

import ch.awae.mycloud.module.auth.dto.*
import ch.awae.mycloud.module.auth.service.*
import org.springframework.security.access.prepost.*
import org.springframework.web.bind.annotation.*

@RestController
@PreAuthorize("hasAuthority('admin')")
@RequestMapping("/rest/auth/roles")
class RoleController(
    private val svc: RoleService
) {

    @GetMapping("")
    fun listAll(): List<RoleDto> {
        return svc.getAllRoles()
    }

    @PutMapping("/{role}")
    fun create(@PathVariable role: String, @RequestBody request: CreateRoleRequest): RoleDto {
        return svc.createRole(role, request.description)
    }

    @PatchMapping("/{role}")
    fun edit(@PathVariable role: String, @RequestBody request: PathRoleRequest): RoleDto {
        return svc.editRole(role, request.description, request.enabled)
    }

    @DeleteMapping("/{role}")
    fun delete(@PathVariable role: String) {
        svc.deleteRole(role)
    }

    data class CreateRoleRequest(
        val description: String?
    )

    data class PathRoleRequest(
        val enabled: Boolean?,
        val description: String?,
    )

}
