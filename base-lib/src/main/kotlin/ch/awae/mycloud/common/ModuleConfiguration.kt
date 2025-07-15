package ch.awae.mycloud.common

typealias RoleDescriptor = Pair<String, String>

abstract class ModuleConfiguration(
    val databaseSchemaName: String? = null,
    val roles: List<RoleDescriptor> = emptyList(),
) {

    val flywayMigrationPath: String? = databaseSchemaName?.let { "db/$it/migration" }

}