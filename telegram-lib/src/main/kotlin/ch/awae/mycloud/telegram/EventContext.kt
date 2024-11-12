package ch.awae.mycloud.telegram

import org.telegram.telegrambots.meta.api.objects.*

data class EventContext<U>(val chatId: Long, val messageId: Int, val telegramUser: User, val user: U?)