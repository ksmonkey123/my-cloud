package ch.awae.mycloud.service.email

import com.mailjet.client.*
import org.springframework.boot.context.properties.*
import org.springframework.context.annotation.*

@ConfigurationProperties(prefix = "mailjet")
data class MailjetProperties(
    val apiKeyPublic: String,
    val apiKeySecret: String,
)

@Configuration
class MailjetClientConfiguration {

    @Bean
    fun mailjetClient(mailjetProperties: MailjetProperties): com.mailjet.client.MailjetClient {
        return MailjetClient(
            ClientOptions.builder()
                .apiKey(mailjetProperties.apiKeyPublic)
                .apiSecretKey(mailjetProperties.apiKeySecret)
                .build()
        )
    }

}
