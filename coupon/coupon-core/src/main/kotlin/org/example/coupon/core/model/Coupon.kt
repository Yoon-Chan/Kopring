package org.example.coupon.core.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "coupons")
class Coupon(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    var couponType: CouponType,

    var totalQuantity: Int?,

    @Column(nullable = false)
    var issuedQuantity: Int,

    @Column(nullable = false)
    var minAvailableAmount: Int,

    @Column(nullable = false)
    var dateIssueStart: LocalDateTime,

    @Column(nullable = false)
    var dateIssueEnd: LocalDateTime,
) : BaseTimeEntity()
