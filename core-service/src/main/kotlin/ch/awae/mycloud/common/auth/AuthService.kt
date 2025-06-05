package ch.awae.mycloud.common.auth

interface AuthService {
    fun authenticateToken(tokenString: String): AuthInfo?
}