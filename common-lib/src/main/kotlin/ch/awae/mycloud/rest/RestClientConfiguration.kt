package ch.awae.mycloud.rest

import org.springframework.beans.factory.annotation.*
import org.springframework.boot.web.client.*
import org.springframework.cloud.client.loadbalancer.*
import org.springframework.context.annotation.*
import org.springframework.http.*
import org.springframework.web.client.*

@Configuration
class RestClientConfiguration {

    @External
    @Bean
    fun externalRestTemplate() = RestTemplate()

    @LoadBalanced
    @Internal
    @Bean
    fun internalRestTemplate() : RestTemplate = RestTemplateBuilder()
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .interceptors(InternalHttpRequestInterceptor())
        .build()
}

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class Internal

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class External
