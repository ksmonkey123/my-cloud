package ch.awae.mycloud.module.canary

import ch.awae.mycloud.common.ModuleConfiguration
import org.springframework.context.annotation.Configuration

@Configuration
class CanaryModuleConfiguration : ModuleConfiguration() {

    override fun databaseSchemaName(): String? {
        return "canary"
    }

}