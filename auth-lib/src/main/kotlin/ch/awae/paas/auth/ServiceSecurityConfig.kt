package ch.awae.paas.auth

import org.springframework.boot.context.properties.*
import org.springframework.context.annotation.*
import org.springframework.security.authentication.*
import org.springframework.security.config.annotation.method.configuration.*
import org.springframework.security.config.annotation.web.builders.*
import org.springframework.security.config.annotation.web.configuration.*
import org.springframework.security.config.http.*
import org.springframework.security.web.*
import org.springframework.security.web.authentication.*

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ComponentScan(basePackages = ["ch.awae.paas.auth", "ch.awae.paas.rest"])
@ConfigurationPropertiesScan(basePackages = ["ch.awae.paas.auth"])
class ServiceSecurityConfig {

    @Bean
    fun authManager(): AuthenticationManager = AuthenticationManager { it }

    @Bean
    fun filterChain(
        http: HttpSecurity,
        filter: HttpAuthorizationTokenFilter,
        entryPoint: HttpAuthorizationTokenEntryPoint,
        securityProperties: SecurityProperties,
    ): SecurityFilterChain {
        return http.csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .anonymous { it.disable() }
            .authorizeHttpRequests {
                val whitelist = securityProperties.publicEndpoints
                if (whitelist.isNullOrEmpty()) {
                    it.anyRequest().authenticated()
                } else {
                    it.requestMatchers(*whitelist.toTypedArray()).permitAll()
                        .anyRequest().authenticated()
                }
            }
            .exceptionHandling {
                it.authenticationEntryPoint(entryPoint)
            }
            .httpBasic { it.disable() }
            .logout { it.disable() }
            .addFilterBefore(filter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

}
