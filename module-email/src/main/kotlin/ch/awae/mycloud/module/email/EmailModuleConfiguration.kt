package ch.awae.mycloud.module.email

import ch.awae.mycloud.common.*
import org.springframework.context.annotation.*

@Configuration
class EmailModuleConfiguration : ModuleConfiguration(
    databaseSchemaName = "email",
)
