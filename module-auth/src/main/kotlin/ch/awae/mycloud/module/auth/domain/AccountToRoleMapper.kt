package ch.awae.mycloud.module.auth.domain

object AccountToRoleMapper {

    fun getRoles(account: Account, includeUserRole: Boolean = false): List<String> {
        return account.roles
            .filter { it.enabled }
            .map { it.name }
            .let { roles ->
                if (account.admin) {
                    roles + "admin"
                } else {
                    roles
                }
            }
            .let { roles ->
                if (includeUserRole) {
                    roles + "user"
                } else {
                    roles
                }
            }
    }
}