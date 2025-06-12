package ch.awae.mycloud.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class RestClientConfiguration {

    @Bean
    fun restTemplate() = RestTemplate()

}