package ch.awae.mycloud

import org.springframework.beans.factory.config.*
import org.springframework.beans.factory.support.*
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
class ApplicationServiceApplication

fun main(args: Array<String>) {
    SpringApplication.run(ApplicationServiceApplication::class.java, *args)
}

class JpaRepositoryBeanNameGenerator : BeanNameGenerator {
    override fun generateBeanName(definition: BeanDefinition, registry: BeanDefinitionRegistry): String {
        return definition.beanClassName ?: throw IllegalArgumentException("JPA Bean without class name: $definition")
    }
}