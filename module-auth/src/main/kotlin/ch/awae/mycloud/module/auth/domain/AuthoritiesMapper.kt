package ch.awae.mycloud.module.auth.domain

object AuthoritiesMapper {

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

    fun getAuthorities(apiKey: ApiKey): Set<String> {
        return (apiKey.authorities intersect apiKey.owner.roles.flatMap { it.authorities }.toSet()) + "api"
    }
}