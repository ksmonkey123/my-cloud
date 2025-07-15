package ch.awae.mycloud.module.bookkeping

import ch.awae.mycloud.common.ModuleConfiguration
import org.springframework.context.annotation.Configuration

@Configuration
class BookkeepingModuleConfiguration : ModuleConfiguration(
    databaseSchemaName = "bookkeeping",
    roles = listOf("bookkeeping"),
)
