package ch.awae.mycloud.features

import ch.awae.mycloud.api.features.*
import ch.awae.mycloud.common.*
import org.aopalliance.aop.*
import org.aopalliance.intercept.*
import org.springframework.aop.*
import org.springframework.aop.support.*
import org.springframework.aop.support.annotation.*
import org.springframework.beans.factory.config.*
import org.springframework.context.annotation.*


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

        private val log = createLogger()

        override fun invoke(invocation: MethodInvocation): Any? {
            val annotation = invocation.method.getAnnotation(FeatureCheck::class.java)

            if (service.isEnabled(annotation.feature)) {
                return invocation.proceed()
            }

            log.warn("invocation of '${invocation.method}' blocked due to disabled feature flag '${annotation.feature}'")

            if (invocation.method.returnType != Void.TYPE || annotation.alwaysThrow) {
                throw UnsupportedOperationException("function disabled by feature flag '${annotation.feature}'")
            }

            return Unit
        }

    }
}