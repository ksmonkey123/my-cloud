package ch.awae.mycloud.ytdl

import ch.awae.mycloud.common.*
import ch.awae.mycloud.common.RoleConfig
import org.springframework.context.annotation.*

@Configuration
class YtdlModuleConfiguration : ModuleConfiguration(
    databaseSchemaName = "ytdl",
    roles = listOf(RoleConfig("ytdl")),
)
