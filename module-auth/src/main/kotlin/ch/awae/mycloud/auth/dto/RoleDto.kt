package ch.awae.mycloud.auth.dto

import ch.awae.mycloud.auth.domain.Role

data class RoleDto(
    val name: String,
    val description: String?,
) {
    constructor(role: Role) : this(role.name, role.description)
}