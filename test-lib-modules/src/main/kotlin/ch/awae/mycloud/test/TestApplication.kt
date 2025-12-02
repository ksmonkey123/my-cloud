package ch.awae.mycloud.test

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@SpringBootApplication(scanBasePackages = ["ch.awae.mycloud"])
@EnableJpaRepositories(basePackages = ["ch.awae.mycloud"])
@ConfigurationPropertiesScan(basePackages = ["ch.awae.mycloud"])
@EntityScan(basePackages = ["ch.awae.mycloud"])
@EnableWebSecurity
@EnableMethodSecurity
class TestApplication {
}