package ch.awae.mycloud.ytdl

import org.springframework.boot.context.properties.*

@ConfigurationProperties(prefix = "ytdl")
data class YtdlProperties(
    val jobPath: String,
    val dataPath: String,
)