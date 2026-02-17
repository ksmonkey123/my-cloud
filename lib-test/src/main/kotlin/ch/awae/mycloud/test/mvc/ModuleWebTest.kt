package ch.awae.mycloud.test.mvc

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc

@ContextConfiguration(classes = [ModuleWebTestApplication::class])
@ActiveProfiles("module-web-test")
abstract class ModuleWebTest {

    @Autowired
    lateinit var mvc: MockMvc

}