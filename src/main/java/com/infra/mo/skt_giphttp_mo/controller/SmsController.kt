package com.infra.mo.skt_giphttp_mo.controller

import ch.qos.logback.classic.Level
import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseTR
import com.infra.mo.skt_giphttp_mo.service.SmsResService
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * SMS 결과 수신을 위한 HTTP API Controller
 *
 * C 코드 참고: GIPEVENT_c.c LINE 1928 (ProcessSMRes)
 * - 기존 TCP 환경에서 실시간 통신으로 처리하던 것을 HTTP POST로 변환
 */
@RestController
@RequestMapping("/sms")
class SmsController(
    private val smsResService: SmsResService,
    private val witcomLog: WitcomLog
) {

    /**
     * ProcessSMRes() 함수의 HTTP 버전
     * CP 서버로부터 MO 메시지 ACK 또는 TR 결과를 받아서 처리
     *
     * C 코드 참고: GIPEVENT_c.c LINE 1928 (ProcessSMRes)
     *
     * @param request ResponseTR (CP 서버로부터 받은 결과)
     * @return 처리 결과
     */
    @PostMapping("/mo-report")
    fun receiveSmsResult(
        @RequestBody request: ResponseTR
    ): ResponseEntity<Map<String, String>> {
        return try {
            witcomLog.p_write(
                Level.INFO,
                String.format(
                    "Receive SMS Result: msgCode(%d), msgSubCode(%d), srcCID(%s), destCID(%s), ackResult(%d)",
                    request.data.msgCode,
                    request.data.msgSubCode,
                    request.data.srcCID,
                    request.data.destCID,
                    request.data.ackResult ?: 0
                )
            )

            // ProcessSMRes 로직 실행 (suspend 함수이므로 runBlocking 사용)
            runBlocking {
                smsResService.processSMRes(request)
            }

            ResponseEntity.ok(
                mapOf(
                    "status" to "success",
                    "message" to "Result processed successfully"
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            witcomLog.p_write(
                Level.ERROR,
                String.format(
                    "Error processing SMS Result: msgCode(%d), msgSubCode(%d), error(%s)",
                    request.data.msgCode,
                    request.data.msgSubCode,
                    e.message
                )
            )
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    mapOf(
                        "status" to "error",
                        "message" to (e.message ?: "Unknown error")
                    )
                )
        }
    }
}

