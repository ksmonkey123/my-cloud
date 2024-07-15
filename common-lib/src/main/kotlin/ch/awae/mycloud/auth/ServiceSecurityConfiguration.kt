package ch.awae.mycloud.auth

import ch.awae.mycloud.audit.*
import ch.awae.mycloud.rest.*
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
@Import(
    HttpAuthorizationTokenFilter::class,
    HttpAuthorizationTokenEntryPoint::class,
    RestClientConfiguration::class,
    TraceInformationRequestFilter::class,
    GlobalExceptionHandler::class
)
@ConfigurationPropertiesScan(basePackages = ["ch.awae.mycloud.auth"])
class ServiceSecurityConfiguration {

    @Bean
    fun authManager(): AuthenticationManager = AuthenticationManager { it }

    @Bean
    fun filterChain(
        http: HttpSecurity,
        filter: HttpAuthorizationTokenFilter,
        traceInformationRequestFilter: TraceInformationRequestFilter,
        entryPoint: HttpAuthorizationTokenEntryPoint,
        securityProperties: SecurityProperties,
    ): SecurityFilterChain {
        return http.csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .anonymous { it.disable() }
            .authorizeHttpRequests {
                val whitelist = securityProperties.publicEndpoints

                if (whitelist.isNullOrEmpty()) {
                    it.requestMatchers("/error", "/actuator/**").permitAll()
                        .anyRequest().authenticated()
                } else {
                    it.requestMatchers("/error", "/actuator/**", *whitelist.toTypedArray()).permitAll()
                        .anyRequest().authenticated()
                }
            }
            .exceptionHandling {
                it.authenticationEntryPoint(entryPoint)
            }
            .httpBasic { it.disable() }
            .logout { it.disable() }
            .addFilterBefore(filter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(traceInformationRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

}
