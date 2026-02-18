package ch.awae.mycloud.auth

import ch.awae.mycloud.common.TokenGenerator
import ch.awae.mycloud.auth.config.*
import ch.awae.mycloud.auth.domain.*
import ch.awae.mycloud.auth.service.*
import ch.awae.mycloud.test.*
import com.ninjasquad.springmockk.*
import org.springframework.beans.factory.annotation.*
import org.springframework.security.crypto.password.*

class AuthModuleTest : ModuleTest() {

    @MockkBean
    lateinit var tokenGenerator: TokenGenerator

    @Autowired
    lateinit var initialUserCreator: InitialUserCreator

    @Autowired
    lateinit var authenticationService: AuthenticationService

    @Autowired
    lateinit var securityService: SecurityService

    @Autowired
    lateinit var authTokenRepository: AuthTokenRepository

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

}