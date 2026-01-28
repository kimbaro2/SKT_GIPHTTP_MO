package com.infra.mo.skt_giphttp_mo.controller

import ch.qos.logback.classic.Level
import com.fasterxml.jackson.databind.ObjectMapper
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
import com.infra.mo.skt_giphttp_mo.dto.smsController.MoReportRequest
import com.infra.mo.skt_giphttp_mo.service.SmsResService
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.MOCallInfoRepository
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
    private val gipHttpMoAccessRepository: GipHttpMoAccessRepository,
    private val moCallInfoRepository: MOCallInfoRepository
) {
    private val gServerID = System.getenv("SMSS_NO")?.trim()?.toIntOrNull() ?: 0

    /**
     * ProcessSMRes() 함수의 HTTP 버전
     * CP 서버로부터 MO-TR 결과를 받아서 처리
     *
     * C 코드 참고: GIPEVENT_c.c LINE 1928 (ProcessSMRes)
     *
     * @param request MoReportRequest (LOG_NO, CID, MSG_ID, MSG_STATUS, TRACE_ID)
     * @return 처리 결과
     */
    @PostMapping("/mo-report")
    fun receiveSmsResult(
        @RequestBody request: MoReportRequest,
        exchange: ServerWebExchange
    ): ResponseEntity<MoReportRequest> {
        // MoReportRequest JSON 로깅
        val objectMapper = ObjectMapper()
        val jsonPayload = objectMapper.writeValueAsString(request)
        witcomLog.p_write(Level.INFO, 
            String.format("[mo-report] API 인입 JSON: %s", jsonPayload)
        )
        
        // API 인입 확인용 로깅
        witcomLog.p_write(Level.INFO, 
            String.format("[mo-report] API 인입: cid(%s), msgId(%s), status(%d), traceId(%s), msgType(%d)",
                request.data?.cid ?: "null",
                request.data?.msgId ?: "null",
                request.data?.status ?: -1,
                request.data?.traceId ?: "null",
                request.data?.msgType ?: -1
            )
        )
        
        return try {
            // 필수 파라미터 검증
            if (request.data == null) {
                val errorMsg = "Missing required parameter: data"
                witcomLog.p_write(Level.ERROR, errorMsg)
                // 요청값 그대로 반환하되 status를 5로 변경
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        cid = ""
                        msgId = ""
                        traceId = ""
                        status = 5  // UNDELIVERABLE
                        msgType = null
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }
            
            if (request.data.msgId.isNullOrBlank()) {
                val errorMsg = "Missing required parameter: data.msgId"
                witcomLog.p_write(Level.ERROR, errorMsg)
                // 요청값 그대로 반환하되 status를 5로 변경
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        cid = request.data.cid ?: ""
                        msgId = request.data.msgId ?: ""
                        traceId = request.data.traceId ?: ""
                        status = 5  // UNDELIVERABLE
                        msgType = null  // 에러 응답에서는 제외됨
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }
            
            if (request.data.status == null) {
                val errorMsg = "Missing required parameter: data.status"
                witcomLog.p_write(Level.ERROR, errorMsg)
                // 요청값 그대로 반환하되 status를 5로 변경
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        cid = request.data.cid ?: ""
                        msgId = request.data.msgId ?: ""
                        traceId = request.data.traceId ?: ""
                        status = 5  // UNDELIVERABLE
                        msgType = null  // 에러 응답에서는 제외됨
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }
            
            if (request.data.traceId.isNullOrBlank()) {
                val errorMsg = "Missing required parameter: data.traceId"
                witcomLog.p_write(Level.ERROR, errorMsg)
                // 요청값 그대로 반환하되 status를 5로 변경
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        cid = request.data.cid ?: ""
                        msgId = request.data.msgId ?: ""
                        traceId = request.data.traceId ?: ""
                        status = 5  // UNDELIVERABLE
                        msgType = null  // 에러 응답에서는 제외됨
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }
            
            // MSG_TYPE 검증: 없으면 4로 가정 (MO-TR)
            val validatedMsgType = request.data.msgType ?: 4
            if (validatedMsgType != 4) {
                val errorMsg = "Invalid msgType: $validatedMsgType. MO-TR 요청은 msgType=4만 허용됩니다."
                witcomLog.p_write(Level.ERROR, errorMsg)
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        cid = request.data.cid
                        msgId = request.data.msgId
                        traceId = request.data.traceId
                        status = 5  // UNDELIVERABLE
                        msgType = null  // 에러 응답에서는 제외됨
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }
            
            // 클라이언트 IP 추출
            val xForwardedFor = exchange.request.headers.getFirst("X-Forwarded-For")
            val remoteAddress = exchange.request.remoteAddress?.address?.hostAddress
            val clientIp = xForwardedFor
                ?.split(",")?.firstOrNull()?.trim()
                ?: remoteAddress
                ?: "unknown"
            
            // 서버 리스닝 포트 추출
            val localPort = exchange.request.localAddress?.port
            val uriPort = if (exchange.request.uri.port != -1) exchange.request.uri.port else 0
            val serverPort = localPort ?: uriPort
            
            // IP/PORT 유효성 검증
            if (clientIp == "unknown" || clientIp.isBlank()) {
                val errorMsg = "Invalid client IP: clientIp($clientIp). Cannot extract client IP from request."
                witcomLog.p_write(Level.ERROR, errorMsg)
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        cid = request.data.cid
                        msgId = request.data.msgId
                        traceId = request.data.traceId
                        status = 5  // UNDELIVERABLE
                        msgType = null
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }
            
            if (serverPort == 0 || serverPort < 1) {
                val errorMsg = "Invalid server port: serverPort($serverPort). Cannot extract server port from request."
                witcomLog.p_write(Level.ERROR, errorMsg)
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        cid = request.data.cid
                        msgId = request.data.msgId
                        traceId = request.data.traceId
                        status = 5  // UNDELIVERABLE
                        msgType = null
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }
            
            // status 유효값 검증
            var status = request.data.status
            if (status == null) {
                val errorMsg = "Missing required parameter: data.status"
                witcomLog.p_write(Level.ERROR, errorMsg)
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        cid = request.data.cid
                        msgId = request.data.msgId
                        traceId = request.data.traceId
                        status = 5  // UNDELIVERABLE
                        msgType = null
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }
            
            val validMsgStatuses = setOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 14, 16, 17, 19, 20)
            if (status !in validMsgStatuses) {
                val errorMsg = "Invalid status: $status. Valid values are: ${validMsgStatuses.sorted().joinToString(", ")}"
                witcomLog.p_write(Level.ERROR, errorMsg)
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        cid = request.data.cid
                        msgId = request.data.msgId
                        traceId = request.data.traceId
                        status = 5  // UNDELIVERABLE
                        msgType = null
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }
            
            // 1. MOCALLINFO에서 msgId로 레코드 찾기 (먼저 조회하여 destCId 획득)
            val moCallInfo = runBlocking {
                moCallInfoRepository.findByMsgId(request.data.msgId)
            }
            
            if (moCallInfo == null) {
                val errorMsg = "MOCALLINFO not found: msgId=${request.data.msgId}"
                witcomLog.p_write(Level.ERROR, errorMsg)
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        cid = request.data.cid
                        msgId = request.data.msgId
                        traceId = request.data.traceId
                        status = 5  // UNDELIVERABLE
                        msgType = null
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }
            
            // destCId 획득 (MOCALLINFO에서 또는 요청에서)
            val destCId = request.data.cid ?: moCallInfo.destCId
            
            if (destCId.isNullOrBlank()) {
                val errorMsg = "destCId not found: MOCALLINFO.destCId is null or blank"
                witcomLog.p_write(Level.ERROR, errorMsg)
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        cid = moCallInfo.destCId  // MOCALLINFO에서 가져온 값 사용 (null일 수 있음)
                        msgId = request.data.msgId
                        traceId = request.data.traceId
                        status = 5  // UNDELIVERABLE
                        msgType = null
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }
            
            // traceId 검증
            val dbTraceId = moCallInfo.traceId ?: ""
            if (request.data.traceId != dbTraceId) {
                val errorMsg = "traceId mismatch: request.traceId(${request.data.traceId}) != db.traceId($dbTraceId)"
                witcomLog.p_write(Level.ERROR, errorMsg)
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        cid = destCId  // destCId 사용
                        msgId = request.data.msgId
                        traceId = request.data.traceId
                        status = 5  // UNDELIVERABLE
                        msgType = null
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }
            
            // loggerName 생성 (destCId 사용)
            val loggerName = "${destCId}-${clientIp}-${serverPort}"
            
            // 2. HTTP_MOSEND_ACCESS에서 CID, IP_ADDR, PORT_NO로 정합성 검증 (MOTRBILL로 로직 분기)
            val gipHttpMoAccess = try {
                var access = gipHttpMoAccessRepository.findByCidAndIpAddrAndPortNo(
                    destCId,
                    clientIp,
                    serverPort
                ).orElse(null)
                
                // 조회 실패 시 IP 127.0.0.1로 재조회
                if (access == null) {
                    access = gipHttpMoAccessRepository.findByCidAndIpAddrAndPortNo(
                        destCId,
                        "127.0.0.1",
                        serverPort
                    ).orElse(null)
                }
                access
            } catch (e: Exception) {
                witcomLog.c_write(
                    loggerName,
                    Level.ERROR,
                    String.format("[mo-report] HTTP_MOSEND_ACCESS 조회 실패: cid(%s), IP(%s), PORT(%d), error(%s)",
                        destCId, clientIp, serverPort, e.message),
                    Thread.currentThread().getId()
                )
                null
            }
            
            // HTTP_MOSEND_ACCESS 검증 실패: 요청값 그대로 반환하되 status를 5로 변경
            if (gipHttpMoAccess == null) {
                val errorMsg = "HTTP_MOSEND_ACCESS not found: cid($destCId), IP($clientIp), PORT($serverPort)"
                witcomLog.c_write(loggerName, Level.ERROR, errorMsg, Thread.currentThread().getId())
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        cid = destCId
                        msgId = request.data.msgId
                        traceId = request.data.traceId
                        status = 5  // UNDELIVERABLE
                        msgType = null
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }
            
            // CID 검증 (MOCALLINFO의 destCId와 HTTP_MOSEND_ACCESS의 cid 일치 확인)
            if (destCId != gipHttpMoAccess.cid) {
                val errorMsg = "cid mismatch: MOCALLINFO.destCId($destCId) != HTTP_MOSEND_ACCESS.cid(${gipHttpMoAccess.cid})"
                witcomLog.c_write(loggerName, Level.ERROR, errorMsg, Thread.currentThread().getId())
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        cid = destCId
                        msgId = request.data.msgId
                        traceId = request.data.traceId
                        status = 5  // UNDELIVERABLE
                        msgType = null
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }
            
            // 3. 모든 검증 통과: MSG_STATUS에 따라 MO-TR 로직 처리
            runBlocking {
                smsResService.processMoReport(
                    request,
                    moCallInfo,
                    gipHttpMoAccess,
                    clientIp,
                    serverPort
                )
            }
            
            // HTTP 응답 생성: 요청값 그대로 반환 (status 유지, cid는 destCId로 설정)
            val responseBody = MoReportRequest().apply {
                msgVerId = request.msgVerId ?: 510
                encFlag = request.encFlag ?: 0
                data = MoReportRequest.DataBody().apply {
                    cid = destCId  // MOCALLINFO에서 획득한 destCId 사용
                    msgId = request.data.msgId
                    traceId = request.data.traceId
                    status = request.data.status  // 요청의 status 값 직접 사용
                    msgType = validatedMsgType  // 검증된 msgType 사용 (기본값 4)
                }
            }
            
            ResponseEntity.ok(responseBody)
            
        } catch (e: Exception) {
            // 예외 처리
            val errorClientIp = try { 
                val xff = exchange.request.headers.getFirst("X-Forwarded-For")
                xff?.split(",")?.firstOrNull()?.trim() ?: exchange.request.remoteAddress?.address?.hostAddress ?: "unknown"
            } catch (ex: Exception) { "unknown" }
            val errorServerPort = try { exchange.request.localAddress?.port ?: 0 } catch (ex: Exception) { 0 }
            val errorLoggerName = "${request.data?.msgId ?: "unknown"}-${errorClientIp}-${errorServerPort}"
            witcomLog.c_write(
                errorLoggerName,
                Level.ERROR,
                String.format("[mo-report] 예외 발생: msgId(%s), error(%s), stackTrace(%s)",
                    request.data?.msgId ?: "unknown",
                    e.message,
                    e.stackTraceToString()
                ),
                Thread.currentThread().getId()
            )
            val errorResponse = MoReportRequest().apply {
                msgVerId = request.msgVerId ?: 510
                encFlag = request.encFlag ?: 0
                data = MoReportRequest.DataBody().apply {
                    cid = request.data?.cid  // null 가능
                    msgId = request.data?.msgId ?: ""
                    traceId = request.data?.traceId ?: ""
                    status = 5  // UNDELIVERABLE
                    msgType = null  // 에러 응답에서는 제외됨
                }
            }
            ResponseEntity.ok(errorResponse)
        }
    }
}

