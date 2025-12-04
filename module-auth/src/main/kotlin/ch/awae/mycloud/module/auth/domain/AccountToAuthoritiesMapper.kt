package ch.awae.mycloud.module.auth.domain

object AccountToAuthoritiesMapper {

    fun getAuthorities(account: Account): Set<String> {
        return account.roles
            .flatMap { it.authorities }
            .let { authorities ->
                if (account.admin) {
                    authorities + "admin" + "user"
                } else {
                    authorities + "user"
                }
            }
            .toSet()
    }
}