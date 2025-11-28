package org.example.coupon.core.repository.mysql

import com.querydsl.jpa.impl.JPAQueryFactory
import org.example.coupon.core.model.CouponIssue
import org.example.coupon.core.model.QCouponIssue.couponIssue
import org.springframework.stereotype.Repository

@Repository
class CouponIssueRepository constructor(
    val queryFactory: JPAQueryFactory
) {

    fun findFirstCouponIssue(couponId: Long, userId: Long): CouponIssue? {
        return queryFactory.selectFrom(couponIssue)
            .where(couponIssue.couponId.eq(couponId))
            .where(couponIssue.userId.eq(userId))
            .fetchFirst()
    }
}