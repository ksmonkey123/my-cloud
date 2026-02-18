package ch.awae.mycloud.auth

import ch.awae.mycloud.common.*
import org.springframework.context.annotation.*

@Configuration
class AuthModuleConfiguration : ModuleConfiguration(
    databaseSchemaName = "auth",
)
