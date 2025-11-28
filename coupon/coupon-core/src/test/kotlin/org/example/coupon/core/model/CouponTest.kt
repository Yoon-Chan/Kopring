package org.example.coupon.core.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import org.example.coupon.core.exception.CouponIssueException
import org.example.coupon.core.exception.ErrorCode
import org.junit.jupiter.api.DisplayName
import java.time.LocalDateTime

class CouponTest : AnnotationSpec() {
    @Test
    fun `최대 발급 수량부다 낮은 경우 true를 반환한다`() {
        //given
        val coupon = Coupon(
            totalQuantity = 100,
            issuedQuantity = 99,
            id = 0L,
            title = "",
            couponType = CouponType.FIRST_COME_FIRST_SERVED,
            minAvailableAmount = 0,
            dateIssueStart = LocalDateTime.now(),
            dateIssueEnd = LocalDateTime.now()
        )

        coupon.availableIssueQuantity() shouldBe true
    }

    @Test
    fun `최대 발급수량과 같으면 false를 반환한다`() {
        //given
        val coupon = Coupon(
            totalQuantity = 100,
            issuedQuantity = 100,
            id = 0L,
            title = "",
            couponType = CouponType.FIRST_COME_FIRST_SERVED,
            minAvailableAmount = 0,
            dateIssueStart = LocalDateTime.now(),
            dateIssueEnd = LocalDateTime.now()
        )

        coupon.availableIssueQuantity() shouldBe false
    }

    @Test
    fun `최대 발급수량이 null이면 true를 반환한다`() {
        //given
        val coupon = Coupon(
            totalQuantity = null,
            issuedQuantity = 100,
            id = 0L,
            title = "",
            couponType = CouponType.FIRST_COME_FIRST_SERVED,
            minAvailableAmount = 0,
            dateIssueStart = LocalDateTime.now(),
            dateIssueEnd = LocalDateTime.now()
        )

        coupon.availableIssueQuantity() shouldBe true
    }

    @Test
    fun `발급 기간이 시작되지 않았다면 false를 반환한다`() {
        //given
        val coupon = Coupon(
            totalQuantity = null,
            issuedQuantity = 100,
            id = 0L,
            title = "",
            couponType = CouponType.FIRST_COME_FIRST_SERVED,
            minAvailableAmount = 0,
            dateIssueStart = LocalDateTime.now().plusDays(1),
            dateIssueEnd = LocalDateTime.now().plusDays(2)
        )

        coupon.availableIssueDate() shouldBe false
    }

    @Test
    fun `발급 기간에 해당되면 true를 반환한다`() {
        //given
        val coupon = Coupon(
            totalQuantity = null,
            issuedQuantity = 100,
            id = 0L,
            title = "",
            couponType = CouponType.FIRST_COME_FIRST_SERVED,
            minAvailableAmount = 0,
            dateIssueStart = LocalDateTime.now().minusDays(1),
            dateIssueEnd = LocalDateTime.now().plusDays(2)
        )

        coupon.availableIssueDate() shouldBe true
    }

    @Test
    fun `발급 기간이 종료되면 false를 반환한다`() {
        //given
        val coupon = Coupon(
            totalQuantity = null,
            issuedQuantity = 100,
            id = 0L,
            title = "",
            couponType = CouponType.FIRST_COME_FIRST_SERVED,
            minAvailableAmount = 0,
            dateIssueStart = LocalDateTime.now().minusDays(2),
            dateIssueEnd = LocalDateTime.now().minusDays(1)
        )

        coupon.availableIssueDate() shouldBe false
    }


    @Test
    fun `발급 수량과 발급 기간이 유효하다면 발급에 성공한다`() {
        //given
        val coupon = Coupon(
            totalQuantity = 100,
            issuedQuantity = 99,
            id = 0L,
            title = "",
            couponType = CouponType.FIRST_COME_FIRST_SERVED,
            minAvailableAmount = 0,
            dateIssueStart = LocalDateTime.now().minusDays(1),
            dateIssueEnd = LocalDateTime.now().plusDays(2)
        )

        coupon.issue()

        coupon.issuedQuantity shouldBe 100
    }

    @Test
    fun `발급 수량을 초과하면 예외를 반환한다`() {
        //given
        val coupon = Coupon(
            totalQuantity = 100,
            issuedQuantity = 100,
            id = 0L,
            title = "",
            couponType = CouponType.FIRST_COME_FIRST_SERVED,
            minAvailableAmount = 0,
            dateIssueStart = LocalDateTime.now().minusDays(1),
            dateIssueEnd = LocalDateTime.now().plusDays(2)
        )

        val res = shouldThrow<CouponIssueException> {
            coupon.issue()
        }

        res.errorCode shouldBe ErrorCode.INVALID_COUPON_ISSUE_QUANTITY
    }

    @Test
    fun `발급 기간이 유효하지 않으면 예외를 반환한다`() {
        //given
        val coupon = Coupon(
            totalQuantity = 100,
            issuedQuantity = 99,
            id = 0L,
            title = "",
            couponType = CouponType.FIRST_COME_FIRST_SERVED,
            minAvailableAmount = 0,
            dateIssueStart = LocalDateTime.now().minusDays(2),
            dateIssueEnd = LocalDateTime.now().minusDays(1)
        )

        val res = shouldThrow<CouponIssueException> {
            coupon.issue()
        }

        res.errorCode shouldBe ErrorCode.INVALID_COUPON_ISSUE_DATE
    }
}