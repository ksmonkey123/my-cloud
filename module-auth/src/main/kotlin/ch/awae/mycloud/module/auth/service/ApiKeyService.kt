package ch.awae.mycloud.module.auth.service

import ch.awae.mycloud.api.common.*
import ch.awae.mycloud.common.*
import ch.awae.mycloud.module.auth.domain.*
import ch.awae.mycloud.module.auth.dto.*
import jakarta.transaction.*
import org.springframework.stereotype.*

@Service
@Transactional
class ApiKeyService(
    private val accountService: AccountService,
    private val apiKeyRepository: ApiKeyRepository,
    private val tokenGenerator: TokenGenerator,
) {

    fun listKeys(username: String): List<ApiKeyDto> {
        return apiKeyRepository.listByOwnerUsername(username).map { ApiKeyDto.of(it) }
    }

    fun delete(username: String, name: String) {
        val key = apiKeyRepository.findByOwnerAndName(username, name)
            ?: throw ResourceNotFoundException("/auth/api_key/$name")
        apiKeyRepository.delete(key)
    }

    fun create(username: String, name: String, roles: List<String>): Pair<ApiKeyDto, String> {
        if (apiKeyRepository.findByOwnerAndName(username, name) != null) {
            throw ResourceAlreadyExistsException("/auth/api_key/$name")
        }

        val saved = apiKeyRepository.save(
            ApiKey(
                name = name,
                tokenString = tokenGenerator.generate(64, TokenGenerator.EncoderType.URL),
                owner = accountService.getAccount(username),
                authorities = roles.toSet(),
            )
        )

        return Pair(ApiKeyDto.of(saved), saved.tokenString)
    }

}
