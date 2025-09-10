package ch.awae.mycloud.module.auth.rest

import ch.awae.mycloud.api.auth.*
import ch.awae.mycloud.module.auth.dto.*
import ch.awae.mycloud.module.auth.service.*
import jakarta.validation.*
import org.springframework.http.*
import org.springframework.security.access.prepost.*
import org.springframework.web.bind.annotation.*

@PreAuthorize("hasAuthority('user')")
@RequestMapping("/rest/auth/api_keys")
@RestController
class ApiKeyController(
    private val apiKeyService: ApiKeyService,
) {

    @GetMapping
    fun listAll(): List<ApiKeyDto> {
        return apiKeyService.listKeys(AuthInfo.username)
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable name: String) {
        apiKeyService.delete(AuthInfo.username, name)
    }

    @PutMapping("/{name}")
    fun create(@PathVariable name: String, @RequestBody @Valid request: CreateApiKeyRequest): CreatedApiKeyResponse {
        val (key, tokenString) = apiKeyService.create(AuthInfo.username, name, request.roles)
        return CreatedApiKeyResponse(key, tokenString)
    }

}

data class CreateApiKeyRequest(
    val roles: List<String>,
)

data class CreatedApiKeyResponse(
    val key: ApiKeyDto,
    val tokenString: String,
)