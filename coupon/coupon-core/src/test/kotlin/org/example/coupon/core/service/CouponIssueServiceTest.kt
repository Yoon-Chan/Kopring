package org.example.coupon.core.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.example.coupon.core.CouponCoreConfiguration
import org.example.coupon.core.TestConfig
import org.example.coupon.core.exception.CouponIssueException
import org.example.coupon.core.exception.ErrorCode
import org.example.coupon.core.model.Coupon
import org.example.coupon.core.model.CouponIssue
import org.example.coupon.core.model.CouponType
import org.example.coupon.core.repository.mysql.CouponIssueJpaRepository
import org.example.coupon.core.repository.mysql.CouponIssueRepository
import org.example.coupon.core.repository.mysql.CouponJpaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


class CouponIssueServiceTest: AnnotationSpec(), TestConfig {

    @Autowired
    lateinit var sut: CouponIssueService

    @Autowired
    lateinit var couponJpaRepository: CouponJpaRepository

    @Autowired
    lateinit var couponIssueRepository: CouponIssueRepository

    @Autowired
    lateinit var couponIssueJpaRepository: CouponIssueJpaRepository

    @BeforeEach
    fun clean() {
        couponJpaRepository.deleteAllInBatch()
        couponIssueJpaRepository.deleteAllInBatch()
    }

    @Test
    fun `쿠폰 발급 내역이 존재하면 예외를 반환한다`() {
        val couponIssue = CouponIssue(
            couponId = 1L,
            userId = 1L
        )

        couponIssueJpaRepository.save(couponIssue)

        val throws = shouldThrow<CouponIssueException> {
            sut.saveCouponIssue(couponIssue.couponId, couponIssue.userId)
        }

        throws.errorCode shouldBe ErrorCode.DUPLICATED_COUPON_ISSUE
    }

    @Test
    fun `쿠폰 발급내역이 존재하지 않는 경우 쿠폰을 발급한다`() {
        val couponId = 1L
        val userId = 1L

        val result = sut.saveCouponIssue(couponId, userId)

        result.id.shouldNotBeNull()

        couponIssueJpaRepository.findById(result.id!!).isPresent shouldBe true
    }

    @Test
    fun `발급 수량, 기한, 중복 발급 문제가 없다면 쿠폰을 발급한다`() {
        //when
        val userId = 1L
        val coupon = Coupon(
            couponType = CouponType.FIRST_COME_FIRST_SERVED,
            title = "선착순 테스트 쿠폰",
            totalQuantity = 100,
            issuedQuantity = 0,
            dateIssueStart = LocalDateTime.now().minusDays(1),
            dateIssueEnd = LocalDateTime.now().plusDays(2)
        )
        couponJpaRepository.save(coupon)

        sut.issue(coupon.id!!, userId)

        val couponResult = couponJpaRepository.findById(coupon.id!!).get()
        couponResult.issuedQuantity shouldBe 1

        val couponIssueResult = couponIssueRepository.findFirstCouponIssue(coupon.id!!, userId)
        couponIssueResult.shouldNotBeNull()
    }

    @Test
    fun `발급 수량에 문제가 있다면 예외를 반화한다`() {
        //when
        val userId = 1L
        val coupon = Coupon(
            couponType = CouponType.FIRST_COME_FIRST_SERVED,
            title = "선착순 테스트 쿠폰",
            totalQuantity = 100,
            issuedQuantity = 100,
            dateIssueStart = LocalDateTime.now().minusDays(1),
            dateIssueEnd = LocalDateTime.now().plusDays(2)
        )
        couponJpaRepository.save(coupon)

        val result = shouldThrow<CouponIssueException>{
            sut.issue(coupon.id!!, userId)
        }

        result.errorCode shouldBe ErrorCode.INVALID_COUPON_ISSUE_QUANTITY
    }

    @Test
    fun `발급 기한에 문제가 있는 경우`() {
        //when
        val userId = 1L
        val coupon = Coupon(
            couponType = CouponType.FIRST_COME_FIRST_SERVED,
            title = "선착순 테스트 쿠폰",
            totalQuantity = 100,
            issuedQuantity = 99,
            dateIssueStart = LocalDateTime.now().minusDays(2),
            dateIssueEnd = LocalDateTime.now().minusDays(1)
        )
        couponJpaRepository.save(coupon)

        val result = shouldThrow<CouponIssueException>{
            sut.issue(coupon.id!!, userId)
        }

        result.errorCode shouldBe ErrorCode.INVALID_COUPON_ISSUE_DATE
    }

    @Test
    fun `중복 발급에 문제가 있는 경우 예외를 반환한다`() {
        //when
        val userId = 1L
        val coupon = Coupon(
            couponType = CouponType.FIRST_COME_FIRST_SERVED,
            title = "선착순 테스트 쿠폰",
            totalQuantity = 100,
            issuedQuantity = 0,
            dateIssueStart = LocalDateTime.now().minusDays(2),
            dateIssueEnd = LocalDateTime.now().plusDays(1)
        )
        couponJpaRepository.save(coupon)

        val couponIssue = CouponIssue(
            couponId = coupon.id!!,
            userId = userId
        )

        couponIssueJpaRepository.save(couponIssue)

        val result = shouldThrow<CouponIssueException>{
            sut.issue(coupon.id!!, userId)
        }

        result.errorCode shouldBe ErrorCode.DUPLICATED_COUPON_ISSUE
    }
}