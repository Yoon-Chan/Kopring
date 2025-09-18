package com.chan.banklecture.common.logging

import com.chan.banklecture.common.exception.CustomException
import com.chan.banklecture.common.exception.ErrorCode
import org.slf4j.*;

object Logging {
    fun <T : Any> getLogger(clazz: Class<T>): Logger {
        return LoggerFactory.getLogger(clazz)
    }

    fun <T> logFor(log: Logger, function: (MutableMap<String, Any>) -> T?): T {
        val logInfo = mutableMapOf<String, Any>()
        logInfo["start_at"] = now()

        val result = function(logInfo)

        logInfo["end_at"] = now()
        log.info(logInfo.toString())
        return result ?: throw CustomException(ErrorCode.FAILED_TO_INVOKE_IN_LOGGED)
    }

    private fun now(): Long {
        return System.currentTimeMillis()
    }
}