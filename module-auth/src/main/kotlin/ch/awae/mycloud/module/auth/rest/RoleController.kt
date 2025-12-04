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

}
