package ch.awae.mycloud.test

import jakarta.persistence.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.test.context.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = [ModuleTestApplication::class])
@SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED, errorMode = SqlConfig.ErrorMode.FAIL_ON_ERROR)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test-container", "module-test")
abstract class ModuleTest {

    @Autowired
    lateinit var entityManager: EntityManager

}