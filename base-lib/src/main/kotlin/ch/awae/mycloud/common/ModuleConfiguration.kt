package ch.awae.mycloud.common

abstract class ModuleConfiguration {

    open fun databaseSchemaName(): String? {
        return null
    }

    open fun flywayMigrationPath(): String? {
        val schemaName = databaseSchemaName() ?: return null
        return "db/$schemaName/migration"
    }
}