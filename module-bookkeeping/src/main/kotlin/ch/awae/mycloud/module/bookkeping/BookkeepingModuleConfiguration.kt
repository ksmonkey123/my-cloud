package ch.awae.mycloud.module.bookkeping

import ch.awae.mycloud.common.ModuleConfiguration
import org.springframework.context.annotation.Configuration

@Configuration
class BookkeepingModuleConfiguration : ModuleConfiguration() {

    override fun databaseSchemaName(): String? {
        return "bookkeeping"
    }

}