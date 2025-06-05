package ch.awae.mycloud.service.auth.config

import org.springframework.context.annotation.*
import org.springframework.security.crypto.bcrypt.*
import org.springframework.security.crypto.password.*

@Configuration
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

}
