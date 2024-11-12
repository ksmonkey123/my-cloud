package ch.awae.mycloud.telegram

import ch.awae.mycloud.telegram.response.*
import org.slf4j.*
import org.telegram.telegrambots.client.okhttp.*
import org.telegram.telegrambots.longpolling.interfaces.*
import org.telegram.telegrambots.longpolling.starter.*
import org.telegram.telegrambots.meta.api.methods.botapimethods.*
import org.telegram.telegrambots.meta.api.methods.send.*
import org.telegram.telegrambots.meta.api.methods.updatingmessages.*
import org.telegram.telegrambots.meta.api.objects.*
import org.telegram.telegrambots.meta.api.objects.replykeyboard.*
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.*
import java.io.*
import java.util.concurrent.*

abstract class TelegramBot<U : Any>(
    private val token: String,
    private val executor: Executor = Executors.newSingleThreadExecutor(),
) : SpringLongPollingBot, LongPollingUpdateConsumer {

    private val logger = LoggerFactory.getLogger(TelegramBot::class.java)
    private val client = OkHttpTelegramClient(token)

    final override fun getBotToken() = token
    final override fun getUpdatesConsumer() = this

    fun <T : Serializable, M : BotApiMethod<T>> execute(method: M): T {
        return client.execute(method)
    }

    fun execute(response: Response?, context: EventContext<U>) {
        when (response) {
            null -> return
            is MultiResponse -> response.responses.forEach { execute(it, context) }
            is MessageResponse -> execute(SendMessage(context.chatId.toString(), response.text).apply {
                if (response.reply) {
                    this.replyToMessageId = context.messageId
                }
            })

            is CallbackResponse -> execute(
                SendMessage(context.chatId.toString(), response.text).apply {
                    if (response.reply) {
                        this.replyToMessageId = context.messageId
                    }
                    this.replyMarkup = InlineKeyboardMarkup(
                        response.options.chunked(response.columns).map { col ->
                            InlineKeyboardRow(col.map {
                                InlineKeyboardButton(it.text).apply {
                                    callbackData = it.data
                                }
                            })
                        }
                    )
                }
            )
        }
    }

    abstract fun resolveTelegramUserId(userId: Long): U?

    @AfterBotRegistration
    fun afterRegistration() {
        logger.info("Telegram Bot ${this.javaClass.simpleName} successfully registered")
    }

    final override fun consume(updates: MutableList<Update>) {
        for (update in updates) {
            executor.execute { handleUpdate(update) }
        }
    }

    private fun handleUpdate(update: Update) {
        val context = buildEventContext(update) ?: return

        update.message?.let {
            execute(onMessage(it.text, context), context)
        }

        update.callbackQuery?.let {
            // remove keyboard
            execute(
                EditMessageReplyMarkup(
                    it.message.chatId.toString(),
                    it.message.messageId,
                    null, null, null
                )
            )
            execute(onCallbackQuery(it.data, context), context)
        }
    }

    private fun buildEventContext(update: Update): EventContext<U>? {
        val message = update.message ?: update.callbackQuery?.message
        val user = update.message?.from ?: update.callbackQuery?.from

        if (message != null && user != null) {
            return EventContext(message.chatId, message.messageId, user, resolveTelegramUserId(user.id))
        }
        return null
    }

    open fun onMessage(text: String, context: EventContext<U>): Response? {
        return null
    }

    open fun onCallbackQuery(data: String, context: EventContext<U>): Response? {
        return null
    }

}

