package ch.awae.mycloud.common

import org.springframework.http.*
import org.springframework.web.server.*

class ResourceAlreadyExistsException(resource: String) :
    ResponseStatusException(HttpStatus.BAD_REQUEST, "resource already exists: $resource")

class ResourceNotFoundException(resource: String) :
    ResponseStatusException(HttpStatus.NOT_FOUND, "resource not found: $resource")

class InvalidRequestException(reason: String) :
    ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request: $reason")