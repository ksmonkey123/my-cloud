package ch.awae.mycloud.module.docker

import ch.awae.mycloud.common.ModuleConfiguration
import org.springframework.context.annotation.Configuration

@Configuration
class DockerModuleConfiguration : ModuleConfiguration(
    databaseSchemaName = "docker",
    roles = listOf("docker"),
)
