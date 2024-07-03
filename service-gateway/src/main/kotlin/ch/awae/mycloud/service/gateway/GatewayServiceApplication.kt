package ch.awae.mycloud.service.gateway

import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.cloud.client.discovery.*

@SpringBootApplication
@EnableDiscoveryClient
class GatewayServiceApplication

fun main(args: Array<String>) {
    SpringApplication.run(GatewayServiceApplication::class.java, *args)
}
