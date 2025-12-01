package ch.awae.mycloud.api.features

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class FeatureCheck(val feature: String, val alwaysThrow: Boolean = false)