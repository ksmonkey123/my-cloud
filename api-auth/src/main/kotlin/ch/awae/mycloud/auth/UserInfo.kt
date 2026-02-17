package ch.awae.mycloud.auth

interface UserInfo {
    val username: String
    val email: String?
    val language: Language
    val enabled: Boolean
}