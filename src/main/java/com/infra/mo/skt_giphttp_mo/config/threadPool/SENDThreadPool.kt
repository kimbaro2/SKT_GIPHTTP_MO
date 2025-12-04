package com.infra.mo.skt_giphttp_mo.config.threadPool

import ch.qos.logback.classic.Level
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.AI_SURVEY_NUMBER
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ALTI_FAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ALTI_NODATA
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ALTI_SUCCESS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.APPLY_NEW_SMSMANAGER
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DEFINE_GIPVERID_510
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_TR_FAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_TR_SUCCESS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.IF_NULL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LT_BOTH
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MESSAGE_TR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MODULEID_GIPALL_C
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NPDB
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SERVICEID_GIPALL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SMSMOR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_SEND
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_SIMPLE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_TRANS_RESULT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_MRMSPAM
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_PORTEDOUT_KTF
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_PORTEDOUT_LGT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_PORTEDOUT_SKT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_PORTED_OUT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_SPAMERR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIPALL_MTTR_SEND_OK
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.TID_NO_SAVE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.VSMSS_TYPE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MESSAGE_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_LIMIT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_SUCCESS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.QTYPE_SM_REQ
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SUB_QTYPE_RCS_TR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.TERM_TYPE_KOR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_KSC5601
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.RCS_RESULT_INCALIDDST
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.Q_INSERT_SUCCESS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LT_TRACE
import java.nio.charset.Charset
import com.infra.mo.skt_giphttp_mo.config.application.LiveReloadCLibraryFile
import com.infra.mo.skt_giphttp_mo.config.application.PerformanceSettings
import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.CallInfoEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.CfgEtcEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpAccessEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.SpcodeEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.CallInfoRepository
import com.infra.mo.skt_giphttp_mo.dto.TrResultRequest
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.BILLTYPE_SRC
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.GIPEVENT_BLOCK_NOTI_CALLBACK
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.GIPEVENT_BLOCK_NOTI_CID
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MODULEID_GIPEVENT_C
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SERVICEID_GIPEVENT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIPEVENT_INSQ_BLOCKNOTI
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIP_MO_LIMIT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_Q_INSERT_FAIL_BLOCKNOTI
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseRenewVO
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseTR
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseTR.DataBody
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseMO
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseMO.DataBody as MODataBody
import com.infra.mo.skt_giphttp_mo.dto.smsController.RequestMOTR
import com.infra.mo.skt_giphttp_mo.dto.smsController.RequestMOTR.DataBody as MOTRDataBody
import com.infra.mo.skt_giphttp_mo.dto.smsController.Rsv4ProtocolItem
import com.infra.mo.skt_giphttp_mo.utils.EncryptionExample
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil.QueueResult
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SM_REQ_TRANS_RESULT_PROCESSOR
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.impl.SmsQLibImpl
import io.netty.channel.ChannelOption
import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.awaitSingle
import java.net.URI
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import kotlin.system.measureNanoTime
import javax.annotation.PreDestroy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

/*TODO 캐시메모리의 READ/WRITE를 위한 스레드풀을 구성합니다.*/
/*나중에 여기서 CLibrary 접근로직 구성하자*/
@Component("SENDThreadPool")
class SENDThreadPool(
    @Qualifier("ApplicationCacheMap") private val cacheMap: ConcurrentHashMap<String, Any>,
    @Qualifier("ApplicationCacheQueue") private val cacheQueue: ConcurrentLinkedQueue<Any>,
    @Qualifier("GipHttpMoAccessList")
    private val gipHttpMoAccessEntityList: CopyOnWriteArrayList<GipHttpMoAccessEntity>,
    @Qualifier("CfgEtcMap") private val cfgEtcMap: HashMap<String, CfgEtcEntity>,
    @Qualifier("SpcodeMap") private val spcodeMap: ConcurrentHashMap<String, SpcodeEntity>,
    private val witcomLog: WitcomLog,
    private val performanceSettings: PerformanceSettings,
    private val context: ApplicationContext,
    private val smsQLibImpl: SmsQLibImpl,
    private val liveReloadCLibraryFile: LiveReloadCLibraryFile,
    private val callInfoRepository: CallInfoRepository,
    private val telePrefixRepository:
    com.infra.mo.skt_giphttp_mo.db.altibase.repository.TELEPrefixRepository,
    private val msgLimitListRepository:
    com.infra.mo.skt_giphttp_mo.db.altibase.repository.MsgLimitListRepository
) {
    private val log: Logger = LoggerFactory.getLogger(SENDThreadPool::class.java)
    
    // 코루틴 스코프 저장 (애플리케이션 종료 시 취소하기 위함)
    private var coroutineScope: CoroutineScope? = null

    /**
     * CFG_SPCODE 캐시에서 CID로 SPCODE를 조회합니다 (C 코드의 DBReadCFG_SPCODE와 동일한 동작)
     * @param cid CID
     * @return -1: 에러, 0: 데이터 없음, 1: 데이터 있음
     */
    private fun dbReadCfgSpcode(cid: String?): Int {
        return try {
            if (cid == null) {
                return 0
            }
            if (spcodeMap.containsKey(cid)) {
                1 // SMS Manager Service CP 존재
            } else {
                0 // SMS Manager Service CP 없음
            }
        } catch (e: Exception) {
            log.error("DBReadCFG_SPCODE() Select Error: CID($cid)", e)
            -1 // 에러
        }
    }

    /**
     * CALLINFO 테이블에서 MSGIDSERVER와 DESTCALLNO로 데이터를 조회하여 ptrCallInfo에 채웁니다. C 코드의 DBReadW2PCALLINFO
     * 함수와 동일한 동작을 수행합니다.
     *
     * @param szMsgIdServer MSGIDSERVER 값
     * @param szDestCallNo DESTCALLNO 값
     * @param ptrCallInfo 출력 파라미터: 조회된 데이터를 채울 CallInfoEntity 객체
     * @param szFWDNO 출력 파라미터: FWD_SRC 값을 복사할 CharArray (크기 21)
     * @return ALTI_SUCCESS: 성공, ALTI_NODATA: 데이터 없음, ALTI_FAIL: 에러
     *
     * C 코드 참고: GIPALL/GIDBLib.c LINE 4532-4910
     */
    private fun DBReadW2PCALLINFO(
        szMsgIdServer: String,
        szDestCallNo: String,
        ptrCallInfo: CallInfoEntity,
        szFWDNO: CharArray
    ): Int {
        return try {
            // CallInfo 테이블에서 조회
            val foundCallInfo =
                callInfoRepository.findByMsgIdServerAndDestCallNo(szMsgIdServer, szDestCallNo)

            if (foundCallInfo.isPresent) {
                val dbCallInfo = foundCallInfo.get()

                // C 코드 LINE 4859-4907 참고: ptrCallInfo에 데이터 채우기
                // 복합 키 설정 (삭제를 위해 필요)
                ptrCallInfo.msgidcenter = dbCallInfo.msgidcenter
                ptrCallInfo.destcallno = dbCallInfo.destcallno
                ptrCallInfo.msgidserver = dbCallInfo.msgidserver
                ptrCallInfo.srccid = dbCallInfo.srccid
                ptrCallInfo.srccallno = dbCallInfo.srccallno
                ptrCallInfo.destcid = dbCallInfo.destcid
                ptrCallInfo.msg = dbCallInfo.msg
                ptrCallInfo.cb = dbCallInfo.cb
                ptrCallInfo.rd = dbCallInfo.rd
                ptrCallInfo.msglen = dbCallInfo.msglen
                ptrCallInfo.dcsType = dbCallInfo.dcsType
                ptrCallInfo.orgMsglen = dbCallInfo.orgMsglen
                ptrCallInfo.origMvnoInfo = dbCallInfo.origMvnoInfo
                ptrCallInfo.destMvnoInfo = dbCallInfo.destMvnoInfo

                // szFWD_NO에 FWD_SRC 복사 (C 코드 LINE 4895-4896)
                // C 코드: if(vSrcFWD.len > 0) strncpy(szFWDNO, (char *)vSrcFWD.arr, vSrcFWD.len + 1);
                if (dbCallInfo.fwdSrc != null && dbCallInfo.fwdSrc.isNotEmpty()) {
                    dbCallInfo
                        .fwdSrc
                        .toCharArray()
                        .copyInto(
                            szFWDNO,
                            0,
                            0,
                            minOf(dbCallInfo.fwdSrc.length, szFWDNO.size - 1)
                        )
                }

                ALTI_SUCCESS
            } else {
                // C 코드 LINE 4855-4857: SQL_NO_DATA 처리
                witcomLog.p_write(
                    Level.WARN,
                    String.format(
                        "ASP_NO_DATA ReadPCSMTCallInfo() - msgIdServer(%s), destCallNo(%s)",
                        szMsgIdServer,
                        szDestCallNo
                    )
                )
                ALTI_NODATA
            }
        } catch (e: Exception) {
            // C 코드 LINE 4843-4854: SQL_ERROR 처리
            log.error(
                "DBReadW2PCALLINFO() Altibase Error: msgIdServer($szMsgIdServer), destCallNo($szDestCallNo)",
                e
            )
            witcomLog.p_write(
                Level.ERROR,
                String.format(
                    "DBReadW2PCALLINFO() Altibase Error: msgIdServer(%s), destCallNo(%s), error(%s)",
                    szMsgIdServer,
                    szDestCallNo,
                    e.message
                )
            )
            ALTI_FAIL
        }
    }

    /**
     * C 코드의 CheckTelecom 함수와 동일한 동작을 수행합니다. TELE_PREFIX 테이블에서 TelePrefix를 조회합니다.
     *
     * @param szDestCID DestCID (예: "010", "011", "012")
     * @return 1: 조회 성공, 0: 데이터 없음, -1: 에러
     *
     * C 코드 참고: lib/QLIB64/CFG_ReadTbl.c LINE 3234-3381
     */
    private fun checkTelecom(szDestCID: String): Int {
        return try {
            // szDestCID에서 TelePrefix 추출 (앞 3자리)
            // gMMCSFlag는 현재 프로젝트에서 사용하지 않는 것으로 보이므로 앞 3자리만 사용
            val szTelePre =
                if (szDestCID.length >= 3) {
                    szDestCID.substring(0, 3)
                } else {
                    szDestCID
                }

            witcomLog.p_write(
                Level.DEBUG,
                String.format(
                    "CheckTelecom() TelePrefix[%s] DestCID[%s]",
                    szTelePre,
                    szDestCID
                )
            )

            // TELE_PREFIX 테이블에서 조회
            // 먼저 정확히 일치하는지 확인
            val telePrefixOpt = telePrefixRepository.findTelePrefixByTelePrefix(szTelePre)

            if (telePrefixOpt.isPresent) {
                witcomLog.p_write(
                    Level.DEBUG,
                    String.format(
                        "CheckTelecom() SELECT TELE PREFIX[%s] SUCCESS!!",
                        szTelePre
                    )
                )
                ALTI_SUCCESS
            } else {
                // LIKE 조회 시도 (szTelePre[0] != '0'인 경우)
                if (szTelePre.isNotEmpty() && szTelePre[0] != '0') {
                    val likeOpt = telePrefixRepository.findTelePrefixLikeTelePre(szTelePre)
                    if (likeOpt.isPresent) {
                        witcomLog.p_write(
                            Level.DEBUG,
                            String.format(
                                "CheckTelecom() SELECT TELE PREFIX[%s] SUCCESS!! (LIKE)",
                                szTelePre
                            )
                        )
                        ALTI_SUCCESS
                    } else {
                        witcomLog.p_write(
                            Level.WARN,
                            String.format(
                                "CheckTelecom() No TelePrefix[%s] in table of TELE_PREFIX",
                                szTelePre
                            )
                        )
                        ALTI_NODATA
                    }
                } else {
                    witcomLog.p_write(
                        Level.WARN,
                        String.format(
                            "CheckTelecom() No TelePrefix[%s] in table of TELE_PREFIX",
                            szTelePre
                        )
                    )
                    ALTI_NODATA
                }
            }
        } catch (e: Exception) {
            witcomLog.p_write(
                Level.ERROR,
                String.format("CheckTelecom() SELECT TELE_PREFIX FAIL :: %s", e.message)
            )
            ALTI_FAIL
        }
    }

    /**
     * C 코드의 GetTelecom_New 함수와 동일한 동작을 수행합니다. TELE_PREFIX 테이블에서 DestCID와 DestCallNo를 기반으로 통신사 코드를
     * 조회합니다.
     *
     * @param destCID DestCID (예: "010", "011", "012")
     * @param destCallNo DestCallNo (앞 4자리 사용)
     * @return 통신사 코드 (11:SKT, 16:KTF, 19:LGT), -1: 에러, 0: 데이터 없음
     *
     * C 코드 참고: lib/QLIB64/CFG_ReadTbl.c LINE 2546-2675
     */
    private fun getTelecomNew(destCID: String, destCallNo: String): Int {
        return try {
            // szTelePrefix 추출 (앞 3자리)
            val szTelePrefix =
                if (destCID.length >= 3) {
                    destCID.substring(0, 3)
                } else {
                    destCID
                }

            // szPrefix 추출 (앞 4자리)
            val szPrefix =
                if (destCallNo.length >= 4) {
                    destCallNo.substring(0, 4)
                } else {
                    destCallNo
                }

            witcomLog.p_write(
                Level.DEBUG,
                String.format(
                    "GetTelecom_New() TelePrefix(%s) Prefix(%s)",
                    szTelePrefix,
                    szPrefix
                )
            )

            // TELE_PREFIX 테이블에서 조회
            val telePrefixOpt = telePrefixRepository.findTelecomByPrefix(szTelePrefix, szPrefix)

            if (telePrefixOpt.isPresent) {
                val telecom = telePrefixOpt.get().telecom
                witcomLog.p_write(
                    Level.DEBUG,
                    String.format(
                        "GetTelecom_New() SELECT TELECOM[%d] SUCCESS!!",
                        telecom
                    )
                )
                telecom
            } else {
                witcomLog.p_write(
                    Level.WARN,
                    String.format(
                        "GetTelecom_New() No prefix <%s> in table of TELE_PREFIX",
                        szPrefix
                    )
                )
                ALTI_NODATA
            }
        } catch (e: Exception) {
            witcomLog.p_write(
                Level.ERROR,
                String.format(
                    "GetTelecom_New() SELECT TELE_PREFIX FAIL Prefix(%s) :: %s",
                    if (destCallNo.length >= 4) destCallNo.substring(0, 4) else destCallNo,
                    e.message
                )
            )
            ALTI_FAIL
        }
    }

    /** TODO Enqueue All 시뮬레이션 및 비지니스 로직을 수행합니다. */
    fun executeEventHandlerTask(poolSize: Int) {
        // poolSize가 0 이하이면 에러
        require(poolSize > 0) { "poolSize must be greater than zero" }
        
        val dispatcher = Executors.newFixedThreadPool(poolSize).asCoroutineDispatcher()
        coroutineScope = CoroutineScope(dispatcher)
        val coroutineCacheCheckPool = coroutineScope!!
        
        // gipHttpMoAccessEntityList가 비어있어도 스케줄러는 계속 동작해야 함
        // chunked size가 0이 되지 않도록 최소값을 1로 설정
        val chunkSize = if (gipHttpMoAccessEntityList.isEmpty()) {
            1 // 빈 리스트일 때도 chunked(1)로 처리하여 스케줄러는 동작하되 forEach는 실행되지 않음
        } else {
            maxOf(1, (gipHttpMoAccessEntityList.size + poolSize - 1) / poolSize)
        }
        val partitionedList = gipHttpMoAccessEntityList.chunked(chunkSize)
        val gServerID = System.getenv("SMSS_NO").trim().toInt()
        witcomLog.p_write(Level.DEBUG, "🔹 Partitioned result:")
        partitionedList.forEachIndexed { index, list ->
            witcomLog.p_write(
                Level.DEBUG,
                String.format("  Worker[%d] -> %s", index, list.joinToString())
            )
        }
        repeat(poolSize) { index ->
            val assignedCids = partitionedList.getOrNull(index) ?: emptyList()
            coroutineCacheCheckPool.launch {
                while (coroutineCacheCheckPool.isActive) {
                    val smsQLib: SmsQLib = liveReloadCLibraryFile.getPreInitializedLibrary(index)
                    assignedCids.forEach { entity ->
                        val latencyNs: Long
                        val latencyNs_SMS_MANAGER: Long
                        val result: Int
                        var qItem = QITEM.ByReference()
                        val queueNo = entity.queueNo
                        val cpUrl = entity.cpUrl
                        val logNo = entity.logNo
                        // 문자매니저 - CFG_SPCODE 캐시에서 CID로 조회
                        val cid = entity.cid
                        
                        // loggerName 생성 (entity 기반)
                        val loggerName = "${entity.cid}-${entity.ipAddr}-${entity.portNo}"

                        // 큐에서 데이터 가져오기
                        val formatted =
                            String.format("et CP Qno(%s) ==", queueNo)
                        witcomLog.c_write(loggerName, Level.DEBUG, formatted, Thread.currentThread().getId())
                        val gstQResultObj: QueueResult =
                            QItemServiceUtil.fetchAndConvert2(queueNo, smsQLib, witcomLog) as
                                    QueueResult // -> ### Print QITEM
                        val gstQItemTrans = gstQResultObj.result
                        var gstQItem = gstQResultObj.qItem as? QITEM
                        
                        // null 체크: 큐에서 데이터를 가져오지 못한 경우 처리하지 않음
                        if (gstQItemTrans == null || gstQItem == null) {
                            witcomLog.c_write(
                                loggerName,
                                Level.DEBUG,
                                String.format(
                                    "Queue is empty or failed to get message from queue. queueNo=%d, result=%s, qItem=%s",
                                    queueNo,
                                    if (gstQItemTrans == null) "null" else "not null",
                                    if (gstQItem == null) "null" else "not null"
                                ),
                                Thread.currentThread().getId()
                            )
                            delay(50L)
                            return@forEach
                        }
                        
                        gstQItem = QItemServiceUtil.qItemToMsgHdr(gstQItem)
                        
                        // C 코드 LINE 892: PrintMsgQueue 호출 (2022.07 KISA 식별코드)
                        QItemServiceUtil.printQItem3(gstQItem, witcomLog)
                        
                        // C 코드 LINE 893: ucServerType 설정
                        gstQItem.ucServerType = VSMSS_TYPE.code.toByte()
                        
                        // C 코드 LINE 1173: MakePacketFromQItem에서 SM_REQ_SEND -> SM_REQ_SIMPLE로 변경
                        // SM_REQ_SEND인 경우 SM_REQ_SIMPLE로 변경 (C 코드와 동일한 흐름)
                        if (gstQItem.usMsgSubCode == SM_REQ_SEND.toShort()) {
                            gstQItem.usMsgSubCode = SM_REQ_SIMPLE.toShort()
                            // gstQItemTrans도 동기화 (나중에 사용하기 위해)
                            gstQItemTrans.msgSubCode = SM_REQ_SIMPLE.toShort()
                        }
                        
                        // MO 전송을 위한..
                        latencyNs = measureNanoTime {//LINE 937
                            // C 코드 LINE 937: switch(ptrMsgHdr->usMsgSubCode) - 이제 SM_REQ_SIMPLE로 switch
                            when (gstQItem.usMsgSubCode.toInt()) {
                                    // ========== MO 발송 처리 (SM_REQ_SIMPLE) ==========
                                    // C 코드 참고: GIPEVENT_c.c LINE 939-1054
                                    SM_REQ_SIMPLE -> {
                                        // C 코드 LINE 940: CheckLimitMO 체크 (한도 차단 확인)
                                        val limitCheckResult = checkLimitMO(gstQItemTrans, entity, witcomLog)
                                        
                                        // C 코드 LINE 941: if(nRet == TRUE) - 한도 초과인 경우
                                        if (limitCheckResult == true) {
                                            // C 코드 LINE 943-945: QITEM 복사
                                            val stQItem = QItemServiceUtil.qItemToMsgHdr(gstQItem)
                                            
                                            witcomLog.p_write(
                                                Level.INFO,
                                                String.format(
                                                    "LimitMO : %s%s",
                                                    gstQItemTrans.srcCid,
                                                    gstQItemTrans.srcMinNo
                                                )
                                            )
                                            
                                            // C 코드 LINE 953: if (gGIPEVENT_BLK_NOTI == TRUE) - 블록 알림 처리
                                            val gGIPEVENT_BLK_NOTI = cfgEtcMap["GIPEVENT_BLK_NOTI"]?.pvalue ?: 0
                                            if (gGIPEVENT_BLK_NOTI > 0) { // <<-  CFG_ETC
                                                // C 코드 LINE 955-979: 블록 알림 메시지 구성
                                                val nQueueNo = 0
                                                val currtime = System.currentTimeMillis()
                                                val dateFormat = java.text.SimpleDateFormat("MM/dd HH:mm", java.util.Locale.getDefault())
                                                val strtime = dateFormat.format(java.util.Date(currtime))
                                                
                                                stQItem.ucTermType = '1'.code.toByte()
                                                stQItem.usMsgCode = QTYPE_SM_REQ.toShort()
                                                stQItem.usMsgSubCode = SM_REQ_SIMPLE.toShort()
                                                
                                                // 블록 알림 메시지 구성 (C 코드 LINE 967 참고)
                                                // TODO: CFG_ETC 테이블의 BLOCK_NOTI_MSG는 문자열이어야 하지만, pvalue가 int 타입이므로
                                                // 실제 구현 시 문자열 필드를 추가하거나 다른 방법을 사용해야 함
                                                val blockNotiMsgFormat = "[%s] %s로부터 차단된 메시지입니다."
                                                val blockNotiMsg = String.format(blockNotiMsgFormat, strtime, QItemServiceUtil.byteArrayToKString(stQItem.szCId))
                                                
                                                // C 코드 LINE 969-970: Dest와 Src 교환
                                                val tempCid = stQItem.szCId.clone()
                                                System.arraycopy(stQItem.szSrcCId, 0, stQItem.szCId, 0, minOf(stQItem.szSrcCId.size, stQItem.szCId.size))
                                                System.arraycopy(tempCid, 0, stQItem.szSrcCId, 0, minOf(tempCid.size, stQItem.szSrcCId.size))
                                                
                                                val tempMin = stQItem.szMinNo.clone()
                                                System.arraycopy(stQItem.szSrcMinNo, 0, stQItem.szMinNo, 0, minOf(stQItem.szSrcMinNo.size, stQItem.szMinNo.size))
                                                System.arraycopy(tempMin, 0, stQItem.szSrcMinNo, 0, minOf(tempMin.size, stQItem.szSrcMinNo.size))

                                                val blockNotiCidBytes = GIPEVENT_BLOCK_NOTI_CID.toByteArray(Charset.forName("CP949"))
                                                System.arraycopy(blockNotiCidBytes, 0, stQItem.szSrcCId, 0, minOf(blockNotiCidBytes.size, stQItem.szSrcCId.size - 1))
                                                if (blockNotiCidBytes.size < stQItem.szSrcCId.size) {
                                                    stQItem.szSrcCId[blockNotiCidBytes.size] = 0x00
                                                }
                                                
                                                stQItem.szSrcMinNo.fill(0x00)
                                                
                                                val blockNotiCallbackBytes = GIPEVENT_BLOCK_NOTI_CALLBACK.toByteArray(Charset.forName("CP949"))
                                                System.arraycopy(blockNotiCallbackBytes, 0, stQItem.szCB, 0, minOf(blockNotiCallbackBytes.size, stQItem.szCB.size - 1))
                                                if (blockNotiCallbackBytes.size < stQItem.szCB.size) {
                                                    stQItem.szCB[blockNotiCallbackBytes.size] = 0x00
                                                }
                                                
                                                // C 코드 LINE 977-979: 메시지 길이 및 유효기간 설정
                                                val blockNotiMsgBytes = blockNotiMsg.toByteArray(Charset.forName("CP949"))
                                                val msgLen = minOf(blockNotiMsgBytes.size, stQItem.szMsg.size)
                                                System.arraycopy(blockNotiMsgBytes, 0, stQItem.szMsg, 0, msgLen)
                                                stQItem.ucMsgLen = msgLen
                                                stQItem.nVldPrd = 86400
                                                stQItem.ucRgtDlvFlg = 0


                                                // C 코드 LINE 981: InsertIntoSmsQnQNo 호출
                                                val insertResult = smsQLib.InsertIntoSmsQnQNo(stQItem, nQueueNo) //<- 한도차단 노티 발송 수행
                                                
                                                // C 코드 LINE 983: if(ret == Q_INSERT_SUCCESS) - 큐 삽입 성공
                                                if (insertResult == Q_INSERT_SUCCESS) {
                                                    witcomLog.p_write(
                                                        Level.INFO,
                                                        String.format(
                                                            "BLOCK_NOTIFICATION Send : SrcCId(%s),SrcCallNo(%s),DestCId(%s),DestCallNo(%s),szCB(%s),nVldPrd(%d),MsgLen(%d)",
                                                            QItemServiceUtil.byteArrayToKString(stQItem.szSrcCId),
                                                            QItemServiceUtil.byteArrayToKString(stQItem.szSrcMinNo),
                                                            QItemServiceUtil.byteArrayToKString(stQItem.szCId),
                                                            QItemServiceUtil.byteArrayToKString(stQItem.szMinNo),
                                                            QItemServiceUtil.byteArrayToKString(stQItem.szCB),
                                                            stQItem.nVldPrd,
                                                            stQItem.ucMsgLen
                                                        )
                                                    )
                                                    
                                                    // C 코드 LINE 997-998: InsqStat 호출
                                                    smsQLib.InsqStat(
                                                        stQItem,
                                                        MESSAGE_MO,
                                                        0,
                                                        gServerID,
                                                        MODULEID_GIPEVENT_C,
                                                        SERVICEID_GIPEVENT,
                                                        ERRORID_CP_MO_SUCCESS,
                                                        ST_GIPEVENT_INSQ_BLOCKNOTI,
                                                        nQueueNo,
                                                        TID_NO_SAVE,
                                                        LT_TRACE,
                                                        0
                                                    )
                                                } else {
                                                    // C 코드 LINE 1000-1017: 큐 삽입 실패
                                                    witcomLog.p_write(
                                                        Level.ERROR,
                                                        String.format(
                                                            "BLOCK_NOTIFICATION Send Fail : SrcCId(%s),SrcCallNo(%s),DestCId(%s),DestCallNo(%s),szCB(%s),nVldPrd(%d),MsgLen(%d)",
                                                            QItemServiceUtil.byteArrayToKString(stQItem.szSrcCId),
                                                            QItemServiceUtil.byteArrayToKString(stQItem.szSrcMinNo),
                                                            QItemServiceUtil.byteArrayToKString(stQItem.szCId),
                                                            QItemServiceUtil.byteArrayToKString(stQItem.szMinNo),
                                                            QItemServiceUtil.byteArrayToKString(stQItem.szCB),
                                                            stQItem.nVldPrd,
                                                            stQItem.ucMsgLen
                                                        )
                                                    )
                                                    
                                                    smsQLib.InsqStat(
                                                        stQItem,
                                                        MESSAGE_MO,
                                                        0,
                                                        gServerID,
                                                        MODULEID_GIPEVENT_C,
                                                        SERVICEID_GIPEVENT,
                                                        ERRORID_CP_MO_SUCCESS,
                                                        ST_Q_INSERT_FAIL_BLOCKNOTI,
                                                        nQueueNo,
                                                        TID_NO_SAVE,
                                                        LT_TRACE,
                                                        0
                                                    )
                                                }
                                            }
                                            
                                            // C 코드 LINE 1020-1024: InsqStat 호출
                                            smsQLib.InsqStat(
                                                stQItem,
                                                MESSAGE_MO,
                                                0,
                                                gServerID,
                                                MODULEID_GIPEVENT_C,
                                                SERVICEID_GIPEVENT,
                                                ERRORID_CP_MO_LIMIT,
                                                ST_GIP_MO_LIMIT,
                                                IF_NULL,
                                                TID_NO_SAVE,
                                                LT_BOTH,
                                                0
                                            )

                                            //	InsertHistory(gCallHistory, &stQitem, NULL, MODULE_GIPEVENT, MO_LIMIT, __LINE__, 0x00);
                                            
                                            // C 코드 LINE 1026: if(strlen(gstQItem.RcsTag) > 0) - RCS 태그 처리
                                            val rcsTag = QItemServiceUtil.byteArrayToKString(gstQItem.RcsTag)
                                            
                                            if (rcsTag.isNotEmpty()) {
                                                // C 코드 LINE 1028-1048: RCS TR 메시지 구성 및 큐 삽입
                                                val rcsQItem = QItemServiceUtil.qItemToMsgHdr(gstQItem)
                                                
                                                // C 코드 LINE 1032-1038: Dest와 Src 교환
                                                val tempCidRcs = rcsQItem.szCId.clone()
                                                System.arraycopy(rcsQItem.szSrcCId, 0, rcsQItem.szCId, 0, minOf(rcsQItem.szSrcCId.size, rcsQItem.szCId.size))
                                                System.arraycopy(tempCidRcs, 0, rcsQItem.szSrcCId, 0, minOf(tempCidRcs.size, rcsQItem.szSrcCId.size))
                                                
                                                val tempMinRcs = rcsQItem.szMinNo.clone()
                                                System.arraycopy(rcsQItem.szSrcMinNo, 0, rcsQItem.szMinNo, 0, minOf(rcsQItem.szSrcMinNo.size, rcsQItem.szMinNo.size))
                                                System.arraycopy(tempMinRcs, 0, rcsQItem.szSrcMinNo, 0, minOf(tempMinRcs.size, rcsQItem.szSrcMinNo.size))
                                                
                                                // C 코드 LINE 1040-1046: RCS TR 메시지 설정
                                                rcsQItem.usMsgCode = QTYPE_SM_REQ.toShort()
                                                rcsQItem.usMsgSubCode = SUB_QTYPE_RCS_TR.toShort()
                                                rcsQItem.ucTermType = TERM_TYPE_KOR.code.toByte()
                                                rcsQItem.ucDataEncoding = DCS_TYPE_KSC5601
                                                rcsQItem.nVldPrd = 43200
                                                rcsQItem.RcsResult = RCS_RESULT_INCALIDDST.toShort()
                                                
                                                val rcsQueueNo = 0
                                                smsQLib.InsertIntoSmsQnQNo(rcsQItem, rcsQueueNo)
                                            }
                                            
                                            return@measureNanoTime // C 코드 LINE 1050: return 1
                                        }
                                        
                                        // C 코드 LINE 1052: SendTcpMsgSimpleGetQ 호출 (MO 메시지 전송)
                                        // HTTP POST로 CP 서버에 MO 메시지 전송
                                        val moSendSuccess = sendMoMessageToCp(gstQItemTrans, entity, gstQItem)
                                        
                                        if (moSendSuccess) {
                                            witcomLog.p_write(
                                                Level.DEBUG,
                                                String.format(
                                                    "MO Message Send Success : SrcCId(%s),SrcCallNo(%s),DestCId(%s),DestCallNo(%s)",
                                                    gstQItemTrans.srcCid,
                                                    gstQItemTrans.srcMinNo,
                                                    gstQItemTrans.destCid,
                                                    gstQItemTrans.destMinNo
                                                )
                                            )
                                        }
                                        
                                        // C 코드 LINE 1053: PrintHexa 호출 (로깅)
                                        QItemServiceUtil.printQItem3(gstQItem, witcomLog)
                                    }
                                    
                                    // ========== MO-TR 결과 전송 처리 (SM_REQ_TRANS_RESULT) ==========
                                    // C 코드 참고: GIPEVENT_c.c LINE 1309-1470 (MakePacketFromQItem)
                                    // C 코드 참고: GIPEVENT_c.c LINE 1056-1074 (실제 전송)
                                    SM_REQ_TRANS_RESULT -> {
                                        // C 코드 LINE 1313: ptrTransRes = (SMREQTRANSRESPTR)(ptrMsgHdr->ucData)
                                        // C 코드 LINE 1314: ptrTransRes->ucMsgStatus = ptrQItem->ucMsgStatus
                                        // C 코드 LINE 1444: ptrTransRes->ucGSMErrCode = ptrQItem->ucGSMErrCode
                                        // C 코드 LINE 1445: memcpy(ptrTransRes->ucMsgId, ptrQItem->ucMsgId, QITEM_SIZE_MSGID)
                                        
                                        // HTTP POST로 CP 서버에 MO-TR 결과 전송
                                        val moTrSendSuccess = sendMoTrToCp(gstQItem, gstQItemTrans, entity)
                                        
                                        if (moTrSendSuccess) {
                                            witcomLog.p_write(
                                                Level.INFO,
                                                String.format(
                                                    "MO-TR Result Send Success : SrcCId(%s),SrcCallNo(%s),DestCId(%s),DestCallNo(%s),MsgStatus(%d),MsgId(%s)",
                                                    gstQItemTrans.srcCid,
                                                    gstQItemTrans.srcMinNo,
                                                    gstQItemTrans.destCid,
                                                    gstQItemTrans.destMinNo,
                                                    gstQItem.ucMsgStatus.toInt(),
                                                    QItemServiceUtil.byteArrayToKString(gstQItem.ucMsgId)
                                                )
                                            )
                                            
                                            // C 코드 LINE 1066-1072: InsqStat 호출 (성공/실패에 따라)
                                            val msgStatus = gstQItem.ucMsgStatus.toInt()
                                            val isSuccess = (msgStatus == 2) || 
                                                          (gstQItem.szFree2[0].toInt() != SM_STATE_MRMSPAM && msgStatus == SM_STATE_SPAMERR)
                                            
                                            smsQLib.InsqStat(
                                                gstQItem,
                                                MESSAGE_TR,
                                                0,
                                                gServerID,
                                                MODULEID_GIPEVENT_C,
                                                SERVICEID_GIPEVENT,
                                                if (isSuccess) ERRORID_CP_TR_SUCCESS else ERRORID_CP_TR_FAIL,
                                                ST_GIPALL_MTTR_SEND_OK,
                                                IF_NULL,
                                                TID_NO_SAVE,
                                                LT_BOTH,
                                                0
                                            )
                                        } else {
                                            witcomLog.p_write(
                                                Level.ERROR,
                                                String.format(
                                                    "MO-TR Result Send Fail : SrcCId(%s),SrcCallNo(%s),DestCId(%s),DestCallNo(%s),MsgStatus(%d),MsgId(%s)",
                                                    gstQItemTrans.srcCid,
                                                    gstQItemTrans.srcMinNo,
                                                    gstQItemTrans.destCid,
                                                    gstQItemTrans.destMinNo,
                                                    gstQItem.ucMsgStatus.toInt(),
                                                    QItemServiceUtil.byteArrayToKString(gstQItem.ucMsgId)
                                                )
                                            )
                                        }
                                        
                                        // C 코드 LINE 1053: PrintHexa 호출 (로깅)
                                        QItemServiceUtil.printQItem3(gstQItem, witcomLog)
                                    }
                                    
                                    else -> {
                                        val formatted =
                                            String.format(
                                                "MakePacketFromQItem : Invalid Message SubCode(%s)",
                                                gstQItemTrans.msgSubCode
                                            )
                                        witcomLog.p_write(Level.ERROR, formatted)
                                    }
                            }
                        }
                        delay(50L)
                    }
                    delay(2000L)
                }
            }
        }
        //        coroutineCacheCheckPool.cancel()
    }

    /**
     * CP 서버로 TR 결과를 HTTP POST로 전송합니다 (평문 전송)
     * @param responseRenewVO 전송할 TR 결과 데이터
     * @param access CP 접속 정보 (URL, 재시도 설정 등)
     * @return 전송 성공 여부
     */
    suspend fun sendTrResultToCp(
        responseRenewVO: ResponseRenewVO,
        access: GipHttpAccessEntity
    ): Boolean {
        val retryCount = access.rc.takeIf { it != 0 } ?: 1
        val timeoutSeconds = access.tc.takeIf { it != 0 } ?: 30

        repeat(retryCount) { attempt ->
            try {
                witcomLog.p_write(
                    Level.DEBUG,
                    String.format(
                        "🔸 CP SEND 시도 ${attempt + 1}/${retryCount}: msgId(%s), cpUrl(%s)",
                        responseRenewVO.msgId,
                        access.cpUrl
                    )
                )

                val httpClient =
                    HttpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutSeconds * 1000)
                        .responseTimeout(Duration.ofSeconds(timeoutSeconds.toLong()))

                // cpUrl에서 호스트와 경로 분리 (예: http://localhost:8500/sms/send/result)
                val uri = URI(access.cpUrl)
                val baseUrl =
                    "${uri.scheme}://${uri.host}${if (uri.port != -1) ":${uri.port}" else ""}"
                val path = uri.path

                val client =
                    WebClient.builder()
                        .baseUrl(baseUrl)
                        .defaultHeader(
                            HttpHeaders.CONTENT_TYPE,
                            MediaType.APPLICATION_JSON_VALUE
                        )
                        .clientConnector(ReactorClientHttpConnector(httpClient))
                        .build()

                val response =
                    client.post()
                        .uri(path)
                        .bodyValue(responseRenewVO)
                        .retrieve()
                        .toBodilessEntity()
                        .block()

                if (response?.statusCode?.is2xxSuccessful == true) {
                    witcomLog.p_write(
                        Level.INFO,
                        String.format(
                            "CP SEND 성공: msgId(%s), statusCode(%s)",
                            responseRenewVO.msgId,
                            response.statusCode
                        )
                    )
                    return true
                } else {
                    witcomLog.p_write(
                        Level.WARN,
                        String.format(
                            "CP SEND 실패 (응답 실패): msgId(%s), statusCode(%s)",
                            responseRenewVO.msgId,
                            response?.statusCode
                        )
                    )
                }
            } catch (e: Exception) {
                witcomLog.p_write(
                    Level.ERROR,
                    String.format(
                        "CP SEND 중 예외 발생: msgId(%s), error(%s)",
                        responseRenewVO.msgId,
                        e.message
                    )
                )
                log.error("CP SEND 예외 발생 재전송 시도", e)
            }

            // 재시도 전 대기 (마지막 시도가 아닌 경우)
            if (attempt < retryCount - 1) {
                delay(timeoutSeconds.toLong() * 1000)
            }
        }

        witcomLog.p_write(
            Level.ERROR,
            String.format(
                "CP SEND 최종 실패: msgId(%s), cpUrl(%s), 재시도 횟수(%s)",
                responseRenewVO.msgId,
                access.cpUrl,
                retryCount
            )
        )
        return false
    }

    suspend fun sendMOResult(trResultRequest: TrResultRequest, it: GipHttpAccessEntity): Boolean {
        repeat(it.rc.takeIf { it != 0 } ?: 1) { attempt ->
            try {
                log.info("MO 결과 전송 시도 ${attempt + 1}: ${trResultRequest.msgId}")

                val success = mockHttpSend(trResultRequest, it)

                if (success) {
                    log.info("MO 결과 전송 성공: ${trResultRequest.msgId}")
                    return true
                } else {
                    log.warn("MO 결과 전송 실패 (응답 실패): ${trResultRequest.msgId}")
                }
            } catch (e: Exception) {
                log.error("MO 결과 전송 중 예외 발생: ${e.message}")
            }
            delay(it.tc.toLong() * 1000) // 재시도 전 잠시 대기
        }

        log.error("MO 결과 전송 최종 실패: ${trResultRequest.msgId}")
        return false
    }

    // Mock: 실제 HTTP 요청 대체용 함수
    fun mockHttpSend(trResultRequest: TrResultRequest, access: GipHttpAccessEntity): Boolean {
        val httpClient =
            HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, access.tc * 1000) // 연결 타임아웃

        val client =
            WebClient.builder()
                .baseUrl(access.cpUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(ReactorClientHttpConnector(httpClient))
                .build()

        val data = trResultRequest.data
        data.destCID =
            EncryptionExample.encryptWithKey(data.destCID, access.aesKeyBase64, access.ivBase64)
        data.destCallNo =
            EncryptionExample.encryptWithKey(
                data.destCallNo,
                access.aesKeyBase64,
                access.ivBase64
            )
        data.callback =
            EncryptionExample.encryptWithKey(
                data.callback,
                access.aesKeyBase64,
                access.ivBase64
            )

        if (access.billType == "0") {
            log.info("무료문자 건입니다. 발송처리 없이 성공처리 합니다.")
            return try {
                val response =
                    client.post()
                        .uri("/demo/send/mo")
                        .bodyValue(data)
                        .retrieve()
                        .toBodilessEntity()
                        .block()

                response?.statusCode?.is2xxSuccessful == true
            } catch (e: Exception) {
                log.error("❌ mockHttpSend 요청 중 예외 발생: ${e.message}")
                false
            }
        } else if (access.billType == "1") {

            log.info("유료문자 건입니다. 발송처리 합니다.")
            when (access.msgType) {
                "1" -> {
                    /*MO 처리*/
                    return try {
                        val response =
                            client.post()
                                .uri("/demo/send/mo")
                                .bodyValue(data)
                                .retrieve()
                                .toBodilessEntity()
                                .block()

                        if (response?.statusCode?.is2xxSuccessful == true) {
                            // HTTP 성공시
                            log.info("✅ 여기서 과금처리 로직 수행: ${trResultRequest.msgId}")
                            true
                        }
                        false
                    } catch (e: Exception) {
                        log.error("❌ mockHttpSend 요청 중 예외 발생: ${e.message}")
                        false
                    }
                }

                "4" -> {
                    /*MO-TR 처리*/
                    return try {
                        val response =
                            client.post()
                                .uri("/demo/send/mo")
                                .bodyValue(data)
                                .retrieve()
                                .toBodilessEntity()
                                .block()

                        if (response?.statusCode?.is2xxSuccessful == true) {
                            // HTTP 성공시
                            log.info("✅ TR 정보 검증 수행: ${trResultRequest.msgId}")

                            /*여기서 과금처리 로직 수행*/
                            response?.body?.let { log.info("✅ 여기서 과금처리 로직 수행") }

                            true
                        }
                        false
                    } catch (e: Exception) {
                        log.error("❌ mockHttpSend 요청 중 예외 발생: ${e.message}")
                        false
                    }
                }

                else -> {
                    log.warn("알 수 없는 메시지 타입")
                    return false
                }
            }
        }
        return false
    }
    
    /**
     * C 코드의 CheckLimitMO 함수와 동일한 동작을 수행합니다.
     * MO 메시지 한도 체크를 수행합니다.
     * 
     * @param msgHdr SMReqTransResult (GIMSGHDR에 해당)
     * @param entity GipHttpMoAccessEntity
     * @param witcomLog WitcomLog
     * @return true: 한도 초과, false: 정상
     * 
     * C 코드 참고: GIPEVENT_c.c LINE 4033-4050 (CheckLimitMO 함수)
     * C 코드 참고: GIDBLib.c LINE 4646-4772 (GetLimitCheck 함수)
     * C 코드 참고: GIDBLib.c LINE 4782-4879 (CheckLimitMdn 함수)
     */
    private fun checkLimitMO(
        msgHdr: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        witcomLog: WitcomLog
    ): Boolean {
        // C 코드 LINE 4038: if(gMOLimit == TRUE) 확인
        // GetLimitCheck 로직: LIMIT_CHECK_FLAG == 'Y' && BILL_TYPE == BILLTYPE_SRC(2)이면 gMOLimit = TRUE
        val gMOLimit = (entity.limitCheckFlag == "Y" || entity.limitCheckFlag == "y") && 
                       entity.billType == BILLTYPE_SRC.toString()
        
        if (!gMOLimit) {
            // C 코드 LINE 4044-4047: gMOLimit가 FALSE이면 한도 체크 안 함
            return false
        }
        
        // C 코드 LINE 4040: MDN 생성 (szSrcCId + uSrcCallNo)
        // C 코드: sprintf(szMdn,"%s%u",ptrGIMsgHdr->szSrcCId,ptrGIMsgHdr->uSrcCallNo);
        val szMdn = "${msgHdr.srcCid}${msgHdr.srcMinNo}"
        
        witcomLog.p_write(
            Level.DEBUG,
            String.format(
                "CheckLimitMO() MDN: %s, limitCheckFlag: %s, billType: %s",
                szMdn,
                entity.limitCheckFlag,
                entity.billType
            )
        )
        
        // C 코드 LINE 4041: CheckLimitMdn(szMdn) 호출
        // MSG_LIMIT_LIST 테이블에서 MDN으로 조회
        return try {
            val count = msgLimitListRepository.countByMdn(szMdn)
            
            // C 코드 LINE 4852-4859: count가 0이면 FALSE, 0이 아니면 TRUE
            if (count == 0L) {
                witcomLog.p_write(
                    Level.DEBUG,
                    String.format("CheckLimitMO() MDN(%s) not found in MSG_LIMIT_LIST", szMdn)
                )
                false // 한도 초과 아님
            } else {
                witcomLog.p_write(
                    Level.INFO,
                    String.format("CheckLimitMO() MDN(%s) found in MSG_LIMIT_LIST (count=%d)", szMdn, count)
                )
                true // 한도 초과
            }
        } catch (e: Exception) {
            // C 코드 LINE 4861-4874: DB 에러 처리
            witcomLog.p_write(
                Level.ERROR,
                String.format("CheckLimitMO() DB Error: MDN(%s), error(%s)", szMdn, e.message)
            )
            log.error("CheckLimitMO() DB Error: MDN($szMdn)", e)
            false // 에러 시 한도 초과로 처리하지 않음
        }
    }
    
    /**
     * CP 서버로 MO 메시지를 HTTP POST로 전송합니다.
     * rc, tc를 활용한 재시도 로직 포함
     * 
     * C 코드 참고: GIPEVENT_c.c LINE 1052 (SendTcpMsgSimpleGetQ)
     * - GIMSGHDR 헤더 + SMPPSIMPLE 데이터를 TCP로 전송하던 것을 HTTP POST로 변환
     * 
     * @param moResult SMReqTransResult (MO 메시지 데이터)
     * @param entity GipHttpMoAccessEntity (CP_URL, rc, tc 포함)
     * @param qItem QITEM (원본 큐 아이템)
     * @return 전송 성공 여부
     * 
     * rc, tc 처리:
     * - tc: connectionTimeout 시간 (초)
     * - rc = 0: 한 번만 요청 (재시도 없음)
     * - rc > 0: 그 횟수만큼 재시도 (총 rc+1번 시도)
     * - 성공하면 즉시 종료
     */
    private suspend fun sendMoMessageToCp(
        moResult: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        qItem: QITEM?
    ): Boolean {
        val maxRetryCount = entity.rc
        val connectionTimeoutSeconds = entity.tc
        
        // rc = 0인 경우 한 번만 요청
        val totalAttempts = if (maxRetryCount == 0) 1 else maxRetryCount + 1
        
        for (attempt in 1..totalAttempts) {
            val success = doSendMoMessage(moResult, entity, qItem, connectionTimeoutSeconds, attempt, totalAttempts)
            
            if (success) {
                // 성공하면 즉시 종료
                return true
            }
            
            // 마지막 시도가 아니면 재시도
            if (attempt < totalAttempts) {
                witcomLog.p_write(
                    Level.WARN,
                    String.format(
                        "MO 메시지 전송 재시도: 시도(%d/%d), srcCID(%s), destCID(%s), CP_URL(%s)",
                        attempt,
                        totalAttempts,
                        moResult.srcCid,
                        moResult.destCid,
                        entity.cpUrl
                    )
                )
            }
        }
        
        // 모든 시도 실패
        witcomLog.p_write(
            Level.ERROR,
            String.format(
                "MO 메시지 전송 최종 실패: 총 시도(%d), srcCID(%s), destCID(%s), destCallNo(%s), CP_URL(%s)",
                totalAttempts,
                moResult.srcCid,
                moResult.destCid,
                moResult.destMinNo,
                entity.cpUrl
            )
        )
        return false
    }
    
    /**
     * 실제 MO 메시지 HTTP POST 전송 수행
     * 
     * @param moResult SMReqTransResult (MO 메시지 데이터)
     * @param entity GipHttpMoAccessEntity (CP_URL 포함)
     * @param qItem QITEM (원본 큐 아이템)
     * @param connectionTimeoutSeconds connectionTimeout 시간 (초)
     * @param attempt 현재 시도 횟수
     * @param totalAttempts 총 시도 횟수
     * @return 전송 성공 여부
     */
    private suspend fun doSendMoMessage(
        moResult: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        qItem: QITEM?,
        connectionTimeoutSeconds: Int,
        attempt: Int,
        totalAttempts: Int
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // ResponseMO DTO 생성
                val responseMO = ResponseMO().apply {
                    msgVerId = moResult.msgVerId
                    encFlag = 0 // 암호화 플래그 (필요시 entity 설정에서 가져오기)
                    
                    data = MODataBody().apply {
                        // GIMSGHDR 헤더 정보
                        srcCID = moResult.srcCid
                        srcCallNo = moResult.srcMinNo
                        srcAddrRsv = 0 // SrcAddrRsv int (0)
                        destCID = moResult.destCid
                        destCallNo = moResult.destMinNo
                        destAddrRsv = 0 // DestAddrRsv int (0)
                        msgCode = moResult.msgCode
                        // C 코드 LINE 1526: SendTcpMsgSimpleGetQ에서 SM_REQ_SEND -> SM_REQ_SIMPLE로 변경
                        msgSubCode = SM_REQ_SIMPLE.toShort()
                        bodyDataLen = moResult.msgLen
                        // C 코드 LINE 1448: ptrMsgHdr->uMsgSerialNo = GetSerialNo(gChildId)
                        msgSeqNo = if (qItem != null && qItem.uMsgSerialNo > 0) qItem.uMsgSerialNo.toInt() else 0
                        termtype = moResult.termType.toString()
                        dataType = 0.toByte() // DataType char (0)
                        dataEncoding = moResult.dataEncoding.toInt()
                        
                        // concatenate 정보 (QITEM에서 가져오기)
                        concatenateflag = if (qItem != null) {
                            (qItem.ucRsv[0].toInt() and 0xFF).toString()
                        } else {
                            "0"
                        }
                        concatenateInfo = if (qItem != null) {
                            (qItem.ucRsv[1].toInt() and 0xFF).toString()
                        } else {
                            "0"
                        }
                        
                        // rsv4Protocol 변환
                        rsv4Protocol = moResult.rsv4Protocol.map { value ->
                            // Rsv4ProtocolItem 생성자에 Integer 값을 직접 전달
                            Rsv4ProtocolItem(value)
                        }
                        
                        // SMPPSIMPLE 데이터 (MO 메시지 전용)
                        nVldPrd = moResult.vldPrd
                        ucRgtDlvFlg = moResult.rgtDlvFlg
                        callback = moResult.callback
                        msgLen = moResult.msgLen.toByte()
                        msg = moResult.msg
                        orgMsgTotalLen = if (qItem != null && qItem.uOrgMsgLen > 0) {
                            qItem.uOrgMsgLen.toByte()
                        } else {
                            1.toByte() // 기본값
                        }
                    }
                }
                
                // 암호화 적용 (entity 설정에 따라)
                if (entity.aesKeyBase64 != null && entity.ivBase64 != null) {
                    //검증단계에서 일단 보류하자.
//                    val data = responseMO.data
//                    data.destCID = EncryptionExample.encryptWithKey(
//                        data.destCID,
//                        entity.aesKeyBase64,
//                        entity.ivBase64
//                    )
//                    data.destCallNo = EncryptionExample.encryptWithKey(
//                        data.destCallNo,
//                        entity.aesKeyBase64,
//                        entity.ivBase64
//                    )
//                    data.callback = EncryptionExample.encryptWithKey(
//                        data.callback,
//                        entity.aesKeyBase64,
//                        entity.ivBase64
//                    )
                    // 메시지 내용도 암호화할지 결정 (필요시)
                    // data.msg = EncryptionExample.encryptWithKey(...)
                }
                
                // WebClient 생성 (tc를 connectionTimeout으로 사용)
                val connectionTimeoutMillis = connectionTimeoutSeconds * 1000L
                val client = WebClient.builder()
                    .baseUrl(entity.cpUrl)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .clientConnector(
                        ReactorClientHttpConnector(
                            HttpClient.create()
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeoutMillis?.toInt())
                                .responseTimeout(Duration.ofSeconds(connectionTimeoutSeconds.toLong()))
                        )
                    )
                    .build()
                
                // HTTP POST 전송
                val response = client.post()
                    .uri(URI(entity.cpUrl))
                    .bodyValue(responseMO)
                    .retrieve()
                    .toBodilessEntity()
                    .awaitSingle()
                
                val success = response.statusCode.is2xxSuccessful
                
                if (success) {
                    witcomLog.p_write(
                        Level.INFO,
                        String.format(
                            "MO 메시지 전송 성공: 시도(%d/%d), srcCID(%s), srcCallNo(%s), destCID(%s), destCallNo(%s), CP_URL(%s), timeout(%d초)",
                            attempt,
                            totalAttempts,
                            moResult.srcCid,
                            moResult.srcMinNo,
                            moResult.destCid,
                            moResult.destMinNo,
                            entity.cpUrl,
                            connectionTimeoutSeconds
                        )
                    )
                } else {
                    witcomLog.p_write(
                        Level.ERROR,
                        String.format(
                            "MO 메시지 전송 실패: 시도(%d/%d), HTTP Status(%s), srcCID(%s), srcCallNo(%s), destCID(%s), destCallNo(%s), CP_URL(%s), timeout(%d초)",
                            attempt,
                            totalAttempts,
                            response.statusCode,
                            moResult.srcCid,
                            moResult.srcMinNo,
                            moResult.destCid,
                            moResult.destMinNo,
                            entity.cpUrl,
                            connectionTimeoutSeconds
                        )
                    )
                }
                
                success
            } catch (e: Exception) {
                witcomLog.p_write(
                    Level.ERROR,
                    String.format(
                        "MO 메시지 전송 중 예외 발생: 시도(%d/%d), srcCID(%s), srcCallNo(%s), destCID(%s), destCallNo(%s), CP_URL(%s), timeout(%d초), error(%s)",
                        attempt,
                        totalAttempts,
                        moResult.srcCid,
                        moResult.srcMinNo,
                        moResult.destCid,
                        moResult.destMinNo,
                        entity.cpUrl,
                        connectionTimeoutSeconds,
                        e.message
                    )
                )
                log.error("MO 메시지 전송 중 예외 발생: CP_URL(${entity.cpUrl}), 시도($attempt/$totalAttempts)", e)
                false
            }
        }
    }

    /**
     * CP 서버로 MO-TR 결과를 HTTP POST로 전송합니다.
     * rc, tc를 활용한 재시도 로직 포함
     * 
     * C 코드 참고: GIPEVENT_c.c LINE 1309-1470 (MakePacketFromQItem - SM_REQ_TRANS_RESULT 케이스)
     * - LINE 1314: ptrTransRes->ucMsgStatus = ptrQItem->ucMsgStatus (MsgStatus 설정)
     * - LINE 1444: ptrTransRes->ucGSMErrCode = ptrQItem->ucGSMErrCode (Rsv 설정)
     * - LINE 1445: memcpy(ptrTransRes->ucMsgId, ptrQItem->ucMsgId, QITEM_SIZE_MSGID) (MsgId 복사)
     * 
     * @param qItem QITEM (원본 큐 아이템, ucMsgStatus, ucGSMErrCode, ucMsgId 포함)
     * @param moResult SMReqTransResult (헤더 정보)
     * @param entity GipHttpMoAccessEntity (CP_URL, rc, tc 포함)
     * @return 전송 성공 여부
     * 
     * rc, tc 처리:
     * - tc: connectionTimeout 시간 (초)
     * - rc = 0: 한 번만 요청 (재시도 없음)
     * - rc > 0: 그 횟수만큼 재시도 (총 rc+1번 시도)
     * - 성공하면 즉시 종료
     */
    private suspend fun sendMoTrToCp(
        qItem: QITEM,
        moResult: SMReqTransResult,
        entity: GipHttpMoAccessEntity
    ): Boolean {
        val maxRetryCount = entity.rc
        val connectionTimeoutSeconds = entity.tc
        
        // rc = 0인 경우 한 번만 요청
        val totalAttempts = if (maxRetryCount == 0) 1 else maxRetryCount + 1
        
        for (attempt in 1..totalAttempts) {
            val success = doSendMoTr(qItem, moResult, entity, connectionTimeoutSeconds, attempt, totalAttempts)
            
            if (success) {
                // 성공하면 즉시 종료
                return true
            }
            
            // 마지막 시도가 아니면 재시도
            if (attempt < totalAttempts) {
                witcomLog.p_write(
                    Level.WARN,
                    String.format(
                        "MO-TR 결과 전송 재시도: 시도(%d/%d), srcCID(%s), destCID(%s), msgId(%s), CP_URL(%s)",
                        attempt,
                        totalAttempts,
                        moResult.srcCid,
                        moResult.destCid,
                        QItemServiceUtil.byteArrayToKString(qItem.ucMsgId),
                        entity.cpUrl
                    )
                )
            }
        }
        
        // 모든 시도 실패
        witcomLog.p_write(
            Level.ERROR,
            String.format(
                "MO-TR 결과 전송 최종 실패: 총 시도(%d), srcCID(%s), destCID(%s), destCallNo(%s), msgStatus(%d), msgId(%s), CP_URL(%s)",
                totalAttempts,
                moResult.srcCid,
                moResult.destCid,
                moResult.destMinNo,
                qItem.ucMsgStatus.toInt(),
                QItemServiceUtil.byteArrayToKString(qItem.ucMsgId),
                entity.cpUrl
            )
        )
        return false
    }
    
    /**
     * 실제 MO-TR 결과 HTTP POST 전송 수행
     * 
     * @param qItem QITEM (원본 큐 아이템)
     * @param moResult SMReqTransResult (헤더 정보)
     * @param entity GipHttpMoAccessEntity (CP_URL 포함)
     * @param connectionTimeoutSeconds connectionTimeout 시간 (초)
     * @param attempt 현재 시도 횟수
     * @param totalAttempts 총 시도 횟수
     * @return 전송 성공 여부
     */
    private suspend fun doSendMoTr(
        qItem: QITEM,
        moResult: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        connectionTimeoutSeconds: Int,
        attempt: Int,
        totalAttempts: Int
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // RequestMOTR DTO 생성
                val requestMOTR = RequestMOTR().apply {
                    msgVerId = moResult.msgVerId
                    encFlag = 0 // 암호화 플래그 (필요시 entity 설정에서 가져오기)
                    
                    data = MOTRDataBody().apply {
                        // GIMSGHDR 헤더 정보
                        srcCID = moResult.srcCid
                        srcCallNo = moResult.srcMinNo
                        srcAddrRsv = 0 // SrcAddrRsv int (0)
                        destCID = moResult.destCid
                        destCallNo = moResult.destMinNo
                        destAddrRsv = 0 // DestAddrRsv int (0)
                        msgCode = SM_REQ_MO.toShort() // C 코드 LINE 1130: MSG_CODE_SM_REQ = 11
                        msgSubCode = SM_REQ_SEND.toShort() // C 코드 LINE 1309: SM_REQ_TRANS_RESULT
                        bodyDataLen = 11 // MsgStatus(1) + Rsv(1) + MsgId(9) = 11
                        // C 코드 LINE 1448: ptrMsgHdr->uMsgSerialNo = GetSerialNo(gChildId)
                        msgSeqNo = if (qItem.uMsgSerialNo > 0) qItem.uMsgSerialNo.toInt() else 0
                        termtype = moResult.termType.toString()
                        dataType = 0.toByte() // DataType char (0)
                        dataEncoding = moResult.dataEncoding.toInt()
                        
                        // concatenate 정보 (QITEM에서 가져오기)
                        concatenateflag = (qItem.ucRsv[0].toInt() and 0xFF).toString()
                        concatenateInfo = (qItem.ucRsv[1].toInt() and 0xFF).toString()
                        
                        // rsv4Protocol 변환
                        rsv4Protocol = moResult.rsv4Protocol.map { value ->
                            Rsv4ProtocolItem(value).apply {
                                data = if (value in 0..65535) {
                                    value.toChar()
                                } else {
                                    (value and 0xFFFF).toChar()
                                }
                            }
                        }
                        
                        // TIME 필드 설정 (YYMMDDHHMM 형식)
                        val now = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                        time = String.format(
                            "%02d%02d%02d%02d%02d",
                            now.year % 100,
                            now.monthValue,
                            now.dayOfMonth,
                            now.hour,
                            now.minute
                        )
                        
                        // Body 필드 (MO-TR 전송용)
                        // C 코드 LINE 1314: ptrTransRes->ucMsgStatus = ptrQItem->ucMsgStatus
                        msgStatus = qItem.ucMsgStatus
                        
                        // C 코드 LINE 1444: ptrTransRes->ucGSMErrCode = ptrQItem->ucGSMErrCode
                        rsv = qItem.ucGSMErrCode
                        
                        // C 코드 LINE 1445: memcpy(ptrTransRes->ucMsgId, ptrQItem->ucMsgId, QITEM_SIZE_MSGID)
                        msgId = QItemServiceUtil.byteArrayToKString(qItem.ucMsgId)
                    }
                }
                
                // 암호화 적용 (entity 설정에 따라)
                if (entity.aesKeyBase64 != null && entity.ivBase64 != null) {
                    // TODO: 필요시 암호화 로직 추가
                }
                
                // WebClient 생성 (tc를 connectionTimeout으로 사용)
                val connectionTimeoutMillis = connectionTimeoutSeconds * 1000L
                val client = WebClient.builder()
                    .baseUrl(entity.cpUrl)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .clientConnector(
                        ReactorClientHttpConnector(
                            HttpClient.create()
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeoutMillis?.toInt())
                                .responseTimeout(Duration.ofSeconds(connectionTimeoutSeconds.toLong()))
                        )
                    )
                    .build()

                // HTTP POST 전송
                val response = client.post()
                    .uri(URI(entity.cpUrl))
                    .bodyValue(requestMOTR)
                    .retrieve()
                    .toBodilessEntity()
                    .awaitSingle()
                
                val success = response.statusCode.is2xxSuccessful
                
                if (success) {
                    witcomLog.p_write(
                        Level.INFO,
                        String.format(
                            "MO-TR 결과 전송 성공: 시도(%d/%d), srcCID(%s), srcCallNo(%s), destCID(%s), destCallNo(%s), msgStatus(%d), msgId(%s), CP_URL(%s), timeout(%d초)",
                            attempt,
                            totalAttempts,
                            moResult.srcCid,
                            moResult.srcMinNo,
                            moResult.destCid,
                            moResult.destMinNo,
                            qItem.ucMsgStatus.toInt(),
                            QItemServiceUtil.byteArrayToKString(qItem.ucMsgId),
                            entity.cpUrl,
                            connectionTimeoutSeconds
                        )
                    )
                } else {
                    witcomLog.p_write(
                        Level.ERROR,
                        String.format(
                            "MO-TR 결과 전송 실패: 시도(%d/%d), HTTP Status(%s), srcCID(%s), srcCallNo(%s), destCID(%s), destCallNo(%s), msgStatus(%d), msgId(%s), CP_URL(%s), timeout(%d초)",
                            attempt,
                            totalAttempts,
                            response.statusCode,
                            moResult.srcCid,
                            moResult.srcMinNo,
                            moResult.destCid,
                            moResult.destMinNo,
                            qItem.ucMsgStatus.toInt(),
                            QItemServiceUtil.byteArrayToKString(qItem.ucMsgId),
                            entity.cpUrl,
                            connectionTimeoutSeconds
                        )
                    )
                }
                
                success
            } catch (e: Exception) {
                witcomLog.p_write(
                    Level.ERROR,
                    String.format(
                        "MO-TR 결과 전송 중 예외 발생: 시도(%d/%d), srcCID(%s), srcCallNo(%s), destCID(%s), destCallNo(%s), CP_URL(%s), timeout(%d초), error(%s)",
                        attempt,
                        totalAttempts,
                        moResult.srcCid,
                        moResult.srcMinNo,
                        moResult.destCid,
                        moResult.destMinNo,
                        entity.cpUrl,
                        connectionTimeoutSeconds,
                        e.message
                    )
                )
                log.error("MO-TR 결과 전송 중 예외 발생: CP_URL(${entity.cpUrl}), 시도($attempt/$totalAttempts)", e)
                false
            }
        }
    }

    // Mock: 실제 HTTP 요청 대체용 함수
    fun mockHttpSendTrListener(
        trResultRequest: TrResultRequest,
        access: GipHttpAccessEntity
    ): Boolean {
        val client =
            WebClient.builder()
                .baseUrl(access.cpUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build()

        // 복호화 적용
        val data = trResultRequest.data
        data.destCID =
            EncryptionExample.encryptWithKey(data.destCID, access.aesKeyBase64, access.ivBase64)
        data.destCallNo =
            EncryptionExample.encryptWithKey(
                data.destCallNo,
                access.aesKeyBase64,
                access.ivBase64
            )
        data.callback =
            EncryptionExample.encryptWithKey(
                data.callback,
                access.aesKeyBase64,
                access.ivBase64
            )

        return try {
            val response =
                client.post()
                    .uri("/demo/send/mo-tr")
                    .bodyValue(data)
                    .retrieve()
                    .toBodilessEntity()
                    .block()

            response?.statusCode?.is2xxSuccessful == true
        } catch (e: Exception) {
            log.error("mockHttpSend 요청 중 예외 발생: ${e.message}")
            false
        }
    }
    
    /**
     * 애플리케이션 종료 시 코루틴 스코프를 취소하여 리소스 정리
     */
    @PreDestroy
    fun cleanup() {
        log.info("SENDThreadPool cleanup: 코루틴 스코프 취소 중...")
        coroutineScope?.cancel()
        coroutineScope = null
    }
}
