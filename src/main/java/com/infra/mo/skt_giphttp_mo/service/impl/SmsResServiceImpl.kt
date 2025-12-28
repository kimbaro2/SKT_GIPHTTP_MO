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
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_FAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_NODATA
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_SUCCESS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_TR_FAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_TR_SUCCESS
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
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NOTI_NORMAL_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NOTI_PORTED_MO
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
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseTR
import com.infra.mo.skt_giphttp_mo.service.SmsResService
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import java.io.File
import java.io.FileWriter
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
    
    private val gServerID = System.getenv("SMSS_NO")?.trim()?.toIntOrNull() ?: 0
    
    // bprintf 로그 파일 경로: C 코드 bprintf.c LINE 78 - getenv("SMS_VCDR")/LOG/
    private val vcdrDir: String = System.getenv("SMS_VCDR") ?: "/APP/sms/vcdr"
    private val billingLogDir = "$vcdrDir/LOG"
    private var billingLogFile: FileWriter? = null
    private var lastBillingLogMinute: Int = -1

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
    override suspend fun processSMRes(request: ResponseTR, clientIp: String?, serverPort: Int?) {
        // loggerName 생성: gipHttpAccessMap 키 형식과 일치하도록 생성
        val loggerName = getLoggerName(request.data.destCID, clientIp, serverPort)

        // C 코드 LINE 1978: MsgHdrToQItem
        val qItem = QItemServiceUtil.responseTRToQItem(request)
        qItem.ucServerType = VSMSS_TYPE.code.toByte()
        
        // C 코드 LINE 1981: switch(ptrMsgHdr->usMsgSubCode)
        val msgSubCodeValue = request.data.msgSubCode.toInt()
        witcomLog.c_write(loggerName, Level.DEBUG, 
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
                witcomLog.c_write(loggerName, Level.INFO, 
                    String.format("[processSMRes] SM_REQ_SIMPLE 분기 선택: msgSubCode(%d) - MO ACK 처리 시작", msgSubCodeValue),
                    Thread.currentThread().getId()
                )
                // C 코드 LINE 1983-2245: MO ACK 처리
                processSMReqSimple(qItem, request, clientIp, serverPort)
                witcomLog.c_write(loggerName, Level.INFO, 
                    String.format("[processSMRes] SM_REQ_SIMPLE 분기 완료: msgSubCode(%d) - MO ACK 처리 완료", msgSubCodeValue),
                    Thread.currentThread().getId()
                )
            }
            SM_REQ_TRANS_RESULT -> {
                witcomLog.c_write(loggerName, Level.INFO, 
                    String.format("[processSMRes] SM_REQ_TRANS_RESULT 분기 선택: msgSubCode(%d) - TR 결과 처리 시작", msgSubCodeValue),
                    Thread.currentThread().getId()
                )
                // C 코드 LINE 2247-2272: TR 결과 처리
                processSMReqTransResult(qItem, request, clientIp, serverPort)
                witcomLog.c_write(loggerName, Level.INFO, 
                    String.format("[processSMRes] SM_REQ_TRANS_RESULT 분기 완료: msgSubCode(%d) - TR 결과 처리 완료", msgSubCodeValue),
                    Thread.currentThread().getId()
                )
            }
            else -> {
                witcomLog.c_write(
                    loggerName,
                    Level.ERROR,
                    String.format("[processSMRes] Invalid 분기: msgSubCode(%d) - 지원하지 않는 msgSubCode", msgSubCodeValue),
                    Thread.currentThread().getId()
                )
            }
        }
    }
    
    /**
     * C 코드 LINE 1983-2245: SM_REQ_SIMPLE 케이스 처리 (MO ACK)
     */
    private suspend fun processSMReqSimple(
        qItem: QITEM, 
        request: ResponseTR,
        clientIp: String? = null,
        serverPort: Int? = null
    ) {
        // C 코드 LINE 1989-1990: gMsgSendReady, gRetryCount 초기화
        // HTTP 환경에서는 불필요
        
        val smsQLib: SmsQLib = liveReloadCLibraryFile.getPreInitializedLibrary(0)
        
        // IP와 PORT가 제공된 경우 유니크 제약조건으로 정확히 조회, 없으면 CID만으로 조회
        // SM_REQ_SIMPLE (10)는 MO ACK이므로 MSG_TYPE='1' (MO)
        // 주의: HBILL(billType='1')은 서드파티에서 처리하므로 여기서는 스킵
        val msgType = "1"  // MO
        val gipHttpMoAccess = if (clientIp != null && serverPort != null) {
            try {
                gipHttpMoAccessRepository.findByCidAndIpAddrAndPortNoAndMsgType(
                    request.data.destCID,
                    clientIp,
                    serverPort,
                    msgType
                ).orElse(null)
            } catch (e: Exception) {
                // NonUniqueResultException 방지: MSG_TYPE 없이 조회
                try {
                    gipHttpMoAccessRepository.findByCidAndIpAddrAndPortNo(
                        request.data.destCID,
                        clientIp,
                        serverPort
                    ).orElse(null)
                } catch (e2: Exception) {
                    // IP만으로 조회
                    try {
                        gipHttpMoAccessRepository.findByCidAndIpAddr(
                            request.data.destCID,
                            clientIp
                        ).orElse(null)
                    } catch (e3: Exception) {
                        // IP로도 찾지 못한 경우 CID만으로 조회
                        gipHttpMoAccessRepository.findByCid(request.data.destCID).orElse(null)
                    }
                }
            }
        } else {
            gipHttpMoAccessRepository.findByCid(request.data.destCID).orElse(null)
        }
        val billType = gipHttpMoAccess?.billType ?: "2"  // 기본값: '2' (SRC)
        val loggerName = getLoggerName(request.data.destCID, clientIp, serverPort)
        
        witcomLog.c_write(loggerName, Level.DEBUG,
            String.format("[processSMReqSimple] billType 분기 체크: billType(%s), gipHttpMoAccess(%s)",
                billType,
                if (gipHttpMoAccess != null) "NOT_NULL" else "NULL"
            ),
            Thread.currentThread().getId()
        )
        
        // BILLTYPE == '1' (비과금)인 경우: 레코드 삭제만 수행하고 과금 처리는 스킵
        if (billType == "1") {
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[processSMReqSimple] BILLTYPE='1' (비과금) 분기 선택: billType(%s)=='1' - 레코드 삭제만 수행, 과금 처리 스킵", billType),
                Thread.currentThread().getId()
            )
            
            val msgId = request.data.msgId ?: ""
            
            // MO ACK에서는 MOCALLINFO 레코드 삭제만 수행 (ESMClass 정보 없음)
            val deletedMOCallInfo = selectGIPMOCallInfo(
                request.data.srcCallNo ?: "",
                request.data.destCID ?: "",
                msgId
            )
            if (deletedMOCallInfo != null) {
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
                String.format("[processSMReqSimple] BILLTYPE='1' (비과금) 분기 완료: 레코드 삭제 완료, 과금 처리 스킵"),
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
            // C 코드 LINE 2073-2075: InsqStat 호출 (성공)
            smsQLib.InsqStat(
                qItem,
                MESSAGE_MO,
                0,
                gServerID,
                MODULEID_GIPEVENT_C,
                SERVICEID_GIPEVENT,
                ERRORID_CP_MO_SUCCESS,
                ST_GIPEVENT_MORS_OK,
                ackResult,
                TID_NO_SAVE,
                LT_BOTH,
                0
            )
            
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
                LT_BOTH,
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
        
        // C 코드 LINE 2087-2244: 과금 처리 로직 (!gMOTRBILL)
        val gMOTRBILL = gipHttpMoAccess?.moTrBill?.trim()?.equals("Y", ignoreCase = true) == true
        
        witcomLog.c_write(loggerName, Level.DEBUG,
            String.format("[processSMReqSimple] gMOTRBILL 분기 체크: gMOTRBILL(%s), billType(%s)",
                if (gMOTRBILL) "Y" else "N",
                billType
            ),
            Thread.currentThread().getId()
        )
        
        if (!gMOTRBILL) {  // C 코드: !gMOTRBILL
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[processSMReqSimple] !gMOTRBILL 분기 선택: 즉시 과금 처리 시작"),
                Thread.currentThread().getId()
            )
            processMOBilling(qItem, request, gipHttpMoAccess, smsQLib)
            witcomLog.c_write(loggerName, Level.INFO,
                String.format("[processSMReqSimple] !gMOTRBILL 분기 완료: 즉시 과금 처리 완료"),
                Thread.currentThread().getId()
            )
            return
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
     */
    private suspend fun processMOBilling(
        qItem: QITEM,
        request: ResponseTR,
        gipHttpMoAccess: GipHttpMoAccessEntity?,
        smsQLib: SmsQLib
    ) {
        // loggerName 생성: gipHttpMoAccess에서 IP/PORT 추출
        val loggerName = getLoggerName(
            request.data.destCID,
            gipHttpMoAccess?.ipAddr,
            gipHttpMoAccess?.portNo
        )
        
        witcomLog.c_write(loggerName, Level.DEBUG,
            String.format("[processMOBilling] 함수 진입: destCID(%s), srcCallNo(%s), msgSeqNo(%d)",
                request.data.destCID ?: "",
                request.data.srcCallNo ?: "",
                request.data.msgSeqNo ?: 0
            ),
            Thread.currentThread().getId()
        )
        
        // C 코드 LINE 2096: DBGet_GIENQ_CID 호출
        witcomLog.c_write(loggerName, Level.DEBUG,
            String.format("[processMOBilling] DBGet_GIENQ_CID 호출 시작: destCID(%s)", request.data.destCID ?: ""),
            Thread.currentThread().getId()
        )
        val cidLen = dbGetGIENQCID(request.data.destCID, loggerName)
        
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
            smsQLib.InsqStat(
                qItem,
                MESSAGE_MO,
                0,
                gServerID,
                MODULEID_GIPEVENT_C,
                SERVICEID_GIPEVENT,
                ERRORID_CP_MO_FAIL,
                ST_GIP_INVALID_CID,
                IF_NULL,
                TID_NO_SAVE,
                LT_TRACE,
                0
            )
            
            witcomLog.c_write(
                loggerName,
                Level.ERROR,
                String.format("[processMOBilling] Invalid CID 분기: Invalid DestCID [%s]", request.data.destCID),
                Thread.currentThread().getId()
            )
            return
        }
        
        val cdrCId = request.data.destCID.substring(0, cidLen)
        
        // C 코드 LINE 2122: SelectGIPMOCallInfo 호출
        // HTTP 환경에서는 MO와 MO-TR이 동일한 msgID를 사용하므로,
        // 원본 레코드를 조회하기 위해 request.data.msgId를 사용
        val msgId = request.data.msgId ?: ""  // MO와 MO-TR 모두 동일한 msgID
        
        witcomLog.c_write(loggerName, Level.DEBUG,
            String.format("[processMOBilling] SelectGIPMOCallInfo 호출 시작: srcCallNo(%s), destCID(%s), msgId(%s)",
                request.data.srcCallNo ?: "",
                request.data.destCID ?: "",
                msgId
            ),
            Thread.currentThread().getId()
        )
        val moCallInfo = selectGIPMOCallInfo(
            request.data.srcCallNo,
            request.data.destCID,
            msgId
        )
        
        witcomLog.c_write(loggerName, Level.DEBUG,
            String.format("[processMOBilling] SelectGIPMOCallInfo 결과: moCallInfo(%s)",
                if (moCallInfo != null) "FOUND" else "NOT_FOUND"
            ),
            Thread.currentThread().getId()
        )
        
        // C 코드 LINE 408: SelectTRMOCallInfo 호출 후, 실패 시 SelectTRMO_NOTISEND 호출
        // MOCALLINFO 우선 조회, 실패 시 MO_NOTISEND 조회
        val effectiveMoInfo = moCallInfo ?: moNotISendRepository
            .findByMsgIdAndDestCIdAndSrcCIdAndServerType(
                request.data.msgSeqNo.toString(),
                request.data.destCID,
                request.data.srcCID
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
            
            val destCallNoInt = request.data.destCallNo.toIntOrNull() ?: 0
            val isPortableNo = destCallNoInt >= 100000000
            
            val isColorOrAvata = request.data.destCID.startsWith("COL") || request.data.destCID.startsWith("AVAT")
            
            val callTypeMo = 1
            val msgDeleverOk = 0
            val moduleNo = 0
            
            if (isColorOrAvata) {
                val billingLine = if (isPortableNo) {
                    String.format(";%d;;;%d;%s;%s%d;0%d;%s;11;%08d;%s;%s;%s%s;%d;%d;%d;;;;;;%s;%s;0;%c;;%s;%s;%d;0;;\n",
                        callTypeMo, moduleNo, "", request.data.srcCID, request.data.srcCallNo.toIntOrNull() ?: 0,
                        destCallNoInt, cdrCId, request.data.msgSeqNo,
                        moRecvTime, moSubTime, buf, szUsec, msgDeleverOk, qItem.ucMsgLen.toInt(), (effectiveMoInfo.roamingId ?: 0L).toInt(),
                        effectiveMoInfo.roamPMN ?: "", dstCallback, effectiveMoInfo.wZone?.get(0) ?: '0',
                        effectiveMoInfo.origMvnoInfo ?: "", effectiveMoInfo.destMvnoInfo ?: "", nRcs)
                } else {
                    String.format(";%d;;;%d;%s;%s%d;%d;%s;11;%08d;%s;%s;%s%s;%d;%d;%d;;;;;;%s;%s;0;%c;;%s;%s;%d;0;;\n",
                        callTypeMo, moduleNo, "", request.data.srcCID, request.data.srcCallNo.toIntOrNull() ?: 0,
                        destCallNoInt, cdrCId, request.data.msgSeqNo,
                        moRecvTime, moSubTime, buf, szUsec, msgDeleverOk, qItem.ucMsgLen.toInt(), (effectiveMoInfo.roamingId ?: 0L).toInt(),
                        effectiveMoInfo.roamPMN ?: "", dstCallback, effectiveMoInfo.wZone?.get(0) ?: '0',
                        effectiveMoInfo.origMvnoInfo ?: "", effectiveMoInfo.destMvnoInfo ?: "", nRcs)
                }
                writeBillingLog(billingLine, loggerName)
            } else {
                val billingLine = String.format(";%d;;;%d;%s;%s%d;%s;;11;%08d;%s;%s;%s%s;%d;%d;%d;;;;;;%s;%s;0;%c;;%s;%s;%d;0;;\n",
                    callTypeMo, moduleNo, "", request.data.srcCID, request.data.srcCallNo.toIntOrNull() ?: 0,
                    cdrCId, request.data.msgSeqNo,
                    moRecvTime, moSubTime, buf, szUsec, msgDeleverOk, qItem.ucMsgLen.toInt(), effectiveMoInfo.roamingId ?: 0,
                    effectiveMoInfo.roamPMN ?: "", dstCallback, effectiveMoInfo.wZone?.get(0) ?: '0',
                    effectiveMoInfo.origMvnoInfo ?: "", effectiveMoInfo.destMvnoInfo ?: "", nRcs)
                writeBillingLog(billingLine, loggerName)
            }
            
            // C 코드 LINE 518-519 (MOCALLINFO) 또는 LINE 651-652 (MO_NOTISEND): InsqStat 호출
            // MO-TR 결과 처리이므로 ERRORID_CP_MO_TR_SUCCESS 사용
            val statTraceId = if (isFromMOCallInfo) {
                // C 코드 LINE 518-519: MOCALLINFO 케이스
                ST_VBILLMO_OK
            } else {
                // C 코드 LINE 651-652: MO_NOTISEND 케이스
                ST_VBILLMO_NOTISEND_OK
            }
            
            witcomLog.c_write(loggerName, Level.DEBUG,
                String.format("[processMOBilling] InsqStat 호출: isFromMOCallInfo(%s), isFromMONotISend(%s), statTraceId(%d)",
                    if (isFromMOCallInfo) "YES" else "NO",
                    if (isFromMONotISend) "YES" else "NO",
                    statTraceId
                ),
                Thread.currentThread().getId()
            )
            
            // 과금 처리 규칙: LT_TRACE(과금) + LT_BOTH(통계) 두 번 호출
            // 1. 과금용 InsqStat 호출 (LT_TRACE)
            witcomLog.c_write(loggerName, Level.DEBUG,
                String.format("[processMOBilling] InsqStat 호출 시작 (과금용 LT_TRACE): MODULEID_VBILLMO(%d), SERVICEID_GIPEVENT(%d), ERRORID_CP_MO_TR_SUCCESS(%d), statTraceId(%d)",
                    MODULEID_VBILLMO,
                    SERVICEID_GIPEVENT,
                    ERRORID_CP_MO_TR_SUCCESS,
                    statTraceId
                ),
                Thread.currentThread().getId()
            )
            
            val insqStatBillingResult = smsQLib.InsqStat(
                qItem,
                MESSAGE_TR,  // C 코드와 동일: MESSAGE_TR
                0,
                gServerID,
                MODULEID_VBILLMO,  // C 코드와 동일: MODULEID_VBILLMO
                SERVICEID_GIPEVENT,
                ERRORID_CP_MO_TR_SUCCESS,  // C 코드와 동일: ERRORID_CP_MO_TR_SUCCESS
                statTraceId,  // ST_VBILLMO_OK 또는 ST_VBILLMO_NOTISEND_OK
                IF_NULL,
                TID_NO_SAVE,
                LT_TRACE,  // 과금용
                0
            )
            
            witcomLog.c_write(loggerName,
                if (insqStatBillingResult == 1) Level.DEBUG else Level.ERROR,
                String.format("[processMOBilling] InsqStat 호출 결과 (과금용 LT_TRACE): 반환값(%d), 성공여부(%b)",
                    insqStatBillingResult,
                    insqStatBillingResult == 1
                ),
                Thread.currentThread().getId()
            )
            
            // 2. 통계용 InsqStat 호출 (LT_BOTH)
            witcomLog.c_write(loggerName, Level.DEBUG,
                String.format("[processMOBilling] InsqStat 호출 시작 (통계용 LT_BOTH): MODULEID_VBILLMO(%d), SERVICEID_GIPEVENT(%d), ERRORID_CP_MO_TR_SUCCESS(%d), statTraceId(%d)",
                    MODULEID_VBILLMO,
                    SERVICEID_GIPEVENT,
                    ERRORID_CP_MO_TR_SUCCESS,
                    statTraceId
                ),
                Thread.currentThread().getId()
            )
            
            val insqStatResult = smsQLib.InsqStat(
                qItem,
                MESSAGE_TR,  // C 코드와 동일: MESSAGE_TR
                0,
                gServerID,
                MODULEID_VBILLMO,  // C 코드와 동일: MODULEID_VBILLMO
                SERVICEID_GIPEVENT,
                ERRORID_CP_MO_TR_SUCCESS,  // C 코드와 동일: ERRORID_CP_MO_TR_SUCCESS
                statTraceId,  // ST_VBILLMO_OK 또는 ST_VBILLMO_NOTISEND_OK
                IF_NULL,
                TID_NO_SAVE,
                LT_BOTH,  // 통계용
                0
            )
            
            witcomLog.c_write(loggerName,
                if (insqStatResult == 1) Level.DEBUG else Level.ERROR,
                String.format("[processMOBilling] InsqStat 호출 결과 (통계용 LT_BOTH): 반환값(%d), 성공여부(%b)",
                    insqStatResult,
                    insqStatResult == 1
                ),
                Thread.currentThread().getId()
            )
            
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
            smsQLib.InsqStat(
                qItem,
                MESSAGE_MO,
                0,
                gServerID,
                MODULEID_GIPEVENT_C,
                SERVICEID_GIPEVENT,
                ERRORID_CP_MO_NODATA,
                ST_DB_NO_DATA_GIPMOCALLINFO,
                IF_NULL,
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
        serverPort: Int? = null
    ) {
        // loggerName 생성
        val loggerName = getLoggerName(request.data.destCID, clientIp, serverPort)
        
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
        
        // C 코드 LINE 2248-2249: gMsgSendReady, gRetryCount 초기화
        // HTTP 환경에서는 불필요
        
        // REQ_TRANS_RESULT 로그 출력
        // IP와 PORT가 제공된 경우 유니크 제약조건으로 정확히 조회
        // SM_REQ_TRANS_RESULT (9)는 MO-TR 결과이므로 MSG_TYPE='4' (MO-TR)
        val msgType = "4"  // MO-TR
        
        witcomLog.c_write(loggerName, Level.DEBUG,
            String.format("[processSMReqTransResult] gipHttpMoAccess 조회 시작: destCID(%s), clientIp(%s), serverPort(%d), msgType(%s)",
                request.data.destCID ?: "",
                clientIp ?: "",
                serverPort ?: 0,
                msgType
            ),
            Thread.currentThread().getId()
        )
        val gipHttpMoAccess = if (clientIp != null && serverPort != null) {
            try {
                gipHttpMoAccessRepository.findByCidAndIpAddrAndPortNoAndMsgType(
                    request.data.destCID,
                    clientIp,
                    serverPort,
                    msgType
                ).orElse(null)
            } catch (e: Exception) {
                // MSG_TYPE='4' 레코드를 찾지 못함
                witcomLog.c_write(
                    loggerName,
                    Level.ERROR,
                    String.format("[processSMReqTransResult] gipHttpMoAccess 조회 예외: destCID(%s), clientIp(%s), serverPort(%d), msgType(%s), error(%s)",
                        request.data.destCID ?: "",
                        clientIp ?: "",
                        serverPort ?: 0,
                        msgType,
                        e.message
                    ),
                    Thread.currentThread().getId()
                )
                null
            }
        } else {
            // IP/PORT가 없는 경우 에러
            witcomLog.c_write(
                loggerName,
                Level.ERROR,
                String.format("[processSMReqTransResult] gipHttpMoAccess 조회 실패: clientIp 또는 serverPort 없음 - destCID(%s), clientIp(%s), serverPort(%s)",
                    request.data.destCID ?: "",
                    clientIp ?: "null",
                    serverPort?.toString() ?: "null"
                ),
                Thread.currentThread().getId()
            )
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
        
        // C 코드 LINE 2266-2269: InsqStat 호출
        // HTTP 요청은 단일 스레드이므로 index 0 사용
        val smsQLib: SmsQLib = liveReloadCLibraryFile.getPreInitializedLibrary(0)
        
        witcomLog.c_write(loggerName, Level.DEBUG,
            String.format("[processSMReqTransResult] InsqStat 호출: ERRORID_CP_MO_TR_SUCCESS(%d) - TR 결과 통계 기록", ERRORID_CP_MO_TR_SUCCESS),
            Thread.currentThread().getId()
        )
        smsQLib.InsqStat(
            qItem,
            MESSAGE_TR,
            0,
            gServerID,
            MODULEID_GIPEVENT_C,
            SERVICEID_GIPEVENT,
            ERRORID_CP_MO_TR_SUCCESS,
            ST_GIPEVENT_MTTR_OK,
            IF_NULL,
            TID_SAVE,
            LT_BOTH,
            0
        )

        // HTTP 방식: MO-TR 수신 시 CP로부터 받은 정보로 MOCALLINFO 업데이트 후 MO ACK 기능 수행
        if (gipHttpMoAccess != null) {
            val gMOTRBILL = gipHttpMoAccess.moTrBill?.trim()?.equals("Y", ignoreCase = true) == true
            val gBILLTYPE = gipHttpMoAccess.billType?.trim()?.firstOrNull() ?: '0'
            val billType = gipHttpMoAccess.billType ?: "2"

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
                    val deletedMOCallInfo = selectGIPMOCallInfo(
                        request.data.srcCallNo,
                        request.data.destCID,
                        msgId
                    )
                    if (deletedMOCallInfo != null) {
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
                witcomLog.c_write(loggerName, Level.DEBUG,
                    String.format("[processSMReqTransResult] 과금 처리 분기 체크: billType(%s), gMOTRBILL(%s)",
                        billType,
                        if (gMOTRBILL) "Y" else "N"
                    ),
                    Thread.currentThread().getId()
                )
                
                if (!gMOTRBILL) {
                    witcomLog.c_write(loggerName, Level.INFO,
                        String.format("[processSMReqTransResult] 과금 처리 분기 선택: billType(%s)!='1' && gMOTRBILL(%s) - processMOBilling 호출",
                            billType,
                            if (gMOTRBILL) "Y" else "N"
                        ),
                        Thread.currentThread().getId()
                    )
                    // SRC 케이스이고 !gMOTRBILL인 경우 과금 처리 수행
                    processMOBilling(qItem, request, gipHttpMoAccess, smsQLib)
                    witcomLog.c_write(loggerName, Level.INFO,
                        String.format("[processSMReqTransResult] 과금 처리 분기 완료: processMOBilling 완료"),
                        Thread.currentThread().getId()
                    )
                    
                    // 3. 과금 처리 후 ESMClass별 분기 처리
                    processESMClassBranch(qItem, request, gipHttpMoAccess, smsQLib)
                    witcomLog.c_write(loggerName, Level.INFO,
                        String.format("[processSMReqTransResult] ESMClass 분기 처리 완료"),
                        Thread.currentThread().getId()
                    )
                } else {
                    witcomLog.c_write(loggerName, Level.INFO,
                        String.format("[processSMReqTransResult] VBILL_MO 분기 선택: gMOTRBILL(%s)=='Y' - enqueueVbillMoTR 호출",
                            if (gMOTRBILL) "Y" else "N"
                        ),
                        Thread.currentThread().getId()
                    )
                    // gMOTRBILL인 경우 VBILL_MO 큐 적재
                    enqueueVbillMoTR(qItem, request, gipHttpMoAccess)
                    witcomLog.c_write(loggerName, Level.INFO,
                        String.format("[processSMReqTransResult] VBILL_MO 분기 완료: enqueueVbillMoTR 완료"),
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
                smsQLib.InsqStat(
                    qItem,
                    MESSAGE_TR,
                    0,
                    gServerID,
                    MODULEID_VBILLMO,
                    SERVICEID_GIPEVENT,
                    ERRORID_CP_MO_TR_FAIL,
                    ST_VBILLMO_DONT_BILL_TRFAIL,
                    IF_NULL,
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
     */
    private suspend fun enqueueVbillMoTR(
        qItem: QITEM,
        request: ResponseTR,
        gipHttpMoAccess: GipHttpMoAccessEntity
    ) {
        val smsQLib: SmsQLib = liveReloadCLibraryFile.getPreInitializedLibrary(0)

        // 1. VBILL용 QITEM 생성 (C 코드 LINE 3930-3955 대응)
        val trQItem = QITEM()

        trQItem.ucServerType = VSMSS_TYPE.code.toByte()

        // Src CID
        run {
            val srcCidBytes = request.data.srcCID.toByteArray(Charsets.US_ASCII)
            System.arraycopy(
                srcCidBytes,
                0,
                trQItem.szSrcCId,
                0,
                minOf(srcCidBytes.size, trQItem.szSrcCId.size)
            )
        }

        // Src CallNo → szSrcMinNo
        run {
            val srcCallNoStr = request.data.srcCallNo
            val srcCallNoBytes = srcCallNoStr.toByteArray(Charsets.US_ASCII)
            System.arraycopy(
                srcCallNoBytes,
                0,
                trQItem.szSrcMinNo,
                0,
                minOf(srcCallNoBytes.size, trQItem.szSrcMinNo.size)
            )
        }

        // Dest CID → szCId
        run {
            val destCidBytes = request.data.destCID.toByteArray(Charsets.US_ASCII)
            System.arraycopy(
                destCidBytes,
                0,
                trQItem.szCId,
                0,
                minOf(destCidBytes.size, trQItem.szCId.size)
            )
        }

        // Dest CallNo → szMinNo
        run {
            val destCallNoStr = request.data.destCallNo
            val destCallNoBytes = destCallNoStr.toByteArray(Charsets.US_ASCII)
            System.arraycopy(
                destCallNoBytes,
                0,
                trQItem.szMinNo,
                0,
                minOf(destCallNoBytes.size, trQItem.szMinNo.size)
            )
        }

        // MsgCode / SubCode
        trQItem.usMsgCode = QTYPE_SM_REQ.toShort()
        trQItem.usMsgSubCode = SM_REQ_TRANS_RESULT.toShort()

        // MsgStatus, MsgId는 기존 qItem에서 복사 (C: ptrTransRes 기반)
        trQItem.ucMsgStatus = qItem.ucMsgStatus
        System.arraycopy(qItem.ucMsgId, 0, trQItem.ucMsgId, 0, trQItem.ucMsgId.size)

        // 2. VBILL_MO Queue 번호 획득
        val vbillMoQueueNo = getVbillMoQueueNo()

        // 3. 큐 적재
        val ret = smsQLib.InsertIntoSmsQWithQNo(trQItem, vbillMoQueueNo)

        // 4. 결과에 따른 통계 처리 (C 코드 3973-3983 대응)
        if (ret < 0) {
            smsQLib.InsqStat(
                trQItem,
                MESSAGE_TR,
                0,
                gServerID,
                MODULEID_GIPEVENT_C,
                SERVICEID_GIPEVENT,
                ERRORID_CP_MO_TR_FAIL,
                ST_Q_INSERT_FAIL_VBILLMO,
                vbillMoQueueNo,
                SmsDef.TID_SAVE,
                LT_TRACE,
                0
            )

            witcomLog.c_write(
                getLoggerName(
                    request.data.destCID,
                    gipHttpMoAccess.ipAddr,
                    gipHttpMoAccess.portNo
                ),
                Level.ERROR,
                "Insert VBILL_MO Q Error QNO($vbillMoQueueNo)",
                Thread.currentThread().id
            )
        } else {
            smsQLib.InsqStat(
                trQItem,
                MESSAGE_TR,
                0,
                gServerID,
                MODULEID_GIPEVENT_C,
                SERVICEID_GIPEVENT,
                ERRORID_CP_MO_TR_FAIL,   // C 코드와 동일: SUCCESS 아님에 주의
                ST_GIPEVENT_MOTR_OK,
                vbillMoQueueNo,
                SmsDef.TID_SAVE,
                LT_TRACE,
                0
            )
        }
    }
    
    /**
     * SelectGIPMOCallInfo 함수
     * 
     * C 코드 참고: GIDBLib.c LINE 2528-2607, 3254-3317
     * SELECT MOSUBTIME, MSGLEN, ROAMINGID, CB, ROAMPMN, W_ZONE, TRACE_ID, 
     *        ORIG_MVNO_INFO, DEST_MVNO_INFO, RCS, DCS_TYPE, ORG_MSGLEN, MORECVTIME
     * FROM MOCALLINFO
     * WHERE SRCCALLNO = ? AND DESTCID = ? AND MSGID = ?
     * 
     * DELETE FROM MOCALLINFO
     * WHERE SRCCALLNO = ? AND DESTCID = ? AND MSGID = ? (조회 후 삭제)
     * 
     * @return MOCallInfoEntity 또는 null (데이터 없음)
     */
    @Transactional
    open suspend fun selectGIPMOCallInfo(
        srcCallNo: String,
        destCId: String,
        msgId: String
    ): MOCallInfoEntity? {
        val loggerName = getLoggerName(destCId, null, null)
        return try {
            // ROWNUM = 1로 첫 번째 결과만 조회 (가장 최근 것)
            val moCallInfo = moCallInfoRepository.findBySrcCallNoAndDestCIdAndMsgId(srcCallNo, destCId, msgId)
            
            // C 코드 LINE 3254-3317: SELECT 성공 후 DELETE 수행
            if (moCallInfo != null) {
                val deleteResult = moCallInfoRepository.deleteBySrcCallNoAndDestCIdAndMsgId(srcCallNo, destCId, msgId)
                if (deleteResult > 0) {
                    witcomLog.c_write(
                        loggerName,
                        Level.DEBUG,
                        String.format("SelectGIPMOCallInfo() Deleted: SourceCallNo(%s) DestCID(%s) MsgId(%s) DeleteResult(%d)",
                            srcCallNo, destCId, msgId, deleteResult),
                        Thread.currentThread().getId()
                    )
                }
            }
            
            moCallInfo
        } catch (e: Exception) {
            witcomLog.c_write(
                loggerName,
                Level.ERROR,
                String.format("SelectGIPMOCallInfo() Select Error: %s", e.message),
                Thread.currentThread().getId()
            )
            null
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
            // HTTP 환경에서는 MO-ACK를 생략하고 MO-TR로 통합했으므로,
            // MO와 MO-TR이 동일한 msgID를 사용함
            // 따라서 새 레코드를 만들 필요가 없고, 원본 레코드를 그대로 사용
            // updateGIPMOCallInfo()는 실제로 아무 작업도 하지 않거나,
            // 단순히 레코드 존재 여부만 확인하면 됨
            
            val srcCallNo = request.data.srcCallNo
            val destCId = request.data.destCID
            val msgId = request.data.msgId ?: ""  // MO와 MO-TR 모두 동일한 msgID
            
            // 원본 레코드가 존재하는지 확인만 하면 됨
            val moCallInfo = moCallInfoRepository.findBySrcCallNoAndDestCIdAndMsgId(
                srcCallNo,
                destCId,
                msgId
            )
            
            if (moCallInfo == null) {
                // 레코드가 없으면 에러 처리
                witcomLog.c_write(
                    loggerName,
                    Level.ERROR,
                    String.format(
                        "UpdateGIPMOCallInfo() 레코드 없음: SourceCallNo(%s) DestCID(%s) MsgId(%s) - MO-TR 통합 환경에서는 원본 레코드가 존재해야 함",
                        srcCallNo,
                        destCId,
                        msgId
                    ),
                    Thread.currentThread().getId()
                )
            } else {
                witcomLog.c_write(
                    loggerName,
                    Level.INFO,
                    String.format(
                        "UpdateGIPMOCallInfo() 레코드 확인 완료: SourceCallNo(%s) DestCID(%s) MsgId(%s) - MO-TR 통합 환경: 새 레코드 생성 불필요, 원본 레코드 사용",
                        srcCallNo,
                        destCId,
                        msgId
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
     * bprintf 과금 데이터 출력 함수
     * 
     * C 코드 참고: bprintf.c LINE 114-210
     * 로그 파일 경로: $SMS_VCDR/LOG/ (환경변수 SMS_VCDR 사용)
     * 파일명 형식: {모듈번호}_{YYYYMMDDHHMM} (5분 단위)
     * 
     * @param billingLine 과금 데이터 라인
     */
    private fun writeBillingLog(billingLine: String, loggerName: String) {
        try {
            val now = Calendar.getInstance()
            val currentMinute = now.get(Calendar.MINUTE)
            
            // 분이 변경되면 파일 재생성 (C 코드 LINE 156-164)
            if (lastBillingLogMinute != currentMinute) {
                billingLogFile?.close()
                
                val logFile = File(billingLogDir)
                if (!logFile.exists()) {
                    logFile.mkdirs()
                }
                
                // C 코드 LINE 93: 파일명 형식 {모듈번호}_{YYYYMMDDHHMM}
                val moduleNo = "00"  // 모듈 번호 00으로 고정
                val fileDate = SimpleDateFormat("yyyyMMddHHmm").format(now.time)
                val fileName = "$billingLogDir/${moduleNo}_$fileDate"
                
                val billingFile = File(fileName)
                billingLogFile = FileWriter(billingFile, true)
                lastBillingLogMinute = currentMinute
            }
            
            // C 코드 LINE 205: vfprintf로 데이터 출력
            billingLogFile?.write(billingLine)
            billingLogFile?.flush()
        } catch (e: Exception) {
            witcomLog.c_write(
                loggerName,
                Level.ERROR,
                String.format("bprintf write error: %s", e.message),
                Thread.currentThread().getId()
            )
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
     * UpdateMO_NOTISEND 함수 (HTTP 환경용)
     * 
     * HTTP 환경에서는 MO-ACK를 생략하고 MO-TR로 통합했으므로,
     * MO와 MO-TR이 동일한 msgID를 사용함
     * 따라서 새 레코드를 만들 필요가 없고, 원본 레코드를 그대로 사용
     * updateMO_NOTISEND()는 실제로 아무 작업도 하지 않거나,
     * 단순히 레코드 존재 여부만 확인하면 됨
     * 
     * 참고: C 코드의 UpdateMO_NOTISEND()는 MO-ACK 처리용으로 새 레코드(ackMsgId)를 생성함
     *       HTTP 환경에서는 MO-ACK 생략이므로 이 로직은 불필요함
     * 
     * C 코드 참고: GIDBLib.c LINE 4356-4459 (UpdateMO_NOTISEND 함수)
     * C 코드 참고: GIPEVENT_c.c LINE 2238 (NOTI_PLUS 케이스에서 호출 - MO-ACK 처리)
     */
    @Transactional
    open fun updateMO_NOTISEND(request: ResponseTR) {
        val loggerName = getLoggerName(request.data.destCID, null, null)
        try {
            // HTTP 환경에서는 MO-ACK를 생략하고 MO-TR로 통합했으므로,
            // MO와 MO-TR이 동일한 msgID를 사용함
            // 따라서 새 레코드를 만들 필요가 없고, 원본 레코드를 그대로 사용
            
            val srcCallNo = request.data.srcCallNo
            val destCId = request.data.destCID
            val msgId = request.data.msgId ?: ""  // MO와 MO-TR 모두 동일한 msgID
            
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
            
            // 원본 레코드가 존재하는지 확인만 하면 됨
            // SERVERTYPE='V'는 쿼리 내부에 하드코딩되어 있음
            val moNotISend = moNotISendRepository.findBySrcCallNoAndDestCIdAndMsgIdAndServerType(
                srcCallNo,
                destCId,
                msgId
            ).orElse(null)
            
            if (moNotISend == null) {
                // 레코드가 없으면 에러 처리
                witcomLog.c_write(
                    loggerName,
                    Level.ERROR,
                    String.format(
                        "UpdateMO_NOTISEND() 레코드 없음: SourceCallNo(%s) DestCID(%s) MsgId(%s) - MO-TR 통합 환경에서는 원본 레코드가 존재해야 함",
                        srcCallNo,
                        destCId,
                        msgId
                    ),
                    Thread.currentThread().getId()
                )
            } else {
                witcomLog.c_write(
                    loggerName,
                    Level.INFO,
                    String.format(
                        "UpdateMO_NOTISEND() 레코드 확인 완료: SourceCallNo(%s) DestCID(%s) MsgId(%s) - MO-TR 통합 환경: 새 레코드 생성 불필요, 원본 레코드 사용",
                        srcCallNo,
                        destCId,
                        msgId
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
    
}

