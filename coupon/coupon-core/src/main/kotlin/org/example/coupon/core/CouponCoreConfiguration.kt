package org.example.coupon.core

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@ComponentScan
@EnableAutoConfiguration
@EnableJpaAuditing
class CouponCoreConfiguration {
}