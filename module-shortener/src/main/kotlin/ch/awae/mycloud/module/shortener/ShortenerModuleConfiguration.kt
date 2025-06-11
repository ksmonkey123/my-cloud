package ch.awae.mycloud.module.shortener

import ch.awae.mycloud.common.*
import org.springframework.context.annotation.*

@Configuration
class ShortenerModuleConfiguration : ModuleConfiguration() {

    override fun databaseSchemaName(): String? {
        return "shortener"
    }

}