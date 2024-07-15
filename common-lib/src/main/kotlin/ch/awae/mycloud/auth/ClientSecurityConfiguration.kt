package ch.awae.mycloud.auth

import ch.awae.mycloud.rest.*
import org.springframework.context.annotation.*
import org.springframework.web.client.*

@Configuration
@Import(ServiceSecurityConfiguration::class)
class ClientSecurityConfiguration {

    @Bean
    fun authService(@Internal http : RestTemplate) : AuthService = AuthServiceClient(http)

}