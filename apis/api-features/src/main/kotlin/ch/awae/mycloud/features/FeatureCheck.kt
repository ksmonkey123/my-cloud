package ch.awae.mycloud.features

/**
 * Adds feature flag validation to functions.
 *
 * Internally, [FeatureFlagService.isEnabled] is used to determine the flag state.
 *
 * If the declared feature flag is active, the function is invoked normally.
 *
 * If the declared feature flag is inactive, the function call is blocked.
 * For functions with a non-[Unit] / void return type, this always causes an [FeatureDisabledException] exception
 * to be thrown. For functions returning [Unit] / void, the function call will be silently dropped, unless [alwaysThrow]
 * is `true`
 *
 * @param feature The feature id to check. Must comply with the restrictions present on [FeatureFlagService.isEnabled]!
 * @param alwaysThrow default `false`. If `true`, a function returning [Unit] will throw an [FeatureDisabledException]
 *                    if the flag is disabled. Ignored for functions returning anything but [Unit].
 * @param silent default `false`. If `true`, the log entry written upon blocking the function call uses level DEBUG.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class FeatureCheck(val feature: String, val alwaysThrow: Boolean = false, val silent: Boolean = false)