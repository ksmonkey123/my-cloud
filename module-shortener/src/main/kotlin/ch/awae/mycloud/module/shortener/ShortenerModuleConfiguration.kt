package ch.awae.mycloud.module.shortener

import ch.awae.mycloud.common.*
import ch.awae.mycloud.common.RoleConfig
import org.springframework.context.annotation.*

@Configuration
class ShortenerModuleConfiguration : ModuleConfiguration(
    databaseSchemaName = "shortener",
    roles = listOf(RoleConfig("shortener")),
    features = listOf("shortener:frontend"),
)
