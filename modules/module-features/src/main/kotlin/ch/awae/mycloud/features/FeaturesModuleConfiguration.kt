package ch.awae.mycloud.features

import ch.awae.mycloud.common.*
import org.springframework.context.annotation.*

@Configuration
class FeaturesModuleConfiguration : ModuleConfiguration(
    databaseSchemaName = "features",
)
