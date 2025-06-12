package ch.awae.mycloud

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanNameGenerator
import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.boot.context.properties.*
import org.springframework.data.jpa.repository.config.*
import org.springframework.scheduling.annotation.*
import org.springframework.transaction.annotation.*

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@EnableJpaRepositories(nameGenerator = JpaRepositoryBeanNameGenerator::class)
@ConfigurationPropertiesScan
class CoreServiceApplication

fun main(args: Array<String>) {
    SpringApplication.run(CoreServiceApplication::class.java, *args)
}

class JpaRepositoryBeanNameGenerator: BeanNameGenerator {
    override fun generateBeanName(definition: BeanDefinition, registry: BeanDefinitionRegistry): String {
        return definition.beanClassName ?: throw IllegalArgumentException("JPA Bean without class name: $definition")
    }
}