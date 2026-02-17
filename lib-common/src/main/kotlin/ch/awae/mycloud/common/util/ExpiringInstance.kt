package ch.awae.mycloud.common.util

import kotlin.time.Duration

/**
 * Represents an instance that expires after a certain duration and provides a way to retrieve its current value.
 *
 * An example usecase for this class would be a webservice client that uses short-lived access tokens.
 * The supplier function could create a new client and generate the access token. Any business use of the service
 * could use the [ExpiringInstance]. This way no business logic needs to concern itself with token expiration. As long
 * as the webservice client is retrieved from the [ExpiringInstance] on every use, the business logic can safely assume
 * that the token is always valid.
 *
 * This class is thread-safe and ensures that only one thread can create a new instance at a time.
 *
 * @param T the type of the instance
 * @param supplier the supplier function to create a new instance
 * @param expiration the duration after which a newly created value expires
 */
class ExpiringInstance<out T : Any>(
    private val expiration: Duration,
    private val supplier: () -> T,
) {

    @Volatile
    private var currentInstance: Instance<T>? = null
    private val lock = Any()

    /**
     * Retrieves the current value for this instance or creates a new value if the previous one expired.
     *
     * @return a valid (non-expired) instance
     */
    operator fun invoke(): T {
        val instance = currentInstance

        return if (instance == null || instance.isExpired()) {
            replaceInstance()
        } else {
            instance.value
        }
    }

    /**
     * Replaces the internal instance with a new one. also returns the newly created instance.
     */
    private fun replaceInstance(): T {
        synchronized(lock) {
            val newValue = supplier()
            currentInstance = Instance(newValue, System.currentTimeMillis() + expiration.inWholeMilliseconds)
            return newValue
        }
    }

    private data class Instance<out T : Any>(val value: T, val expirationTimeMillis: Long) {
        fun isExpired(): Boolean {
            return System.currentTimeMillis() > expirationTimeMillis
        }
    }

}
