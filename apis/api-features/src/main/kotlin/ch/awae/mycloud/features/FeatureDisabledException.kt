package ch.awae.mycloud.features

/**
 * Exception thrown upon invocation of a function guarded by a [FeatureCheck] when the corresponding feature flag is
 * disabled.
 */
class FeatureDisabledException(val feature: String, val blockedSignature: String) :
    RuntimeException("function call to '$blockedSignature' disabled by feature flag '$feature'")