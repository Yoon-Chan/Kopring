package org.example.coupon.core.exception

class CouponIssueException(
    val errorCode: ErrorCode,
    override val message: String? = null
) : RuntimeException() {

    fun getErrorMessage(): String = String.format("[%s] %s", errorCode.name, message)
}