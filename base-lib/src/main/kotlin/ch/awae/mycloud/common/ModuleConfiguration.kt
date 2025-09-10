package ch.awae.mycloud.common

abstract class ModuleConfiguration(
    name: String? = null,
    val databaseSchemaName: String? = null,
    val roles: List<RoleConfig> = emptyList(),
) {

    val name = name ?: databaseSchemaName ?: this.toString()
    val flywayMigrationPath: String? = databaseSchemaName?.let { "db/$it/migration" }

}

data class RoleConfig(
    val name: String,
    val authorities: List<String> = listOf(name),
)