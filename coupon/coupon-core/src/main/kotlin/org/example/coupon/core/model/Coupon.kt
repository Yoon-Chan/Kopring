package org.example.coupon.core.model

import jakarta.persistence.*
import org.example.coupon.core.exception.CouponIssueException
import org.example.coupon.core.exception.ErrorCode
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
) : BaseTimeEntity() {

    fun availableIssueQuantity(): Boolean {
        return totalQuantity?.let { it > issuedQuantity  } ?: true
    }

    fun availableIssueDate(): Boolean {
        val now = LocalDateTime.now()
        return dateIssueStart.isBefore(now) && dateIssueEnd.isAfter(now)
    }

    fun issue() {
        if(!availableIssueQuantity()) {
            throw CouponIssueException(errorCode = ErrorCode.INVALID_COUPON_ISSUE_QUANTITY, "발급 가능한 수량을 초과합니다. total : $totalQuantity, issued : $issuedQuantity")
        }

        if(!availableIssueDate()) {
            throw CouponIssueException(errorCode = ErrorCode.INVALID_COUPON_ISSUE_DATE, "발급 가능한 일자가 아닙니다. request : ${LocalDateTime.now()} issueStart : $dateIssueStart, issueEnd : $dateIssueEnd")
        }
        issuedQuantity++
    }
}
