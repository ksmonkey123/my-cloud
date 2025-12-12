package ch.awae.mycloud.test

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@Profile("module-test")
@SpringBootApplication(scanBasePackages = ["ch.awae.mycloud"])
@EnableJpaRepositories(basePackages = ["ch.awae.mycloud"])
@ConfigurationPropertiesScan(basePackages = ["ch.awae.mycloud"])
@EntityScan(basePackages = ["ch.awae.mycloud"])
@EnableWebSecurity
@EnableMethodSecurity
class ModuleTestApplication {
}