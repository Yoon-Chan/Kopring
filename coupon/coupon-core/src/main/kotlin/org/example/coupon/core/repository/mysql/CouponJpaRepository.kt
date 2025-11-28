package org.example.coupon.core.repository.mysql

import org.example.coupon.core.model.Coupon
import org.springframework.data.jpa.repository.JpaRepository

interface CouponJpaRepository: JpaRepository<Coupon, Long> {
}