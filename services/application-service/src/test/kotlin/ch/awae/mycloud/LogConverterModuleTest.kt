package ch.awae.mycloud

import ch.qos.logback.classic.spi.ILoggingEvent
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class LogConverterModuleTest {

    private val converter = LogConverterModule()

    private fun mockEvent(loggerName: String): ILoggingEvent {
        val event = mockk<ILoggingEvent>()
        every { event.loggerName } returns loggerName
        return event
    }

    @ParameterizedTest
    @CsvSource(
        "org.flywaydb.core.Flyway, flyway",
        "org.flywaydb.core.internal.command.DbMigrate, flyway",
        "org.flywaydb.something, flyway"
    )
    fun testFlywayLoggers(loggerName: String, expected: String) {
        val event = mockEvent(loggerName)
        assertEquals(expected, converter.convert(event))
    }

    @ParameterizedTest
    @CsvSource(
        "org.springframework.boot.SpringApplication, spring",
        "org.springframework.web.servlet.DispatcherServlet, spring",
        "org.springframework.security.web.FilterChainProxy, spring"
    )
    fun testSpringLoggers(loggerName: String, expected: String) {
        val event = mockEvent(loggerName)
        assertEquals(expected, converter.convert(event))
    }

    @ParameterizedTest
    @CsvSource(
        "ch.awae.mycloud.auth.service.AuthenticationService, auth",
        "ch.awae.mycloud.documents.DocumentStoreImpl, documents",
        "ch.awae.mycloud.features.model.FeatureFlag, features",
        "ch.awae.mycloud.email.outbox.EmailSendService, email",
        "ch.awae.mycloud.bookkeeping.model.Account, bookkeeping",
        "ch.awae.mycloud.submodule.deep.package.ClassName, submodule"
    )
    fun testInternalSubmoduleLoggers(loggerName: String, expected: String) {
        val event = mockEvent(loggerName)
        assertEquals(expected, converter.convert(event))
    }

    @ParameterizedTest
    @CsvSource(
        "ch.awae.mycloud.ApplicationServiceApplication, application",
        "ch.awae.mycloud.LogConverterModule, application"
    )
    fun testInternalRootClassLoggers(loggerName: String, expected: String) {
        val event = mockEvent(loggerName)
        assertEquals(expected, converter.convert(event))
    }

    @ParameterizedTest
    @CsvSource(
        "com.zaxxer.hikari.HikariDataSource, application",
        "java.lang.Object, application",
        "org.apache.catalina.core.StandardService, application",
        "org.flywaydb, application",
        "org.springframework, application",
        "ch.awae.mycloud, application",
        "'', application"
    )
    fun testDefaultFallbackLoggers(loggerName: String, expected: String) {
        val event = mockEvent(loggerName)
        assertEquals(expected, converter.convert(event))
    }
}
