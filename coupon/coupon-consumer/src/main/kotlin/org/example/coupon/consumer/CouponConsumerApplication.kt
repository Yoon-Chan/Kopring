package org.example.coupon.consumer

import org.example.coupon.core.CouponCoreConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@Import(CouponCoreConfiguration::class)
@SpringBootApplication
class CouponConsumerApplication

fun main(args: Array<String>) {
    System.setProperty("spring.config.name", "application-core,application-consumer")
    runApplication<CouponConsumerApplication>(*args)
}
