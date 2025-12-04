package ch.awae.mycloud.test.mvc

import org.springframework.boot.autoconfigure.*
import org.springframework.boot.autoconfigure.domain.*
import org.springframework.boot.context.properties.*
import org.springframework.context.annotation.*
import org.springframework.security.config.annotation.method.configuration.*
import org.springframework.security.config.annotation.web.builders.*
import org.springframework.security.config.annotation.web.configuration.*
import org.springframework.security.config.http.*
import org.springframework.security.web.*

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
    ): SecurityFilterChain {
        return http.csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .anonymous { it.disable() }
            .authorizeHttpRequests {
                it.anyRequest().permitAll()
            }
            .httpBasic { it.disable() }
            .logout { it.disable() }
            .build()
    }

}