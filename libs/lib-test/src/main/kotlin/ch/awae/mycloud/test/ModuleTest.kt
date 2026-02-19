package ch.awae.mycloud.test

import jakarta.persistence.EntityManager
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest(classes = [ModuleTestApplication::class])
@SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED, errorMode = SqlConfig.ErrorMode.FAIL_ON_ERROR)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test-container", "module-test")
abstract class ModuleTest {

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var entityManager: EntityManager

}