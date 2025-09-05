package ch.awae.mycloud

import ch.awae.mycloud.common.*
import org.flywaydb.core.*
import org.springframework.boot.autoconfigure.flyway.*
import org.springframework.stereotype.*

@Component
class MultiModuleFlywayStrategy(
    val moduleConfigurations: List<ModuleConfiguration>,
) : FlywayMigrationStrategy {

    val logger = createLogger()

    override fun migrate(flyway: Flyway) {
        val dataSource = flyway.configuration.dataSource

        logger.info("migrating core schema")
        flyway.migrate()

        for (module in moduleConfigurations) {
            val schema = module.databaseSchemaName ?: continue
            val location = module.flywayMigrationPath
                ?: throw IllegalArgumentException("flyway schema location cannot be null if a database module is provided. processing $module")

            logger.info("migrating schema $schema from $location for module ${module.name}")
            Flyway.configure()
                .schemas(schema)
                .locations(location)
                .dataSource(dataSource)
                .load()
                .migrate()
        }
        logger.info("flyway migrations completed")
    }

}