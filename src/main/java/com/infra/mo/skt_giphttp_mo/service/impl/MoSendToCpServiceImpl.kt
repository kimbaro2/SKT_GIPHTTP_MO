package com.infra.mo.skt_giphttp_mo.service.impl

import ch.qos.logback.classic.Level
import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.dto.SegmentInfo
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_FAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MSG_CODE_SM_RES
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MODULEID_GIPEVENT_C
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SERVICEID_GIPEVENT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_SIMPLE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_DELIVERED
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MESSAGE_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.TID_NO_SAVE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LT_TRACE
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_GIP_SOCK_MAX_RETRY_FAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_GIP_SOCK_SEND_FAIL
import com.infra.mo.skt_giphttp_mo.dto.smsController.MoReportRequest
import com.infra.mo.skt_giphttp_mo.service.MoSendToCpService
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib
import io.netty.channel.ChannelOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.net.URI
import java.time.Duration

/**
 * CP 서버로 MO/MO-TR 전송 서비스 구현.
 * sendMoMessageToCp: MO 메시지 HTTP POST 전송 (재시도 포함).
 * doSendMoTr: MO-TR 결과 HTTP POST 전송.
 */
@Service
class MoSendToCpServiceImpl(
    private val witcomLog: WitcomLog
) : MoSendToCpService {

    override suspend fun sendMoMessageToCp(
        moResult: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        qItem: QITEM?,
        smsQLib: SmsQLib,
        gServerID: Int,
        loggerName: String,
        workerThreadId: Long,
        segmentInfo: SegmentInfo?
    ): Boolean {
        val maxRetryCount = entity.rc
        val connectionTimeoutSeconds = entity.tc
        val totalAttempts = if (maxRetryCount == 0) 1 else maxRetryCount + 1

        for (attempt in 1..totalAttempts) {
            if (attempt > 1) {
                witcomLog.c_write(
                    loggerName,
                    Level.INFO,
                    String.format("MO HTTP 전송 재시도: 시도(%d/%d), CP_URL(%s), MsgSeqNo(%d)", attempt, totalAttempts, entity.cpUrl, qItem?.uMsgSerialNo?.toInt() ?: 0),
                    workerThreadId
                )
            }

            val success = doSendMoMessage(
                moResult,
                entity,
                qItem,
                smsQLib,
                gServerID,
                connectionTimeoutSeconds,
                attempt,
                totalAttempts,
                loggerName,
                workerThreadId,
                segmentInfo
            )
            if (success) {
                if (attempt > 1) {
                    witcomLog.c_write(loggerName, Level.INFO, String.format("MO HTTP 전송 재시도 성공: 시도(%d/%d), CP_URL(%s)", attempt, totalAttempts, entity.cpUrl), workerThreadId)
                }
                return true
            }
        }

        if (qItem != null) {
            smsQLib.InsqStat(
                qItem,
                MESSAGE_MO,
                0,
                gServerID,
                MODULEID_GIPEVENT_C,
                SERVICEID_GIPEVENT,
                ERRORID_CP_MO_FAIL,
                ST_GIP_SOCK_MAX_RETRY_FAIL,
                maxRetryCount,
                TID_NO_SAVE,
                LT_TRACE,
                0
            )
        }
        return false
    }

    override suspend fun sendMoTrToCp(
        qItem: QITEM,
        moResult: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        loggerName: String,
        workerThreadId: Long
    ): Boolean {
        val maxRetryCount = entity.rc
        val connectionTimeoutSeconds = entity.tc
        val totalAttempts = if (maxRetryCount == 0) 1 else maxRetryCount + 1
        val gServerID = System.getenv("SMSS_NO")?.trim()?.toIntOrNull() ?: 0

        for (attempt in 1..totalAttempts) {
            val success = doSendMoTr(
                qItem,
                moResult,
                entity,
                connectionTimeoutSeconds,
                attempt,
                totalAttempts,
                loggerName,
                workerThreadId,
                gServerID
            )
            if (success) return true
        }
        return false
    }

    private suspend fun doSendMoMessage(
        moResult: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        qItem: QITEM?,
        smsQLib: SmsQLib,
        gServerID: Int,
        connectionTimeoutSeconds: Int,
        attempt: Int,
        totalAttempts: Int,
        loggerName: String,
        workerThreadId: Long,
        segmentInfo: SegmentInfo?
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val cid = entity.cid ?: ""
            val msgId = qItem?.let { QItemServiceUtil.byteArrayToKString(it.ucMsgId) } ?: ""
            val traceId = qItem?.let { QItemServiceUtil.byteArrayToKString(it.szTraceId) } ?: ""
            val gipverid = entity.gipverid ?: 510

            val moReportRequest = MoReportRequest().apply {
                msgVerId = gipverid
                encFlag = 0
                data = MoReportRequest.DataBody().apply {
                    this.cid = cid
                    this.msgId = msgId
                    this.traceId = traceId
                    this.status = SM_STATE_DELIVERED
                }
            }

            witcomLog.c_write(loggerName, Level.INFO, String.format("MO Send Request JSON: %s", moReportRequest.toString()), workerThreadId)

            val connectionTimeoutMillis = connectionTimeoutSeconds * 1000L
            val client = WebClient.builder()
                .baseUrl(entity.cpUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(
                    ReactorClientHttpConnector(
                        HttpClient.create()
                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeoutMillis.toInt())
                            .responseTimeout(Duration.ofSeconds(connectionTimeoutSeconds.toLong()))
                    )
                )
                .build()

            witcomLog.c_write(
                loggerName,
                Level.INFO,
                String.format("MO HTTP POST 요청 전송: CP_URL(%s), 시도(%d/%d), MsgSeqNo(%d), BodySize(%d bytes)", entity.cpUrl, attempt, totalAttempts, qItem?.uMsgSerialNo?.toInt() ?: 0, moReportRequest.toString().length),
                workerThreadId
            )

            val response = client.post()
                .uri(URI(entity.cpUrl))
                .bodyValue(moReportRequest)
                .exchangeToMono { clientResponse ->
                    clientResponse.bodyToMono(String::class.java)
                        .defaultIfEmpty("")
                        .map { body ->
                            val statusCode = clientResponse.statusCode()
                            val isSuccess = statusCode.is2xxSuccessful
                            val statusText = String.format("%d:%s", statusCode.value(), statusCode.reasonPhrase)
                            val logNo = entity.logNo?.let { String.format("%04d", it.toIntOrNull() ?: 0) } ?: "0000"
                            val cpName = entity.cpName ?: ""
                            // 응답 바디가 비어있으면 "EMPTY"로 표기 (HTTP Status는 별도 필드로 항상 출력)
                            val resBodyForLog = if (body.isNotBlank()) body else "EMPTY"
                            val resLog = String.format(
                                "[GIPHTTPMO_C_%s] [RES_SIMPLE] [%s->SMSS#%d] SrcCId(%s) SrcCallNo(%s) DestCId(%s) DestCallNo(%s) MsgID(%s) HttpStatus(%s) MsgCode(%d) MsgSubCode(%d) HttpBody(%s)",
                                logNo,
                                cpName,
                                gServerID,
                                moResult.srcCid,
                                moResult.srcMinNo,
                                moResult.destCid,
                                moResult.destMinNo,
                                msgId,
                                statusText,
                                MSG_CODE_SM_RES,
                                SM_REQ_SIMPLE,
                                resBodyForLog
                            )
                            witcomLog.c_write(loggerName, Level.INFO, resLog, workerThreadId)
                            witcomLog.c_write(
                                loggerName,
                                if (isSuccess) Level.INFO else Level.INFO,
                                String.format("MO HTTP POST 응답 수신: CP_URL(%s), StatusCode(%d), 시도(%d/%d), 성공(%s), BodySize(%d bytes)", entity.cpUrl, statusCode.value(), attempt, totalAttempts, if (isSuccess) "YES" else "NO", body.length),
                                workerThreadId
                            )
                            statusCode
                        }
                }
                .timeout(Duration.ofSeconds(connectionTimeoutSeconds.toLong()))
                .awaitSingle()

            val isSuccess = response.is2xxSuccessful
            witcomLog.c_write(
                loggerName,
                if (isSuccess) Level.INFO else Level.INFO,
                String.format("MO HTTP 전송 최종 결과: CP_URL(%s), 시도(%d/%d), 결과(%s), StatusCode(%d)", entity.cpUrl, attempt, totalAttempts, if (isSuccess) "SUCCESS" else "FAILED", response.value()),
                workerThreadId
            )
            isSuccess
        } catch (e: Exception) {
            witcomLog.c_write(loggerName, Level.INFO, String.format("MO 메시지 전송 중 예외 발생: CP_URL(%s), 시도(%d/%d)", entity.cpUrl, attempt, totalAttempts), workerThreadId)
            if (qItem != null) {
                smsQLib.InsqStat(qItem, MESSAGE_MO, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT, ERRORID_CP_MO_FAIL, ST_GIP_SOCK_SEND_FAIL, attempt, TID_NO_SAVE, LT_TRACE, 0)
            }
            false
        }
    }

    override suspend fun doSendMoTr(
        qItem: QITEM,
        moResult: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        connectionTimeoutSeconds: Int,
        attempt: Int,
        totalAttempts: Int,
        loggerName: String,
        workerThreadId: Long,
        gServerID: Int
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val msgId = QItemServiceUtil.byteArrayToKString(qItem.ucMsgId)
            val traceId = QItemServiceUtil.byteArrayToKString(qItem.szTraceId)
            val cid = entity.cid ?: ""
            val msgStatus = qItem.ucMsgStatus.toInt()

            val moReportRequest = MoReportRequest().apply {
                msgVerId = 510
                encFlag = 0
                data = MoReportRequest.DataBody().apply {
                    this.cid = cid
                    this.msgId = msgId
                    this.traceId = traceId
                    this.status = msgStatus
                }
            }

            witcomLog.c_write(loggerName, Level.INFO, String.format("MO-TR Send Request JSON: %s", moReportRequest.toString()), workerThreadId)

            val connectionTimeoutMillis = connectionTimeoutSeconds * 1000L
            val client = WebClient.builder()
                .baseUrl(entity.cpUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(
                    ReactorClientHttpConnector(
                        HttpClient.create()
                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeoutMillis.toInt())
                            .responseTimeout(Duration.ofSeconds(connectionTimeoutSeconds.toLong()))
                    )
                )
                .build()

            val logNoForLog = entity.logNo?.let { String.format("%04d", it.toIntOrNull() ?: 0) } ?: "0000"
            witcomLog.c_write(
                loggerName,
                Level.INFO,
                String.format(
                    "MO-TR HTTP POST 요청 전송: CP_URL(%s), 시도(%d/%d), logNo(%s), cid(%s), msgId(%s), status(%d), traceId(%s), BodySize(%d bytes)",
                    entity.cpUrl, attempt, totalAttempts, logNoForLog, moReportRequest.data.cid, moReportRequest.data.msgId, moReportRequest.data.status ?: -1, moReportRequest.data.traceId, moReportRequest.toString().length
                ),
                workerThreadId
            )

            val response = client.post()
                .uri(URI(entity.cpUrl))
                .bodyValue(moReportRequest)
                .exchangeToMono { clientResponse ->
                    clientResponse.bodyToMono(String::class.java)
                        .defaultIfEmpty("")
                        .map { body ->
                            val reportLogNo = entity.logNo?.let { String.format("%04d", it.toIntOrNull() ?: 0) } ?: "0000"
                            val cpName = entity.cpName ?: ""
                            val statusValue = moReportRequest.data.status ?: -1
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
                            val resTransResultLog = String.format(
                                "[GIPALL_C_%s] [RES_TRANS_RESULT] [VSMSS#%d->%s] logNo(%s) cid(%s) msgId(%s) status(%s) traceId(%s) ResponseStatus(%s) ResponseBody(%s)",
                                reportLogNo, gServerID, cpName, reportLogNo, moReportRequest.data.cid, moReportRequest.data.msgId, statusStr, moReportRequest.data.traceId,
                                clientResponse.statusCode().toString(),
                                if (body.length > 200) body.substring(0, 200) + "..." else body
                            )
                            witcomLog.c_write(loggerName, Level.INFO, resTransResultLog, workerThreadId)
                            clientResponse.statusCode()
                        }
                }
                .timeout(Duration.ofSeconds(connectionTimeoutSeconds.toLong()))
                .awaitSingle()

            response.is2xxSuccessful
        } catch (e: Exception) {
            val errorMsgId = QItemServiceUtil.byteArrayToKString(qItem.ucMsgId)
            val errorTraceId = QItemServiceUtil.byteArrayToKString(qItem.szTraceId)
            val errorLogNo = entity.logNo?.let { String.format("%04d", it.toIntOrNull() ?: 0) } ?: "0000"
            val errorCid = entity.cid ?: ""
            val msgStatusErr = qItem.ucMsgStatus.toInt()
            witcomLog.c_write(
                loggerName,
                Level.INFO,
                String.format(
                    "MO-TR 결과 전송 중 예외 발생: CP_URL(%s), 시도(%d/%d), LOG_NO(%s), CID(%s), MSG_ID(%s), MSG_STATUS(%d), TRACE_ID(%s), Error(%s)",
                    entity.cpUrl, attempt, totalAttempts, errorLogNo, errorCid, errorMsgId, msgStatusErr, errorTraceId, e.message
                ),
                workerThreadId
            )
            false
        }
    }
}
