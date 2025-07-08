package ch.awae.mycloud.common

abstract class ModuleConfiguration(private val dbSchemaName: String? = null) {

    open fun databaseSchemaName(): String? {
        return dbSchemaName
    }

    open fun flywayMigrationPath(): String? {
        val schemaName = databaseSchemaName() ?: return null
        return "db/$schemaName/migration"
    }
}