package com.infra.mo.skt_giphttp_mo.service.impl

import ch.qos.logback.classic.Level
import com.infra.mo.skt_giphttp_mo.config.application.LiveReloadCLibraryFile
import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GIENQEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.MOCallInfoEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.MONotISendEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.GIENQRepository
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.GipHttpMoAccessRepository
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.MOCallInfoRepository
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.MONotISendRepository
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.COMMON_SMS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.AI_SURVEY_NUMBER
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_8BIT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_ASCII7
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_8BIT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_ASCII7
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_GSM7
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_KSC5601
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_UCS2
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_UNKNOWN
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_GSM7
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_KSC5601
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_UCS2
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_UNKNOWN
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.CDMA_ROAMING
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.GSM_WCDMA_ROAMING
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.PORTED_CDMA_ROAMING
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.PORTED_GSM_WCDMA_ROAMING
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_FAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_NODATA
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_SUCCESS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CENTER_MO_SUCCESS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_TR_FAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_TR_SUCCESS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_TR_SUCCESS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CENTER_TR_SUCCESS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CENTER_TR_EXPIRED
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CENTER_TR_PORTOUT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.GI_RES_NO_ERR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.IF_NULL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LT_BOTH
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LT_TRACE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MESSAGE_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MESSAGE_TR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MODULEID_GIPEVENT_C
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MODULEID_VBILLMO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NOTI_PLUS_NORMAL_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NOTI_PLUS_PORTED_MO
import com.infra.mo.skt_giphttp_mo.service.handler.EsmClassHandler
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NOTI_NORMAL_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NOTI_PORTED_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NORMAL_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.PORTED_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.FORWARD_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NUMBER_PLUS_CDMA_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NUMBER_PLUS_GSM_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.BIZ_NUMBER_CDMA_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.BIZ_NUMBER_GSM_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.QTYPE_SM_REQ
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.RCS_RESULT_ETC
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.RCS_RESULT_SENT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.RCS_TO_SMS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SEND_OK
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SERVICEID_GIPEVENT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_SEND
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_SIMPLE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_TRANS_RESULT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_DB_NO_DATA_GIPMOCALLINFO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIPEVENT_MOACK_BILL_OK
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIPEVENT_MO_OK
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIPEVENT_MORS_OK
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIPEVENT_MTTR_OK
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIP_INVALID_CID
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIP_MORS_FAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIPEVENT_MOTR_OK
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_Q_INSERT_FAIL_VBILLMO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SUB_QTYPE_RCS_TR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.TERM_TYPE_KOR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.TID_NO_SAVE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.TID_SAVE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.VSMSS_TYPE
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_VBILLMO_DONT_BILL_TRFAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_VBILLMO_OK
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_VBILLMO_NOTISEND_OK
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_VRECV_TR_EXPIRED
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_VRECV_TR_UNDELIVERED
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_SMSMOR_TR_FWDFAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_SMSMOR_TR_SPAMERR
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_SMSMOR_TR_USERDEL
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_SMSMOR_TR_NPREFIX
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_SMSMOR_TR_ADMCANC
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_SMSMOR_TR_SEND
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_SMSMOR_TR_PORTOUT
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_SMSMOR_TR_FORWARD
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_SMSMOR_TR_UNDELIVERED
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseTR
import com.infra.mo.skt_giphttp_mo.dto.smsController.MoReportRequest
import com.infra.mo.skt_giphttp_mo.service.SmsResService
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * SMS 결과 처리 서비스 구현체
 * 
 * C 코드 참고: GIPEVENT_c.c LINE 1928 (ProcessSMRes)
 * - SM_REQ_SIMPLE: MO 메시지 ACK 처리
 * - SM_REQ_TRANS_RESULT: TR 결과 처리
 */
@Service
open class SmsResServiceImpl(
    private val liveReloadCLibraryFile: LiveReloadCLibraryFile,
    private val witcomLog: WitcomLog,
    @Qualifier("ApplicationCacheMap") private val cacheMap: ConcurrentHashMap<String, Any>,
    private val moCallInfoRepository: MOCallInfoRepository,
    private val gienqRepository: GIENQRepository,
    private val gipHttpMoAccessRepository: GipHttpMoAccessRepository,
    private val moNotISendRepository: MONotISendRepository
) : SmsResService {
    @PersistenceContext
    private lateinit var entityManager: EntityManager
    
    private val gServerID: Int = run {
        val smssNo = System.getenv("SMSS_NO")?.trim()
        if (smssNo.isNullOrBlank()) {
            throw IllegalStateException("환경변수 SMSS_NO가 설정되지 않았습니다. SMSS_NO는 필수 환경변수입니다.")
        }
        val serverId = smssNo.toIntOrNull()
        if (serverId == null || serverId <= 0) {
            throw IllegalStateException("환경변수 SMSS_NO 값이 유효하지 않습니다. SMSS_NO=$smssNo (1 이상의 정수여야 합니다)")
        }
        serverId
    }
    
    /**
     * qItem.usSource를 nInforNo로 반환
     * dequeue된 QITEM의 usSource는 항상 유효한 값이므로 직접 사용
     * IF_NULL은 에러 케이스이므로 사용하지 않음
     */
    private fun getNInforNo(qItem: QITEM): Int {
        return qItem.usSource
    }
    
    /**
     * bprintf 호출 후 VSTAT 15 InsqStat 호출 (MO-ACK 성공 시)
     *
     * @param qItem QITEM
     * @param request ResponseTR
     * @param smsQLib SmsQLib
     * @param loggerName 로거 이름
     * @param destCID 목적지 CID
     */
    private fun recordVstat15AfterBprintf(
        qItem: QITEM,
        request: ResponseTR,
        smsQLib: SmsQLib,
        loggerName: String,
        destCID: String
    ) {
        val ackResult = request.data.ackResult ?: GI_RES_NO_ERR
        val isMOACKForVstat15 = request.data.msgSubCode.toInt() == SM_REQ_SIMPLE
        val shouldRecordVstat15 = (ackResult == GI_RES_NO_ERR) && isMOACKForVstat15

        if (!shouldRecordVstat15) {
            return
        }

        val msgId = request.data.msgId ?: ""

        witcomLog.c_write(
            loggerName,
            Level.INFO,
            String.format(
                "[processMOBilling] bprintf 호출 후 InsqStat(VSTAT 15 + TRC) 기록 시작 - destCID(%s), ackResult(%d), msgId(%s)",
                destCID,
                ackResult,
                msgId
            ),
            Thread.currentThread().getId()
        )

        // InsqStat 호출: VSTAT 15와 TRC를 모두 기록 (LT_BOTH)
        // C 코드 LINE 2219-2220 패턴과 동일하게, 같은 MsgId로 35/15 두 번 InsqStat 호출
        // nErrorID 파라미터가 실제 VSTAT 파일에 기록되는 값이므로 15를 사용
        // nInforNo: qItem.usSource 사용 (IF_NULL은 에러 케이스이므로 사용하지 않음)
        val insqStatResult15 = smsQLib.InsqStat(
            qItem,
            MESSAGE_MO,
            0,
            gServerID,
            MODULEID_GIPEVENT_C,
            SERVICEID_GIPEVENT,
            ERRORID_CP_MO_SUCCESS,  // 15 (VSTAT 파일에 기록되는 실제 값)
            ST_GIPEVENT_MOACK_BILL_OK,  // 80 (과금성공(MOACK))
            getNInforNo(qItem),  // qItem.usSource (nInforNo)
            TID_NO_SAVE,
            LT_BOTH,  // VSTAT(15) + TRC 모두 기록 (VVST 옵션=15 저장)
            0
        )

        witcomLog.c_write(
            loggerName,
            if (insqStatResult15 == 1) Level.INFO else Level.ERROR,
            String.format(
                "[processMOBilling] InsqStat(VSTAT 15 + TRC) 호출 결과: 반환값(%d), 성공여부(%b), destCID(%s), msgId(%s)",
                insqStatResult15,
                insqStatResult15 == 1,
                destCID,
                msgId
            ),
            Thread.currentThread().getId()
        )
    }
    
    // bprintf 로그 파일 경로: C 코드 bprintf.c LINE 78 - getenv("SMS_VCDR")/LOG/
    // JNA bprintf 함수가 내부적으로 파일 경로를 처리하므로 여기서는 주석으로만 남김
    // private val vcdrDir: String = System.getenv("SMS_VCDR") ?: "/APP/sms/vcdr"

    // CFG_VBILL(PROC_TYPE='MO')에서 VBILL_MO 큐 번호를 조회하여 캐싱
    @Volatile
    private var cachedVbillMoQueueNo: Int? = null

    /**
     * loggerName 생성 헬퍼 함수
     * gipHttpAccessMap의 키 형식과 일치하도록 생성: "${cid}-${ipAddr}-${portNo}"
     * 
     * @param destCID 목적지 CID
     * @param clientIp 클라이언트 IP (선택)
     * @param serverPort 서버 포트 (선택)
     * @return loggerName (gipHttpAccessMap 키 형식)
     */
    private fun getLoggerName(destCID: String?, clientIp: String?, serverPort: Int?): String {
        return when {
            !destCID.isNullOrBlank() && clientIp != null && serverPort != null ->
                "$destCID-$clientIp-$serverPort"
            !destCID.isNullOrBlank() ->
                "$destCID-unknown-unknown"
            else ->
                "unknown-unknown-unknown"
        }
    }

    private fun getVbillMoQueueNo(): Int {
        cachedVbillMoQueueNo?.let { return it }

        val query = """
            SELECT QUEUE_NO
            FROM SMS.CFG_VBILL
            WHERE PROC_TYPE = 'MO'
        """.trimIndent()

        val result = entityManager.createNativeQuery(query).resultList
        val queueNo = (result.firstOrNull() as? Number)?.toInt()
            ?: throw IllegalStateException("CFG_VBILL(PROC_TYPE='MO') has no QUEUE_NO")

        cachedVbillMoQueueNo = queueNo
        return queueNo
    }
    
    /**
     * C 코드의 ProcessSMRes() 함수와 동일한 로직 수행
     * 
     * @param request ResponseTR (CP 서버로부터 받은 결과)
     */
    override suspend fun processSMRes(request: ResponseTR, clientIp: String?, serverPort: Int?, queueNo: Int?, loggerName: String?) {
        // loggerName: 전달받은 값 사용, 없으면 자동 생성
        val effectiveLoggerName = loggerName ?: getLoggerName(request.data.destCID, clientIp, serverPort)
        
        // 중복 호출 방지: 같은 msgId에 대해 이미 처리 중이거나 처리 완료된 경우 스킵
        val msgId = request.data.msgId ?: ""
        val msgSubCodeValue = request.data.msgSubCode?.toInt() ?: -1
        val processingCacheKey = "PROCESSING_SMRES:$msgId:$msgSubCodeValue"
        
        // 동시성 제어: putIfAbsent를 사용하여 원자적 연산 보장
        // 이미 처리 중이거나 처리 완료된 경우 스킵
        val alreadyProcessing = cacheMap.putIfAbsent(processingCacheKey, true) != null
        
        if (alreadyProcessing) {
            witcomLog.c_write(effectiveLoggerName, Level.WARN,
                String.format("[processSMRes] 중복 호출 스킵: 이미 처리 중이거나 처리 완료됨 - msgId(%s), msgSubCode(%d), destCID(%s)",
                    msgId, msgSubCodeValue, request.data.destCID ?: "null"),
                Thread.currentThread().getId()
            )
            return
        }

        try {
            witcomLog.c_write(effectiveLoggerName, Level.INFO,
                String.format("[processSMRes] 함수 진입: loggerName(%s), destCID(%s), clientIp(%s), serverPort(%d), msgSubCode(%d), msgId(%s)",
                    effectiveLoggerName,
                    request.data.destCID ?: "null",
                    clientIp ?: "null",
                    serverPort ?: 0,
                    msgSubCodeValue,
                    msgId
                ),
                Thread.currentThread().getId()
            )

            // C 코드 LINE 1978: MsgHdrToQItem
            val qItem = QItemServiceUtil.responseTRToQItem(request)
            qItem.ucServerType = VSMSS_TYPE.code.toByte()
            
            // C 코드 LINE 1981: switch(ptrMsgHdr->usMsgSubCode)
            // msgSubCodeValue는 이미 위에서 추출됨
            witcomLog.c_write(effectiveLoggerName, Level.INFO, 
            String.format("[processSMRes] 분기 진입: msgSubCode(%d), msgCode(%d), destCID(%s), srcCallNo(%s), msgId(%s), result(%s)",
                msgSubCodeValue,
                request.data.msgCode?.toInt() ?: 0,
                request.data.destCID ?: "",
                request.data.srcCallNo ?: "",
                request.data.msgId ?: "",
                request.data.result?.toString() ?: "null"
            ),
            Thread.currentThread().getId()
        )
        
        when (msgSubCodeValue) {
            SM_REQ_SIMPLE -> {
                witcomLog.c_write(effectiveLoggerName, Level.INFO, 
                    String.format("[processSMRes] SM_REQ_SIMPLE 분기 선택: msgSubCode(%d) - MO ACK 처리 시작", msgSubCodeValue),
                    Thread.currentThread().getId()
                )
                // C 코드 LINE 1983-2245: MO ACK 처리
                processSMReqSimple(qItem, request, clientIp, serverPort, queueNo, effectiveLoggerName)
                witcomLog.c_write(effectiveLoggerName, Level.INFO, 
                    String.format("[processSMRes] SM_REQ_SIMPLE 분기 완료: msgSubCode(%d) - MO ACK 처리 완료", msgSubCodeValue),
                    Thread.currentThread().getId()
                )
            }
            SM_REQ_TRANS_RESULT -> {
                witcomLog.c_write(effectiveLoggerName, Level.INFO, 
                    String.format("[processSMRes] SM_REQ_TRANS_RESULT 분기 선택: msgSubCode(%d) - TR 결과 처리 시작", msgSubCodeValue),
                    Thread.currentThread().getId()
                )
                // C 코드 LINE 2247-2272: TR 결과 처리
                processSMReqTransResult(qItem, request, clientIp, serverPort, queueNo, effectiveLoggerName)
                witcomLog.c_write(effectiveLoggerName, Level.INFO, 
                    String.format("[processSMRes] SM_REQ_TRANS_RESULT 분기 완료: msgSubCode(%d) - TR 결과 처리 완료", msgSubCodeValue),
                    Thread.currentThread().getId()
                )
            }
            else -> {
                witcomLog.c_write(
                    effectiveLoggerName,
                    Level.ERROR,
                    String.format("[processSMRes] Invalid 분기: msgSubCode(%d) - 지원하지 않는 msgSubCode", msgSubCodeValue),
                    Thread.currentThread().getId()
                )
            }
        }
        } catch (e: Exception) {
            witcomLog.c_write(
                effectiveLoggerName,
                Level.ERROR,
                String.format("[processSMRes] 예외 발생: destCID(%s), msgId(%s), error(%s), stackTrace(%s)",
                    request.data.destCID ?: "null",
                    msgId,
                    e.message ?: "null",
                    e.stackTraceToString()
                ),
                Thread.currentThread().getId()
            )
            // 예외 발생 시 캐시 정리 (재처리 가능하도록)
            cacheMap.remove(processingCacheKey)
            throw e
        }
        // 정상 완료 시 캐시는 유지 (같은 요청이 다시 들어오면 중복 방지)
        // 메모리 누수 방지를 위해서는 별도의 TTL 기반 캐시 정리 로직이 필요할 수 있음
    }
    
    /**
     * C 코드 LINE 1983-2245: SM_REQ_SIMPLE 케이스 처리 (MO ACK)
     */
    private suspend fun processSMReqSimple(
        qItem: QITEM, 
        request: ResponseTR,
        clientIp: String? = null,
        serverPort: Int? = null,
        queueNo: Int? = null,
        parentLoggerName: String? = null
    ) {
        // C 코드 LINE 1989-1990: gMsgSendReady, gRetryCount 초기화
        // HTTP 환경에서는 불필요
        
        val smsQLib: SmsQLib = liveReloadCLibraryFile.getPreInitializedLibrary(0)
        
        // JNA dequeue (clientIp == null): CID + 127.0.0.1 + PORT (MSG_TYPE 제외, MOTRBILL로 확인)
        // HTTP 요청 (clientIp != null): CID + IP + PORT (MSG_TYPE 제외, MOTRBILL로 확인)
        val gipHttpMoAccess = if (serverPort != null) {
            if (clientIp == null) {
                // JNA dequeue: MSG_TYPE 없이 조회
                gipHttpMoAccessRepository.findByCidAndIpAddrAndPortNo(
                    request.data.destCID,
                    "127.0.0.1",
                    serverPort
                ).orElse(null)
            } else {
                // HTTP 요청: MSG_TYPE 없이 조회 (MOTRBILL로 확인)
                var access = gipHttpMoAccessRepository.findByCidAndIpAddrAndPortNo(
                    request.data.destCID,
                    clientIp,
                    serverPort
                ).orElse(null)
                
                // 조회 실패 시 IP 127.0.0.1로 재조회 (사용자 요청)
                if (access == null) {
                    access = gipHttpMoAccessRepository.findByCidAndIpAddrAndPortNo(
                        request.data.destCID,
                        "127.0.0.1",
                        serverPort
                    ).orElse(null)
                }
                access
            }
        } else {
            null
        }
        
        // MSG_TYPE 컬럼 삭제됨: 분기 기준은 MO_TR_BILL 사용
        val billType = com.infra.mo.skt_giphttp_mo.utils.BillTypeValidator.validateAndNormalize(gipHttpMoAccess?.billType)  // 검증된 BILL_TYPE
        val moTrBill = (gipHttpMoAccess?.moTrBill == 1)  // MOTRBILL=1이면 MO-TR까지 처리
        val loggerName = parentLoggerName ?: getLoggerName(request.data.destCID, clientIp, serverPort)

        // [디버그 로그] DB 조회 결과 및 결정된 값 확인
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[processSMReqSimple] DB 조회 결과: gipHttpMoAccess=%s, moTrBill(DB)=%d, moTrBill(Final)=%d, destCID=%s, clientIp=%s",
                if (gipHttpMoAccess != null) "FOUND" else "NULL",
                gipHttpMoAccess?.moTrBill ?: 0,
                if (moTrBill) 1 else 0,
                request.data.destCID,
                clientIp ?: "null"
            ),
            Thread.currentThread().getId()
        )
        // 함수 진입 로그 추가
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[processSMReqSimple] 함수 진입: loggerName(%s), destCID(%s), clientIp(%s), serverPort(%d), msgId(%s)",
                loggerName,
                request.data.destCID ?: "null",
                clientIp ?: "null",
                serverPort ?: 0,
                request.data.msgId ?: "null"
            ),
            Thread.currentThread().getId()
        )
        
        witcomLog.c_write(loggerName, Level.DEBUG,
            String.format("[processSMReqSimple] billType 분기 체크: billType(%s), gipHttpMoAccess(%s)",
                billType,
                if (gipHttpMoAccess != null) "NOT_NULL" else "NULL"
            ),
            Thread.currentThread().getId()
        )
        
        // MOTRBILL 체크를 먼저 수행 (BILLTYPE == '1'이어도 MOTRBILL == 'Y'이면 Update 처리 필요)
        // NOTE: moTrBill이 null(조회 실패/컬럼 NULL)인 경우에는 "불명"이므로, MOCALLINFO를 조기 삭제하지 않도록 방어한다.
        val moTrBillValue: Int? = gipHttpMoAccess?.moTrBill
        val gMOTRBILL = (moTrBillValue == 1)
        
        // BILLTYPE == '1' (비과금)이고 MOTRBILL != 'Y'인 경우: 레코드 삭제만 수행하고 과금 처리는 스킵
        // 단, MOTRBILL 값이 불명(null)인 경우에는 조기 삭제하지 않고 스킵(데이터 유실 방지)
        if (billType == "1" && !gMOTRBILL) {
            if (moTrBillValue == null) {
                witcomLog.c_write(
                    loggerName,
                    Level.WARN,
                    String.format(
                        "[processSMReqSimple] BILLTYPE='1' 이지만 MO_TR_BILL 값을 확인할 수 없어(MO_TR_BILL=NULL/조회 실패) MOCALLINFO 삭제 스킵 - msgId(%s), destCID(%s)",
                        request.data.msgId ?: "",
                        request.data.destCID ?: ""
                    ),
                    Thread.currentThread().getId()
                )
                return
            }
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[processSMReqSimple] BILLTYPE='1' && MOTRBILL!='Y' (비과금) 분기 선택: billType(%s)=='1', gMOTRBILL(%s) - 레코드 삭제만 수행, 과금 처리 스킵", 
                    billType, if (gMOTRBILL) "Y" else "N"),
                Thread.currentThread().getId()
            )
            
            val msgId = request.data.msgId ?: ""
            
            // MO ACK에서는 MOCALLINFO 레코드 삭제만 수행 (ESMClass 정보 없음)
            val moCallInfo = selectGIPMOCallInfo(msgId)
            if (moCallInfo != null) {
                deleteGIPMOCallInfo(moCallInfo)
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processSMReqSimple] BILLTYPE='1' MOCALLINFO 삭제 완료: SourceCallNo(%s) DestCID(%s) MsgId(%s)",
                        request.data.srcCallNo, request.data.destCID, msgId),
                    Thread.currentThread().getId()
                )
            } else {
                // MO_NOTISEND도 확인 (fallback)
                val deletedNotISend = selectTRMO_NOTISEND(
                    request.data.srcCallNo ?: "",
                    request.data.destCID ?: "",
                    msgId
                )
                if (deletedNotISend != null) {
                    witcomLog.c_write(loggerName, Level.INFO,
                        String.format("[processSMReqSimple] BILLTYPE='1' MO_NOTISEND 삭제 완료: SourceCallNo(%s) DestCID(%s) MsgId(%s)",
                            request.data.srcCallNo, request.data.destCID, msgId),
                        Thread.currentThread().getId()
                    )
                } else {
                    witcomLog.c_write(loggerName, Level.WARN,
                        String.format("[processSMReqSimple] BILLTYPE='1' 레코드 삭제 실패: 레코드 없음 - SourceCallNo(%s) DestCID(%s) MsgId(%s)",
                            request.data.srcCallNo, request.data.destCID, msgId),
                        Thread.currentThread().getId()
                    )
                }
            }
            
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[processSMReqSimple] BILLTYPE='1' && MOTRBILL!='Y' (비과금) 분기 완료: 레코드 삭제 완료, 과금 처리 스킵"),
                Thread.currentThread().getId()
            )
            return
        }
        
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[processSMReqSimple] billType!='1' 분기 선택: SRC 처리 시작 - billType(%s)", billType),
            Thread.currentThread().getId()
        )
        
        // C 코드 LINE 2069: RcsResult 설정
        qItem.RcsResult = RCS_RESULT_SENT.toShort()
        
        // C 코드 LINE 2071: ACK 결과 확인
        val ackResult = request.data.ackResult ?: GI_RES_NO_ERR
        
        witcomLog.c_write(loggerName, Level.DEBUG,
            String.format("[processSMReqSimple] ACK 결과 분기 체크: ackResult(%d), GI_RES_NO_ERR(%d)",
                ackResult,
                GI_RES_NO_ERR
            ),
            Thread.currentThread().getId()
        )
        
        if (ackResult == GI_RES_NO_ERR) {
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[processSMReqSimple] ACK 성공 분기 선택: ackResult(%d) - 성공 처리", ackResult),
                Thread.currentThread().getId()
            )
            
            // CDMA 1584 항목 체크
            val destCIDValue = request.data.destCID ?: ""
            
            // 기존 구현: MO_TR_BILL=0(즉시 과금)일 때 VSTAT 35(ST_GIPEVENT_MORS_OK)를 기록했으나,
            // 전체 플로우에서 35가 두 번 기록되는 문제가 있어 여기서의 VSTAT 35 기록은 제거함.
            // 35(HTTP 전송 시도)는 상위 플로우에서 한 번만 기록되도록 한다.
            
            witcomLog.c_write(
                loggerName,
                Level.INFO,
                String.format(
                    "[processSMReqSimple] ACK 성공 분기: MO ACK Success - srcCID(%s), srcCallNo(%s), destCID(%s), destCallNo(%s), ackResult(%d)",
                    request.data.srcCID,
                    request.data.srcCallNo,
                    request.data.destCID,
                    request.data.destCallNo,
                    ackResult
                ),
                Thread.currentThread().getId()
            )
        } else {
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[processSMReqSimple] ACK 실패 분기 선택: ackResult(%d) - 실패 처리", ackResult),
                Thread.currentThread().getId()
            )
            // C 코드 LINE 2079-2081: InsqStat 호출 (실패)
            // TRC 생성은 processMOBilling에서만 수행하므로 LT_TRACE 사용
            smsQLib.InsqStat(
                qItem,
                MESSAGE_MO,
                0,
                gServerID,
                MODULEID_GIPEVENT_C,
                SERVICEID_GIPEVENT,
                ERRORID_CP_MO_FAIL,
                ST_GIP_MORS_FAIL,
                ackResult,
                TID_NO_SAVE,
                LT_TRACE,  // LT_BOTH → LT_TRACE로 변경 (TRC 생성 안 함, 일관성 유지)
                0
            )
            
            witcomLog.c_write(
                loggerName,
                Level.ERROR,
                String.format(
                    "[processSMReqSimple] ACK 실패 분기: MO ACK Fail - srcCID(%s), srcCallNo(%s), destCID(%s), destCallNo(%s), ackResult(%d)",
                    request.data.srcCID,
                    request.data.srcCallNo,
                    request.data.destCID,
                    request.data.destCallNo,
                    ackResult
                ),
                Thread.currentThread().getId()
            )
            
            // C 코드 LINE 2083-2084: InsertHistory 호출
            // InsertHistory(gCallHistory, &gstQItem, NULL, MODULE_GIPEVENT, ACK_RESULT_FAIL, __LINE__, (int)ptrMsgHdr->ucData[3]);
            // TODO: History 저장 로직 구현 필요 (C 코드 LINE 2083-2084)
        }
        
        // MOTRBILL='N': 즉시 과금 처리 (MO-ACK만, processMOBilling에서 MOCALLINFO 삭제)
        // MOTRBILL='Y': MOCALLINFO 업데이트만 수행 (MO-ACK + MO-TR, 과금은 MO-TR에서)
        
        witcomLog.c_write(loggerName, Level.DEBUG,
            String.format("[processSMReqSimple] MOTRBILL 분기 체크: moTrBill(%s), billType(%s)",
                if (moTrBill) "Y" else "N",
                billType
            ),
            Thread.currentThread().getId()
        )
        
        if (!moTrBill) {
            // MOTRBILL='N': 즉시 과금 처리 (MO-ACK만)
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[processSMReqSimple] MOTRBILL='N' 분기 선택: 즉시 과금 처리 시작"),
                Thread.currentThread().getId()
            )
            processMOBilling(qItem, request, gipHttpMoAccess, smsQLib, loggerName)
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[processSMReqSimple] MOTRBILL='N' 분기 완료: 즉시 과금 처리 완료"),
                Thread.currentThread().getId()
            )
            return
        } else {
            // MOTRBILL='Y': VSTAT 15 기록 후 bprintf 호출, MOCALLINFO 업데이트 (MO-TR에서 과금)
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[processSMReqSimple] MOTRBILL='Y' 분기 선택: VSTAT 15 기록 후 bprintf 호출, UpdateGIPMOCallInfo 또는 UpdateMO_NOTISEND 호출 시작"),
                Thread.currentThread().getId()
            )
            
            // MSG_TYPE='4'일 때 VSTAT 15 기록 후 bprintf 호출 (processMOBilling에서 bprintf만 호출)
            processMOBilling(qItem, request, gipHttpMoAccess, smsQLib, loggerName)
            
            val rsv4Protocol11 = request.data.rsv4Protocol?.getOrNull(11)?.data ?: 0
            val isNotiPlusType =
                rsv4Protocol11 == NOTI_PLUS_NORMAL_MO ||
                    rsv4Protocol11 == NOTI_PLUS_PORTED_MO
            val isNotiType =
                rsv4Protocol11 == NOTI_NORMAL_MO ||
                    rsv4Protocol11 == NOTI_PORTED_MO
            
            witcomLog.c_write(loggerName, Level.DEBUG,
                String.format("[processSMReqSimple] ESMClass 분기 체크: rsv4Protocol11(%d), isNotiPlusType(%s), isNotiType(%s), NOTI_PLUS_NORMAL_MO(%d), NOTI_PLUS_PORTED_MO(%d), NOTI_NORMAL_MO(%d), NOTI_PORTED_MO(%d)",
                    rsv4Protocol11,
                    if (isNotiPlusType) "YES" else "NO",
                    if (isNotiType) "YES" else "NO",
                    NOTI_PLUS_NORMAL_MO,
                    NOTI_PLUS_PORTED_MO,
                    NOTI_NORMAL_MO,
                    NOTI_PORTED_MO
                ),
                Thread.currentThread().getId()
            )
            
            if (isNotiPlusType || isNotiType) {
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processSMReqSimple] NOTI_PLUS/NOTI 분기 선택: isNotiPlusType(%s), isNotiType(%s) - updateMO_NOTISEND 호출",
                        if (isNotiPlusType) "YES" else "NO",
                        if (isNotiType) "YES" else "NO"
                    ),
                    Thread.currentThread().getId()
                )
                updateMO_NOTISEND(request)
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processSMReqSimple] NOTI_PLUS/NOTI 분기 완료: updateMO_NOTISEND 완료"),
                    Thread.currentThread().getId()
                )
            } else {
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processSMReqSimple] 일반 분기 선택: isNotiPlusType(NO), isNotiType(NO) - updateGIPMOCallInfo 호출"),
                    Thread.currentThread().getId()
                )
                updateGIPMOCallInfo(request)
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processSMReqSimple] 일반 분기 완료: updateGIPMOCallInfo 완료"),
                    Thread.currentThread().getId()
                )
            }
        }
    }
    
    /**
     * MO 과금 처리 공통 함수
     * 
     * C 코드 참고: GIDBLib.c LINE 2087-2244
     * - DBGet_GIENQ_CID 호출
     * - SelectGIPMOCallInfo 호출
     * - bprintf 과금 데이터 출력
     * - InsqStat 호출 (과금 완료)
     * - RCS 처리 (필요 시)
     * - MOCALLINFO 삭제 (과금 성공 후)
     */
    override suspend fun processMOBilling(
        qItem: QITEM,
        request: ResponseTR,
        gipHttpMoAccess: GipHttpMoAccessEntity?,
        smsQLib: SmsQLib,
        loggerName: String
    ) {
        val destCIDAtEntry = QItemServiceUtil.byteArrayToKString(qItem.szCId)
        
        witcomLog.c_write(loggerName, Level.DEBUG,
            String.format("[processMOBilling] 함수 진입: destCID(%s), srcCallNo(%s), msgSeqNo(%d)",
                request.data.destCID ?: "",
                request.data.srcCallNo ?: "",
                request.data.msgSeqNo ?: 0
            ),
            Thread.currentThread().getId()
        )
        
        // C 코드 LINE 2122: SelectGIPMOCallInfo 호출
        // HTTP 환경에서는 MO와 MO-TR이 동일한 msgID를 사용하므로,
        // 원본 레코드를 조회하기 위해 request.data.msgId를 사용
        val msgId = request.data.msgId ?: ""  // MO와 MO-TR 모두 동일한 msgID
        
        witcomLog.c_write(loggerName, Level.DEBUG,
            String.format("[processMOBilling] SelectGIPMOCallInfo 호출 시작: msgId(%s)",
                msgId
            ),
            Thread.currentThread().getId()
        )
        val moCallInfo = selectGIPMOCallInfo(msgId)
        
        witcomLog.c_write(loggerName, 
            if (moCallInfo != null) Level.INFO else Level.WARN,
            String.format("[processMOBilling] SelectGIPMOCallInfo 결과: moCallInfo(%s), msgId(%s)",
                if (moCallInfo != null) "FOUND" else "NOT_FOUND",
                msgId
            ),
            Thread.currentThread().getId()
        )
        
        // MOCALLINFO 조회 실패 시 상세 로그 출력
        if (moCallInfo == null) {
            witcomLog.c_write(loggerName, Level.WARN,
                String.format("[processMOBilling] ⚠️ MOCALLINFO 레코드 없음: srcCallNo(%s), destCID(%s), msgId(%s) - MO 메시지 수신 시 insertGIPMOCallInfo로 레코드가 생성되어야 함",
                    request.data.srcCallNo ?: "",
                    request.data.destCID ?: "",
                    msgId
                ),
                Thread.currentThread().getId()
            )
        } else {
            witcomLog.c_write(loggerName, Level.DEBUG,
                String.format("[processMOBilling] MOCALLINFO 레코드 조회 성공: srcCallNo(%s), destCID(%s), msgId(%s), traceId(%s), moSubTime(%s)",
                    moCallInfo.srcCallNo ?: "",
                    moCallInfo.destCId ?: "",
                    moCallInfo.msgId ?: "",
                    moCallInfo.traceId ?: "",
                    moCallInfo.moSubTime ?: ""
                ),
                Thread.currentThread().getId()
            )
        }
        
        // C 코드 LINE 408: SelectTRMOCallInfo 호출 후, 실패 시 SelectTRMO_NOTISEND 호출
        // MOCALLINFO 우선 조회, 실패 시 MO_NOTISEND 조회
        // msgId는 고유 값이므로 msgId만으로 조회
        val effectiveMoInfo = moCallInfo ?: moNotISendRepository
            .findByMsgIdAndServerType(
                msgId,
                "V"  // SERVERTYPE='V'
            )
            .orElse(null)
            ?.let { noti ->
                MOCallInfoEntity().apply {
                    this.msgId = noti.msgId
                    srcCId = noti.srcCId
                    destCId = noti.destCId
                    srcCallNo = noti.srcCallNo
                    destCallNo = noti.destCallNo
                    moSubTime = noti.moSubTime?.let { dt ->
                        SimpleDateFormat("yyMMddHHmmss").format(dt)
                    }
                    msgLen = noti.msgLen
                    cb = noti.cb
                    wZone = noti.wZone
                    traceId = noti.traceId
                    origMvnoInfo = noti.origMvnoInfo
                    destMvnoInfo = noti.destMvnoInfo
                    dcsType = noti.dcsType
                    orgMsgLen = noti.orgMsgLen
                    moRecvTime = noti.moRecvTime
                    virtualNum = noti.destCallNo
                }
            }
        
        // MOCALLINFO에서 온 것인지 MO_NOTISEND에서 온 것인지 구분
        val isFromMOCallInfo = moCallInfo != null
        val isFromMONotISend = moCallInfo == null && effectiveMoInfo != null
        
        witcomLog.c_write(loggerName, Level.DEBUG,
            String.format("[processMOBilling] effectiveMoInfo 분기 체크: effectiveMoInfo(%s), isFromMOCallInfo(%s), isFromMONotISend(%s)",
                if (effectiveMoInfo != null) "NOT_NULL" else "NULL",
                if (isFromMOCallInfo) "YES" else "NO",
                if (isFromMONotISend) "YES" else "NO"
            ),
            Thread.currentThread().getId()
        )
        
        if (effectiveMoInfo != null) {
            // C 코드 LINE 518-519 (MOCALLINFO) 또는 LINE 651-652 (MO_NOTISEND): InsqStat 호출
            // statTraceId는 effectiveMoInfo != null 블록 내부의 모든 분기에서 사용되므로 여기서 정의
            val statTraceId = if (isFromMOCallInfo) {
                // C 코드 LINE 518-519: MOCALLINFO 케이스
                ST_VBILLMO_OK
            } else {
                // C 코드 LINE 651-652: MO_NOTISEND 케이스
                ST_VBILLMO_NOTISEND_OK
            }
            
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[processMOBilling] effectiveMoInfo NOT_NULL 분기 선택: 과금 데이터 처리 시작"),
                Thread.currentThread().getId()
            )
            // C 코드 LINE 2125-2128: TraceId, MVNO 정보, RCS Tag 복사
            if (effectiveMoInfo.traceId != null) {
                val traceIdBytes = effectiveMoInfo.traceId.toByteArray(Charset.forName("CP949"))
                System.arraycopy(traceIdBytes, 0, qItem.szTraceId, 0,
                    minOf(traceIdBytes.size, qItem.szTraceId.size - 1))
                if (traceIdBytes.size < qItem.szTraceId.size) {
                    qItem.szTraceId[traceIdBytes.size] = 0x00
                }
            }
            
            if (effectiveMoInfo.origMvnoInfo != null) {
                val origMvnoBytes = effectiveMoInfo.origMvnoInfo.toByteArray(Charset.forName("CP949"))
                System.arraycopy(origMvnoBytes, 0, qItem.szOrigMvnoInformation, 0,
                    minOf(origMvnoBytes.size, qItem.szOrigMvnoInformation.size - 1))
            }
            
            if (effectiveMoInfo.destMvnoInfo != null) {
                val destMvnoBytes = effectiveMoInfo.destMvnoInfo.toByteArray(Charset.forName("CP949"))
                System.arraycopy(destMvnoBytes, 0, qItem.szDestMvnoInformation, 0,
                    minOf(destMvnoBytes.size, qItem.szDestMvnoInformation.size - 1))
            }
            
            if (effectiveMoInfo.rcs != null && effectiveMoInfo.rcs.isNotEmpty()) {
                val rcsBytes = effectiveMoInfo.rcs.toByteArray(Charset.forName("CP949"))
                System.arraycopy(rcsBytes, 0, qItem.RcsTag, 0,
                    minOf(rcsBytes.size, qItem.RcsTag.size - 1))
                if (rcsBytes.size < qItem.RcsTag.size) {
                    qItem.RcsTag[rcsBytes.size] = 0x00
                }
            }
            
            // C 코드 LINE 2130-2142: DCS Type 변환
            val nDCSType = effectiveMoInfo.dcsType ?: DCS_TYPE_DEC_UNKNOWN
            when (nDCSType) {
                DCS_TYPE_DEC_GSM7 -> qItem.ucDataEncoding = DCS_TYPE_GSM7.code.toByte()
                DCS_TYPE_DEC_ASCII7 -> qItem.ucDataEncoding = DCS_TYPE_ASCII7.code.toByte()
                DCS_TYPE_DEC_8BIT -> qItem.ucDataEncoding = DCS_TYPE_8BIT.code.toByte()
                DCS_TYPE_DEC_UCS2 -> qItem.ucDataEncoding = DCS_TYPE_UCS2.code.toByte()
                DCS_TYPE_DEC_KSC5601 -> qItem.ucDataEncoding = DCS_TYPE_KSC5601
                else -> qItem.ucDataEncoding = DCS_TYPE_UNKNOWN.code.toByte()
            }
            
            qItem.ucMsgLen = (effectiveMoInfo.msgLen ?: 0).toInt()
            qItem.uOrgMsgLen = effectiveMoInfo.orgMsgLen ?: 0
            
            // C 코드 LINE 2149-2154: RCS 여부 확인
            val nRcs = if (effectiveMoInfo.rcs != null && effectiveMoInfo.rcs.isNotEmpty()) {
                RCS_TO_SMS
            } else {
                COMMON_SMS
            }
            
            // C 코드 LINE 2156: CallbackFilter 호출
            val callback = effectiveMoInfo.cb ?: ""
            val srcCallback = callback
            val dstCallback = callbackFilter(callback)
            
            if (dstCallback != srcCallback) {
                witcomLog.c_write(
                    loggerName,
                    Level.WARN,
                    String.format("[WARNING] Callback values are filtered, Before[%s] => After[%s]", srcCallback, dstCallback),
                    Thread.currentThread().getId()
                )
            }
            
            // C 코드 LINE 2163-2195: bprintf 과금 데이터 출력
            val now = Calendar.getInstance()
            val moSubTime = effectiveMoInfo.moSubTime ?: ""
            val moRecvTime = effectiveMoInfo.moRecvTime ?: ""
            val buf = SimpleDateFormat("yyMMddHHmmss").format(Date(now.timeInMillis - 15000))
            val szUsec = String.format("%.4f", (now.get(Calendar.MILLISECOND) / 1000.0)).substring(2)
            
            // DB 정보만 사용 (msgID는 고유 값이므로 겹치지 않음)
            val dbSrcCId = effectiveMoInfo.srcCId ?: ""
            val dbDestCId = effectiveMoInfo.destCId ?: ""
            val dbSrcCallNo = effectiveMoInfo.srcCallNo ?: ""
            val dbDestCallNo = effectiveMoInfo.destCallNo ?: ""
            
            // DBGet_GIENQ_CID 호출 (DB 정보 사용)
            witcomLog.c_write(loggerName, Level.DEBUG,
                String.format("[processMOBilling] DBGet_GIENQ_CID 호출 시작: destCID(%s) - DB 정보 사용", dbDestCId),
                Thread.currentThread().getId()
            )
            val cidLen = dbGetGIENQCID(dbDestCId, loggerName)
            
            witcomLog.c_write(loggerName, Level.DEBUG,
                String.format("[processMOBilling] DBGet_GIENQ_CID 결과: cidLen(%d), result(%s)",
                    cidLen,
                    if (cidLen >= 0) "SUCCESS" else "FAIL"
                ),
                Thread.currentThread().getId()
            )
            
            if (cidLen < 0) {
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processMOBilling] Invalid CID 분기 선택: cidLen(%d) - 에러 처리", cidLen),
                    Thread.currentThread().getId()
                )
                // C 코드 LINE 2107-2112: Invalid CID 처리
                // nInforNo: qItem.usSource 사용 (IF_NULL은 에러 케이스이므로 사용하지 않음)
                smsQLib.InsqStat(
                    qItem,
                    MESSAGE_MO,
                    0,
                    gServerID,
                    MODULEID_GIPEVENT_C,
                    SERVICEID_GIPEVENT,
                    ERRORID_CP_MO_FAIL,
                    ST_GIP_INVALID_CID,
                    getNInforNo(qItem),  // qItem.usSource (nInforNo)
                    TID_NO_SAVE,
                    LT_TRACE,
                    0
                )
                
                witcomLog.c_write(
                    loggerName,
                    Level.ERROR,
                    String.format("[processMOBilling] Invalid CID 분기: Invalid DestCID [%s] - DB 정보", dbDestCId),
                    Thread.currentThread().getId()
                )
                return
            }
            
            val cdrCId = dbDestCId.substring(0, cidLen)
            val destCallNoInt = dbDestCallNo.toLongOrNull() ?: 0L
            val isPortableNo = destCallNoInt >= 100000000L
            // C 코드 LINE 2186: COLORSMS = "200", AVATASMS = "1584"
            // C 코드: if(!strncmp((char *)ptrMsgHdr->szDestCId,COLORSMS,3) || !strncmp((char *)ptrMsgHdr->szDestCId,AVATASMS,4))
            val isColorOrAvata = dbDestCId.startsWith("200") || dbDestCId.startsWith("1584")
            // srcCallNo에서 * 이전의 숫자 부분만 추출 (Print QITEM: 33847189*12 → 실제 사용: 33847189)
            // C 모듈에서는 uSrcCallNo가 정수형이므로 *가 없지만, JAVA에서는 문자열로 처리되므로 * 처리 필요
            val srcCallNoBeforeSpecial = dbSrcCallNo.split('*').firstOrNull() ?: dbSrcCallNo
            val srcCallNoInt = srcCallNoBeforeSpecial.toLongOrNull() ?: 0L
            
            val callTypeMo = 1
            val msgDeleverOk = 0
            val moduleNo = 0
            val msgSeqNo = 0  // msgSeqNo는 DB에 저장되지 않으므로 0 사용
            
            // ESMCLASS + CID 조합 검증을 위한 ESMCLASS 추출
            val qItemEsmClass = qItem.nRsv4Protocol[11]
            
            // CID 2580/6381 + ESMCLASS 1 (NORMAL_MO) 조합만 bprintf 스킵 (C 코드와 동일)
            val shouldSkipBprintf = EsmClassHandler().isNormalMoWithSpecialCid(qItemEsmClass, dbDestCId)
            
            if (!shouldSkipBprintf) {
                // bprintf 호출 전 로그
                witcomLog.c_write(loggerName, Level.DEBUG,
                    String.format("[processMOBilling] bprintf 호출 진행: CID(%s), ESMCLASS(%d), isColorOrAvata(%b), srcCallNo(%s), destCallNo(%s), cdrCId(%s)",
                        dbDestCId,
                        qItemEsmClass,
                        isColorOrAvata,
                        dbSrcCallNo,
                        dbDestCallNo,
                        cdrCId),
                    Thread.currentThread().getId()
                )
                
                // C 코드 LINE 2163-2195: bprintf 과금 데이터 출력 (JNA bprintf 함수 사용)
                val szServerNo = ""  // C 코드 LINE 2096: szServerNo (환경변수에서 가져오거나 빈 문자열)
                val nRoaming = (effectiveMoInfo.roamingId ?: 0L).toInt()
                val RoamPMN = effectiveMoInfo.roamPMN ?: ""
                val CallBack = dstCallback
                val cWZone = effectiveMoInfo.wZone?.get(0) ?: '0'
                val origMvnoInfo = effectiveMoInfo.origMvnoInfo ?: ""
                val destMvnoInfo = effectiveMoInfo.destMvnoInfo ?: ""
                
                if (isColorOrAvata) {
                    if (isPortableNo) {
                    // C 코드 LINE 2169: bprintf 호출 (가입자 번호 형식: 0%d)
                    val bprintfResult = smsQLib.bprintf(
                        ";%d;;;%d;%s;%s%d;0%d;%s;11;%08d;%s;%s;%s%s;%d;%d;%d;;;;;;%s;%s;0;%c;;%s;%s;%d;0;;\n",
                        callTypeMo,
                        moduleNo,
                        szServerNo,
                        dbSrcCId,
                        srcCallNoInt,
                        destCallNoInt,
                        cdrCId,
                        msgSeqNo,
                        moRecvTime,
                        moSubTime,
                        buf,
                        szUsec,
                        msgDeleverOk,
                        qItem.ucMsgLen.toInt(),
                        nRoaming,
                        RoamPMN,
                        CallBack,
                        cWZone,
                        origMvnoInfo,
                        destMvnoInfo,
                        nRcs
                    )
                        witcomLog.c_write(loggerName, Level.INFO,
                            String.format("[processMOBilling] bprintf 호출 완료 (Color/Avata, 가입자 번호): 반환값(%d)", bprintfResult),
                            Thread.currentThread().getId()
                        )
                        
                        // bprintf 호출 후 VSTAT 15 InsqStat 호출 (MO-ACK 성공 시)
                        recordVstat15AfterBprintf(qItem, request, smsQLib, loggerName, dbDestCId)
                    } else {
                        // C 코드 LINE 2179: bprintf 호출 (일반 번호 형식: %d)
                        val bprintfResult = smsQLib.bprintf(
                            ";%d;;;%d;%s;%s%d;%d;%s;11;%08d;%s;%s;%s%s;%d;%d;%d;;;;;;%s;%s;0;%c;;%s;%s;%d;0;;\n",
                            callTypeMo,
                            moduleNo,
                            szServerNo,
                            dbSrcCId,
                            srcCallNoInt,
                            destCallNoInt,
                            cdrCId,
                            msgSeqNo,
                            moRecvTime,
                            moSubTime,
                            buf,
                            szUsec,
                            msgDeleverOk,
                            qItem.ucMsgLen.toInt(),
                            nRoaming,
                            RoamPMN,
                            CallBack,
                            cWZone,
                            origMvnoInfo,
                            destMvnoInfo,
                            nRcs
                        )
                        witcomLog.c_write(loggerName, Level.INFO,
                            String.format("[processMOBilling] bprintf 호출 완료 (Color/Avata, 일반 번호): 반환값(%d)", bprintfResult),
                            Thread.currentThread().getId()
                        )
                        
                        // bprintf 호출 후 VSTAT 15 InsqStat 호출 (MO-ACK 성공 시)
                        recordVstat15AfterBprintf(qItem, request, smsQLib, loggerName, dbDestCId)
                    }
                } else {
                    // C 코드 LINE 2190: bprintf 호출 (일반 DestCID)
                    val bprintfResult = smsQLib.bprintf(
                        ";%d;;;%d;%s;%s%d;%s;;11;%08d;%s;%s;%s%s;%d;%d;%d;;;;;;%s;%s;0;%c;;%s;%s;%d;0;;\n",
                        callTypeMo,
                        moduleNo,
                        szServerNo,
                        dbSrcCId,
                        srcCallNoInt,
                        cdrCId,
                        msgSeqNo,
                        moRecvTime,
                        moSubTime,
                        buf,
                        szUsec,
                        msgDeleverOk,
                        qItem.ucMsgLen.toInt(),
                        nRoaming,
                        RoamPMN,
                        CallBack,
                        cWZone,
                        origMvnoInfo,
                        destMvnoInfo,
                        nRcs
                    )
                    witcomLog.c_write(loggerName, Level.INFO,
                        String.format("[processMOBilling] bprintf 호출 완료 (일반 DestCID): 반환값(%d)", bprintfResult),
                        Thread.currentThread().getId()
                    )
                }
            } else {
                // CID+ESMCLASS 조합이 일치: bprintf 스킵
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processMOBilling] bprintf 스킵: CID+ESMCLASS 조합 일치 - CID(%s), ESMCLASS(%d)",
                        dbDestCId,
                        qItemEsmClass),
                    Thread.currentThread().getId()
                )
            }
            
            // C 코드 LINE 553 (ST_VBILLMO_OK) 또는 LINE 751 (ST_VBILLMO_NOTISEND_OK): ErrorID 분기
            // MSG_TYPE 컬럼 삭제됨: MO-ACK 단계에서의 중복 InsqStat을 피하기 위해 shouldSkipInsqStat 로직만 유지
            val isMOACK = request.data.msgSubCode.toInt() == SM_REQ_SIMPLE  // MO-ACK 단계인지 확인
            val moTrBillValue: Int? = gipHttpMoAccess?.moTrBill
            val gMOTRBILL = (moTrBillValue == 1)
            val isMoTrBillExplicitDisabled = (moTrBillValue == 0)
            val isMoTrBillUnknown = (moTrBillValue == null)
            
            // request.data.destCID 우선 사용, 없으면 함수 진입 시점의 qItem.szCId 사용 (DB 조회 없이 내부 코드에서 직접 체크)
            val destCID = request.data.destCID ?: destCIDAtEntry
            
            // ESMClass 기준으로 서비스 구분 (CID prefix 기반 구분 제거)
            val esmClassHandler = EsmClassHandler()
            val isSmsMessengerService = esmClassHandler.isSmsMessengerService(qItemEsmClass)  // 문자메신저서비스(2580)
            val isSmsManagerService = esmClassHandler.isSmsManagerService(qItemEsmClass)  // 문자매니저서비스(6381)
            
            // MO-ACK 단계는 processSMReqSimple()에서 이미 InsqStat/VSTAT 처리가 되므로 기본 스킵.
            // ESMClass 기반 예외 케이스는 아래 else-if 분기에서 별도 처리.
            val shouldSkipInsqStat = isMOACK
            
            if (shouldSkipInsqStat) {
                // MSG_TYPE='1'이고 MO-ACK 단계: InsqStat은 이미 processSMReqSimple()에서 호출했으므로 스킵
                // 또는 MOTRBILL='N'이고 MO-ACK 단계: InsqStat은 이미 processSMReqSimple()에서 호출했으므로 스킵
                // MSG_TYPE='4'이고 MO-ACK 단계: bprintf만 호출하고 InsqStat과 MOCALLINFO 삭제는 스킵
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processMOBilling] InsqStat 스킵: isMOACK(%b), gMOTRBILL(%b) - msgId(%s)",
                        isMOACK,
                        gMOTRBILL,
                        msgId),
                    Thread.currentThread().getId()
                )
                
                // MO_TR_BILL=0일 때만 MOCALLINFO 삭제
                // NOTE: MO_TR_BILL이 null(불명)인 경우에는 조기 삭제하지 않음 (데이터 유실 방지)
                if (isMoTrBillExplicitDisabled) {
                    // MOCALLINFO 삭제 (InsqStat 성공 여부와 관계없이)
                    if (moCallInfo != null) {
                        witcomLog.c_write(loggerName, Level.DEBUG,
                            String.format("[processMOBilling] MOCALLINFO 삭제 시작: MsgId(%s)",
                                msgId),
                            Thread.currentThread().getId()
                        )
                        deleteGIPMOCallInfo(moCallInfo)
                        witcomLog.c_write(loggerName, Level.INFO,
                            String.format("[processMOBilling] MOCALLINFO 삭제 완료: MsgId(%s)",
                                msgId),
                            Thread.currentThread().getId()
                        )
                    } else {
                        witcomLog.c_write(loggerName, Level.WARN,
                            String.format("[processMOBilling] ⚠️ MOCALLINFO 삭제 스킵: moCallInfo가 NULL - msgId(%s)",
                                msgId),
                            Thread.currentThread().getId()
                        )
                    }
                } else {
                    // MO_TR_BILL=1 또는 불명(null)인 경우: MO-ACK 단계에서 삭제 스킵 (MO-TR 단계에서 삭제)
                    witcomLog.c_write(loggerName, Level.INFO,
                        String.format(
                            "[processMOBilling] MO-ACK 단계: MOCALLINFO 삭제 스킵 (MO-TR 단계에서 삭제) - msgId(%s), moTrBill(%s)",
                            msgId,
                            if (isMoTrBillUnknown) "NULL" else if (gMOTRBILL) "1" else "0"
                        ),
                        Thread.currentThread().getId()
                    )
                }
            }
            else if (isMOACK && isMoTrBillExplicitDisabled && isSmsMessengerService) {
                // MO-ACK 단계이고 MOTRBILL='N'이고 문자메신저서비스(ESMClass 기준)인 경우: MODULEID_GIPEVENT_C로 InsqStat 호출
                // VSTAT에 35만 기록되도록 LT_TRACE 사용 (기존 MO-ACK 패턴 35-15가 아닌 35만)
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processMOBilling] MO-ACK 단계 문자메신저서비스: InsqStat 호출 시작 - msgId(%s), ESMCLASS(%d), LT_TRACE 사용 (VSTAT에 기록 안 함, TRC에만 기록)",
                        msgId,
                        qItemEsmClass),
                    Thread.currentThread().getId()
                )
                
                // C 코드 LINE 1768 참고: InsqStat 호출 직전에 ucServerType 설정
                qItem.ucServerType = VSMSS_TYPE.code.toByte()
                
                // nInforNo: qItem.usSource 사용 (IF_NULL은 에러 케이스이므로 사용하지 않음)
//                val insqStatResult = smsQLib.InsqStat(
//                    qItem,
//                    MESSAGE_MO,
//                    0,
//                    gServerID,
//                    MODULEID_GIPEVENT_C,
//                    SERVICEID_GIPEVENT,
//                    ERRORID_CP_MO_SUCCESS,
//                    ST_GIPEVENT_MO_OK,
//                    getNInforNo(qItem),  // qItem.usSource (nInforNo)
//                    TID_NO_SAVE,
//                    LT_TRACE,  // TRC 파일에만 기록, VSTAT에는 기록하지 않음 (15가 VSTAT에 기록되지 않도록)
//                    0
//                )
                
//                witcomLog.c_write(loggerName,
//                    if (insqStatResult == 1) Level.INFO else Level.ERROR,
//                    String.format("[processMOBilling] MO-ACK 단계 문자메신저서비스: InsqStat 호출 결과 - 반환값(%d), 성공여부(%b), msgId(%s), ESMCLASS(%d)",
//                        insqStatResult,
//                        insqStatResult == 1,
//                        msgId,
//                        qItemEsmClass),
//                    Thread.currentThread().getId()
//                )
                
                // MOCALLINFO 삭제
                if (moCallInfo != null) {
                    witcomLog.c_write(loggerName, Level.DEBUG,
                        String.format("[processMOBilling] MO-ACK 단계 문자메신저서비스: MOCALLINFO 삭제 시작 - MsgId(%s)",
                            msgId),
                        Thread.currentThread().getId()
                    )
                    deleteGIPMOCallInfo(moCallInfo)
                    witcomLog.c_write(loggerName, Level.INFO,
                        String.format("[processMOBilling] MO-ACK 단계 문자메신저서비스: MOCALLINFO 삭제 완료 - MsgId(%s)",
                            msgId),
                        Thread.currentThread().getId()
                    )
                } else {
                    witcomLog.c_write(loggerName, Level.WARN,
                        String.format("[processMOBilling] MO-ACK 단계 문자메신저서비스: ⚠️ MOCALLINFO 삭제 스킵 - moCallInfo가 NULL - msgId(%s)",
                            msgId),
                        Thread.currentThread().getId()
                    )
                }
            }
            else if (isMOACK && isMoTrBillExplicitDisabled && isSmsManagerService) {
                // MO-ACK 단계이고 MOTRBILL='N'이고 문자매니저서비스(ESMClass 기준)인 경우: MODULEID_GIPEVENT_C로 InsqStat 호출
                // VSTAT에 35만 기록되도록 LT_BOTH 사용 (ST_GIPEVENT_MOACK_BILL_OK는 스킵하므로 15는 기록되지 않음)
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processMOBilling] MO-ACK 단계 문자매니저서비스: InsqStat 호출 시작 - msgId(%s), ESMCLASS(%d), LT_BOTH 사용 (VSTAT에 35만 기록)",
                        msgId,
                        qItemEsmClass),
                    Thread.currentThread().getId()
                )
                
                // C 코드 LINE 1768 참고: InsqStat 호출 직전에 ucServerType 설정
                qItem.ucServerType = VSMSS_TYPE.code.toByte()

                // nInforNo: qItem.usSource 사용 (IF_NULL은 에러 케이스이므로 사용하지 않음)
//                val insqStatResult = smsQLib.InsqStat(
//                    qItem,
//                    MESSAGE_MO,
//                    0,
//                    gServerID,
//                    MODULEID_GIPEVENT_C,
//                    SERVICEID_GIPEVENT,
//                    ERRORID_CP_MO_SUCCESS,
//                    ST_GIPEVENT_MO_OK,
//                    getNInforNo(qItem),  // qItem.usSource (nInforNo)
//                    TID_NO_SAVE,
//                    LT_TRACE,  // TRC 파일에만 기록, VSTAT에는 기록하지 않음 (15가 VSTAT에 기록되지 않도록)
//                    0
//                )
                
//                witcomLog.c_write(loggerName,
//                    if (insqStatResult == 1) Level.INFO else Level.ERROR,
//                    String.format("[processMOBilling] MO-ACK 단계 문자매니저서비스: InsqStat 호출 결과 - 반환값(%d), 성공여부(%b), msgId(%s), ESMCLASS(%d)",
//                        insqStatResult,
//                        insqStatResult == 1,
//                        msgId,
//                        qItemEsmClass),
//                    Thread.currentThread().getId()
//                )
                
                // MOCALLINFO 삭제
                if (moCallInfo != null) {
                    witcomLog.c_write(loggerName, Level.DEBUG,
                        String.format("[processMOBilling] MO-ACK 단계 문자매니저서비스: MOCALLINFO 삭제 시작 - MsgId(%s)",
                            msgId),
                        Thread.currentThread().getId()
                    )
                    deleteGIPMOCallInfo(moCallInfo)
                    witcomLog.c_write(loggerName, Level.INFO,
                        String.format("[processMOBilling] MO-ACK 단계 문자매니저서비스: MOCALLINFO 삭제 완료 - MsgId(%s)",
                            msgId),
                        Thread.currentThread().getId()
                    )
                } else {
                    witcomLog.c_write(loggerName, Level.WARN,
                        String.format("[processMOBilling] MO-ACK 단계 문자매니저서비스: ⚠️ MOCALLINFO 삭제 스킵 - moCallInfo가 NULL - msgId(%s)",
                            msgId),
                        Thread.currentThread().getId()
                    )
                }
            } else {
                // MOTRBILL='Y'이고 MO-TR 단계: InsqStat 호출 필요
                val errorId = if (isFromMOCallInfo) {
                    // MOTRBILL='Y'이고 MO-TR 단계: MO-TR 과금이므로 ERRORID_CP_MO_TR_SUCCESS (27) 사용
                    // C 코드 LINE 553: ST_VBILLMO_OK → ERRORID_CP_MO_TR_SUCCESS (27)
                    ERRORID_CP_MO_TR_SUCCESS
                } else {
                    // C 코드 LINE 751: ST_VBILLMO_NOTISEND_OK → ERRORID_CP_TR_SUCCESS (25)
                    ERRORID_CP_TR_SUCCESS
                }
                
                witcomLog.c_write(loggerName, Level.DEBUG,
                    String.format("[processMOBilling] InsqStat 호출: isFromMOCallInfo(%s), isFromMONotISend(%s), statTraceId(%d), errorId(%d)",
                        if (isFromMOCallInfo) "YES" else "NO",
                        if (isFromMONotISend) "YES" else "NO",
                        statTraceId,
                        errorId
                    ),
                    Thread.currentThread().getId()
                )
                
                // InsqStat 호출 전: DB에서 조회한 정보로 qItem의 dest/src 스왑 설정 (통계 기록용)
                // 통계 기록 시 dest와 src를 스왑해야 함
                val originalSzCId = ByteArray(16)
                val originalSzSrcCId = ByteArray(16)
                val originalSzMinNo = ByteArray(12)
                val originalSzSrcMinNo = ByteArray(12)
                
                // 원본 값 백업
                System.arraycopy(qItem.szCId, 0, originalSzCId, 0, 16)
                System.arraycopy(qItem.szSrcCId, 0, originalSzSrcCId, 0, 16)
                System.arraycopy(qItem.szMinNo, 0, originalSzMinNo, 0, 12)
                System.arraycopy(qItem.szSrcMinNo, 0, originalSzSrcMinNo, 0, 12)
                
                // DB에서 조회한 정보로 스왑 설정
                // szCId = effectiveMoInfo.srcCId (원래 dest)
                // szSrcCId = effectiveMoInfo.destCId (원래 src)
                // szMinNo = effectiveMoInfo.srcCallNo (원래 dest)
                // szSrcMinNo = effectiveMoInfo.destCallNo (원래 src)
                val srcCIdBytes = (effectiveMoInfo?.srcCId ?: "").toByteArray(Charset.forName("CP949"))
                val destCIdBytes = (effectiveMoInfo?.destCId ?: "").toByteArray(Charset.forName("CP949"))
                val srcCallNoBytes = (effectiveMoInfo?.srcCallNo ?: "").toByteArray(Charset.forName("CP949"))
                val destCallNoBytes = (effectiveMoInfo?.destCallNo ?: "").toByteArray(Charset.forName("CP949"))
                
                // 스왑 설정: szCId = srcCId, szSrcCId = destCId
                qItem.szCId.fill(0)
                System.arraycopy(srcCIdBytes, 0, qItem.szCId, 0, minOf(srcCIdBytes.size, 16))
                if (srcCIdBytes.size < 16) {
                    qItem.szCId[srcCIdBytes.size] = 0x00
                }
                qItem.szSrcCId.fill(0)
                System.arraycopy(destCIdBytes, 0, qItem.szSrcCId, 0, minOf(destCIdBytes.size, 16))
                if (destCIdBytes.size < 16) {
                    qItem.szSrcCId[destCIdBytes.size] = 0x00
                }
                
                // 스왑 설정: szMinNo = srcCallNo, szSrcMinNo = destCallNo
                qItem.szMinNo.fill(0)
                System.arraycopy(srcCallNoBytes, 0, qItem.szMinNo, 0, minOf(srcCallNoBytes.size, 12))
                if (srcCallNoBytes.size < 12) {
                    qItem.szMinNo[srcCallNoBytes.size] = 0x00
                }
                qItem.szSrcMinNo.fill(0)
                System.arraycopy(destCallNoBytes, 0, qItem.szSrcMinNo, 0, minOf(destCallNoBytes.size, 12))
                if (destCallNoBytes.size < 12) {
                    qItem.szSrcMinNo[destCallNoBytes.size] = 0x00
                }
                
                witcomLog.c_write(loggerName, Level.DEBUG,
                    String.format("[processMOBilling] qItem 스왑 설정 완료: szCId(%s), szSrcCId(%s), szMinNo(%s), szSrcMinNo(%s)",
                        QItemServiceUtil.byteArrayToKString(qItem.szCId),
                        QItemServiceUtil.byteArrayToKString(qItem.szSrcCId),
                        QItemServiceUtil.byteArrayToKString(qItem.szMinNo),
                        QItemServiceUtil.byteArrayToKString(qItem.szSrcMinNo)
                    ),
                    Thread.currentThread().getId()
                )
                
                // C 코드 LINE 553 또는 LINE 751: InsqStat을 LT_BOTH로 한 번만 호출 (과금 + 통계 기록)
                // C 코드 LINE 1768 참고: InsqStat 호출 직전에 ucServerType 설정
                qItem.ucServerType = VSMSS_TYPE.code.toByte()
                
                witcomLog.c_write(loggerName, Level.DEBUG,
                    String.format("[processMOBilling] InsqStat 호출 시작: MODULEID_VBILLMO(%d), SERVICEID_GIPEVENT(%d), errorId(%d), statTraceId(%d), LT_BOTH",
                        MODULEID_VBILLMO,
                        SERVICEID_GIPEVENT,
                        errorId,
                        statTraceId
                    ),
                    Thread.currentThread().getId()
                )
                
                // nInforNo: qItem.usSource 사용 (IF_NULL은 에러 케이스이므로 사용하지 않음)
//                val insqStatResult = smsQLib.InsqStat(
//                    qItem,
//                    MESSAGE_TR,  // C 코드와 동일: MESSAGE_TR
//                    0,
//                    gServerID,
//                    MODULEID_VBILLMO,  // C 코드와 동일: MODULEID_VBILLMO
//                    SERVICEID_GIPEVENT,
//                    errorId,  // ERRORID_CP_MO_TR_SUCCESS (27) 또는 ERRORID_CP_TR_SUCCESS (25)
//                    statTraceId,  // ST_VBILLMO_OK 또는 ST_VBILLMO_NOTISEND_OK
//                    getNInforNo(qItem),  // qItem.usSource (nInforNo)
//                    TID_NO_SAVE,
//                    LT_BOTH,  // C 코드와 동일: LT_BOTH (과금 + 통계 기록)
//                    0
//                )
                
                // InsqStat 호출 후: 원본 값 복원
                System.arraycopy(originalSzCId, 0, qItem.szCId, 0, 16)
                System.arraycopy(originalSzSrcCId, 0, qItem.szSrcCId, 0, 16)
                System.arraycopy(originalSzMinNo, 0, qItem.szMinNo, 0, 12)
                System.arraycopy(originalSzSrcMinNo, 0, qItem.szSrcMinNo, 0, 12)
                
//                witcomLog.c_write(loggerName,
//                    if (insqStatResult == 1) Level.DEBUG else Level.ERROR,
//                    String.format("[processMOBilling] InsqStat 호출 결과: 반환값(%d), 성공여부(%b)",
//                        insqStatResult,
//                        insqStatResult == 1
//                    ),
//                    Thread.currentThread().getId()
//                )
//
//                // MOTRBILL='Y'이고 MO-TR 단계: msgId로 조회하여 존재한다면 삭제 처리
//                // 규칙: MOTRBILL='Y'인 경우 MO-TR 단계에서만 삭제 (MO-ACK 단계에서는 삭제 안 함)
//                witcomLog.c_write(loggerName, Level.DEBUG,
//                    String.format("[processMOBilling] 삭제 조건 체크: isMOACK(%b), insqStatResult(%d), moCallInfo(%s), msgId(%s), destCID(%s)",
//                        isMOACK,
//                        insqStatResult,
//                        if (moCallInfo != null) "NOT_NULL" else "NULL",
//                        msgId,
//                        request.data.destCID ?: destCIDAtEntry),
//                    Thread.currentThread().getId()
//                )
                
                // MOTRBILL='Y'이고 MO-TR 단계일 때만 삭제 (MO-ACK 단계에서는 삭제 안 함)
                if (moCallInfo != null && !isMOACK) {
//                    witcomLog.c_write(loggerName, Level.DEBUG,
//                        String.format("[processMOBilling] MO-TR 요청 후 MOCALLINFO 삭제 시작: MsgId(%s), insqStatResult(%d), MOTRBILL='Y'",
//                            msgId,
//                            insqStatResult),
//                        Thread.currentThread().getId()
//                    )
                    deleteGIPMOCallInfo(moCallInfo)
                    witcomLog.c_write(loggerName, Level.INFO,
                        String.format("[processMOBilling] MO-TR 요청 후 MOCALLINFO 삭제 완료: MsgId(%s), MOTRBILL='Y'",
                            msgId),
                        Thread.currentThread().getId()
                    )
                } else if (moCallInfo != null && isMOACK) {
                    // MOTRBILL='Y'이고 MO-ACK 단계: 삭제하지 않음 (MO-TR 단계에서 삭제 예정)
                    witcomLog.c_write(loggerName, Level.DEBUG,
                        String.format("[processMOBilling] MOTRBILL='Y' MO-ACK 단계: MOCALLINFO 삭제 스킵 (MO-TR 단계에서 삭제 예정) - MsgId(%s)",
                            msgId),
                        Thread.currentThread().getId()
                    )
                } else {
                    // MOCALLINFO가 없는 경우 로그 추가
                    witcomLog.c_write(loggerName, Level.WARN,
                        String.format("[processMOBilling] ⚠️ MOCALLINFO 삭제 스킵: moCallInfo가 NULL - msgId(%s), MO-TR 요청 시 MOCALLINFO가 존재하지 않음",
                            msgId),
                        Thread.currentThread().getId()
                    )
                }
            }
            
            // C 코드 LINE 2199-2221: RCS 태그 처리 및 큐 삽입
            if (effectiveMoInfo.rcs != null && effectiveMoInfo.rcs.isNotEmpty()) {
                val rcsQItem = QITEM()
                QItemServiceUtil.copyQItem(qItem, rcsQItem)
                
                val tempCid = ByteArray(16)
                System.arraycopy(rcsQItem.szCId, 0, tempCid, 0, 16)
                System.arraycopy(rcsQItem.szSrcCId, 0, rcsQItem.szCId, 0, 16)
                System.arraycopy(tempCid, 0, rcsQItem.szSrcCId, 0, 16)
                
                val tempMin = ByteArray(12)
                System.arraycopy(rcsQItem.szMinNo, 0, tempMin, 0, 12)
                System.arraycopy(rcsQItem.szSrcMinNo, 0, rcsQItem.szMinNo, 0, 12)
                System.arraycopy(tempMin, 0, rcsQItem.szSrcMinNo, 0, 12)
                
                rcsQItem.usMsgCode = QTYPE_SM_REQ.toShort()
                rcsQItem.usMsgSubCode = SUB_QTYPE_RCS_TR.toShort()
                rcsQItem.ucTermType = TERM_TYPE_KOR.code.toByte()
                rcsQItem.ucDataEncoding = DCS_TYPE_KSC5601
                rcsQItem.nVldPrd = 43200
                
                val rcsQueueNo = 0
                smsQLib.InsertIntoSmsQnQNo(rcsQItem, rcsQueueNo)
                witcomLog.c_write(loggerName, Level.DEBUG,
                    String.format("[processMOBilling] RCS 처리 완료: rcsQueueNo(%d)", rcsQueueNo),
                    Thread.currentThread().getId()
                )
            }
            
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[processMOBilling] effectiveMoInfo NOT_NULL 분기 완료: 과금 데이터 처리 완료"),
                Thread.currentThread().getId()
            )
        } else {
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[processMOBilling] effectiveMoInfo NULL 분기 선택: ORA_NODATA 처리"),
                Thread.currentThread().getId()
            )
            // C 코드 LINE 2225-2231: ORA_NODATA 처리
            // nInforNo: qItem.usSource 사용 (IF_NULL은 에러 케이스이므로 사용하지 않음)
            smsQLib.InsqStat(
                qItem,
                MESSAGE_MO,
                0,
                gServerID,
                MODULEID_GIPEVENT_C,
                SERVICEID_GIPEVENT,
                ERRORID_CP_MO_NODATA,
                ST_DB_NO_DATA_GIPMOCALLINFO,
                getNInforNo(qItem),  // qItem.usSource (nInforNo)
                TID_NO_SAVE,
                LT_BOTH,
                0
            )
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[processMOBilling] effectiveMoInfo NULL 분기 완료: ORA_NODATA 처리 완료"),
                Thread.currentThread().getId()
            )
        }
    }
    
    /**
     * C 코드 LINE 2247-2272: SM_REQ_TRANS_RESULT 케이스 처리 (TR 결과)
     */
    private suspend fun processSMReqTransResult(
        qItem: QITEM, 
        request: ResponseTR,
        clientIp: String? = null,
        serverPort: Int? = null,
        queueNo: Int? = null,
        parentLoggerName: String? = null
    ) {
        // loggerName 생성: parentLoggerName이 있으면 사용, 없으면 자동 생성
        val loggerName = parentLoggerName ?: getLoggerName(request.data.destCID, clientIp, serverPort)
        
        witcomLog.c_write(loggerName, Level.DEBUG,
            String.format("[processSMReqTransResult] 함수 진입: destCID(%s), clientIp(%s), serverPort(%d), msgCode(%d), msgSubCode(%d), result(%s), msgId(%s)",
                request.data.destCID ?: "",
                clientIp ?: "",
                serverPort ?: 0,
                request.data.msgCode?.toInt() ?: 0,
                request.data.msgSubCode?.toInt() ?: 0,
                request.data.result?.toString() ?: "null",
                request.data.msgId ?: ""
            ),
            Thread.currentThread().getId()
        )
        
        // MO-TR 요청 시 원본 MO 요청의 traceId를 MOCALLINFO에서 조회하여 qItem에 설정
        // 절대 새로 생성하지 않고, 원본 MO 요청 시 생성된 traceId를 사용해야 함
        val msgId = request.data.msgId ?: ""
        if (msgId.isNotEmpty()) {
            witcomLog.c_write(loggerName, Level.DEBUG,
                String.format("[processSMReqTransResult] MOCALLINFO에서 traceId 조회 시작: msgId(%s)",
                    msgId),
                Thread.currentThread().getId()
            )
            
            val moCallInfo = selectGIPMOCallInfo(msgId)
            if (moCallInfo?.traceId != null && moCallInfo.traceId.isNotEmpty()) {
                // 원본 MO 요청의 traceId를 qItem에 설정 (절대 새로 생성하지 않음)
                val traceIdBytes = moCallInfo.traceId.toByteArray(Charset.forName("CP949"))
                System.arraycopy(traceIdBytes, 0, qItem.szTraceId, 0,
                    minOf(traceIdBytes.size, qItem.szTraceId.size - 1))
                if (traceIdBytes.size < qItem.szTraceId.size) {
                    qItem.szTraceId[traceIdBytes.size] = 0x00
                }
                
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processSMReqTransResult] 원본 traceId 설정 완료: msgId(%s), traceId(%s)",
                        msgId,
                        moCallInfo.traceId),
                    Thread.currentThread().getId()
                )
            } else {
                // traceId가 MOCALLINFO에서 확인되지 않으면 에러
                val errorMsg = String.format(
                    "[processSMReqTransResult] ⚠️ traceId 조회 실패: msgId(%s), MOCALLINFO 조회 결과(%s) - 원본 MO 요청의 traceId가 없어 MO-TR 처리가 불가능합니다",
                    msgId,
                    if (moCallInfo == null) "NULL (레코드 없음)" else "traceId 없음"
                )
                witcomLog.c_write(loggerName, Level.ERROR, errorMsg, Thread.currentThread().getId())
                throw IllegalStateException(errorMsg)
            }
        } else {
            // msgId가 없으면 에러
            val errorMsg = "[processSMReqTransResult] ⚠️ msgId가 없음 - MO-TR 요청에는 msgId가 필수입니다"
            witcomLog.c_write(loggerName, Level.ERROR, errorMsg, Thread.currentThread().getId())
            throw IllegalStateException(errorMsg)
        }
        
        // C 코드 LINE 2248-2249: gMsgSendReady, gRetryCount 초기화
        // HTTP 환경에서는 불필요
        
        // SM_REQ_TRANS_RESULT (9)는 MO-TR 결과이므로 MSG_TYPE='4' (MO-TR)
        // CP로부터 HTTP 요청이 들어온 케이스이므로 항상 MSG_TYPE='4'
        val msgType = "4"  // MO-TR
        
        witcomLog.c_write(loggerName, Level.DEBUG,
            String.format("[processSMReqTransResult] gipHttpMoAccess 조회 시작: destCID(%s), clientIp(%s), serverPort(%d), msgType(%s)",
                request.data.destCID ?: "",
                clientIp ?: "null",
                serverPort ?: 0,
                msgType
            ),
            Thread.currentThread().getId()
        )
        
        // JNA dequeue (clientIp == null): CID + 127.0.0.1 + PORT (MSG_TYPE 제외, MOTRBILL로 확인)
        // HTTP 요청 (clientIp != null): CID + IP + PORT (MSG_TYPE 제외, MOTRBILL로 확인)
        val gipHttpMoAccess = if (serverPort != null) {
            if (clientIp == null) {
                // JNA dequeue: MSG_TYPE 없이 조회
                gipHttpMoAccessRepository.findByCidAndIpAddrAndPortNo(
                    request.data.destCID,
                    "127.0.0.1",
                    serverPort
                ).orElse(null)
            } else {
                // HTTP 요청: MSG_TYPE 없이 조회 (MOTRBILL로 확인)
                var access = gipHttpMoAccessRepository.findByCidAndIpAddrAndPortNo(
                    request.data.destCID,
                    clientIp,
                    serverPort
                ).orElse(null)
                
                // 조회 실패 시 IP 127.0.0.1로 재조회 (사용자 요청)
                if (access == null) {
                    access = gipHttpMoAccessRepository.findByCidAndIpAddrAndPortNo(
                        request.data.destCID,
                        "127.0.0.1",
                        serverPort
                    ).orElse(null)
                }
                access
            }
        } else {
            null
        }
        
        witcomLog.c_write(loggerName, Level.DEBUG,
            String.format("[processSMReqTransResult] gipHttpMoAccess 조회 결과: found(%s), cid(%s), ipAddr(%s), portNo(%d), billType(%s), moTrBill(%s)",
                if (gipHttpMoAccess != null) "YES" else "NO",
                gipHttpMoAccess?.cid ?: "null",
                gipHttpMoAccess?.ipAddr ?: "null",
                gipHttpMoAccess?.portNo ?: 0,
                gipHttpMoAccess?.billType ?: "null",
                gipHttpMoAccess?.moTrBill ?: "null"
            ),
            Thread.currentThread().getId()
        )
        
        if (gipHttpMoAccess != null) {
            val logNo = gipHttpMoAccess.logNo?.let {
                String.format("%04d", it.toIntOrNull() ?: 0)
            } ?: "0000"
            val cpName = gipHttpMoAccess.cpName ?: ""
            
            // DataEncoding 포맷팅
            val dataEncodingValue = request.data.dataEncoding.toInt() and 0xFF
            val encodingName = when (dataEncodingValue) {
                DCS_TYPE_DEC_KSC5601, DCS_TYPE_KSC5601.toInt() -> "CP949"
                DCS_TYPE_DEC_UCS2, DCS_TYPE_UCS2.toInt() -> "UCS2"
                DCS_TYPE_DEC_GSM7, DCS_TYPE_GSM7.toInt() -> "GSM7"
                DCS_TYPE_DEC_ASCII7, DCS_TYPE_ASCII7.toInt() -> "ASCII7"
                DCS_TYPE_DEC_8BIT, DCS_TYPE_8BIT.toInt() -> "8BIT"
                else -> "UNKNOWN"
            }
            val dataEncoding = String.format("%02X:%s", dataEncodingValue, encodingName)
            
        // Status를 문자열로 변환 (qItem에서 가져오기)
            val statusValue = qItem.ucMsgStatus.toInt()
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
            
            // MsgId를 문자열로 변환 (qItem에서 가져오기)
            val msgId = QItemServiceUtil.byteArrayToKString(qItem.ucMsgId)
            
            val reqLoggerName = "${gipHttpMoAccess.cid}-${gipHttpMoAccess.ipAddr}-${gipHttpMoAccess.portNo}"
            // 실제 사용자 전송 파라미터 기반으로 로그 출력
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
            witcomLog.c_write(reqLoggerName, Level.DEBUG, reqTransResultLog, Thread.currentThread().getId())
        }

        witcomLog.c_write(
            loggerName,
            Level.INFO,
            String.format(
                "[processSMReqTransResult] MT-TR 수신: srcCallNo(%s), destCID(%s), msgCode(%d), msgSubCode(%d), msgId(%s), result(%d)",
                request.data.srcCallNo,
                request.data.destCID,
                request.data.msgCode,
                request.data.msgSubCode,
                request.data.msgId,
                request.data.result
            ),
            Thread.currentThread().getId()
        )
        
        // C 코드 LINE 3371: if (gMOTRBILL) ProcessTRVBILLMO(ptrGIMsgHdr);
        // C 코드에서는 GIPEVENT_c.c에서 InsqStat을 호출하지 않고, ProcessTRVBILLMO를 호출해서 VBILL_MO 큐에만 적재
        // 실제 InsqStat은 VBILLMO_c.c의 ProcessTR에서 한 번만 호출됨 (LINE 553: LT_BOTH)
        // 따라서 여기서는 InsqStat을 호출하지 않음 (중복 TRC 생성 방지)
        
        // HTTP 요청은 단일 스레드이므로 index 0 사용
        val smsQLib: SmsQLib = liveReloadCLibraryFile.getPreInitializedLibrary(0)

        // HTTP 방식: MO-TR 수신 시 CP로부터 받은 정보로 MOCALLINFO 업데이트 후 MO ACK 기능 수행
        if (gipHttpMoAccess != null) {
            val gMOTRBILL = (gipHttpMoAccess.moTrBill == 1)
            val gBILLTYPE = com.infra.mo.skt_giphttp_mo.utils.BillTypeValidator.toChar(gipHttpMoAccess.billType, '0')
            val billType = com.infra.mo.skt_giphttp_mo.utils.BillTypeValidator.validateAndNormalize(gipHttpMoAccess.billType)

            // 1. MOCALLINFO/MO_NOTISEND 업데이트 (CP로부터 받은 정보로)
            // BILLTYPE == '1' (비과금)인 경우에도 레코드 확인은 수행
            val rsv4Protocol11 = request.data.rsv4Protocol?.getOrNull(11)?.data ?: 0
            val isNotiPlusType =
                rsv4Protocol11 == NOTI_PLUS_NORMAL_MO ||
                    rsv4Protocol11 == NOTI_PLUS_PORTED_MO
            val isNotiType =
                rsv4Protocol11 == NOTI_NORMAL_MO ||
                    rsv4Protocol11 == NOTI_PORTED_MO

            witcomLog.c_write(loggerName, Level.DEBUG,
                String.format("[processSMReqTransResult] ESMClass 분기 체크: rsv4Protocol11(%d), isNotiPlusType(%s), isNotiType(%s), NOTI_PLUS_NORMAL_MO(%d), NOTI_PLUS_PORTED_MO(%d), NOTI_NORMAL_MO(%d), NOTI_PORTED_MO(%d)",
                    rsv4Protocol11,
                    if (isNotiPlusType) "YES" else "NO",
                    if (isNotiType) "YES" else "NO",
                    NOTI_PLUS_NORMAL_MO,
                    NOTI_PLUS_PORTED_MO,
                    NOTI_NORMAL_MO,
                    NOTI_PORTED_MO
                ),
                Thread.currentThread().getId()
            )

            if (isNotiPlusType || isNotiType) {
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processSMReqTransResult] NOTI_PLUS/NOTI 분기 선택: isNotiPlusType(%s), isNotiType(%s) - updateMO_NOTISEND 호출",
                        if (isNotiPlusType) "YES" else "NO",
                        if (isNotiType) "YES" else "NO"
                    ),
                    Thread.currentThread().getId()
                )
                updateMO_NOTISEND(request)
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processSMReqTransResult] NOTI_PLUS/NOTI 분기 완료: updateMO_NOTISEND 완료"),
                    Thread.currentThread().getId()
                )
            } else {
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processSMReqTransResult] 일반 분기 선택: isNotiPlusType(NO), isNotiType(NO) - updateGIPMOCallInfo 호출"),
                    Thread.currentThread().getId()
                )
                updateGIPMOCallInfo(request)  // 새 레코드 생성 불필요, 원본 레코드 존재 여부만 확인
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processSMReqTransResult] 일반 분기 완료: updateGIPMOCallInfo 완료"),
                    Thread.currentThread().getId()
                )
            }

            // BILLTYPE == '1' (비과금)인 경우: 레코드 삭제만 수행하고 과금 처리는 스킵
            if (gBILLTYPE == '1') {
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processSMReqTransResult] BILLTYPE='1' (비과금) 분기 선택: gBILLTYPE(%c)=='1' - 레코드 삭제만 수행, 과금 처리 스킵", gBILLTYPE),
                    Thread.currentThread().getId()
                )
                
                val msgId = request.data.msgId ?: ""
                
                // 레코드 삭제 수행 (과금 처리는 스킵)
                if (isNotiPlusType || isNotiType) {
                    // MO_NOTISEND 레코드 삭제
                    val deletedNotISend = selectTRMO_NOTISEND(
                        request.data.srcCallNo,
                        request.data.destCID,
                        msgId
                    )
                    if (deletedNotISend != null) {
                        witcomLog.c_write(loggerName, Level.INFO,
                            String.format("[processSMReqTransResult] BILLTYPE='1' MO_NOTISEND 삭제 완료: SourceCallNo(%s) DestCID(%s) MsgId(%s)",
                                request.data.srcCallNo, request.data.destCID, msgId),
                            Thread.currentThread().getId()
                        )
                    } else {
                        witcomLog.c_write(loggerName, Level.WARN,
                            String.format("[processSMReqTransResult] BILLTYPE='1' MO_NOTISEND 삭제 실패: 레코드 없음 - SourceCallNo(%s) DestCID(%s) MsgId(%s)",
                                request.data.srcCallNo, request.data.destCID, msgId),
                            Thread.currentThread().getId()
                        )
                    }
                } else {
                    // MOCALLINFO 레코드 삭제
                    val moCallInfo = selectGIPMOCallInfo(msgId)
                    if (moCallInfo != null) {
                        deleteGIPMOCallInfo(moCallInfo)
                        witcomLog.c_write(loggerName, Level.INFO,
                            String.format("[processSMReqTransResult] BILLTYPE='1' MOCALLINFO 삭제 완료: SourceCallNo(%s) DestCID(%s) MsgId(%s)",
                                request.data.srcCallNo, request.data.destCID, msgId),
                            Thread.currentThread().getId()
                        )
                    } else {
                        witcomLog.c_write(loggerName, Level.WARN,
                            String.format("[processSMReqTransResult] BILLTYPE='1' MOCALLINFO 삭제 실패: 레코드 없음 - SourceCallNo(%s) DestCID(%s) MsgId(%s)",
                                request.data.srcCallNo, request.data.destCID, msgId),
                            Thread.currentThread().getId()
                        )
                    }
                }
                
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processSMReqTransResult] BILLTYPE='1' (비과금) 분기 완료: 레코드 삭제 완료, 과금 처리 스킵"),
                    Thread.currentThread().getId()
                )
                return
            }

            // 2. C 코드 LINE 438: msgStatus == SEND_OK 체크
            // HTTP 환경에서는 HTTP 송신 성공 시 DB에 저장하므로 무조건 OK라고 가정
            // 하지만 CP로부터 받은 msgStatus를 확인하여 과금 여부 결정
            val msgStatus = qItem.ucMsgStatus.toInt()
            witcomLog.c_write(loggerName, Level.DEBUG,
                String.format("[processSMReqTransResult] msgStatus 체크: msgStatus(%d), SEND_OK(%d), result(%s)",
                    msgStatus,
                    SEND_OK,
                    if (msgStatus == SEND_OK) "SEND_OK" else "NOT_SEND_OK"
                ),
                Thread.currentThread().getId()
            )
            
            if (msgStatus == SEND_OK) {
                // C 코드 LINE 438-530: SEND_OK인 경우 과금 처리
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processSMReqTransResult] SEND_OK 분기 선택: msgStatus(%d)==SEND_OK(%d) - 과금 처리 진행",
                        msgStatus,
                        SEND_OK
                    ),
                    Thread.currentThread().getId()
                )
                
                // 2-1. 업데이트된 MOCALLINFO로 MO ACK 기능(과금 처리) 수행
                // C 코드 LINE 3371: if (gMOTRBILL) ProcessTRVBILLMO(ptrGIMsgHdr);
                witcomLog.c_write(loggerName, Level.DEBUG,
                    String.format("[processSMReqTransResult] 과금 처리 분기 체크: billType(%s), gMOTRBILL(%s)",
                        billType,
                        if (gMOTRBILL) "Y" else "N"
                    ),
                    Thread.currentThread().getId()
                )
                
                if (gMOTRBILL) {
                    // HTTP 방식: InsqStat 호출이 VBILLMO 큐 적재와 처리를 함께 수행
                    // processMOBilling 내부에서 InsqStat을 호출하면 자동으로 큐 적재와 처리가 이루어짐
                    witcomLog.c_write(loggerName, Level.INFO,
                        String.format("[processSMReqTransResult] gMOTRBILL='Y' 분기 선택: gMOTRBILL(%s)=='Y' - processMOBilling 호출",
                            if (gMOTRBILL) "Y" else "N"
                        ),
                        Thread.currentThread().getId()
                    )
                    processMOBilling(qItem, request, gipHttpMoAccess, smsQLib, loggerName)
                    witcomLog.c_write(loggerName, Level.INFO,
                        String.format("[processSMReqTransResult] gMOTRBILL='Y' 분기 완료: processMOBilling 완료"),
                        Thread.currentThread().getId()
                    )
                } else {
                    // C 코드 LINE 3371: !gMOTRBILL인 경우 아무것도 하지 않음
                    // MO-ACK 단계에서 이미 과금 처리가 완료되었으므로 MO-TR 단계에서는 처리하지 않음
                    witcomLog.c_write(loggerName, Level.INFO,
                        String.format("[processSMReqTransResult] !gMOTRBILL 분기: gMOTRBILL(%s)=='N' - 과금 처리 없음 (MO-ACK 단계에서 이미 완료)",
                            if (gMOTRBILL) "Y" else "N"
                        ),
                        Thread.currentThread().getId()
                    )
                }
            } else {
                // C 코드 LINE 531-550: SEND_OK가 아닌 경우 NOT BILL 처리
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processSMReqTransResult] NOT_SEND_OK 분기 선택: msgStatus(%d)!=SEND_OK(%d) - NOT BILL 처리",
                        msgStatus,
                        SEND_OK
                    ),
                    Thread.currentThread().getId()
                )
                
                // C 코드 LINE 533: RcsResult 설정
                qItem.RcsResult = RCS_RESULT_ETC.toShort()
                
                // C 코드 LINE 545-546: InsqStat 호출
                // nInforNo: qItem.usSource 사용 (IF_NULL은 에러 케이스이므로 사용하지 않음)
                smsQLib.InsqStat(
                    qItem,
                    MESSAGE_TR,
                    0,
                    gServerID,
                    MODULEID_VBILLMO,
                    SERVICEID_GIPEVENT,
                    ERRORID_CP_MO_TR_FAIL,
                    ST_VBILLMO_DONT_BILL_TRFAIL,
                    getNInforNo(qItem),  // qItem.usSource (nInforNo)
                    TID_NO_SAVE,
                    LT_BOTH,
                    0
                )
                
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processSMReqTransResult] NOT_SEND_OK 분기 완료: NOT BILL 처리 완료 - msgStatus(%d), SourceCID(%s) SourceCallNo(%s) DestCID(%s) DestCallNo(%s) MessageID(%s)",
                        msgStatus,
                        request.data.srcCID ?: "",
                        request.data.srcCallNo ?: "",
                        request.data.destCID ?: "",
                        request.data.destCallNo ?: "",
                        request.data.msgId ?: ""
                    ),
                    Thread.currentThread().getId()
                )
            }
        } else {
            witcomLog.c_write(loggerName, Level.WARN,
                String.format("[processSMReqTransResult] gipHttpMoAccess NULL 분기: gipHttpMoAccess가 null이므로 MOCALLINFO 업데이트 및 과금 처리 스킵"),
                Thread.currentThread().getId()
            )
        }
    }
    
    /**
     * C 코드 ProcessTRVBILLMO() 포팅: VBILL_MO 큐 적재 및 통계 처리
     * 
     * HTTP 방식에서는 더 이상 사용하지 않음.
     * MOTRBILL='Y'일 때는 processMOBilling을 직접 호출하여 처리함.
     * (HTTP 방식에서는 InsqStat 호출이 VBILLMO 큐 적재와 처리를 함께 수행)
     */
    // private suspend fun enqueueVbillMoTR(
    //     qItem: QITEM,
    //     request: ResponseTR,
    //     gipHttpMoAccess: GipHttpMoAccessEntity
    // ) {
    //     // HTTP 방식에서는 더 이상 사용하지 않음
    //     // MOTRBILL='Y'일 때는 processMOBilling을 직접 호출하여 처리함
    // }
    
    /**
     * SelectGIPMOCallInfo 함수
     * 
     * msgId만으로 조회하도록 변경
     * 
     * SELECT * FROM MOCALLINFO
     * WHERE MSGID = ?
     * ORDER BY MOSUBTIME DESC
     * 
     * DELETE FROM MOCALLINFO
     * WHERE MSGID = ? (조회 후 삭제)
     * 
     * @param msgId 메시지 ID
     * @return MOCallInfoEntity 또는 null (데이터 없음)
     */
    @Transactional
    open suspend fun selectGIPMOCallInfo(
        msgId: String
    ): MOCallInfoEntity? {
        val loggerName = getLoggerName(null, null, null)
        return try {
            // msgId만으로 조회 (가장 최근 것)
            val moCallInfo = moCallInfoRepository.findByMsgId(msgId)
            
            // 조회 결과 로깅
            if (moCallInfo != null) {
                witcomLog.c_write(
                    loggerName,
                    Level.DEBUG,
                    String.format("SelectGIPMOCallInfo() 조회 성공: MsgId(%s), srcCallNo(%s), destCID(%s), traceId(%s), moSubTime(%s)",
                        msgId,
                        moCallInfo.srcCallNo ?: "",
                        moCallInfo.destCId ?: "",
                        moCallInfo.traceId ?: "", 
                        moCallInfo.moSubTime ?: ""),
                    Thread.currentThread().getId()
                )
            } else {
                witcomLog.c_write(
                    loggerName,
                    Level.WARN,
                    String.format("SelectGIPMOCallInfo() ⚠️ 조회 실패: MsgId(%s) - 레코드 없음. MO 메시지 수신 시 insertGIPMOCallInfo로 레코드가 생성되어야 함",
                        msgId),
                    Thread.currentThread().getId()
                )
            }
            
            moCallInfo
        } catch (e: Exception) {
            witcomLog.c_write(
                loggerName,
                Level.ERROR,
                String.format("SelectGIPMOCallInfo() Select Error: msgId(%s), error(%s)", msgId, e.message),
                Thread.currentThread().getId()
            )
            null
        }
    }
    
    /**
     * DeleteGIPMOCallInfo 함수
     * 
     * 조회한 Entity로 삭제 (복합 키를 모두 사용하여 정확히 삭제)
     * 
     * @param moCallInfo 삭제할 MOCallInfoEntity
     */
    @Transactional
    open suspend fun deleteGIPMOCallInfo(moCallInfo: MOCallInfoEntity) {
        val loggerName = getLoggerName(moCallInfo.destCId, null, null)
        try {
            moCallInfoRepository.delete(moCallInfo)
            moCallInfoRepository.flush()  // 즉시 DB에 반영
            
            witcomLog.c_write(
                loggerName,
                Level.DEBUG,
                String.format("DeleteGIPMOCallInfo() 삭제 완료: MsgId(%s), srcCId(%s), destCId(%s)",
                    moCallInfo.msgId ?: "",
                    moCallInfo.srcCId ?: "",
                    moCallInfo.destCId ?: ""),
                Thread.currentThread().getId()
            )
        } catch (e: Exception) {
            witcomLog.c_write(
                loggerName,
                Level.ERROR,
                String.format("DeleteGIPMOCallInfo() ⚠️ 삭제 실패: MsgId(%s), srcCId(%s), destCId(%s), error(%s)",
                    moCallInfo.msgId ?: "",
                    moCallInfo.srcCId ?: "",
                    moCallInfo.destCId ?: "",
                    e.message),
                Thread.currentThread().getId()
            )
        }
    }
    
    /**
     * UpdateGIPMOCallInfo 함수
     * 
     * MO-ACK 생략, MO-TR로 통합된 환경에서는 INSERT만 수행
     * 원본 레코드는 유지하고, 새 레코드(ackMsgId)만 추가
     * 
     * 참고: C 코드는 MO-ACK 처리 시 INSERT 후 DELETE를 수행하지만,
     *       HTTP 환경에서는 MO-ACK를 생략하고 MO-TR로 통합했으므로
     *       DELETE는 불필요하며, selectGIPMOCallInfo()에서 조회 후 삭제함
     * 
     * C 코드 참고: GIDBLib.c LINE 3398-3487, 3884-3899
     * INSERT INTO MOCALLINFO (SRCCID, SRCCALLNO, DESTCID, DESTCALLNO, MSGID, ...)
     * SELECT SRCCID, SRCCALLNO, DESTCID, DESTCALLNO, :ackMsgId AS MSGID, ...
     * FROM MOCALLINFO
     * WHERE SRCCALLNO = ? AND DESTCID = ? AND MSGID = ?
     */
    @Transactional
    open suspend fun updateGIPMOCallInfo(request: ResponseTR) {
        val loggerName = getLoggerName(request.data.destCID, null, null)
        try {
            val srcCallNo = request.data.srcCallNo ?: ""
            val destCId = request.data.destCID ?: ""
            val msgId = request.data.msgId ?: ""
            
            // 함수 진입 로그 추가 (INFO 레벨)
            witcomLog.c_write(
                loggerName,
                Level.INFO,
                String.format(
                    "UpdateGIPMOCallInfo() 함수 진입: SourceCallNo(%s) DestCID(%s) MsgId(%s)",
                    srcCallNo,
                    destCId,
                    msgId
                ),
                Thread.currentThread().getId()
            )
            
            // C 코드 참고: GIDBLib.sc LINE 1148-1250
            // 1. 원본 레코드 조회 (SRCCALLNO, DESTCID, MSGID로 조회)
            val moCallInfo = moCallInfoRepository.findBySrcCallNoAndDestCIdAndMsgId(
                srcCallNo,
                destCId,
                msgId
            )
            
            if (moCallInfo == null) {
                witcomLog.c_write(
                    loggerName,
                    Level.WARN,
                    String.format(
                        "UpdateGIPMOCallInfo() ⚠️ 레코드 없음: SourceCallNo(%s) DestCID(%s) MsgId(%s) - 원본 레코드가 존재하지 않음",
                        srcCallNo,
                        destCId,
                        msgId
                    ),
                    Thread.currentThread().getId()
                )
                return
            }
            
            // MOTRBILL 값 확인: MO-ACK 단계에서 MOTRBILL='Y'인 경우 삭제하지 않음 (MO-TR 단계에서 삭제)
            val gipHttpMoAccess = gipHttpMoAccessRepository.findByCid(destCId).orElse(null)
            val gMOTRBILL = (gipHttpMoAccess?.moTrBill == 1)
            
            witcomLog.c_write(
                loggerName,
                Level.INFO,  // DEBUG → INFO로 변경
                String.format(
                    "UpdateGIPMOCallInfo() MOTRBILL 체크: destCID(%s), gMOTRBILL(%s), MO-ACK 단계에서 삭제 여부 결정",
                    destCId,
                    if (gMOTRBILL) "Y" else "N"
                ),
                Thread.currentThread().getId()
            )
            
            // HTTP 환경에서는 MO-ACK를 생략하고 MO-TR로 통합했으므로,
            // MO와 MO-TR이 동일한 msgID를 사용할 수 있음
            // 하지만 C 코드의 UpdateGIPMOCallInfo는 새로운 MSGID(AckMsgId)로 레코드를 복사함
            // 
            // BILLTYPE == '1' && MOTRBILL == 'Y'인 경우에도 레코드가 저장되어야 하므로,
            // HTTP 환경에서는 MSGID가 동일하더라도 레코드가 유지되도록 처리
            // 
            // C 코드: GIDBLib.sc LINE 1192-1199
            // INSERT INTO MOCALLINFO ... SELECT ... FROM MOCALLINFO WHERE ...
            // DELETE FROM MOCALLINFO WHERE ...
            
            // HTTP 환경에서는 MSGID가 동일할 수 있으므로,
            // 새 레코드 생성 시도 (이미 존재하면 무시)
            try {
                // C 코드처럼 새로운 MSGID로 복사 시도
                // HTTP 환경에서는 MSGID가 동일하므로, 실제로는 복사되지 않을 수 있음
                // 하지만 레코드가 존재하는지 확인하고 로그만 남김
                val insertResult = moCallInfoRepository.insertFromExisting(
                    msgId,  // HTTP 환경에서는 AckMsgId == msgId
                    srcCallNo,
                    destCId,
                    msgId
                )
                
                if (insertResult > 0) {
                    witcomLog.c_write(
                        loggerName,
                        Level.INFO,
                        String.format(
                            "UpdateGIPMOCallInfo() 새 레코드 생성 완료: SourceCallNo(%s) DestCID(%s) MsgId(%s) InsertResult(%d)",
                            srcCallNo,
                            destCId,
                            msgId,
                            insertResult
                        ),
                        Thread.currentThread().getId()
                    )
                    
                    // 규칙: MOTRBILL='Y'인 경우 MO-ACK 단계에서 삭제하지 않음 (MO-TR 단계에서 삭제)
                    // MOTRBILL='N'인 경우에만 MO-ACK 단계에서 삭제
                    if (!gMOTRBILL) {
                        // C 코드: 기존 레코드 삭제 (MOTRBILL='N'인 경우에만)
                        val deleteResult = moCallInfoRepository.deleteBySrcCallNoAndDestCIdAndMsgId(
                            srcCallNo,
                            destCId,
                            msgId
                        )
                        
                        witcomLog.c_write(
                            loggerName,
                            Level.INFO,
                            String.format(
                                "UpdateGIPMOCallInfo() 기존 레코드 삭제 완료: SourceCallNo(%s) DestCID(%s) MsgId(%s) DeleteResult(%d), MOTRBILL='N'",
                                srcCallNo,
                                destCId,
                                msgId,
                                deleteResult
                            ),
                            Thread.currentThread().getId()
                        )
                    } else {
                        // MOTRBILL='Y'인 경우: MO-ACK 단계에서 삭제하지 않음 (MO-TR 단계에서 삭제 예정)
                        witcomLog.c_write(
                            loggerName,
                            Level.INFO,
                            String.format(
                                "UpdateGIPMOCallInfo() MOTRBILL='Y' MO-ACK 단계: 기존 레코드 삭제 스킵 (MO-TR 단계에서 삭제 예정) - SourceCallNo(%s) DestCID(%s) MsgId(%s)",
                                srcCallNo,
                                destCId,
                                msgId
                            ),
                            Thread.currentThread().getId()
                        )
                    }
                } else {
                    // HTTP 환경에서는 MSGID가 동일하므로 복사가 실패할 수 있음 (이미 존재)
                    // 이 경우 원본 레코드가 유지되므로 정상 동작
                    witcomLog.c_write(
                        loggerName,
                        Level.INFO,
                        String.format(
                            "UpdateGIPMOCallInfo() 레코드 확인 완료: SourceCallNo(%s) DestCID(%s) MsgId(%s) - HTTP 환경: MSGID 동일하므로 원본 레코드 유지, InsertResult(%d)",
                            srcCallNo,
                            destCId,
                            msgId,
                            insertResult
                        ),
                        Thread.currentThread().getId()
                    )
                }
            } catch (e: Exception) {
                // HTTP 환경에서는 MSGID가 동일하므로 중복 키 오류가 발생할 수 있음
                // 이 경우 원본 레코드가 유지되므로 정상 동작
                witcomLog.c_write(
                    loggerName,
                    Level.INFO,  // DEBUG → INFO로 변경
                    String.format(
                        "UpdateGIPMOCallInfo() 레코드 복사 시도 중 오류 (정상일 수 있음): SourceCallNo(%s) DestCID(%s) MsgId(%s) Error(%s) - HTTP 환경: MSGID 동일하므로 원본 레코드 유지",
                        srcCallNo,
                        destCId,
                        msgId,
                        e.message
                    ),
                    Thread.currentThread().getId()
                )
            }
        } catch (e: Exception) {
            witcomLog.c_write(
                loggerName,
                Level.ERROR,
                String.format("UpdateGIPMOCallInfo() Error: %s", e.message),
                Thread.currentThread().getId()
            )
        }
    }
    
    /**
     * SelectTRMO_NOTISEND 함수
     * 
     * C 코드 참고: GIDBLib.c LINE 2609-2688
     * SELECT * FROM MO_NOTISEND
     * WHERE SRCCALLNO = ? AND DESTCID = ? AND MSGID = ? AND SERVERTYPE = 'V'
     * ORDER BY MOSUBTIME DESC
     * 
     * DELETE FROM MO_NOTISEND
     * WHERE SRCCALLNO = ? AND DESTCID = ? AND MSGID = ? AND SERVERTYPE = 'V' (조회 후 삭제)
     * 
     * @return MONotISendEntity 또는 null (데이터 없음)
     */
    @Transactional
    open suspend fun selectTRMO_NOTISEND(
        srcCallNo: String,
        destCId: String,
        msgId: String
    ): MONotISendEntity? {
        val loggerName = getLoggerName(destCId, null, null)
        return try {
            // ROWNUM = 1로 첫 번째 결과만 조회 (가장 최근 것)
            // SERVERTYPE='V'는 쿼리 내부에서 하드코딩됨
            val moNotISend = moNotISendRepository.findBySrcCallNoAndDestCIdAndMsgIdAndServerType(
                srcCallNo,
                destCId,
                msgId
            ).orElse(null)
            
            // C 코드 LINE 2609-2688: SELECT 성공 후 DELETE 수행
            // SERVERTYPE='V'는 쿼리 내부에서 하드코딩됨
            if (moNotISend != null) {
                val deleteResult = moNotISendRepository.deleteBySrcCallNoAndDestCIdAndMsgIdAndServerType(
                    srcCallNo,
                    destCId,
                    msgId
                )
                if (deleteResult > 0) {
                    witcomLog.c_write(
                        loggerName,
                        Level.DEBUG,
                        String.format("SelectTRMO_NOTISEND() Deleted: SourceCallNo(%s) DestCID(%s) MsgId(%s) DeleteResult(%d)",
                            srcCallNo, destCId, msgId, deleteResult),
                        Thread.currentThread().getId()
                    )
                }
            }
            
            moNotISend
        } catch (e: Exception) {
            witcomLog.c_write(
                loggerName,
                Level.ERROR,
                String.format("SelectTRMO_NOTISEND() Select Error: %s", e.message),
                Thread.currentThread().getId()
            )
            null
        }
    }
    
    /**
     * DBGet_GIENQ_CID 함수
     * 
     * C 코드 참고: GIDBLib.c LINE 3611-3751
     * SELECT CID, LENGTH(CID)
     * FROM GIENQ
     * WHERE CID = SUBSTR(:v_tmp_cid, 1, :Cid_len)
     * 
     * @param tmpCid CID 문자열
     * @return CID 길이 (성공), -1 (실패)
     */
    @Transactional(readOnly = true)
    open suspend fun dbGetGIENQCID(tmpCid: String, loggerName: String? = null): Int {
        val effectiveLoggerName = loggerName ?: getLoggerName(tmpCid, null, null)
        if (tmpCid.isEmpty()) {
            witcomLog.c_write(effectiveLoggerName, Level.ERROR, String.format("[DBGet_GIENQ_CID] Invalid Cid(%s)", tmpCid), Thread.currentThread().getId())
            return -1
        }
        
        try {
            var cidLen = tmpCid.length
            var found = false
            
            while (cidLen > 0) {
                val searchCid = tmpCid.substring(0, cidLen)
                
                // GIENQ 테이블에서 CID 조회
                val gienqList = gienqRepository.findAll()
                val matched = gienqList.firstOrNull { 
                    it.cid != null && it.cid.startsWith(searchCid) 
                }
                
                if (matched != null) {
                    val matchedCidLen = matched.cid?.length ?: 0
                    if (matchedCidLen > 0) {
                        return matchedCidLen
                    }
                    found = true
                    break
                }
                
                cidLen--
            }
            
            if (!found) {
                witcomLog.c_write(effectiveLoggerName, Level.ERROR, String.format("[DBGet_GIENQ_CID] NO_DATA CID(%s)", tmpCid), Thread.currentThread().getId())
                return -1
            }
            
            return cidLen
        } catch (e: Exception) {
            witcomLog.c_write(
                effectiveLoggerName,
                Level.ERROR,
                String.format("[DBGet_GIENQ_CID] Error: %s", e.message),
                Thread.currentThread().getId()
            )
            return -1
        }
    }
    
    /**
     * C 코드의 CallbackFilter 함수와 동일한 동작을 수행합니다.
     * 콜백 값을 필터링합니다.
     * 
     * @param callback 원본 콜백 값
     * @return 필터링된 콜백 값
     * 
     * C 코드 참고: CallbackFilter 함수는 콜백 값을 필터링하는 함수입니다.
     * 동연님 확인해: CallbackFilter 구현 필요 (C 코드 참고)
     * 현재는 원본 값을 그대로 반환하도록 구현 (필터링 로직 추가 필요)
     */
    private fun callbackFilter(callback: String): String {
        // C 코드의 CallbackFilter 구현을 참고하여 필터링 로직 추가 필요
        // 현재는 원본 값을 그대로 반환
        if (callback.isBlank()) {
            return ""
        }
        
        // 기본 필터링: 특수 문자 제거 등 (필요시 추가)
        return callback.trim()
    }
    
    /**
     * UpdateMO_NOTISEND 함수
     * 
     * C 코드 참고: GIDBLib.sc LINE 1606-1709 (UpdateMO_NOTISEND 함수)
     * C 코드 참고: GIPEVENT_c.c LINE 2259-2263 (NOTI_PLUS 케이스에서 호출)
     * 
     * C 코드 동작:
     * 1. INSERT INTO MO_NOTISEND ... SELECT ... FROM MO_NOTISEND WHERE ... (새로운 MSGID로 복사)
     * 2. DELETE FROM MO_NOTISEND WHERE ... (기존 레코드 삭제)
     * 
     * HTTP 환경에서는 MO-ACK를 생략하고 MO-TR로 통합했으므로,
     * MO와 MO-TR이 동일한 msgID를 사용할 수 있음
     * 하지만 BILLTYPE == '1' && MOTRBILL == 'Y'인 경우에도 레코드가 저장되어야 하므로,
     * C 코드와 동일한 로직을 수행하되, MSGID가 동일한 경우 원본 레코드가 유지되도록 처리
     */
    @Transactional
    open fun updateMO_NOTISEND(request: ResponseTR) {
        val loggerName = getLoggerName(request.data.destCID, null, null)
        try {
            val srcCallNo = request.data.srcCallNo ?: ""
            val destCId = request.data.destCID ?: ""
            val msgId = request.data.msgId ?: ""
            
            witcomLog.c_write(
                loggerName,
                Level.DEBUG,
                String.format(
                    "UpdateMO_NOTISEND() 레코드 확인 시작: SourceCallNo(%s) DestCID(%s) MsgId(%s)",
                    srcCallNo,
                    destCId,
                    msgId
                ),
                Thread.currentThread().getId()
            )
            
            // C 코드: GIDBLib.sc LINE 1640-1680
            // 원본 레코드 조회 (SERVERTYPE='V'는 쿼리 내부에 하드코딩됨)
            val moNotISend = moNotISendRepository.findBySrcCallNoAndDestCIdAndMsgIdAndServerType(
                srcCallNo,
                destCId,
                msgId
            ).orElse(null)
            
            if (moNotISend == null) {
                witcomLog.c_write(
                    loggerName,
                    Level.WARN,
                    String.format(
                        "UpdateMO_NOTISEND() ⚠️ 레코드 없음: SourceCallNo(%s) DestCID(%s) MsgId(%s) - 원본 레코드가 존재하지 않음",
                        srcCallNo,
                        destCId,
                        msgId
                    ),
                    Thread.currentThread().getId()
                )
                return
            }
            
            // C 코드: GIDBLib.sc LINE 1650-1680
            // INSERT INTO MO_NOTISEND ... SELECT ... FROM MO_NOTISEND WHERE ...
            // HTTP 환경에서는 MSGID가 동일할 수 있으므로, 새 레코드 생성 시도
            try {
                val insertResult = moNotISendRepository.insertFromExisting(
                    msgId,  // HTTP 환경에서는 AckMsgId == msgId
                    srcCallNo,
                    destCId,
                    msgId
                )
                
                if (insertResult > 0) {
                    witcomLog.c_write(
                        loggerName,
                        Level.INFO,
                        String.format(
                            "UpdateMO_NOTISEND() 새 레코드 생성 완료: SourceCallNo(%s) DestCID(%s) MsgId(%s) InsertResult(%d)",
                            srcCallNo,
                            destCId,
                            msgId,
                            insertResult
                        ),
                        Thread.currentThread().getId()
                    )
                    
                    // C 코드: GIDBLib.sc LINE 1682-1707
                    // DELETE FROM MO_NOTISEND WHERE ...
                    val deleteResult = moNotISendRepository.deleteBySrcCallNoAndDestCIdAndMsgIdAndServerType(
                        srcCallNo,
                        destCId,
                        msgId
                    )
                    
                    witcomLog.c_write(
                        loggerName,
                        Level.INFO,
                        String.format(
                            "UpdateMO_NOTISEND() 기존 레코드 삭제 완료: SourceCallNo(%s) DestCID(%s) MsgId(%s) DeleteResult(%d)",
                            srcCallNo,
                            destCId,
                            msgId,
                            deleteResult
                        ),
                        Thread.currentThread().getId()
                    )
                } else {
                    // HTTP 환경에서는 MSGID가 동일하므로 복사가 실패할 수 있음 (이미 존재)
                    // 이 경우 원본 레코드가 유지되므로 정상 동작
                    witcomLog.c_write(
                        loggerName,
                        Level.INFO,
                        String.format(
                            "UpdateMO_NOTISEND() 레코드 확인 완료: SourceCallNo(%s) DestCID(%s) MsgId(%s) - HTTP 환경: MSGID 동일하므로 원본 레코드 유지",
                            srcCallNo,
                            destCId,
                            msgId
                        ),
                        Thread.currentThread().getId()
                    )
                }
            } catch (e: Exception) {
                // HTTP 환경에서는 MSGID가 동일하므로 중복 키 오류가 발생할 수 있음
                // 이 경우 원본 레코드가 유지되므로 정상 동작
                witcomLog.c_write(
                    loggerName,
                    Level.DEBUG,
                    String.format(
                        "UpdateMO_NOTISEND() 레코드 복사 시도 중 오류 (정상일 수 있음): SourceCallNo(%s) DestCID(%s) MsgId(%s) Error(%s) - HTTP 환경: MSGID 동일하므로 원본 레코드 유지",
                        srcCallNo,
                        destCId,
                        msgId,
                        e.message
                    ),
                    Thread.currentThread().getId()
                )
            }
            
        } catch (e: Exception) {
            witcomLog.c_write(
                loggerName,
                Level.ERROR,
                String.format("UpdateMO_NOTISEND() Error: %s", e.message),
                Thread.currentThread().getId()
            )
            throw e
        }
    }
    
    /**
     * GetFWDLINK 함수
     * 
     * C 코드 참고: GIDBLib.c LINE 2751-2899
     * SELECT MSGIDCENTER, RECV_QNO, FWD_SRC
     * FROM FWDLINK_A_B_C
     * WHERE MSGIDSERVER = ? AND DESTCALLNO = ?
     * 
     * @param msgIdServer MSGIDSERVER
     * @param destCallNo DESTCALLNO
     * @return GetFWDLINKResult (msgIdCenter, recvQNo, fwdNo) 또는 null
     */
    data class GetFWDLINKResult(
        val msgIdCenter: String,
        val recvQNo: Int,
        val fwdNo: String
    )
    
    @Transactional(readOnly = true)
    public open suspend fun getFWDLINK(
        msgIdServer: String,
        destCallNo: String,
        loggerName: String
    ): GetFWDLINKResult? {
        return try {
            // C 코드 LINE 2778-2797: SELECT FROM FWDLINK_A_B_C
            // Native Query 사용
            val query = """
                SELECT MSGIDCENTER, RECV_QNO, FWD_SRC
                FROM SMS.FWDLINK_A_B_C
                WHERE MSGIDSERVER = ? AND DESTCALLNO = ?
            """.trimIndent()
            
            val nativeQuery = entityManager.createNativeQuery(query)
            nativeQuery.setParameter(1, msgIdServer)
            nativeQuery.setParameter(2, destCallNo)
            
            val result = nativeQuery.resultList
            if (result.isNotEmpty()) {
                val row = result[0] as Array<*>
                GetFWDLINKResult(
                    msgIdCenter = row[0]?.toString() ?: "",
                    recvQNo = (row[1] as? Number)?.toInt() ?: 0,
                    fwdNo = row[2]?.toString() ?: ""
                )
            } else {
                null
            }
        } catch (e: Exception) {
            witcomLog.c_write(
                loggerName,
                Level.ERROR,
                String.format("GetFWDLINK() Error: msgIdServer(%s), destCallNo(%s), error(%s)", msgIdServer, destCallNo, e.message),
                Thread.currentThread().getId()
            )
            null
        }
    }
    
    /**
     * DBDelOCSCallInfoFromVN 함수
     * 
     * C 코드 참고: HBILLMO_c.c LINE 1039-1041
     * DELETE FROM OCS_CALLINFO
     * WHERE MSGIDSERVER = ? AND VIRTUAL_NUM = ?
     * 
     * 조건: AI_SURVEY_NUMBER (05016) && Recv_QNo < 10000
     * 
     * @param msgIdServer MSGIDSERVER (ptrQItem->ucMsgId)
     * @param virtualNum VIRTUAL_NUM (ptrMOCallInfo->szVirtualNum)
     * @return 0: 성공, -1: 실패
     */
    @Transactional
    open suspend fun dbDelOCSCallInfoFromVN(
        msgIdServer: String,
        virtualNum: String,
        loggerName: String
    ): Int {
        return try {
            // C 코드 LINE 1039-1041: DELETE FROM OCS_CALLINFO
            // Native Query 사용
            val query = """
                DELETE FROM SMS.OCS_CALLINFO
                WHERE MSGIDSERVER = ? AND VIRTUAL_NUM = ?
            """.trimIndent()
            
            val nativeQuery = entityManager.createNativeQuery(query)
            nativeQuery.setParameter(1, msgIdServer)
            nativeQuery.setParameter(2, virtualNum)
            
            val result = nativeQuery.executeUpdate()
            
            witcomLog.c_write(
                loggerName,
                Level.DEBUG,
                String.format(
                    "DBDelOCSCallInfoFromVN() Delete: msgIdServer(%s), virtualNum(%s), result(%d)",
                    msgIdServer,
                    virtualNum,
                    result
                ),
                Thread.currentThread().getId()
            )
            
            if (result > 0) {
                0 // ALTI_SUCCESS
            } else {
                -1 // ALTI_NODATA
            }
        } catch (e: Exception) {
            witcomLog.c_write(
                loggerName,
                Level.ERROR,
                String.format("DBDelOCSCallInfoFromVN() Error: msgIdServer(%s), virtualNum(%s), error(%s)", msgIdServer, virtualNum, e.message),
                Thread.currentThread().getId()
            )
            -1 // ALTI_FAIL
        }
    }
    
    /**
     * 과금 처리 후 ESMClass별 분기 처리
     * 
     * 규칙: processMOBilling() 완료 후 ESMClass별로 DB 삭제 + 메시지 전송/라우팅 수행
     * - ESMClass 90/91: DBDELMO_NOTISEND + MT_NOTISending
     * - ESMClass 20/21: DBDELMO_NOTISEND + MT_NOTI_PLUS_Sending
     * - 그 외: DBDELMOCallInfo + RouteTRMsg2PCS
     */
    private suspend fun processESMClassBranch(
        qItem: QITEM,
        request: ResponseTR,
        gipHttpMoAccess: GipHttpMoAccessEntity?,
        smsQLib: SmsQLib
    ) {
        val loggerName = getLoggerName(request.data.destCID, null, null)
        
        witcomLog.c_write(loggerName, Level.DEBUG,
            String.format("[processESMClassBranch] 함수 진입: destCID(%s), srcCallNo(%s)",
                request.data.destCID ?: "",
                request.data.srcCallNo ?: ""
            ),
            Thread.currentThread().getId()
        )
        
        // ESMClass 확인
        val rsv4Protocol11 = request.data.rsv4Protocol?.getOrNull(11)?.data ?: 0
        
        when {
            // ESMClass 90, 91: 등기문자
            rsv4Protocol11 == NOTI_NORMAL_MO || rsv4Protocol11 == NOTI_PORTED_MO -> {
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processESMClassBranch] 등기문자 분기 선택: ESMClass(%d) - DBDELMO_NOTISEND + MT_NOTISending",
                        rsv4Protocol11
                    ),
                    Thread.currentThread().getId()
                )
                
                // DB 삭제: MO_NOTISEND에서 SERVERTYPE='V', MSGID=msgId 레코드 삭제
                dbDelMO_NOTISEND(request, loggerName)
                
                // 메시지 전송: 등기문자 전송
                mt_NOTISending(qItem, request, gipHttpMoAccess, smsQLib, loggerName)
            }
            // ESMClass 20, 21: 안심문자
            rsv4Protocol11 == NOTI_PLUS_NORMAL_MO || rsv4Protocol11 == NOTI_PLUS_PORTED_MO -> {
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processESMClassBranch] 안심문자 분기 선택: ESMClass(%d) - DBDELMO_NOTISEND + MT_NOTI_PLUS_Sending",
                        rsv4Protocol11
                    ),
                    Thread.currentThread().getId()
                )
                
                // DB 삭제: MO_NOTISEND에서 SERVERTYPE='V', MSGID=msgId 레코드 삭제
                dbDelMO_NOTISEND(request, loggerName)
                
                // 메시지 전송: 안심문자 전송
                mt_NOTI_PLUS_Sending(qItem, request, gipHttpMoAccess, smsQLib, loggerName)
            }
            // 그 외 ESMClass
            else -> {
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processESMClassBranch] 일반 분기 선택: ESMClass(%d) - DBDELMOCallInfo + RouteTRMsg2PCS",
                        rsv4Protocol11
                    ),
                    Thread.currentThread().getId()
                )
                
                // DB 삭제: MOCALLINFO에서 레코드 삭제
                dbDelMOCallInfo(request, loggerName)
                
                // 메시지 라우팅: PCS로 라우팅
                routeTRMsg2PCS(qItem, request, gipHttpMoAccess, smsQLib, loggerName)
            }
        }
        
        witcomLog.c_write(loggerName, Level.DEBUG,
            String.format("[processESMClassBranch] 함수 완료: ESMClass(%d) 처리 완료",
                rsv4Protocol11
            ),
            Thread.currentThread().getId()
        )
    }
    
    /**
     * MO_NOTISEND 레코드 삭제 (등기문자/안심문자용)
     */
    private suspend fun dbDelMO_NOTISEND(request: ResponseTR, loggerName: String) {
        try {
            val srcCallNo = request.data.srcCallNo
            val destCId = request.data.destCID
            val msgId = request.data.msgId ?: ""  // MO와 MO-TR 모두 동일한 msgID
            
            witcomLog.c_write(loggerName, Level.DEBUG,
                String.format("[dbDelMO_NOTISEND] 삭제 시작: SourceCallNo(%s) DestCID(%s) MsgId(%s)",
                    srcCallNo,
                    destCId,
                    msgId
                ),
                Thread.currentThread().getId()
            )
            
            // MO_NOTISEND에서 SERVERTYPE='V', MSGID=msgId 레코드 삭제
            val deleteResult = moNotISendRepository.deleteBySrcCallNoAndDestCIdAndMsgIdAndServerType(
                srcCallNo,
                destCId,
                msgId
            )
            
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[dbDelMO_NOTISEND] 삭제 완료: SourceCallNo(%s) DestCID(%s) MsgId(%s) DeleteResult(%d)",
                    srcCallNo,
                    destCId,
                    msgId,
                    deleteResult
                ),
                Thread.currentThread().getId()
            )
        } catch (e: Exception) {
            witcomLog.c_write(loggerName, Level.ERROR,
                String.format("[dbDelMO_NOTISEND] Error: %s", e.message),
                Thread.currentThread().getId()
            )
        }
    }
    
    /**
     * MOCALLINFO 레코드 삭제 (일반 MO용)
     */
    private suspend fun dbDelMOCallInfo(request: ResponseTR, loggerName: String) {
        try {
            val srcCallNo = request.data.srcCallNo
            val destCId = request.data.destCID
            val msgId = request.data.msgId ?: ""  // MO와 MO-TR 모두 동일한 msgID
            
            witcomLog.c_write(loggerName, Level.DEBUG,
                String.format("[dbDelMOCallInfo] 삭제 시작: SourceCallNo(%s) DestCID(%s) MsgId(%s)",
                    srcCallNo,
                    destCId,
                    msgId
                ),
                Thread.currentThread().getId()
            )
            
            // MOCALLINFO에서 레코드 삭제
            val deleteResult = moCallInfoRepository.deleteBySrcCallNoAndDestCIdAndMsgId(
                srcCallNo,
                destCId,
                msgId
            )
            
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[dbDelMOCallInfo] 삭제 완료: SourceCallNo(%s) DestCID(%s) MsgId(%s) DeleteResult(%d)",
                    srcCallNo,
                    destCId,
                    msgId,
                    deleteResult
                ),
                Thread.currentThread().getId()
            )
        } catch (e: Exception) {
            witcomLog.c_write(loggerName, Level.ERROR,
                String.format("[dbDelMOCallInfo] Error: %s", e.message),
                Thread.currentThread().getId()
            )
        }
    }
    
    /**
     * 등기문자 전송 (ESMClass 90, 91)
     * 
     * TODO: C 코드 확인 후 구현 필요
     */
    private suspend fun mt_NOTISending(
        qItem: QITEM,
        request: ResponseTR,
        gipHttpMoAccess: GipHttpMoAccessEntity?,
        smsQLib: SmsQLib,
        loggerName: String
    ) {
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[mt_NOTISending] 등기문자 전송 시작: destCID(%s), srcCallNo(%s)",
                request.data.destCID ?: "",
                request.data.srcCallNo ?: ""
            ),
            Thread.currentThread().getId()
        )
        
        // TODO: C 코드 확인 후 구현
        // 등기문자 전송 로직 구현 필요
        
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[mt_NOTISending] 등기문자 전송 완료"),
            Thread.currentThread().getId()
        )
    }
    
    /**
     * 안심문자 전송 (ESMClass 20, 21)
     * 
     * TODO: C 코드 확인 후 구현 필요
     */
    private suspend fun mt_NOTI_PLUS_Sending(
        qItem: QITEM,
        request: ResponseTR,
        gipHttpMoAccess: GipHttpMoAccessEntity?,
        smsQLib: SmsQLib,
        loggerName: String
    ) {
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[mt_NOTI_PLUS_Sending] 안심문자 전송 시작: destCID(%s), srcCallNo(%s)",
                request.data.destCID ?: "",
                request.data.srcCallNo ?: ""
            ),
            Thread.currentThread().getId()
        )
        
        // TODO: C 코드 확인 후 구현
        // 안심문자 전송 로직 구현 필요
        
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[mt_NOTI_PLUS_Sending] 안심문자 전송 완료"),
            Thread.currentThread().getId()
        )
    }
    
    /**
     * TR 메시지 PCS로 라우팅 (일반 ESMClass)
     * 
     * TODO: C 코드 확인 후 구현 필요
     */
    private suspend fun routeTRMsg2PCS(
        qItem: QITEM,
        request: ResponseTR,
        gipHttpMoAccess: GipHttpMoAccessEntity?,
        smsQLib: SmsQLib,
        loggerName: String
    ) {
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[routeTRMsg2PCS] PCS 라우팅 시작: destCID(%s), srcCallNo(%s)",
                request.data.destCID ?: "",
                request.data.srcCallNo ?: ""
            ),
            Thread.currentThread().getId()
        )
        
        // TODO: C 코드 확인 후 구현
        // PCS로 라우팅 로직 구현 필요
        
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[routeTRMsg2PCS] PCS 라우팅 완료"),
            Thread.currentThread().getId()
        )
    }
    
    /**
     * C 코드 기준: MsgStatus에 따른 StatId와 ErrorId 매핑
     * C 코드 로직을 그대로 반영: sndMsg->flags.MsgStatus 값에 따라 StatId와 ErrorId 결정
     * 
     * @param msgStatus MSG_STATUS 값 (2, 3, 7-10, 12, 14, 16, 17, 19, 20 등)
     * @return Pair<StatId, ErrorId?> (ErrorId가 없는 경우 null)
     */
    private fun mapMsgStatusToStatAndError(msgStatus: Int): Pair<Int, Int?> {
        return when (msgStatus) {
            2 -> Pair(ST_SMSMOR_TR_SEND, ERRORID_CENTER_TR_SUCCESS)  // 전송 성공
            3 -> Pair(ST_VRECV_TR_EXPIRED, ERRORID_CENTER_TR_EXPIRED)  // 만료 (기존 코드와 호환: ST_VRECV_TR_EXPIRED 사용)
            7, 8, 9, 10 -> Pair(ST_SMSMOR_TR_PORTOUT, ERRORID_CENTER_TR_PORTOUT)  // PORTOUT
            12 -> Pair(ST_SMSMOR_TR_FORWARD, null)  // FORWARD (ErrorId 없음)
            14 -> Pair(ST_SMSMOR_TR_FWDFAIL, null)  // 전달 실패
            16 -> Pair(ST_SMSMOR_TR_SPAMERR, null)  // 스팸 오류
            17 -> Pair(ST_SMSMOR_TR_USERDEL, null)  // 사용자 삭제
            19 -> Pair(ST_SMSMOR_TR_NPREFIX, null)  // NP Prefix 오류
            20 -> Pair(ST_SMSMOR_TR_ADMCANC, null)  // 관리자 취소
            else -> Pair(ST_SMSMOR_TR_UNDELIVERED, null)  // 기타 미전달
        }
    }
    
    /**
     * ESMClass별 TR 처리 분기 라우터
     * 
     * @param qItem QITEM 객체
     * @param request MoReportRequest
     * @param esmClass ESMClass 값 (qItem.nRsv4Protocol[11])
     * @param msgStatus MSG_STATUS 값
     * @param statId C 코드 매핑으로 결정된 StatId
     * @param errorId C 코드 매핑으로 결정된 ErrorId (없으면 ERRORID_CP_MO_TR_FAIL)
     * @param moCallInfo MOCallInfoEntity
     * @param gipHttpMoAccess GipHttpMoAccessEntity
     * @param smsQLib SmsQLib
     * @param loggerName 로거 이름
     */
    private suspend fun processTRByEsmClass(
        qItem: QITEM,
        request: MoReportRequest,
        esmClass: Int,
        msgStatus: Int,
        statId: Int,
        errorId: Int,
        moCallInfo: MOCallInfoEntity,
        gipHttpMoAccess: GipHttpMoAccessEntity,
        smsQLib: SmsQLib,
        loggerName: String
    ) {
        when (esmClass) {
            // 등기문자 (NOTI)
            NOTI_NORMAL_MO, NOTI_PORTED_MO -> {
                processTR_Noti(qItem, request, msgStatus, statId, errorId, moCallInfo, gipHttpMoAccess, smsQLib, loggerName)
            }
            // 안심문자 (NOTI_PLUS)
            NOTI_PLUS_NORMAL_MO, NOTI_PLUS_PORTED_MO -> {
                processTR_NotiPlus(qItem, request, msgStatus, statId, errorId, moCallInfo, gipHttpMoAccess, smsQLib, loggerName)
            }
            // CDMA 로밍
            CDMA_ROAMING, PORTED_CDMA_ROAMING -> {
                processTR_Roaming_CDMA(qItem, request, msgStatus, statId, errorId, moCallInfo, gipHttpMoAccess, smsQLib, loggerName)
            }
            // GSM/WCDMA/BIZ 로밍
            GSM_WCDMA_ROAMING, PORTED_GSM_WCDMA_ROAMING,
            NUMBER_PLUS_CDMA_ROAMING_MO, NUMBER_PLUS_GSM_ROAMING_MO,
            BIZ_NUMBER_CDMA_ROAMING_MO, BIZ_NUMBER_GSM_ROAMING_MO -> {
                processTR_Roaming_GSM(qItem, request, msgStatus, statId, errorId, moCallInfo, gipHttpMoAccess, smsQLib, loggerName)
            }
            // 일반 SMS
            NORMAL_MO, PORTED_MO, FORWARD_MO -> {
                processTR_Normal(qItem, request, msgStatus, statId, errorId, moCallInfo, gipHttpMoAccess, smsQLib, loggerName)
            }
            // 기타
            else -> {
                witcomLog.c_write(loggerName, Level.WARN,
                    String.format("[processTRByEsmClass] 알 수 없는 ESMClass(%d): 기본 TR 처리 수행", esmClass),
                    Thread.currentThread().getId()
                )
                processTR_Normal(qItem, request, msgStatus, statId, errorId, moCallInfo, gipHttpMoAccess, smsQLib, loggerName)
            }
        }
    }
    
    /**
     * 등기문자 (ESMClass 90, 91) TR 처리
     */
    private suspend fun processTR_Noti(
        qItem: QITEM,
        request: MoReportRequest,
        msgStatus: Int,
        statId: Int,
        errorId: Int,
        moCallInfo: MOCallInfoEntity,
        gipHttpMoAccess: GipHttpMoAccessEntity,
        smsQLib: SmsQLib,
        loggerName: String
    ) {
        val gMOTRBILL = (gipHttpMoAccess.moTrBill == 1)
        
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[processTR_Noti] 등기문자 TR 처리 시작: msgId(%s), msgStatus(%d), statId(%d), errorId(%d), MOTRBILL(%s)",
                request.data.msgId, msgStatus, statId, errorId, if (gMOTRBILL) "Y" else "N"),
            Thread.currentThread().getId()
        )
        
        // 1. MOTRBILL='Y'이고 msgStatus=2인 경우: 과금 처리
        if (gMOTRBILL && msgStatus == 2) {
            val responseTR = convertMoReportToResponseTR(request, moCallInfo)
            processMOBilling(qItem, responseTR, gipHttpMoAccess, smsQLib, loggerName)
        } else {
//            // 2. VSTAT 기록
//            qItem.ucServerType = VSMSS_TYPE.code.toByte()
//            smsQLib.InsqStat(
//                qItem,
//                MESSAGE_TR,
//                0,
//                gServerID,
//                MODULEID_VBILLMO,
//                SERVICEID_GIPEVENT,
//                errorId,
//                statId,
//                getNInforNo(qItem),
//                TID_NO_SAVE,
//                LT_BOTH,
//                0
//            )
        }
        
        // 3. DB 삭제: MO_NOTISEND
        val responseTR = convertMoReportToResponseTR(request, moCallInfo)
        dbDelMO_NOTISEND(responseTR, loggerName)
        
        // 4. 메시지 전송: 등기문자 전송 (성공 시에만)
        if (msgStatus == 2) {
            mt_NOTISending(qItem, responseTR, gipHttpMoAccess, smsQLib, loggerName)
        }
    }
    
    /**
     * 안심문자 (ESMClass 20, 21) TR 처리
     */
    private suspend fun processTR_NotiPlus(
        qItem: QITEM,
        request: MoReportRequest,
        msgStatus: Int,
        statId: Int,
        errorId: Int,
        moCallInfo: MOCallInfoEntity,
        gipHttpMoAccess: GipHttpMoAccessEntity,
        smsQLib: SmsQLib,
        loggerName: String
    ) {
        val gMOTRBILL = (gipHttpMoAccess.moTrBill == 1)
        
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[processTR_NotiPlus] 안심문자 TR 처리 시작: msgId(%s), msgStatus(%d), statId(%d), errorId(%d), MOTRBILL(%s)",
                request.data.msgId, msgStatus, statId, errorId, if (gMOTRBILL) "Y" else "N"),
            Thread.currentThread().getId()
        )
        
        // 1. MOTRBILL='Y'이고 msgStatus=2인 경우: 과금 처리
        if (gMOTRBILL && msgStatus == 2) {
            val responseTR = convertMoReportToResponseTR(request, moCallInfo)
            processMOBilling(qItem, responseTR, gipHttpMoAccess, smsQLib, loggerName)
        } else {
            // 2. VSTAT 기록
//            qItem.ucServerType = VSMSS_TYPE.code.toByte()
//            smsQLib.InsqStat(
//                qItem,
//                MESSAGE_TR,
//                0,
//                gServerID,
//                MODULEID_VBILLMO,
//                SERVICEID_GIPEVENT,
//                errorId,
//                statId,
//                getNInforNo(qItem),
//                TID_NO_SAVE,
//                LT_BOTH,
//                0
//            )
        }
        
        // 3. DB 삭제: MO_NOTISEND
        val responseTR = convertMoReportToResponseTR(request, moCallInfo)
        dbDelMO_NOTISEND(responseTR, loggerName)
        
        // 4. 메시지 전송: 안심문자 전송 (성공 시에만)
        if (msgStatus == 2) {
            mt_NOTI_PLUS_Sending(qItem, responseTR, gipHttpMoAccess, smsQLib, loggerName)
        }
    }
    
    /**
     * CDMA 로밍 (ESMClass 36, 37) TR 처리
     */
    private suspend fun processTR_Roaming_CDMA(
        qItem: QITEM,
        request: MoReportRequest,
        msgStatus: Int,
        statId: Int,
        errorId: Int,
        moCallInfo: MOCallInfoEntity,
        gipHttpMoAccess: GipHttpMoAccessEntity,
        smsQLib: SmsQLib,
        loggerName: String
    ) {
        val gMOTRBILL = (gipHttpMoAccess.moTrBill == 1)
        val cid = request.data.cid ?: moCallInfo.destCId
        val esmClass = qItem.nRsv4Protocol[11]
        
        // CID 1584 + ESMCLASS 36 (CDMA_ROAMING) 조합만 특별 처리 (C 코드와 동일)
        val shouldProcessDirectVstat27 = cid?.let { 
            EsmClassHandler().isCdmaRoamingWithCid1584(esmClass, it)
        } ?: false
        
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[processTR_Roaming_CDMA] CDMA 로밍 TR 처리 시작: msgId(%s), msgStatus(%d), statId(%d), errorId(%d), MOTRBILL(%s), CID(%s), shouldProcessDirectVstat27(%b)",
                request.data.msgId, msgStatus, statId, errorId, if (gMOTRBILL) "Y" else "N", cid ?: "null", shouldProcessDirectVstat27),
            Thread.currentThread().getId()
        )
        
        // 1. MOTRBILL='Y'이고 msgStatus=2인 경우: 과금 처리
        if (gMOTRBILL && msgStatus == 2) {
            val responseTR = convertMoReportToResponseTR(request, moCallInfo)
            processMOBilling(qItem, responseTR, gipHttpMoAccess, smsQLib, loggerName)
        } else {
//            // 2. VSTAT 기록
//            qItem.ucServerType = VSMSS_TYPE.code.toByte()
//            smsQLib.InsqStat(
//                qItem,
//                MESSAGE_TR,
//                0,
//                gServerID,
//                MODULEID_VBILLMO,
//                SERVICEID_GIPEVENT,
//                errorId,
//                statId,
//                getNInforNo(qItem),
//                TID_NO_SAVE,
//                LT_BOTH,
//                0
//            )
        }
        
        // 3. DB 삭제: MOCALLINFO
        val responseTR = convertMoReportToResponseTR(request, moCallInfo)
        dbDelMOCallInfo(responseTR, loggerName)
        
        // 4. 메시지 라우팅: PCS로 라우팅 (성공 시에만)
        if (msgStatus == 2) {
            routeTRMsg2PCS(qItem, responseTR, gipHttpMoAccess, smsQLib, loggerName)
        }
    }
    
    /**
     * GSM/WCDMA/BIZ 로밍 (ESMClass 40, 41, 69, 70, 72, 73) TR 처리
     */
    private suspend fun processTR_Roaming_GSM(
        qItem: QITEM,
        request: MoReportRequest,
        msgStatus: Int,
        statId: Int,
        errorId: Int,
        moCallInfo: MOCallInfoEntity,
        gipHttpMoAccess: GipHttpMoAccessEntity,
        smsQLib: SmsQLib,
        loggerName: String
    ) {
        val gMOTRBILL = (gipHttpMoAccess.moTrBill == 1)
        
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[processTR_Roaming_GSM] GSM/WCDMA/BIZ 로밍 TR 처리 시작: msgId(%s), msgStatus(%d), statId(%d), errorId(%d), MOTRBILL(%s)",
                request.data.msgId, msgStatus, statId, errorId, if (gMOTRBILL) "Y" else "N"),
            Thread.currentThread().getId()
        )
        
        // 1. MOTRBILL='Y'이고 msgStatus=2인 경우: 과금 처리
        if (gMOTRBILL && msgStatus == 2) {
            val responseTR = convertMoReportToResponseTR(request, moCallInfo)
            processMOBilling(qItem, responseTR, gipHttpMoAccess, smsQLib, loggerName)
        } else {
            // 2. VSTAT 기록
            qItem.ucServerType = VSMSS_TYPE.code.toByte()
            smsQLib.InsqStat(
                qItem,
                MESSAGE_TR,
                0,
                gServerID,
                MODULEID_VBILLMO,
                SERVICEID_GIPEVENT,
                errorId,
                statId,
                getNInforNo(qItem),
                TID_NO_SAVE,
                LT_BOTH,
                0
            )
        }
        
        // 3. DB 삭제: MOCALLINFO
        val responseTR = convertMoReportToResponseTR(request, moCallInfo)
        dbDelMOCallInfo(responseTR, loggerName)
        
        // 4. 메시지 라우팅: PCS로 라우팅 (성공 시에만)
        if (msgStatus == 2) {
            routeTRMsg2PCS(qItem, responseTR, gipHttpMoAccess, smsQLib, loggerName)
        }
    }
    
    /**
     * 일반 SMS (ESMClass 1, 57, 48) TR 처리
     */
    private suspend fun processTR_Normal(
        qItem: QITEM,
        request: MoReportRequest,
        msgStatus: Int,
        statId: Int,
        errorId: Int,
        moCallInfo: MOCallInfoEntity,
        gipHttpMoAccess: GipHttpMoAccessEntity,
        smsQLib: SmsQLib,
        loggerName: String
    ) {
        val gMOTRBILL = (gipHttpMoAccess.moTrBill == 1)
        
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[processTR_Normal] 일반 SMS TR 처리 시작: msgId(%s), msgStatus(%d), statId(%d), errorId(%d), MOTRBILL(%s)",
                request.data.msgId, msgStatus, statId, errorId, if (gMOTRBILL) "Y" else "N"),
            Thread.currentThread().getId()
        )
        
        // 1. MOTRBILL='Y'이고 msgStatus=2인 경우: 과금 처리
        if (gMOTRBILL && msgStatus == 2) {
            val responseTR = convertMoReportToResponseTR(request, moCallInfo)
            processMOBilling(qItem, responseTR, gipHttpMoAccess, smsQLib, loggerName)
        } else {
            // 2. VSTAT 기록
            qItem.ucServerType = VSMSS_TYPE.code.toByte()
            smsQLib.InsqStat(
                qItem,
                MESSAGE_TR,
                0,
                gServerID,
                MODULEID_VBILLMO,
                SERVICEID_GIPEVENT,
                errorId,
                statId,
                getNInforNo(qItem),
                TID_NO_SAVE,
                LT_BOTH,
                0
            )
        }
        
        // 3. DB 삭제: MOCALLINFO
        val responseTR = convertMoReportToResponseTR(request, moCallInfo)
        dbDelMOCallInfo(responseTR, loggerName)
        
        // 4. 메시지 라우팅: PCS로 라우팅 (성공 시에만)
        if (msgStatus == 2) {
            routeTRMsg2PCS(qItem, responseTR, gipHttpMoAccess, smsQLib, loggerName)
        }
    }
    
    /**
     * mo-report API로 수신한 MO-TR 결과 처리
     */
    override suspend fun processMoReport(
        request: MoReportRequest,
        moCallInfo: MOCallInfoEntity,
        gipHttpMoAccess: GipHttpMoAccessEntity,
        clientIp: String,
        serverPort: Int
    ) {
        // cid는 요청에서 가져오거나 MOCALLINFO에서 가져옴
        val cid = request.data.cid ?: moCallInfo.destCId
        val loggerName = getLoggerName(cid, clientIp, serverPort)
        val smsQLib: SmsQLib = liveReloadCLibraryFile.getPreInitializedLibrary(0)
        
        val logNoForLog = gipHttpMoAccess.logNo?.let { String.format("%04d", it.toIntOrNull() ?: 0) } ?: "0000"
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[processMoReport] 함수 진입: logNo(%s), cid(%s), msgId(%s), status(%d), traceId(%s)",
                logNoForLog, cid, request.data.msgId, request.data.status ?: -1, request.data.traceId),
            Thread.currentThread().getId()
        )
        
        // MOCALLINFO 정보로 QITEM 생성
        val qItem = createQItemFromMOCallInfo(moCallInfo, request)
        
        // BILLTYPE 확인
        val gBILLTYPE = com.infra.mo.skt_giphttp_mo.utils.BillTypeValidator.toChar(gipHttpMoAccess.billType, '0')
        val gMOTRBILL = (gipHttpMoAccess.moTrBill == 1)
        
        // BILLTYPE == '1' (비과금)인 경우: 레코드 삭제만 수행하고 과금 처리는 스킵
        if (gBILLTYPE == '1') {
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[processMoReport] BILLTYPE='1' (비과금) 분기 선택: gBILLTYPE(%c)=='1' - 레코드 삭제만 수행, 과금 처리 스킵", gBILLTYPE),
                Thread.currentThread().getId()
            )
            
            // MOCALLINFO 레코드 삭제
            deleteGIPMOCallInfo(moCallInfo)
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[processMoReport] BILLTYPE='1' MOCALLINFO 삭제 완료: msgId(%s)", request.data.msgId),
                Thread.currentThread().getId()
            )
            
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[processMoReport] BILLTYPE='1' (비과금) 분기 완료: 레코드 삭제 완료, 과금 처리 스킵"),
                Thread.currentThread().getId()
            )
            return
        }
        
        // ESMClass 추출
        val esmClass = qItem.nRsv4Protocol[11]
        val msgStatus = request.data.status ?: 0
        
        // C 코드 매핑: MsgStatus → StatId/ErrorId
        val (statId, errorIdNullable) = mapMsgStatusToStatAndError(msgStatus)
        val errorId = errorIdNullable ?: ERRORID_CP_MO_TR_FAIL
        
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[processMoReport] C 코드 매핑 결과: msgStatus(%d) → statId(%d), errorId(%d), esmClass(%d)",
                msgStatus, statId, errorId, esmClass),
            Thread.currentThread().getId()
        )
        
        // ESMClass별 TR 처리 (C 코드 매핑 반영)
//        processTRByEsmClass(
//            qItem,
//            request,
//            esmClass,
//            msgStatus,
//            statId,
//            errorId,
//            moCallInfo,
//            gipHttpMoAccess,
//            smsQLib,
//            loggerName
//        )
        
        // 처리 완료 후 MOCALLINFO 삭제
        // 규칙: MOTRBILL='Y'인 경우 MO-TR 단계에서 삭제, MOTRBILL='N'인 경우 MO-ACK 단계에서 삭제
        // - MOTRBILL='Y'이고 MSG_STATUS=2 (DELIVERED)인 경우: processMOBilling에서 이미 삭제됨
        // - 그 외의 경우: 여기서 삭제
        val shouldDeleteHere = !(gMOTRBILL && request.data.status == 2)
        
        if (shouldDeleteHere) {
            deleteGIPMOCallInfo(moCallInfo)
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[processMoReport] MOCALLINFO 삭제 완료: msgId(%s), MOTRBILL(%s), status(%d)",
                    request.data.msgId,
                    if (gMOTRBILL) "Y" else "N",
                    request.data.status ?: -1),
                Thread.currentThread().getId()
            )
        } else {
            witcomLog.c_write(loggerName, Level.DEBUG,
                String.format("[processMoReport] MOCALLINFO 삭제 스킵: processMOBilling에서 이미 삭제됨 - msgId(%s), MOTRBILL(%s), status(%d)",
                    request.data.msgId,
                    if (gMOTRBILL) "Y" else "N",
                    request.data.status ?: -1),
                Thread.currentThread().getId()
            )
        }
    }
    
    /**
     * MSG_STATUS=2 (DELIVERED): 성공, 과금 처리
     */
    private suspend fun processMoReportDelivered(
        qItem: QITEM,
        request: MoReportRequest,
        moCallInfo: MOCallInfoEntity,
        gipHttpMoAccess: GipHttpMoAccessEntity,
        smsQLib: SmsQLib,
        loggerName: String
    ) {
        val gMOTRBILL = (gipHttpMoAccess.moTrBill == 1)
        // cid는 요청에서 가져오거나 MOCALLINFO에서 가져옴
        val cid = request.data.cid ?: moCallInfo.destCId
        
        // ESMCLASS + CID 조합 검증을 위한 ESMCLASS 추출
        val qItemEsmClass = qItem.nRsv4Protocol[11]
        
        // CID 1584 + ESMCLASS 36 (CDMA_ROAMING) 조합만 특별 처리 (C 코드와 동일)
        val shouldProcessDirectVstat27 = cid?.let { 
            EsmClassHandler().isCdmaRoamingWithCid1584(qItemEsmClass, it)
        } ?: false
        
        if (gMOTRBILL) {
            // MOTRBILL='Y': MO-TR 과금 처리
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[processMoReportDelivered] MOTRBILL='Y': MO-TR 과금 처리 시작 - msgId(%s)", request.data.msgId),
                Thread.currentThread().getId()
            )
            
            // processMOBilling 호출 (기존 로직 재사용)
            val responseTR = convertMoReportToResponseTR(request, moCallInfo)
            processMOBilling(qItem, responseTR, gipHttpMoAccess, smsQLib, loggerName)
        } else {
            // MOTRBILL='N': InsqStat만 호출 (과금 없음)
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[processMoReportDelivered] MOTRBILL='N': InsqStat 호출 - msgId(%s), CID(%s), ESMCLASS(%d), shouldProcessDirectVstat27(%b)", 
                    request.data.msgId, cid ?: "null", qItemEsmClass, shouldProcessDirectVstat27),
                Thread.currentThread().getId()
            )
            
            qItem.ucServerType = VSMSS_TYPE.code.toByte()
            
            // CID 1584 + ESMCLASS 36 (CDMA_ROAMING) 조합인 경우: MO-TR 데이터 검증 OK 시 VSTAT 27 기록
            if (shouldProcessDirectVstat27) {
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processMoReportDelivered] CID 1584 + ESMCLASS 36 조합: MO-TR VSTAT 27 기록 - msgId(%s), CID(%s), ESMCLASS(%d)", 
                        request.data.msgId, cid, qItemEsmClass),
                    Thread.currentThread().getId()
                )
            } else {
                witcomLog.c_write(loggerName, Level.INFO,
                    String.format("[processMoReportDelivered] CID+ESMCLASS 조합 불일치: 일반 처리 - msgId(%s), CID(%s), ESMCLASS(%d)", 
                        request.data.msgId, cid, qItemEsmClass),
                    Thread.currentThread().getId()
                )
            }
            
            smsQLib.InsqStat(
                qItem,
                MESSAGE_TR,
                0,
                gServerID,
                MODULEID_VBILLMO,
                SERVICEID_GIPEVENT,
                ERRORID_CP_MO_TR_SUCCESS,  // 27
                ST_VBILLMO_OK,
                getNInforNo(qItem),  // qItem.usSource (nInforNo)
                TID_NO_SAVE,
                LT_BOTH,  // 통계에 27 값 기록
                0
            )
        }
    }
    
    /**
     * MSG_STATUS=3 (EXPIRED): 만료, 과금 실패
     */
    @Suppress("UNUSED_PARAMETER")
    private suspend fun processMoReportExpired(
        qItem: QITEM,
        request: MoReportRequest,
        moCallInfo: MOCallInfoEntity,
        gipHttpMoAccess: GipHttpMoAccessEntity,
        smsQLib: SmsQLib,
        loggerName: String
    ) {
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[processMoReportExpired] EXPIRED 처리 - msgId(%s)", request.data.msgId),
            Thread.currentThread().getId()
        )
        
        qItem.ucServerType = VSMSS_TYPE.code.toByte()
        smsQLib.InsqStat(
            qItem,
            MESSAGE_TR,
            0,
            gServerID,
            MODULEID_VBILLMO,
            SERVICEID_GIPEVENT,
            ERRORID_CP_MO_TR_FAIL,
            ST_VRECV_TR_EXPIRED,
            getNInforNo(qItem),  // qItem.usSource (nInforNo)
            TID_NO_SAVE,
            LT_BOTH,
            0
        )
    }
    
    /**
     * MSG_STATUS=5 (UNDELIVERABLE): 전송 불가, 과금 실패
     */
    @Suppress("UNUSED_PARAMETER")
    private suspend fun processMoReportUndelivered(
        qItem: QITEM,
        request: MoReportRequest,
        moCallInfo: MOCallInfoEntity,
        gipHttpMoAccess: GipHttpMoAccessEntity,
        smsQLib: SmsQLib,
        loggerName: String
    ) {
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[processMoReportUndelivered] UNDELIVERABLE 처리 - msgId(%s)", request.data.msgId),
            Thread.currentThread().getId()
        )
        
        qItem.ucServerType = VSMSS_TYPE.code.toByte()
        smsQLib.InsqStat(
            qItem,
            MESSAGE_TR,
            0,
            gServerID,
            MODULEID_VBILLMO,
            SERVICEID_GIPEVENT,
            ERRORID_CP_MO_TR_FAIL,
            ST_VRECV_TR_UNDELIVERED,
            getNInforNo(qItem),  // qItem.usSource (nInforNo)
            TID_NO_SAVE,
            LT_BOTH,
            0
        )
    }
    
    /**
     * MSG_STATUS=14 (FWDFAIL): 전달 실패
     */
    @Suppress("UNUSED_PARAMETER")
    private suspend fun processMoReportFwdFail(
        qItem: QITEM,
        request: MoReportRequest,
        moCallInfo: MOCallInfoEntity,
        gipHttpMoAccess: GipHttpMoAccessEntity,
        smsQLib: SmsQLib,
        loggerName: String
    ) {
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[processMoReportFwdFail] FWDFAIL 처리 - msgId(%s)", request.data.msgId),
            Thread.currentThread().getId()
        )
        
        qItem.ucServerType = VSMSS_TYPE.code.toByte()
        smsQLib.InsqStat(
            qItem,
            MESSAGE_TR,
            0,
            gServerID,
            MODULEID_VBILLMO,
            SERVICEID_GIPEVENT,
            ERRORID_CP_MO_TR_FAIL,
            ST_SMSMOR_TR_FWDFAIL,
            getNInforNo(qItem),  // qItem.usSource (nInforNo)
            TID_NO_SAVE,
            LT_BOTH,
            0
        )
    }
    
    /**
     * MSG_STATUS=16 (SPAMERR): 스팸 오류
     */
    private suspend fun processMoReportSpamErr(
        qItem: QITEM,
        request: MoReportRequest,
        moCallInfo: MOCallInfoEntity,
        gipHttpMoAccess: GipHttpMoAccessEntity,
        smsQLib: SmsQLib,
        loggerName: String
    ) {
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[processMoReportSpamErr] SPAMERR 처리 - msgId(%s)", request.data.msgId),
            Thread.currentThread().getId()
        )
        
        qItem.ucServerType = VSMSS_TYPE.code.toByte()
        smsQLib.InsqStat(
            qItem,
            MESSAGE_TR,
            0,
            gServerID,
            MODULEID_VBILLMO,
            SERVICEID_GIPEVENT,
            ERRORID_CP_MO_TR_FAIL,
            ST_SMSMOR_TR_SPAMERR,
            getNInforNo(qItem),  // qItem.usSource (nInforNo)
            TID_NO_SAVE,
            LT_BOTH,
            0
        )
    }
    
    /**
     * MSG_STATUS=17 (USERDEL): 사용자 삭제
     */
    private suspend fun processMoReportUserDel(
        qItem: QITEM,
        request: MoReportRequest,
        moCallInfo: MOCallInfoEntity,
        gipHttpMoAccess: GipHttpMoAccessEntity,
        smsQLib: SmsQLib,
        loggerName: String
    ) {
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[processMoReportUserDel] USERDEL 처리 - msgId(%s)", request.data.msgId),
            Thread.currentThread().getId()
        )
        
        qItem.ucServerType = VSMSS_TYPE.code.toByte()
        smsQLib.InsqStat(
            qItem,
            MESSAGE_TR,
            0,
            gServerID,
            MODULEID_VBILLMO,
            SERVICEID_GIPEVENT,
            ERRORID_CP_MO_TR_FAIL,
            ST_SMSMOR_TR_USERDEL,
            getNInforNo(qItem),  // qItem.usSource (nInforNo)
            TID_NO_SAVE,
            LT_BOTH,
            0
        )
    }
    
    /**
     * MSG_STATUS=19 (NPREFIX): NP Prefix 오류
     */
    private suspend fun processMoReportNPrefix(
        qItem: QITEM,
        request: MoReportRequest,
        moCallInfo: MOCallInfoEntity,
        gipHttpMoAccess: GipHttpMoAccessEntity,
        smsQLib: SmsQLib,
        loggerName: String
    ) {
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[processMoReportNPrefix] NPREFIX 처리 - msgId(%s)", request.data.msgId),
            Thread.currentThread().getId()
        )
        
        qItem.ucServerType = VSMSS_TYPE.code.toByte()
        smsQLib.InsqStat(
            qItem,
            MESSAGE_TR,
            0,
            gServerID,
            MODULEID_VBILLMO,
            SERVICEID_GIPEVENT,
            ERRORID_CP_MO_TR_FAIL,
            ST_SMSMOR_TR_NPREFIX,
            getNInforNo(qItem),  // qItem.usSource (nInforNo)
            TID_NO_SAVE,
            LT_BOTH,
            0
        )
    }
    
    /**
     * MSG_STATUS=20 (ADMCANC): 관리자 취소
     */
    private suspend fun processMoReportAdmCanc(
        qItem: QITEM,
        request: MoReportRequest,
        moCallInfo: MOCallInfoEntity,
        gipHttpMoAccess: GipHttpMoAccessEntity,
        smsQLib: SmsQLib,
        loggerName: String
    ) {
        witcomLog.c_write(loggerName, Level.INFO,
            String.format("[processMoReportAdmCanc] ADMCANC 처리 - msgId(%s)", request.data.msgId),
            Thread.currentThread().getId()
        )
        
        qItem.ucServerType = VSMSS_TYPE.code.toByte()
        smsQLib.InsqStat(
            qItem,
            MESSAGE_TR,
            0,
            gServerID,
            MODULEID_VBILLMO,
            SERVICEID_GIPEVENT,
            ERRORID_CP_MO_TR_FAIL,
            ST_SMSMOR_TR_ADMCANC,
            getNInforNo(qItem),  // qItem.usSource (nInforNo)
            TID_NO_SAVE,
            LT_BOTH,
            0
        )
    }
    
    /**
     * 기타 MSG_STATUS 처리
     */
    private suspend fun processMoReportOther(
        qItem: QITEM,
        request: MoReportRequest,
        moCallInfo: MOCallInfoEntity,
        gipHttpMoAccess: GipHttpMoAccessEntity,
        smsQLib: SmsQLib,
        loggerName: String
    ) {
        witcomLog.c_write(loggerName, Level.WARN,
            String.format("[processMoReportOther] 기타 status 처리: status(%d), msgId(%s)", 
                request.data.status ?: -1, request.data.msgId),
            Thread.currentThread().getId()
        )
        
        qItem.ucServerType = VSMSS_TYPE.code.toByte()
        smsQLib.InsqStat(
            qItem,
            MESSAGE_TR,
            0,
            gServerID,
            MODULEID_VBILLMO,
            SERVICEID_GIPEVENT,
            ERRORID_CP_MO_TR_FAIL,
            ST_VBILLMO_DONT_BILL_TRFAIL,
            getNInforNo(qItem),  // qItem.usSource (nInforNo)
            TID_NO_SAVE,
            LT_BOTH,
            0
        )
    }
    
    /**
     * MOCALLINFO로부터 QITEM 생성
     */
    private fun createQItemFromMOCallInfo(
        moCallInfo: MOCallInfoEntity,
        request: MoReportRequest
    ): QITEM {
        val qItem = QITEM()
        
        // msgId 설정
        val msgIdBytes = request.data.msgId.toByteArray(Charset.forName("CP949"))
        System.arraycopy(msgIdBytes, 0, qItem.ucMsgId, 0, minOf(msgIdBytes.size, qItem.ucMsgId.size))
        
        // traceId 설정
        val traceIdBytes = request.data.traceId.toByteArray(Charset.forName("CP949"))
        System.arraycopy(traceIdBytes, 0, qItem.szTraceId, 0, minOf(traceIdBytes.size, qItem.szTraceId.size))
        
        // status 설정
        qItem.ucMsgStatus = (request.data.status ?: 0).toByte()
        
        // 기타 필드 설정 (MOCALLINFO에서 가져오기)
        if (moCallInfo.srcCId != null) {
            val srcCIdBytes = moCallInfo.srcCId.toByteArray(Charset.forName("CP949"))
            System.arraycopy(srcCIdBytes, 0, qItem.szSrcCId, 0, minOf(srcCIdBytes.size, qItem.szSrcCId.size))
        }
        
        if (moCallInfo.destCId != null) {
            val destCIdBytes = moCallInfo.destCId.toByteArray(Charset.forName("CP949"))
            System.arraycopy(destCIdBytes, 0, qItem.szCId, 0, minOf(destCIdBytes.size, qItem.szCId.size))
        }
        
        if (moCallInfo.srcCallNo != null) {
            val srcCallNoBytes = moCallInfo.srcCallNo.toByteArray(Charset.forName("CP949"))
            System.arraycopy(srcCallNoBytes, 0, qItem.szSrcMinNo, 0, minOf(srcCallNoBytes.size, qItem.szSrcMinNo.size))
        }
        
        if (moCallInfo.destCallNo != null) {
            val destCallNoBytes = moCallInfo.destCallNo.toByteArray(Charset.forName("CP949"))
            System.arraycopy(destCallNoBytes, 0, qItem.szMinNo, 0, minOf(destCallNoBytes.size, qItem.szMinNo.size))
        }
        
        qItem.ucMsgLen = (moCallInfo.msgLen ?: 0).toInt()
        qItem.uOrgMsgLen = moCallInfo.orgMsgLen ?: 0
        qItem.ucDataEncoding = (moCallInfo.dcsType ?: 0).toByte()
        
        return qItem
    }
    
    /**
     * MoReportRequest를 ResponseTR로 변환 (기존 로직 호환)
     */
    private fun convertMoReportToResponseTR(
        request: MoReportRequest,
        moCallInfo: MOCallInfoEntity
    ): ResponseTR {
        val dataBody = ResponseTR.DataBody().apply {
            srcCID = moCallInfo.srcCId ?: ""
            srcCallNo = moCallInfo.srcCallNo ?: ""
            destCID = moCallInfo.destCId ?: ""
            destCallNo = moCallInfo.destCallNo ?: ""
            msgCode = 11.toShort()
            msgSubCode = 9.toShort()
            msgId = request.data.msgId
            msgStatus = request.data.status ?: 0
            result = if (request.data.status == 2) 0 else 1
            bodyDataLen = moCallInfo.msgLen ?: 0
            msgSeqNo = 0
            dataEncoding = moCallInfo.dcsType ?: 0
            termtype = ""
            concatenateflag = ""
            concatenateInfo = ""
            rsv4Protocol = emptyList()
        }
        
        val responseTR = ResponseTR()
        responseTR.msgVerId = 0
        responseTR.encFlag = 0
        responseTR.data = dataBody
        return responseTR
    }
    
    /**
     * RoamingIndChk 헬퍼 함수
     * C 코드 참고: GIDBLib.c
     */
    private fun RoamingIndChk(RoamingInd: Byte): Int {
        return if ((RoamingInd.toInt() and 0x40) == 0x40) {
            1
        } else {
            0
        }
    }
    
    /**
     * InsertGIPMOCallInfo 구현
     * C 코드 참고: GIDBLib.c LINE 1851-1950
     * MOThreadPool의 구현을 참고하여 동일한 로직 구현
     * 
     * 별도 트랜잭션으로 분리하여 상위 트랜잭션의 롤백 영향을 받지 않도록 함
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    override fun insertGIPMOCallInfo(
        qItem: QITEM,
        msgHdr: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        workerThreadId: Long
    ): Int {
        val loggerName = "${entity.cid}-${entity.ipAddr}-${entity.portNo}"
        return try {
            val srcCId = QItemServiceUtil.byteArrayToKString(qItem.szSrcCId)
            val srcCallNo = QItemServiceUtil.byteArrayToKString(qItem.szSrcMinNo)
            val destCId = QItemServiceUtil.byteArrayToKString(qItem.szCId)
            val destCallNo = QItemServiceUtil.byteArrayToKString(qItem.szMinNo)
            val msgId = QItemServiceUtil.byteArrayToKString(qItem.ucMsgId)
            
            if (msgId.isBlank() || srcCId.isBlank() || destCId.isBlank() || srcCallNo.isBlank()) {
                witcomLog.c_write(loggerName, Level.ERROR,
                    String.format("[insertGIPMOCallInfo] 필수 필드 누락: msgId(%s), srcCId(%s), destCId(%s), srcCallNo(%s)",
                        msgId, srcCId, destCId, srcCallNo),
                    workerThreadId)
                return -1
            }
            
            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyMMddHHmmss")
            val moSubTime = sdf.format(cal.time) + String.format("%02d", cal.get(Calendar.MILLISECOND) / 10)
            val msgLen = qItem.ucMsgLen.toInt()
            val gESMCLASS = qItem.nRsv4Protocol[11]
            
            val RoamingIndFlag = RoamingIndChk(qItem.szSMS_OSFI[0])
            var v_ucRoaming: Int = 0
            
            // CDMA 로밍 계열 (36, 37, 38, 69, 72)
            if (gESMCLASS == CDMA_ROAMING || 
                gESMCLASS == PORTED_CDMA_ROAMING ||
                gESMCLASS == SmsDef.FORWARD_CDMA_ROAMING_MO ||
                gESMCLASS == SmsDef.NUMBER_PLUS_CDMA_ROAMING ||
                gESMCLASS == SmsDef.BIZ_NUMBER_CDMA_ROAMING_MO) {
                v_ucRoaming = if (RoamingIndFlag == 1) 19 else 17
            } 
            // GSM/WCDMA 로밍 계열 (40, 41, 42, 70, 73)
            else if (gESMCLASS == GSM_WCDMA_ROAMING || 
                     gESMCLASS == PORTED_GSM_WCDMA_ROAMING ||
                     gESMCLASS == SmsDef.FORWARD_GSM_ROAMING_MO ||
                     gESMCLASS == SmsDef.NUMBER_PLUS_GSM_ROAMING ||
                     gESMCLASS == SmsDef.BIZ_NUMBER_GSM_ROAMING_MO) {
                v_ucRoaming = if (RoamingIndFlag == 1) 20 else 18
            }
            
            val cb = QItemServiceUtil.byteArrayToKString(qItem.szCB)
            val roamPMN = if (v_ucRoaming != 0 && qItem.nRsv4Protocol[10] != 0) {
                qItem.nRsv4Protocol[10].toString()
            } else {
                "0"
            }
            
            val wZone = if (qItem.ucRsv4Dlv.isNotEmpty() && qItem.ucRsv4Dlv[1] != 0.toByte()) {
                val wZoneChar = qItem.ucRsv4Dlv[1].toInt().toChar()
                if (wZoneChar != '\u0000') wZoneChar.toString() else null
            } else {
                null
            }
            
            val traceId = QItemServiceUtil.byteArrayToKString(qItem.szTraceId)
            val origMvnoInfoRaw = QItemServiceUtil.byteArrayToKString(qItem.szOrigMvnoInformation)
            val origMvnoInfo = if (origMvnoInfoRaw.isNotBlank() && origMvnoInfoRaw.any { it != '\u0000' }) {
                origMvnoInfoRaw.trim { it <= ' ' }
            } else {
                null
            }
            
            val destMvnoInfoRaw = QItemServiceUtil.byteArrayToKString(qItem.szDestMvnoInformation)
            val destMvnoInfo = if (destMvnoInfoRaw.isNotBlank() && destMvnoInfoRaw.any { it != '\u0000' }) {
                destMvnoInfoRaw.trim { it <= ' ' }
            } else {
                null
            }
            
            val rcsRaw = QItemServiceUtil.byteArrayToKString(qItem.RcsTag)
            val rcs = if (rcsRaw.isNotBlank() && rcsRaw.any { it != '\u0000' }) {
                rcsRaw.trim { it <= ' ' }
            } else {
                null
            }
            
            val dcsType = when (qItem.ucDataEncoding) {
                DCS_TYPE_GSM7.code.toByte() -> DCS_TYPE_DEC_GSM7
                DCS_TYPE_ASCII7.code.toByte() -> DCS_TYPE_DEC_ASCII7
                DCS_TYPE_8BIT.code.toByte() -> DCS_TYPE_DEC_8BIT
                DCS_TYPE_UCS2.code.toByte() -> DCS_TYPE_DEC_UCS2
                DCS_TYPE_KSC5601 -> DCS_TYPE_DEC_KSC5601
                else -> DCS_TYPE_DEC_UNKNOWN
            }
            
            val orgMsgLen = qItem.uOrgMsgLen
            val moRecvTimeRaw = QItemServiceUtil.byteArrayToKString(qItem.szMoRecvTime)
            val moRecvTime = if (moRecvTimeRaw.isBlank()) {
                val calRecv = Calendar.getInstance()
                val sdfRecv = SimpleDateFormat("yyMMddHHmmss")
                sdfRecv.format(calRecv.time) + String.format("%02d", calRecv.get(Calendar.MILLISECOND) / 10)
            } else {
                moRecvTimeRaw
            }
            
            val msg: String? = try {
                val decodedMsg = QItemServiceUtil.decodeQItemMessage(qItem)
                if (decodedMsg != null && decodedMsg.isNotBlank()) {
                    decodedMsg.take(300)
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
            
            val segment = if (qItem.segSeq > 0) qItem.segSeq.toInt() else null
            val tidValue = if (qItem.usMsgCodeReserved.isNotEmpty() && qItem.usMsgCodeReserved[0] != 0.toShort()) {
                String.format("%06d", qItem.usMsgCodeReserved[0].toInt() and 0xFFFF)
            } else {
                null
            }
            val returnQno = if (qItem.ReturnQ_No > 0) qItem.ReturnQ_No else null
            val expireTime = if (qItem.nVldPrd > 0) {
                val calExpire = Calendar.getInstance()
                calExpire.timeInMillis = System.currentTimeMillis() + (qItem.nVldPrd * 1000L)
                calExpire.time
            } else {
                null
            }
            
            val fwdSrc = QItemServiceUtil.byteArrayToKString(qItem.szFWD_NO)
            val fwdSrcValue = if (fwdSrc.isNotBlank() && fwdSrc.any { it != '\u0000' }) {
                fwdSrc.trim { it <= ' ' }.take(20)
            } else {
                null
            }
            
            val moCallInfo = MOCallInfoEntity().apply {
                this.msgId = msgId
                this.srcCId = srcCId
                this.destCId = destCId
                this.srcCallNo = srcCallNo
                this.destCallNo = destCallNo
                this.moSubTime = moSubTime
                this.msgLen = msgLen
                this.roamingId = v_ucRoaming.toLong()
                this.cb = cb
                this.roamPMN = if (roamPMN == "0") null else roamPMN
                this.wZone = wZone
                this.traceId = traceId
                this.origMvnoInfo = origMvnoInfo
                this.destMvnoInfo = destMvnoInfo
                this.rcs = rcs
                this.dcsType = dcsType
                this.orgMsgLen = orgMsgLen
                this.moRecvTime = moRecvTime
                this.msg = msg
                this.segment = segment
                this.tid = tidValue
                this.returnQno = returnQno
                this.expireTime = expireTime
                this.fwdSrc = fwdSrcValue
                this.esmClass = gESMCLASS
            }
            
            // CDMA 케이스 확인 로그
            val isCid1584InInsert = destCId.startsWith("1584")
            witcomLog.c_write(
                loggerName,
                Level.DEBUG,
                String.format(
                    "[insertGIPMOCallInfo] MOCALLINFO 저장 시작: msgId(%s), destCID(%s), isCid1584(%b), roamingId(%d), entity.logNo(%s), entity.moTrBill(%s)",
                    msgId,
                    destCId,
                    isCid1584InInsert,
                    v_ucRoaming,
                    entity.logNo,
                    entity.moTrBill
                ),
                workerThreadId
            )
            
            // 저장 전 moCallInfo 객체 필수 필드 값 확인 로그
            witcomLog.c_write(
                loggerName,
                Level.DEBUG,
                String.format(
                    "[insertGIPMOCallInfo] 저장 전 데이터 확인: msgId(%s), srcCId(%s), destCId(%s), srcCallNo(%s), destCallNo(%s), msgLen(%d), traceId(%s), moSubTime(%s), esmClass(%d)",
                    moCallInfo.msgId,
                    moCallInfo.srcCId,
                    moCallInfo.destCId,
                    moCallInfo.srcCallNo,
                    moCallInfo.destCallNo,
                    moCallInfo.msgLen,
                    moCallInfo.traceId,
                    moCallInfo.moSubTime,
                    moCallInfo.esmClass ?: -1
                ),
                workerThreadId
            )
            
            // DB 저장 시도
            try {
                witcomLog.c_write(
                    loggerName,
                    Level.DEBUG,
                    String.format(
                        "[insertGIPMOCallInfo] moCallInfoRepository.save() 호출 전: msgId(%s), destCID(%s), isCid1584(%b)",
                        msgId,
                        destCId,
                        isCid1584InInsert
                    ),
                    workerThreadId
                )
                moCallInfoRepository.save(moCallInfo)
                witcomLog.c_write(
                    loggerName,
                    Level.DEBUG,
                    String.format(
                        "[insertGIPMOCallInfo] moCallInfoRepository.save() 호출 완료: msgId(%s), destCID(%s), isCid1584(%b)",
                        msgId,
                        destCId,
                        isCid1584InInsert
                    ),
                    workerThreadId
                )
                
                witcomLog.c_write(
                    loggerName,
                    Level.DEBUG,
                    String.format(
                        "[insertGIPMOCallInfo] moCallInfoRepository.flush() 호출 전: msgId(%s), destCID(%s), isCid1584(%b)",
                        msgId,
                        destCId,
                        isCid1584InInsert
                    ),
                    workerThreadId
                )
                moCallInfoRepository.flush()
                witcomLog.c_write(
                    loggerName,
                    Level.DEBUG,
                    String.format(
                        "[insertGIPMOCallInfo] moCallInfoRepository.flush() 호출 완료: msgId(%s), destCID(%s), isCid1584(%b)",
                        msgId,
                        destCId,
                        isCid1584InInsert
                    ),
                    workerThreadId
                )
                
                // 영속성 컨텍스트를 비워서 실제 DB에서 조회하도록 함
                entityManager.clear()
                witcomLog.c_write(
                    loggerName,
                    Level.DEBUG,
                    String.format(
                        "[insertGIPMOCallInfo] entityManager.clear() 호출 완료: msgId(%s), destCID(%s), isCid1584(%b)",
                        msgId,
                        destCId,
                        isCid1584InInsert
                    ),
                    workerThreadId
                )
            } catch (saveException: Exception) {
                witcomLog.c_write(
                    loggerName,
                    Level.ERROR,
                    String.format(
                        "[insertGIPMOCallInfo] DB 저장 중 예외 발생: msgId(%s), destCID(%s), isCid1584(%b), 예외메시지(%s), 예외스택(%s)",
                        msgId,
                        destCId,
                        isCid1584InInsert,
                        saveException.message ?: "null",
                        saveException.stackTraceToString()
                    ),
                    workerThreadId
                )
                throw saveException
            }
            
            // 저장 후 즉시 조회하여 검증 (영속성 컨텍스트 clear 후 실제 DB에서 조회)
            val savedMoCallInfo = try {
                moCallInfoRepository.findBySrcCallNoAndDestCIdAndMsgId(srcCallNo, destCId, msgId)
            } catch (queryException: Exception) {
                witcomLog.c_write(
                    loggerName,
                    Level.ERROR,
                    String.format(
                        "[insertGIPMOCallInfo] 저장 후 조회 중 예외 발생: msgId(%s), srcCallNo(%s), destCId(%s), 예외메시지(%s)",
                        msgId,
                        srcCallNo,
                        destCId,
                        queryException.message ?: "null"
                    ),
                    workerThreadId
                )
                null
            }
            
            if (savedMoCallInfo != null) {
                witcomLog.c_write(
                    loggerName,
                    Level.INFO,
                    String.format(
                        "InsertGIPMOCallInfo OK : MsgId(%s) TraceId(%s) SrcCallNo(%s) DestCID(%s) isCid1584(%b) - 저장 후 조회 성공 (검증 완료)",
                        msgId,
                        traceId,
                        srcCallNo,
                        destCId,
                        isCid1584InInsert
                    ),
                    workerThreadId
                )
                0
            } else {
                // 저장 후 조회 실패: msgId만으로 재시도
                val savedByMsgId = try {
                    moCallInfoRepository.findByMsgId(msgId)
                } catch (e: Exception) {
                    null
                }
                
                if (savedByMsgId != null) {
                    witcomLog.c_write(
                        loggerName,
                        Level.WARN,
                        String.format(
                            "InsertGIPMOCallInfo WARN : MsgId(%s) TraceId(%s) SrcCallNo(%s) DestCID(%s) isCid1584(%b) - 복합키 조회 실패, msgId만으로 조회 성공 (다른 SrcCallNo/DestCID일 수 있음)",
                            msgId,
                            traceId,
                            srcCallNo,
                            destCId,
                            isCid1584InInsert
                        ),
                        workerThreadId
                    )
                    0  // msgId로 조회 성공 시 성공으로 간주
                } else {
                    witcomLog.c_write(
                        loggerName,
                        Level.ERROR,
                        String.format(
                            "InsertGIPMOCallInfo FAIL : MsgId(%s) TraceId(%s) SrcCallNo(%s) DestCID(%s) isCid1584(%b) - 저장 후 조회 실패 (DB에 저장되지 않음). 저장된 데이터: msgId(%s), srcCId(%s), destCId(%s), srcCallNo(%s), destCallNo(%s)",
                            msgId,
                            traceId,
                            srcCallNo,
                            destCId,
                            isCid1584InInsert,
                            moCallInfo.msgId,
                            moCallInfo.srcCId,
                            moCallInfo.destCId,
                            moCallInfo.srcCallNo,
                            moCallInfo.destCallNo
                        ),
                        workerThreadId
                    )
                    -1
                }
            }
        } catch (e: Exception) {
            val errorSrcCId = try {
                QItemServiceUtil.byteArrayToKString(qItem.szSrcCId)
            } catch (ex: Exception) {
                "UNKNOWN"
            }
            val errorSrcCallNo = try {
                QItemServiceUtil.byteArrayToKString(qItem.szSrcMinNo)
            } catch (ex: Exception) {
                "UNKNOWN"
            }
            val errorDestCId = try {
                QItemServiceUtil.byteArrayToKString(qItem.szCId)
            } catch (ex: Exception) {
                "UNKNOWN"
            }
            val errorDestCallNo = try {
                QItemServiceUtil.byteArrayToKString(qItem.szMinNo)
            } catch (ex: Exception) {
                "UNKNOWN"
            }
            val errorDestCIDForException = errorDestCId
            val isCid1584ForException = errorDestCIDForException.startsWith("1584")
            
            witcomLog.c_write(
                loggerName,
                Level.ERROR,
                String.format(
                    "[insertGIPMOCallInfo] ⚠️ 예외 발생: srcCID(%s), srcCallNo(%s), destCID(%s), destCallNo(%s), isCid1584(%b), 예외타입(%s), 예외메시지(%s)",
                    errorSrcCId,
                    errorSrcCallNo,
                    errorDestCId,
                    errorDestCallNo,
                    isCid1584ForException,
                    e.javaClass.simpleName,
                    e.message ?: "null"
                ),
                workerThreadId
            )
            witcomLog.c_write(
                loggerName,
                Level.ERROR,
                String.format(
                    "[insertGIPMOCallInfo] 예외 스택: %s",
                    e.stackTraceToString()
                ),
                workerThreadId
            )
            -1
        }
    }
    
}

