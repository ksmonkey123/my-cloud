package ch.awae.mycloud.features.config

import ch.awae.mycloud.features.FeatureCheck
import ch.awae.mycloud.features.FeatureDisabledException
import ch.awae.mycloud.test.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.context.annotation.*
import org.springframework.stereotype.*
import org.springframework.test.context.*
import org.springframework.test.context.jdbc.*
import kotlin.test.*
import kotlin.test.Test

@ActiveProfiles("advisor-test")
class FeatureCheckAdvisorTest : ModuleTest() {

    @Autowired
    lateinit var service: DummyService

    @Test
    @Sql(scripts = ["/clear_features.sql"])
    fun testVoidCalls() {
        // should work
        service.voidActive("testVoidCalls")
        assertEquals("testVoidCalls", service.lastVoidCall)
        // should not work, should not cause exception
        service.voidInactive()
        // should not work, should cause exception
        assertThrows<FeatureDisabledException> { service.voidException() }
    }

    @Test
    @Sql(scripts = ["/clear_features.sql"])
    fun testStringCalls() {
        // should work
        assertEquals("hello world", service.stringActive("testStringCalls"))
        assertEquals("testStringCalls", service.lastStringCall)
        // should not work, should cause exception
        assertThrows<FeatureDisabledException> { service.stringInactive() }
        // should not work, should cause exception
        assertThrows<FeatureDisabledException> { service.stringException() }
    }

    @Test
    @Sql(scripts = ["/clear_features.sql"])
    fun testIntCalls() {
        // should work
        assertEquals(1, service.intActive("testIntCalls"))
        assertEquals("testIntCalls", service.lastIntCall)
        // should not work, should cause exception
        assertThrows<FeatureDisabledException> { service.intInactive() }
        // should not work, should cause exception
        assertThrows<FeatureDisabledException> { service.intException() }
    }

}

@Profile("advisor-test")
@Component
class DummyService {

    var lastVoidCall: String? = null
    var lastStringCall: String? = null
    var lastIntCall: String? = null

    @FeatureCheck("active")
    fun voidActive(identifier: String) {
        lastVoidCall = identifier
    }

    @FeatureCheck("inactive")
    fun voidInactive() {
        throw AssertionError("should not be called")
    }

    @FeatureCheck("inactive", alwaysThrow = true)
    fun voidException() {
        throw AssertionError("should not be called")
    }

    @FeatureCheck("active")
    fun stringActive(identifier: String): String {
        lastStringCall = identifier
        return "hello world"
    }

    @FeatureCheck("inactive")
    fun stringInactive(): String {
        throw AssertionError("should not be called")
    }

    @FeatureCheck("inactive", alwaysThrow = true)
    fun stringException(): String {
        throw AssertionError("should not be called")
    }

    @FeatureCheck("active")
    fun intActive(identifier: String): Int {
        lastIntCall = identifier
        return 1
    }

    @FeatureCheck("inactive", silent = true)
    fun intInactive(): Int {
        throw AssertionError("should not be called")
    }

    @FeatureCheck("inactive", alwaysThrow = true)
    fun intException(): Int {
        throw AssertionError("should not be called")
    }
}