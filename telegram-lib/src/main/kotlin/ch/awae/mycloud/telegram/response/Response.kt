package ch.awae.mycloud.telegram.response

/**
 * Marker Interface for all response Types
 */
sealed interface Response

data class MultiResponse(val responses: List<Response>) : Response {
    constructor(vararg responses: Response) : this(listOf(*responses))
}

/**
 * Simple Text Response
 *
 * @param text the content of the message
 * @param reply controls if the message is sent as a reply
 */
data class MessageResponse(val text: String, val reply: Boolean = false) : Response

data class CallbackOption(val text: String, val data: String)

data class CallbackResponse(
    val text: String,
    val options: List<CallbackOption>,
    val columns: Int = 1,
    val reply: Boolean = false
) : Response