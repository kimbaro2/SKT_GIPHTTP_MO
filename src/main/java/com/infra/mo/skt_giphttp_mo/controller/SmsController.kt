package com.infra.mo.skt_giphttp_mo.controller

import ch.qos.logback.classic.Level
import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.GipHttpMoAccessRepository
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_8BIT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_ASCII7
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_8BIT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_ASCII7
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_GSM7
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_KSC5601
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_UCS2
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_GSM7
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_KSC5601
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_UCS2
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.GI_RES_NO_ERR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_SIMPLE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_TRANS_RESULT
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseTR
import com.infra.mo.skt_giphttp_mo.service.SmsResService
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange

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
    private val witcomLog: WitcomLog,
    private val gipHttpMoAccessRepository: GipHttpMoAccessRepository
) {
    private val gServerID = System.getenv("SMSS_NO")?.trim()?.toIntOrNull() ?: 0

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
        @RequestBody request: ResponseTR,
        exchange: ServerWebExchange
    ): ResponseEntity<Map<String, String>> {
        // API 인입 확인용 로깅 (p_write만 사용)
        witcomLog.p_write(Level.INFO, 
            String.format("[mo-report] API 인입: msgCode(%d), msgSubCode(%d), destCID(%s)",
                request.data.msgCode,
                request.data.msgSubCode,
                request.data.destCID ?: "null"
            )
        )
//        val loggerName = "${moEntity.cid}-${moEntity.ipAddr}-${moEntity.portNo}"
        return try {
            // 클라이언트 IP 추출 (파라미터 검증 전에 먼저 추출)
            val xForwardedFor = exchange.request.headers.getFirst("X-Forwarded-For")
            val remoteAddress = exchange.request.remoteAddress?.address?.hostAddress
            val clientIp = xForwardedFor
                ?.split(",")?.firstOrNull()?.trim()
                ?: remoteAddress
                ?: "unknown"
            
            // URI에서 프로토콜 확인하여 DB 조회용 포트 결정: HTTP -> 8544, HTTPS -> 8500
            val scheme = exchange.request.uri.scheme
            val dbPort = if ("https".equals(scheme, ignoreCase = true)) 8500 else 8544
            
            // 서버 리스닝 포트 추출 (실제 인입 포트)
            val localPort = exchange.request.localAddress?.port
            val uriPort = if (exchange.request.uri.port != -1) exchange.request.uri.port else 0
            val serverPort = localPort ?: uriPort
            
            // destCID 필수 파라미터 검증 (IP/PORT 추출 전에 먼저 체크)
            val destCIDValue = request.data.destCID
            if (destCIDValue.isNullOrBlank()) {
                val tempLoggerName = "unknown-${clientIp}-${serverPort}"
                val errorMsg = "Missing required parameter: destCID (msgSubCode=9)"
                witcomLog.c_write(tempLoggerName, Level.ERROR, errorMsg, Thread.currentThread().getId())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf(
                        "status" to "error",
                        "message" to errorMsg
                    ))
            }
            
            // IP/PORT 유효성 검증
            if (clientIp == "unknown" || clientIp.isBlank()) {
                val tempLoggerName = "$destCIDValue-unknown-${serverPort}"
                val errorMsg = "Invalid client IP: clientIp($clientIp). Cannot extract client IP from request."
                witcomLog.c_write(tempLoggerName, Level.ERROR, errorMsg, Thread.currentThread().getId())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf(
                        "status" to "error",
                        "message" to errorMsg
                    ))
            }
            
            if (serverPort == 0 || serverPort < 1) {
                val tempLoggerName = "$destCIDValue-${clientIp}-0"
                val errorMsg = "Invalid server port: serverPort($serverPort). Cannot extract server port from request."
                witcomLog.c_write(tempLoggerName, Level.ERROR, errorMsg, Thread.currentThread().getId())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf(
                        "status" to "error",
                        "message" to errorMsg
                    ))
            }
            
            // loggerName 생성 (destCID, clientIp, serverPort 조합) - 실제 인입 포트 사용
            val loggerName = "$destCIDValue-$clientIp-$serverPort"
            
            // msgSubCode 검증: SM_REQ_TRANS_RESULT (9)만 허용
            val msgSubCodeValue = request.data.msgSubCode.toInt()
            witcomLog.c_write(loggerName, Level.DEBUG, 
                String.format("[mo-report] IF 분기 1: msgSubCode 검증 - msgSubCode(%d), expected(%d), result(%s)",
                    msgSubCodeValue,
                    SM_REQ_TRANS_RESULT,
                    if (msgSubCodeValue == SM_REQ_TRANS_RESULT) "PASS" else "FAIL"
                ),
                Thread.currentThread().getId()
            )
            if (msgSubCodeValue != SM_REQ_TRANS_RESULT) {
                val errorMsg = "Invalid msgSubCode: ${request.data.msgSubCode}. Only SM_REQ_TRANS_RESULT(9) is allowed for mo-report API"
                witcomLog.c_write(loggerName, Level.ERROR, errorMsg, Thread.currentThread().getId())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf(
                        "status" to "error",
                        "message" to errorMsg
                    ))
            }
            
            // 필수 파라미터 검증: srcCallNo 필수 (MOCALLINFO 조회용)
            val srcCallNoValue = request.data.srcCallNo
            witcomLog.c_write(loggerName, Level.DEBUG, 
                String.format("[mo-report] IF 분기 2: srcCallNo 검증 - srcCallNo(%s), isNullOrBlank(%s), result(%s)",
                    srcCallNoValue ?: "null",
                    srcCallNoValue.isNullOrBlank(),
                    if (!srcCallNoValue.isNullOrBlank()) "PASS" else "FAIL"
                ),
                Thread.currentThread().getId()
            )
            if (srcCallNoValue.isNullOrBlank()) {
                val errorMsg = "Missing required parameter: srcCallNo (msgSubCode=9)"
                witcomLog.c_write(loggerName, Level.ERROR, errorMsg, Thread.currentThread().getId())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf(
                        "status" to "error",
                        "message" to errorMsg
                    ))
            }
            
            // 필수 파라미터 검증: destCID 필수 (MOCALLINFO 조회용)
            witcomLog.c_write(loggerName, Level.DEBUG, 
                String.format("[mo-report] IF 분기 3: destCID 검증 - destCID(%s), isNullOrBlank(%s), result(%s)",
                    destCIDValue,
                    destCIDValue.isNullOrBlank() || destCIDValue == "unknown",
                    if (!destCIDValue.isNullOrBlank() && destCIDValue != "unknown") "PASS" else "FAIL"
                ),
                Thread.currentThread().getId()
            )
            if (destCIDValue.isNullOrBlank() || destCIDValue == "unknown") {
                val errorMsg = "Missing required parameter: destCID (msgSubCode=9)"
                witcomLog.c_write(loggerName, Level.ERROR, errorMsg, Thread.currentThread().getId())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf(
                        "status" to "error",
                        "message" to errorMsg
                    ))
            }
            
            // 필수 파라미터 검증: msgId 필수 (MOCALLINFO 조회용)
            val msgIdValue = request.data.msgId
            witcomLog.c_write(loggerName, Level.DEBUG, 
                String.format("[mo-report] IF 분기 4: msgId 검증 - msgId(%s), isNullOrBlank(%s), result(%s)",
                    msgIdValue ?: "null",
                    msgIdValue.isNullOrBlank(),
                    if (!msgIdValue.isNullOrBlank()) "PASS" else "FAIL"
                ),
                Thread.currentThread().getId()
            )
            if (msgIdValue.isNullOrBlank()) {
                val errorMsg = "Missing required parameter: msgId (msgSubCode=9)"
                witcomLog.c_write(loggerName, Level.ERROR, errorMsg, Thread.currentThread().getId())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf(
                        "status" to "error",
                        "message" to errorMsg
                    ))
            }
            
            // 필수 파라미터 검증: result 필수
            val resultValue = request.data.result
            witcomLog.c_write(loggerName, Level.DEBUG, 
                String.format("[mo-report] IF 분기 5: result 검증 - result(%s), isNull(%s), result(%s)",
                    resultValue?.toString() ?: "null",
                    resultValue == null,
                    if (resultValue != null) "PASS" else "FAIL"
                ),
                Thread.currentThread().getId()
            )
            if (resultValue == null) {
                val errorMsg = "Missing required parameter: result (msgSubCode=9)"
                witcomLog.c_write(loggerName, Level.ERROR, errorMsg, Thread.currentThread().getId())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf(
                        "status" to "error",
                        "message" to errorMsg
                    ))
            }
            
            witcomLog.c_write(loggerName, Level.DEBUG, 
                String.format("[mo-report] IP/PORT 추출 - X-Forwarded-For(%s), remoteAddress(%s), clientIp(%s), localPort(%s), uriPort(%d), serverPort(%d), scheme(%s)",
                    xForwardedFor ?: "null",
                    remoteAddress ?: "null",
                    clientIp,
                    localPort?.toString() ?: "null",
                    uriPort,
                    serverPort,
                    scheme
                ),
                Thread.currentThread().getId()
            )
            
            // gipHttpMoAccess 조회 (유니크 제약조건: CID, IP, PORT, MSG_TYPE)
            // SM_REQ_TRANS_RESULT (9)는 MO-TR 결과이므로 MSG_TYPE='4' (MO-TR)
            val msgType = "4"  // MO-TR
            witcomLog.c_write(loggerName, Level.DEBUG, 
                String.format("[mo-report] IF 분기 6: gipHttpMoAccess 조회 시작 - destCID(%s), clientIp(%s), serverPort(%d), msgType(%s), scheme(%s)",
                    destCIDValue,
                    clientIp,
                    serverPort,
                    msgType,
                    scheme
                ),
                Thread.currentThread().getId()
            )
            val gipHttpMoAccess = try {
                val result = gipHttpMoAccessRepository.findByCidAndIpAddrAndPortNoAndMsgType(
                    destCIDValue,
                    clientIp,
                    serverPort,  // 실제 인입 포트 사용 (7500 또는 7544)
                    msgType
                ).orElse(null)
                witcomLog.c_write(loggerName, Level.DEBUG, 
                    String.format("[mo-report] IF 분기 6 결과: gipHttpMoAccess 조회 성공 - found(%s), cid(%s), ipAddr(%s), portNo(%s), billType(%s), moTrBill(%s)",
                        if (result != null) "YES" else "NO",
                        result?.cid ?: "null",
                        result?.ipAddr ?: "null",
                        result?.portNo?.toString() ?: "null",
                        result?.billType ?: "null",
                        result?.moTrBill ?: "null"
                    ),
                    Thread.currentThread().getId()
                )
                result
            } catch (e: Exception) {
                // MSG_TYPE='4' 레코드를 찾지 못함
                witcomLog.c_write(
                    loggerName,
                    Level.ERROR,
                    String.format("[mo-report] IF 분기 6 예외: gipHttpMoAccess 조회 실패 - destCID(%s), clientIp(%s), serverPort(%d), msgType(%s), scheme(%s), error(%s)",
                        destCIDValue,
                        clientIp,
                        serverPort,
                        msgType,
                        scheme,
                        e.message
                    ),
                    Thread.currentThread().getId()
                )
                null
            }
            
            // gipHttpMoAccess를 찾지 못한 경우 에러 처리 (MSG_TYPE='4' 필수)
            witcomLog.c_write(loggerName, Level.DEBUG, 
                String.format("[mo-report] IF 분기 7: gipHttpMoAccess null 검증 - gipHttpMoAccess(%s), result(%s)",
                    if (gipHttpMoAccess != null) "NOT_NULL" else "NULL",
                    if (gipHttpMoAccess != null) "PASS" else "FAIL"
                ),
                Thread.currentThread().getId()
            )
            if (gipHttpMoAccess == null) {
                val errorMsg = "GIPHTTP_MO_ACCESS not found with MSG_TYPE='4' (MO-TR): destCID($destCIDValue), clientIp($clientIp), serverPort($serverPort), scheme($scheme). Please register MSG_TYPE='4' record for MO-TR processing."
                witcomLog.c_write(loggerName, Level.ERROR, errorMsg, Thread.currentThread().getId())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf(
                        "status" to "error",
                        "message" to errorMsg
                    ))
            }
            val logNo = gipHttpMoAccess.logNo?.let {
                String.format("%04d", it.toIntOrNull() ?: 0)
            } ?: "0000"
            val cpName = gipHttpMoAccess.cpName ?: ""
            
            // DataEncoding 포맷팅
            val dataEncodingValue = (request.data.dataEncoding ?: 0).toInt() and 0xFF
            val encodingName = when (dataEncodingValue) {
                DCS_TYPE_DEC_KSC5601, DCS_TYPE_KSC5601.toInt() -> "CP949"
                DCS_TYPE_DEC_UCS2, DCS_TYPE_UCS2.toInt() -> "UCS2"
                DCS_TYPE_DEC_GSM7, DCS_TYPE_GSM7.toInt() -> "GSM7"
                DCS_TYPE_DEC_ASCII7, DCS_TYPE_ASCII7.toInt() -> "ASCII7"
                DCS_TYPE_DEC_8BIT, DCS_TYPE_8BIT.toInt() -> "8BIT"
                else -> "UNKNOWN"
            }
            val dataEncoding = String.format("%02X:%s", dataEncodingValue, encodingName)
            
            // SM_REQ_TRANS_RESULT (9): MO-TR 결과 - REQ_TRANS_RESULT 로그 출력
            // 실제 사용자 전송 파라미터 기반으로 로그 출력
            val statusValue = request.data.msgStatus ?: 0
            val statusStr = when (statusValue) {
                2 -> "2:DELIVERED"
                3 -> "3:EXPIRED"
                5 -> "5:UNDELIVERED"
                14 -> "14:FWDFAIL"
                16 -> "16:SPAMERR"
                17 -> "17:USERDEL"
                19 -> "19:NPREFIX"
                20 -> "20:ADMCANC"
                else -> "$statusValue:UNKNOWN"
            }
            
            val reqTransResultLog = String.format(
                "[GIPALL_C_%s] [REQ_TRANS_RESULT] [%s->VSMSS#%d] MsgVerId(%d) EncFlag(%d) SrcCId(%s) SrcCallNo(%s) DestCId(%s) DestCallNo(%s) MsgCode(%d) MsgSubCode(%d) BodyDataLen(%d) MsgSeqNo(%d) DataEncoding(%s) TermType(%s) ConcatenateFlag(%s) ConcatenateInfo(%s) Result(%d) Status(%s) MsgId(%s)",
                logNo,
                cpName,
                gServerID,
                request.msgVerId ?: 0,
                request.encFlag ?: 0,
                request.data.srcCID ?: "",
                request.data.srcCallNo ?: "",
                request.data.destCID ?: "",
                request.data.destCallNo ?: "",
                request.data.msgCode?.toInt() ?: 0,
                request.data.msgSubCode?.toInt() ?: 0,
                request.data.bodyDataLen ?: 0,
                request.data.msgSeqNo ?: 0,
                dataEncoding,
                request.data.termtype ?: "",
                request.data.concatenateflag ?: "",
                request.data.concatenateInfo ?: "",
                request.data.result ?: 0,
                statusStr,
                request.data.msgId ?: ""
            )
            
            val reqLoggerName = "${gipHttpMoAccess.cid}-${gipHttpMoAccess.ipAddr}-${gipHttpMoAccess.portNo}"
            witcomLog.c_write(reqLoggerName, Level.DEBUG, reqTransResultLog, Thread.currentThread().getId())
            
            // ProcessSMRes 로직 실행 (suspend 함수이므로 runBlocking 사용)
            runBlocking {
                smsResService.processSMRes(request, clientIp, serverPort)
            }

            // HTTP 응답 생성
            val responseBody = mapOf(
                "status" to "success",
                "message" to "Result processed successfully"
            )
            val responseBodyJson = "{\"status\":\"success\",\"message\":\"Result processed successfully\"}"
            val contentLength = responseBodyJson.length
            
            // 응답 로그 출력 (HTTP 응답 형식)
            val resTransResultLog = String.format(
                "[GIPALL_C_%s] [RES_TRANS_RESULT] HTTP/1.1 200 OK Content-Type: application/json Content-Length: %d %s",
                logNo,
                contentLength,
                responseBodyJson
            )
            
            val resLoggerName = "${gipHttpMoAccess.cid}-${gipHttpMoAccess.ipAddr}-${gipHttpMoAccess.portNo}"
            witcomLog.c_write(resLoggerName, Level.DEBUG, resTransResultLog, Thread.currentThread().getId())

            ResponseEntity.ok(responseBody)
        } catch (e: IllegalStateException) {
            // traceId 관련 에러 또는 기타 비즈니스 로직 에러
            e.printStackTrace()
            // 예외 발생 시 loggerName 생성 (가능한 정보만 사용)
            val errorDestCID = request.data.destCID ?: "unknown"
            val errorClientIp = try {
                exchange.request.headers.getFirst("X-Forwarded-For")
                    ?.split(",")?.firstOrNull()?.trim()
                    ?: exchange.request.remoteAddress?.address?.hostAddress
                    ?: "unknown"
            } catch (ex: Exception) {
                "unknown"
            }
            val errorServerPort = try {
                exchange.request.localAddress?.port
                    ?: (if (exchange.request.uri.port != -1) exchange.request.uri.port else 0)
            } catch (ex: Exception) {
                0
            }
            val errorLoggerName = "$errorDestCID-$errorClientIp-$errorServerPort"
            val errorMessage = e.message ?: "Unknown error"
            
            witcomLog.c_write(
                errorLoggerName,
                Level.ERROR,
                String.format(
                    "[mo-report] 비즈니스 로직 에러 발생: msgCode(%d), msgSubCode(%d), msgId(%s), error(%s), stackTrace(%s)",
                    request.data.msgCode,
                    request.data.msgSubCode,
                    request.data.msgId ?: "null",
                    errorMessage,
                    e.stackTraceToString()
                ),
                Thread.currentThread().getId()
            )
            
            // traceId 관련 에러는 BAD_REQUEST로 응답
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                    mapOf(
                        "status" to "error",
                        "message" to errorMessage
                    )
                )
        } catch (e: Exception) {
            e.printStackTrace()
            // 예외 발생 시 loggerName 생성 (가능한 정보만 사용)
            val errorDestCID = request.data.destCID ?: "unknown"
            val errorClientIp = try {
                exchange.request.headers.getFirst("X-Forwarded-For")
                    ?.split(",")?.firstOrNull()?.trim()
                    ?: exchange.request.remoteAddress?.address?.hostAddress
                    ?: "unknown"
            } catch (ex: Exception) {
                "unknown"
            }
            val errorServerPort = try {
                exchange.request.localAddress?.port
                    ?: (if (exchange.request.uri.port != -1) exchange.request.uri.port else 0)
            } catch (ex: Exception) {
                0
            }
            val errorLoggerName = "$errorDestCID-$errorClientIp-$errorServerPort"
            witcomLog.c_write(
                errorLoggerName,
                Level.ERROR,
                String.format(
                    "[mo-report] 예외 발생: msgCode(%d), msgSubCode(%d), error(%s), stackTrace(%s)",
                    request.data.msgCode,
                    request.data.msgSubCode,
                    e.message,
                    e.stackTraceToString()
                ),
                Thread.currentThread().getId()
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

