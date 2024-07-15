package ch.awae.mycloud.service.auth.exception

import org.springframework.http.*
import org.springframework.web.server.*

class BadLoginException :
    ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid login credentials")

class InvalidPasswordException :
    ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid password")
