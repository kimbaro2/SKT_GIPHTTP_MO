package com.infra.mo.skt_giphttp_mo.controller

import ch.qos.logback.classic.Level
import com.fasterxml.jackson.databind.ObjectMapper
import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.MOCallInfoEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.MONotISendEntity
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
import com.infra.mo.skt_giphttp_mo.dto.smsController.MoReportRequest
import com.infra.mo.skt_giphttp_mo.service.SmsResService
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.MOCallInfoRepository
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.MONotISendRepository
import kotlinx.coroutines.runBlocking
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
    private val moCallInfoRepository: MOCallInfoRepository,
    private val moNotISendRepository: MONotISendRepository,
) {
    private val gServerID = System.getenv("SMSS_NO")?.trim()?.toIntOrNull() ?: 0

    /**
     * 로그 출력용 문자열 변환 헬퍼
     * - null, 빈 문자열, 공백 문자열은 "-" 로 통일
     */
    private fun toLogStr(value: String?): String =
        if (value.isNullOrBlank()) "-" else value

    /**
     * 로그 출력용 숫자 변환 헬퍼
     * - null 인 경우 "-" 로 통일
     */
    private fun toLogNum(value: Number?): String =
        value?.toString() ?: "-"

    /**
     * DCS 타입을 헤더 로그용 문자열로 변환
     * - 형식: "%02X:NAME" (예: "08:UCS2")
     * - 값이 없으면 "-" 로 표기
     */
    private fun toEncodingStr(dcsType: Int?): String {
        if (dcsType == null) return "-"

        val name = when (dcsType) {
            DCS_TYPE_DEC_ASCII7,
            DCS_TYPE_ASCII7.toInt() -> "ASCII7"

            DCS_TYPE_DEC_GSM7,
            DCS_TYPE_GSM7.toInt() -> "GSM7"

            DCS_TYPE_DEC_KSC5601,
            DCS_TYPE_KSC5601.toInt() -> "KSC5601"

            DCS_TYPE_DEC_UCS2,
            DCS_TYPE_UCS2.toInt() -> "UCS2"

            DCS_TYPE_DEC_8BIT,
            DCS_TYPE_8BIT.toInt() -> "8BIT"

            else -> "UNKNOWN"
        }

        val codeHex = String.format("%02X", dcsType and 0xFF)
        return "$codeHex:$name"
    }

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
        witcomLog.p_write(
            Level.INFO,
            String.format("[mo-report] API 인입 JSON: %s", jsonPayload)
        )

        // API 인입 확인용 로깅
        witcomLog.p_write(
            Level.INFO,
            String.format(
                "[mo-report] API 인입: cid(%s), srcCid(%s), destCid(%s), srcCallNo(%s), destCallNo(%s), msgId(%s), status(%d), traceId(%s), msgType(%d)",
                request.data?.accessCid ?: "null",
                request.data?.srcCid ?: "null",
                request.data?.destCid ?: "null",
                request.data?.srcCallNo ?: "null",
                request.data?.destCallNo ?: "null",
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
                witcomLog.p_write(Level.INFO, errorMsg)
                // 요청값 그대로 반환하되 status를 5로 변경
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        accessCid = ""
                        srcCid = ""
                        destCid = ""
                        srcCallNo = ""
                        destCallNo = ""
                        msgId = ""
                        status = 5  // UNDELIVERABLE
                        msgType = null
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }

            if (request.data.msgId.isNullOrBlank()) {
                val errorMsg = "Missing required parameter: data.msgId"
                witcomLog.p_write(Level.INFO, errorMsg)
                // 요청값 그대로 반환하되 status를 5로 변경
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        accessCid = request.data.accessCid ?: ""
                        srcCid = request.data.srcCid ?: ""
                        destCid = request.data.destCid ?: ""
                        srcCallNo = request.data.srcCallNo ?: ""
                        destCallNo = request.data.destCallNo ?: ""
                        msgId = request.data.msgId ?: ""
                        status = 5  // UNDELIVERABLE
                        msgType = null  // 에러 응답에서는 제외됨
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }

            if (request.data.status == null) {
                val errorMsg = "Missing required parameter: data.status"
                witcomLog.p_write(Level.INFO, errorMsg)
                // 요청값 그대로 반환하되 status를 5로 변경
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        accessCid = request.data.accessCid ?: ""
                        srcCid = request.data.srcCid ?: ""
                        destCid = request.data.destCid ?: ""
                        srcCallNo = request.data.srcCallNo ?: ""
                        destCallNo = request.data.destCallNo ?: ""
                        msgId = request.data.msgId ?: ""
                        status = 5  // UNDELIVERABLE
                        msgType = null  // 에러 응답에서는 제외됨
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }

            if (request.data.accessCid.isNullOrBlank()) {
                val errorMsg = "Missing required parameter: data.accessCid"
                witcomLog.p_write(Level.INFO, errorMsg)
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        accessCid = request.data.accessCid ?: ""
                        srcCid = request.data.srcCid ?: ""
                        destCid = request.data.destCid ?: ""
                        srcCallNo = request.data.srcCallNo ?: ""
                        destCallNo = request.data.destCallNo ?: ""
                        msgId = request.data.msgId ?: ""
                        status = 5  // UNDELIVERABLE
                        msgType = null  // 에러 응답에서는 제외됨
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }

            // srcCid / destCid 필수 검증 (traceId + srcCid + destCid로 DB 조회)
            if (request.data.srcCid.isNullOrBlank()) {
                val errorMsg = "Missing required parameter: data.srcCid"
                witcomLog.p_write(Level.INFO, errorMsg)
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        accessCid = request.data.accessCid ?: ""
                        srcCid = request.data.srcCid ?: ""
                        destCid = request.data.destCid ?: ""
                        srcCallNo = request.data.srcCallNo ?: ""
                        destCallNo = request.data.destCallNo ?: ""
                        msgId = request.data.msgId ?: ""
                        status = 5  // UNDELIVERABLE
                        msgType = null
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }

            if (request.data.destCid.isNullOrBlank()) {
                val errorMsg = "Missing required parameter: data.destCid"
                witcomLog.p_write(Level.INFO, errorMsg)
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        accessCid = request.data.accessCid ?: ""
                        srcCid = request.data.srcCid ?: ""
                        destCid = request.data.destCid ?: ""
                        srcCallNo = request.data.srcCallNo ?: ""
                        destCallNo = request.data.destCallNo ?: ""
                        msgId = request.data.msgId ?: ""
                        status = 5  // UNDELIVERABLE
                        msgType = null
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }

            // srcCallNo / destCallNo 필수 검증 (msgId + srcCid + destCid + srcCallNo + destCallNo 조합으로 식별)
            if (request.data.srcCallNo.isNullOrBlank()) {
                val errorMsg = "Missing required parameter: data.srcCallNo"
                witcomLog.p_write(Level.INFO, errorMsg)
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        accessCid = request.data.accessCid ?: ""
                        srcCid = request.data.srcCid ?: ""
                        destCid = request.data.destCid ?: ""
                        srcCallNo = request.data.srcCallNo ?: ""
                        destCallNo = request.data.destCallNo ?: ""
                        msgId = request.data.msgId ?: ""
                        status = 5  // UNDELIVERABLE
                        msgType = null
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }

            if (request.data.destCallNo.isNullOrBlank()) {
                val errorMsg = "Missing required parameter: data.destCallNo"
                witcomLog.p_write(Level.INFO, errorMsg)
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        accessCid = request.data.accessCid ?: ""
                        srcCid = request.data.srcCid ?: ""
                        destCid = request.data.destCid ?: ""
                        srcCallNo = request.data.srcCallNo ?: ""
                        destCallNo = request.data.destCallNo ?: ""
                        msgId = request.data.msgId ?: ""
                        status = 5  // UNDELIVERABLE
                        msgType = null
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }

            // MSG_TYPE 검증: 없으면 4로 가정 (MO-TR)
            val validatedMsgType = request.data.msgType ?: 4
            if (validatedMsgType != 4) {
                val errorMsg = "Invalid msgType: $validatedMsgType. MO-TR 요청은 msgType=4만 허용됩니다."
                witcomLog.p_write(Level.INFO, errorMsg)
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        accessCid = request.data.accessCid
                        srcCid = request.data.srcCid
                        destCid = request.data.destCid
                        srcCallNo = request.data.srcCallNo
                        destCallNo = request.data.destCallNo
                        msgId = request.data.msgId
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
                witcomLog.p_write(Level.INFO, errorMsg)
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        accessCid = request.data.accessCid
                        srcCid = request.data.srcCid
                        destCid = request.data.destCid
                        srcCallNo = request.data.srcCallNo
                        destCallNo = request.data.destCallNo
                        msgId = request.data.msgId
                        status = 5  // UNDELIVERABLE
                        msgType = null
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }

            if (serverPort == 0 || serverPort < 1) {
                val errorMsg = "Invalid server port: serverPort($serverPort). Cannot extract server port from request."
                witcomLog.p_write(Level.INFO, errorMsg)
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        accessCid = request.data.accessCid
                        srcCid = request.data.srcCid
                        destCid = request.data.destCid
                        srcCallNo = request.data.srcCallNo
                        destCallNo = request.data.destCallNo
                        msgId = request.data.msgId
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
                witcomLog.p_write(Level.INFO, errorMsg)
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        accessCid = request.data.accessCid
                        srcCid = request.data.srcCid
                        destCid = request.data.destCid
                        srcCallNo = request.data.srcCallNo
                        destCallNo = request.data.destCallNo
                        msgId = request.data.msgId
                        status = 5  // UNDELIVERABLE
                        msgType = null
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }

            val validMsgStatuses = setOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 14, 16, 17, 19, 20)
            if (status !in validMsgStatuses) {
                val errorMsg =
                    "Invalid status: $status. Valid values are: ${validMsgStatuses.sorted().joinToString(", ")}"
                witcomLog.p_write(Level.INFO, errorMsg)
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        accessCid = request.data.accessCid
                        srcCid = request.data.srcCid
                        destCid = request.data.destCid
                        srcCallNo = request.data.srcCallNo
                        destCallNo = request.data.destCallNo
                        msgId = request.data.msgId
                        status = 5  // UNDELIVERABLE
                        msgType = null
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }

            // cid, 요청자 IP 선검증 후 MOCALLINFO 조회 (cid, msgId, traceId, 요청자 IP 4가지 활용)
            val destCId = request.data.accessCid!!
            val loggerName = "${destCId}-${clientIp}-${serverPort}"

            // 1. HTTP_MOSEND_ACCESS에서 CID, 요청자 IP, PORT로 허용 여부 검증 (요청자 IP 활용)
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
                witcomLog.p_write(
                    Level.INFO,
                    String.format(
                        "[mo-report] HTTP_MOSEND_ACCESS 조회 실패: cid(%s), IP(%s), PORT(%d), error(%s)",
                        destCId, clientIp, serverPort, e.message
                    )
                )
                null
            }

            // HTTP_MOSEND_ACCESS 검증 실패: 요청값 그대로 반환하되 status를 5로 변경
            if (gipHttpMoAccess == null) {
                val errorMsg = "HTTP_MOSEND_ACCESS not found: cid($destCId), IP($clientIp), PORT($serverPort)"
                witcomLog.p_write(Level.INFO, errorMsg)
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        accessCid = destCId
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
                val errorMsg =
                    "cid mismatch: MOCALLINFO.destCId($destCId) != HTTP_MOSEND_ACCESS.cid(${gipHttpMoAccess.cid})"
                witcomLog.p_write(Level.INFO, errorMsg)
                val errorResponse = MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        accessCid = destCId
                        msgId = request.data.msgId
                        traceId = request.data.traceId
                        status = 5  // UNDELIVERABLE
                        msgType = null
                    }
                }
                return ResponseEntity.ok(errorResponse)
            }

            // 2. MOCALLINFO / MO_NOTISEND 조회 및 비즈니스 로직: 한 번의 runBlocking에서 조회·갱신·processMoReport까지 수행 (객체 재사용)
            // 조회/삭제/추가/업데이트 시 동일 키 사용: srcCID, srcCallNoForKey, destCID, destCallNoForKey, cpMsgId
            val srcCID = request.data.srcCid!!
            val destCID = request.data.destCid!!
            val srcCallNoForKey = request.data.srcCallNo!!
            val destCallNoForKey = request.data.destCallNo!!
            val cpMsgId = request.data.msgId!!

            data class MoReportResolved(
                val moCallInfo: MOCallInfoEntity,
                val moNotiToDelete: MONotISendEntity?,
                val skipMoNotiDelete: Boolean = false
            )

            val resolved = runBlocking {
                // 1차: MOCALLINFO 에서 msgId + srcCallNo + destCid 로 조회 후 srcCid/destCallNo 일치 여부 확인
                var moCallInfo = moCallInfoRepository.findBySrcAndDestAndMsgId(
                    srcCID,
                    srcCallNoForKey,
                    destCID,
                    destCallNoForKey,
                    cpMsgId,
                )?.takeIf { it.srcCId == srcCID && (it.destCallNo ?: "") == destCallNoForKey }

                val moNoti = if (moCallInfo == null) {
                    moNotISendRepository.findOneForVByMsgAndCidAndCallNo(
                        cpMsgId,
                        srcCID,
                        destCID,
                        srcCallNoForKey,
                        destCallNoForKey
                    ).orElse(null)
                } else null

                if (moCallInfo == null && moNoti == null) return@runBlocking null

                val pair = if (moCallInfo != null) {
                    // mo-report에서 수신한 msgId를 최종 MSGID로 사용
                    moCallInfo.msgId = cpMsgId
                    moCallInfoRepository.save(moCallInfo)
                    MoReportResolved(moCallInfo, null, false)
                } else {
                    // mo-report 경로: MO_NOTISEND UPDATE 없이 조회+삭제만 (buildMoCallInfoFromMoNotiForReport 사용)
                    val result = smsResService.buildMoCallInfoFromMoNotiForReport(cpMsgId, moNoti!!)
                    MoReportResolved(result.moCallInfo, result.moNotiToDelete, result.skipMoNotiDelete)
                }
                if (pair.moNotiToDelete != null) {
                    //안심/등기 로직
                    smsResService.processMoReportForNoti(request, pair.moCallInfo, gipHttpMoAccess, clientIp, serverPort, pair.moNotiToDelete, pair.skipMoNotiDelete)
                } else {
                    //그외 메시지 타입 로직
                    smsResService.processMoReportForGeneralMo(request, pair.moCallInfo, gipHttpMoAccess, clientIp, serverPort)
                }
                pair
            }

            if (resolved == null) {
                val errorMsg =
                    "No record found in MOCALLINFO/MO_NOTISEND: msgId=$cpMsgId, srcCID=$srcCID, destCID=$destCID, srcCallNo=$srcCallNoForKey, destCallNo=$destCallNoForKey, clientIp=$clientIp"
                witcomLog.p_write(Level.INFO, errorMsg)
                return ResponseEntity.ok(MoReportRequest().apply {
                    msgVerId = request.msgVerId ?: 510
                    encFlag = request.encFlag ?: 0
                    data = MoReportRequest.DataBody().apply {
                        accessCid = destCId
                        this.srcCid = srcCID
                        destCid = destCID
                        srcCallNo = srcCallNoForKey
                        destCallNo = destCallNoForKey
                        msgId = request.data.msgId
                        status = 5  // UNDELIVERABLE
                        msgType = null
                    }
                })
            }

            val (moCallInfo, _, _) = resolved

            // =================================================================================
            // [LOGGING START] REQ/RES 로그 구성을 위한 변수 설정
            // =================================================================================

            // 1. Server ID 및 Client Name 설정
            val serverIdStr = System.getenv("SMSS_NO")?.trim().takeUnless { it.isNullOrEmpty() } ?: "1"
            val serverName = "VSMSS#$serverIdStr"
            val clientName = gipHttpMoAccess.description.takeUnless { it.isNullOrEmpty() } ?: "Unknown"

            // 2. 상태값 문자열 매핑 (SmsResServiceImpl / MoSendToCpServiceImpl과 동일)
            val statusValue = request.data.status ?: -1
            val statusStr = when (statusValue) {
                0 -> "0:FWD_DETECT_CID"
                1 -> "1:MRMSPAM"
                2 -> "2:DELIVERED"
                3 -> "3:EXPIRED"
                4 -> "4:DELETED"
                5 -> "5:UNDELIVERABLE"
                6 -> "6:ACCEPTED"
                7 -> "7:PORTED_OUT"
                8 -> "8:PORTEDOUT_KTF"
                9 -> "9:PORTEDOUT_LGT"
                10 -> "10:PORTEDOUT_SKT"
                12 -> "12:FORWARD"
                13 -> "13:NCHANGE"
                14 -> "14:FWDFAIL"
                16 -> "16:SPAMERR"
                17 -> "17:USERDEL"
                19 -> "19:NPREFIX"
                20 -> "20:ADMCANC"
                else -> "$statusValue:UNKNOWN"
            }

            // 3. 인코딩 문자열 매핑
            val encodingStr = toEncodingStr(moCallInfo.dcsType)

            // 4. [REQ 로그 작성] SmsResServiceImpl REQ_TRANS_RESULT와 동일: [GIPEVENT_C_%s] [REQ_TRANS_RESULT] [%s->VSMSS#%d] + 동일 필드/인자
            // ConcatenateFlag/ConcatenateInfo: QITEM 사용 시에는 qItem.totalSeg.toInt(), qItem.segSeq.toInt() 사용. 본 경로는 qItem 없음.
            val logNo = String.format("%04d", serverIdStr.toIntOrNull() ?: 1)
            val reqLogContent = String.format(
                "[GIPHTTPMO_C_%s] [REQ_TRANS_RESULT] [%s->VSMSS#%d] MsgVerId(%d) EncFlag(%d) SrcCId(%s) SrcCallNo(%s) DestCId(%s) DestCallNo(%s) MsgCode(%d) MsgSubCode(%d) BodyDataLen(%d) MsgSeqNo(%d) DataEncoding(%s) TermType(%s) ConcatenateFlag(%s) ConcatenateInfo(%s) Result(%d) Status(%s) MsgId(%s)",
                logNo,
                clientName,
                gServerID,
                request.msgVerId ?: 0,
                request.encFlag ?: 0,
                toLogStr(moCallInfo.srcCId),
                toLogStr(moCallInfo.srcCallNo),
                toLogStr(destCId),
                toLogStr(moCallInfo.destCallNo ?: moCallInfo.cb),
                0,
                0,
                0,
                0,
                toLogStr(encodingStr),
                "",  // TermType (qItem 없음)
                "",  // ConcatenateFlag: qItem 있으면 qItem.totalSeg.toInt()
                "",  // ConcatenateInfo: qItem 있으면 qItem.segSeq.toInt()
                0,
                statusStr,
                toLogStr(request.data.msgId)
            )
            witcomLog.c_write(loggerName, Level.INFO, reqLogContent, Thread.currentThread().getId())

            // =================================================================================
            // [LOGGING END] RES 로그 작성 MoSendToCpServiceImpl RES_TRANS_RESULT와 동일. 방향성 지정: [VSMSS#%d->%s] + logNo, cid, msgId, status, traceId, ResponseStatus, ResponseBody
            // =================================================================================

            val resLogContent = String.format(
                "[GIPHTTPMO_C_%s] [RES_TRANS_RESULT] [VSMSS#%d->%s] logNo(%s) cid(%s) msgId(%s) status(%s) traceId(%s) ResponseStatus(%s) ResponseBody(%s)",
                logNo,
                gServerID,
                clientName,
                logNo,
                toLogStr(destCId),
                toLogStr(request.data.msgId),
                statusStr,
                toLogStr(request.data.traceId),
                "200",
                "OK"
            )
            witcomLog.c_write(loggerName, Level.INFO, resLogContent, Thread.currentThread().getId())

            // HTTP 응답 생성: 조회된 MOCALLINFO(moCallInfo) 포함
            val moCallInfoBody = MoReportRequest.MoCallInfoBody.fromEntity(moCallInfo)
            val responseBody = MoReportRequest().apply {
                msgVerId = request.msgVerId ?: 510
                encFlag = request.encFlag ?: 0
                data = MoReportRequest.DataBody().apply {
                    accessCid = destCId
                    srcCid = srcCID
                    destCid = destCID
                    srcCallNo = srcCallNoForKey
                    destCallNo = destCallNoForKey
                    msgId = request.data.msgId
                    status = request.data.status
                    msgType = validatedMsgType
                    this.moCallInfo = moCallInfoBody
                }
            }

            return ResponseEntity.ok(responseBody)
        } catch (e: Exception) {
            // 예외 처리
            val errorClientIp = try {
                val xff = exchange.request.headers.getFirst("X-Forwarded-For")
                xff?.split(",")?.firstOrNull()?.trim() ?: exchange.request.remoteAddress?.address?.hostAddress
                ?: "unknown"
            } catch (ex: Exception) {
                "unknown"
            }
            val errorServerPort = try {
                exchange.request.localAddress?.port ?: 0
            } catch (ex: Exception) {
                0
            }
            val errorLoggerName = "${request.data?.msgId ?: "unknown"}-${errorClientIp}-${errorServerPort}"
            witcomLog.p_write(
                Level.INFO,
                String.format(
                    "[mo-report] 예외 발생: msgId(%s), error(%s), stackTrace(%s)",
                    request.data?.msgId ?: "unknown",
                    e.message,
                    e.stackTraceToString()
                )
            )
            val errorResponse = MoReportRequest().apply {
                msgVerId = request.msgVerId ?: 510
                encFlag = request.encFlag ?: 0
                data = MoReportRequest.DataBody().apply {
                    accessCid = request.data?.accessCid  // null 가능
                    srcCid = request.data?.srcCid
                    destCid = request.data?.destCid
                    srcCallNo = request.data?.srcCallNo
                    destCallNo = request.data?.destCallNo
                    msgId = request.data?.msgId ?: ""
                    status = 5  // UNDELIVERABLE
                    msgType = null  // 에러 응답에서는 제외됨
                }
            }
            ResponseEntity.ok(errorResponse)
        }
    }
}

