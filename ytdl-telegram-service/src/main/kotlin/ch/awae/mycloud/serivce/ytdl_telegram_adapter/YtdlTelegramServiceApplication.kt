package ch.awae.mycloud.serivce.ytdl_telegram_adapter

import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*

@SpringBootApplication
class YtdlTelegramServiceApplication

fun main(args: Array<String>) {
    SpringApplication.run(YtdlTelegramServiceApplication::class.java, *args)
}