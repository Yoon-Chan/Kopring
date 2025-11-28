package org.example.coupon.core.service

import org.example.coupon.core.exception.CouponIssueException
import org.example.coupon.core.exception.ErrorCode
import org.example.coupon.core.model.Coupon
import org.example.coupon.core.model.CouponIssue
import org.example.coupon.core.repository.mysql.CouponIssueJpaRepository
import org.example.coupon.core.repository.mysql.CouponIssueRepository
import org.example.coupon.core.repository.mysql.CouponJpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CouponIssueService(
    private val couponJpaRepository: CouponJpaRepository,
    private val couponIssueJpaRepository: CouponIssueJpaRepository,
    private val couponIssueRepository: CouponIssueRepository
) {
    @Transactional
    fun issue(couponId: Long, userId: Long) {
        val coupon = findCoupon(couponId)
        coupon.issue()
        saveCouponIssue(couponId, userId)
    }

    @Transactional(readOnly = true)
    fun findCoupon(couponId: Long): Coupon {
        return couponJpaRepository.findById(couponId).orElseThrow {
            CouponIssueException(ErrorCode.COUPON_NOT_EXIST, "쿠폰 정책이 존재하지 않습니다. $couponId")
        }
    }

    @Transactional
    fun saveCouponIssue(couponId: Long, userId: Long): CouponIssue {
        checkAlreadyIssuance(couponId, userId)
        val issue = CouponIssue(couponId = couponId, userId = userId)
        return couponIssueJpaRepository.save(issue)
    }

    //이미 쿠폰이 발급되었는지 확인
    private fun checkAlreadyIssuance(couponId: Long, userId: Long) {
        val issue = couponIssueRepository.findFirstCouponIssue(couponId, userId)
        if(issue != null) {
            throw CouponIssueException(ErrorCode.DUPLICATED_COUPON_ISSUE, "이미 발급된 쿠폰입니다. user_id: $userId, coupon_id: $couponId")
        }
    }
}