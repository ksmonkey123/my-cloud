package ch.awae.mycloud.module.auth

import ch.awae.mycloud.common.*
import org.springframework.context.annotation.*

@Configuration
class AuthModuleConfiguration : ModuleConfiguration(
    databaseSchemaName = "auth",
)
