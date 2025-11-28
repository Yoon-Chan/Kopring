package org.example.coupon.core.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "coupon_issue")
@Entity
class CouponIssue(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var couponId: Long,

    @Column(nullable = false)
    var userId: Long,

    @Column(nullable = false)
    var dateIssued: LocalDateTime = LocalDateTime.now(),

    var dateUsed: LocalDateTime? = null
) : BaseTimeEntity()