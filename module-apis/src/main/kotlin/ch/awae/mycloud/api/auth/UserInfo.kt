package ch.awae.mycloud.api.auth

interface UserInfo {
    val username: String
    val email: String?
    val roles: List<String>
    val language: Language
    val enabled: Boolean
}