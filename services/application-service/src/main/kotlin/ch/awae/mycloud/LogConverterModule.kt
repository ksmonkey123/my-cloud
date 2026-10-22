package ch.awae.mycloud

import ch.qos.logback.classic.pattern.ClassicConverter
import ch.qos.logback.classic.spi.ILoggingEvent

class LogConverterModule : ClassicConverter() {

    override fun convert(event: ILoggingEvent): String {
        val loggerName = event.loggerName

        if (loggerName.startsWith("org.flywaydb.")) {
            return "flyway"
        }

        if (loggerName.startsWith("org.springframework.")) {
            return "spring"
        }

        if (loggerName.startsWith("ch.awae.mycloud.")) {
            val trailing = loggerName.removePrefix("ch.awae.mycloud.")
            if (trailing.contains('.')) {
                return trailing.substringBefore('.')
            }
        }

        return "application"
    }
}