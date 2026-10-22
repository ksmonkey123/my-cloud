package ch.awae.mycloud.documents

import ch.awae.mycloud.common.*
import org.springframework.context.annotation.*

@Configuration
class DocumentsModuleConfiguration : ModuleConfiguration(
    databaseSchemaName = "documents",
)
