package ch.awae.mycloud.module.docker

import ch.awae.mycloud.common.*
import org.springframework.context.annotation.Configuration

@Configuration
class DockerModuleConfiguration : ModuleConfiguration(
    databaseSchemaName = "docker",
    roles = listOf(RoleConfig("docker")),
)
