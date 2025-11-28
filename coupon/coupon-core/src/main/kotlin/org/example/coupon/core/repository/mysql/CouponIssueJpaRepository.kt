package org.example.coupon.core.repository.mysql

import org.example.coupon.core.model.CouponIssue
import org.springframework.data.jpa.repository.JpaRepository

interface CouponIssueJpaRepository: JpaRepository<CouponIssue, Long> {
}