package ch.awae.mycloud.canary

import ch.awae.mycloud.common.ModuleConfiguration
import org.springframework.context.annotation.Configuration

@Configuration
class CanaryModuleConfiguration : ModuleConfiguration(
    databaseSchemaName = "canary",
)
