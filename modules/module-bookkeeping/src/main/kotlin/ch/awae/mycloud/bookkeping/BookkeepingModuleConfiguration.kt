package ch.awae.mycloud.bookkeping

import ch.awae.mycloud.common.*
import org.springframework.context.annotation.Configuration

@Configuration
class BookkeepingModuleConfiguration : ModuleConfiguration(
    databaseSchemaName = "bookkeeping",
    roles = listOf(RoleConfig("bookkeeping")),
    features = listOf("bookkeeping:frontend"),
)
