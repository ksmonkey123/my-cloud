package ch.awae.mycloud.common.auth

enum class Language(val code: String) {
    GERMAN("de"),
    ENGLISH("en"),

    ;

    companion object {
        fun fromCode(code: String): Language {
            return entries.find { it.code == code } ?: throw IllegalArgumentException("invalid code $code")
        }
    }
}