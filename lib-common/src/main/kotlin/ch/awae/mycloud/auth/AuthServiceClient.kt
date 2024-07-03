package ch.awae.mycloud.auth

import com.github.benmanes.caffeine.cache.*
import org.springframework.http.*
import org.springframework.web.client.*
import org.springframework.web.client.HttpClientErrorException.*
import kotlin.time.*
import kotlin.time.Duration.Companion.seconds

class AuthServiceClient(
    private val http: RestTemplate,
) : AuthService {

    private val cache: LoadingCache<String, AuthInfo> = Caffeine.newBuilder()
        .maximumSize(100)
        .expireAfterWrite(30.seconds.toJavaDuration())
        .build { token -> fetchToken(token) }

    override fun authenticateToken(tokenString: String): AuthInfo? {
        return cache.get(tokenString)
    }

    private fun fetchToken(tokenString: String): AuthInfo? {
        val headers = HttpHeaders()
        headers.set("Authorization", tokenString)
        val entity = HttpEntity(null, headers)
        return try {
            http.exchange<AuthInfoDto>(
                "http://auth-service/authenticate",
                HttpMethod.GET,
                entity
            ).body?.let(::convertDto)
        } catch (e: Unauthorized) {
            null
        }
    }

    private fun convertDto(dto: AuthInfoDto): AuthInfo {
        return when (dto.type) {
            AuthInfoDto.AuthType.USER -> UserAuthInfo(dto.username, dto.roles, dto.token)
        }
    }

}
