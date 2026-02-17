package ch.awae.mycloud

import org.junit.jupiter.api.*
import org.springframework.boot.test.context.*
import org.springframework.test.context.*
import kotlin.test.Test

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [ApplicationServiceApplication::class]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("local", "test-container", "test")
class ApplicationServiceApplicationTest {

    @Test
    fun contextLoads() {
    }

}