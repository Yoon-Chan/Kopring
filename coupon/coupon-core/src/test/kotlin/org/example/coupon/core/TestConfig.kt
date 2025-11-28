package org.example.coupon.core

import io.kotest.core.spec.style.AnnotationSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@TestPropertySource(properties = ["spring.config.name=application-core"])
@SpringBootTest(classes = [CouponCoreConfiguration::class])
@Transactional
interface TestConfig {
}