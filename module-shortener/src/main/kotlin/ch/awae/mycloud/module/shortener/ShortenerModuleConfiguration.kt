package ch.awae.mycloud.module.shortener

import ch.awae.mycloud.common.*
import org.springframework.context.annotation.*

@Configuration
class ShortenerModuleConfiguration : ModuleConfiguration(
    databaseSchemaName = "shortener",
    roles = listOf("shortener"),
)
