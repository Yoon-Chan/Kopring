package org.example.coupon.core.model

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "coupon_issue")
class CouponIssue(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(nullable = false)
    var couponId: Long,

    @Column(nullable = false)
    var userId: Long,

    @Column(nullable = false)
    var dateIssued: LocalDateTime,

    var dateUsed: LocalDateTime?
) : BaseTimeEntity()