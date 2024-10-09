package ch.awae.mycloud

import kotlin.time.*

class ExpiringInstance<out T : Any>(
    private val expiration: Duration,
    private val supplier: () -> T,
) {

    @Volatile
    private var currentInstance: Instance<T>? = null
    private val lock = Any()

    val instance: T
        get() {
            synchronized(lock) {
                val instance = currentInstance
                if (instance != null && !instance.isExpired()) {
                    return instance.value
                }
                val newInstance = supplier()
                currentInstance = Instance(newInstance, System.currentTimeMillis() + expiration.inWholeMilliseconds)
                return newInstance
            }
        }

}

private class Instance<out T : Any>(
    val value: T,
    val expirationTimeMillis: Long,
) {

    fun isExpired(): Boolean {
        return expirationTimeMillis < System.currentTimeMillis()
    }

}