package ch.awae.mycloud.features

import ch.awae.mycloud.api.features.*
import org.aopalliance.aop.*
import org.aopalliance.intercept.*
import org.springframework.aop.*
import org.springframework.aop.support.*
import org.springframework.aop.support.annotation.*
import org.springframework.beans.factory.config.*
import org.springframework.context.annotation.*
import java.util.*


@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
class FeatureCheckAdvisor(@Lazy featureFlagService: FeatureFlagService) : AbstractPointcutAdvisor() {

    private val pointcut = ComposablePointcut(
        AnnotationMatchingPointcut(
            null, FeatureCheck::class.java, true
        )
    )

    private val advice = FeatureCheckInterceptor(featureFlagService)

    override fun getPointcut(): Pointcut = pointcut

    override fun getAdvice(): Advice = advice

    private class FeatureCheckInterceptor(private val service: FeatureFlagService) : MethodInterceptor {
        override fun invoke(invocation: MethodInvocation): Any? {
            val annotation = invocation.method.getAnnotation(FeatureCheck::class.java)

            if (service.isEnabled(annotation.feature)) {
                return invocation.proceed()
            }

            when (annotation.policy) {
                FeatureCheck.BlockingPolicy.EXCEPTION -> throwException(annotation.feature)
                FeatureCheck.BlockingPolicy.RETURN -> return calculateReturnValueForCall(
                    invocation.method.returnType,
                    annotation.feature
                )
            }
        }


        private fun calculateReturnValueForCall(type: Class<*>, feature: String): Any? {
            return when (type) {
                Void.TYPE -> Unit
                Optional::class.java -> Optional.empty<Any>()
                Result::class.java -> runCatching { throwException(feature) }
                else -> null
            }
        }

        private fun throwException(feature: String): Nothing {
            throw UnsupportedOperationException("function disabled by feature flag '${feature}'")
        }
    }
}