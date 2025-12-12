package ch.awae.mycloud.test.mvc

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.filter.OncePerRequestFilter

@Profile("module-web-test")
@SpringBootApplication(scanBasePackages = ["ch.awae.mycloud"])
@ConfigurationPropertiesScan(basePackages = ["ch.awae.mycloud"])
@EntityScan(basePackages = ["ch.awae.mycloud"])
@EnableWebSecurity
@EnableMethodSecurity
class ModuleWebTestApplication {

    @Bean
    fun filterChain(
        http: HttpSecurity,
        dummyTokenFilter: OncePerRequestFilter,
    ): SecurityFilterChain {
        return http.csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            //.anonymous { it.disable() }
            .authorizeHttpRequests {
                it.anyRequest().permitAll()
            }
            .httpBasic { it.disable() }
            .logout { it.disable() }
            .addFilterBefore(dummyTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

    @Bean
    fun dummyTokenFilter(): OncePerRequestFilter {
        return object : OncePerRequestFilter() {
            override fun doFilterInternal(
                request: HttpServletRequest,
                response: HttpServletResponse,
                filterChain: FilterChain
            ) {
                val context = SecurityContextHolder.getContext()

                return filterChain.doFilter(request, response)
            }

        }
    }

}