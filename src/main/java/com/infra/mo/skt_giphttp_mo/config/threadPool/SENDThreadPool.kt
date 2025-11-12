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
import com.infra.mo.skt_giphttp_mo.config.application.LiveReloadCLibraryFile
import com.infra.mo.skt_giphttp_mo.config.application.PerformanceSettings
import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.CallInfoEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.CfgEtcEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpAccessEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.SpcodeEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.CallInfoRepository
import com.infra.mo.skt_giphttp_mo.dto.TrResultRequest
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseRenewVO
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseTR
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseTR.DataBody
import com.infra.mo.skt_giphttp_mo.dto.smsController.Rsv4ProtocolItem
import com.infra.mo.skt_giphttp_mo.utils.EncryptionExample
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil.QueueResult
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SM_REQ_TRANS_RESULT_PROCESSOR
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.impl.SmsQLibImpl
import io.netty.channel.ChannelOption
import java.net.URI
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import kotlin.system.measureNanoTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    @Qualifier("GipHttpAccessList")
    private val gipHttpAccessList: CopyOnWriteArrayList<GipHttpAccessEntity>,
    @Qualifier("CfgEtcMap") private val cfgEtcMap: HashMap<String, CfgEtcEntity>,
    @Qualifier("SpcodeMap") private val spcodeMap: ConcurrentHashMap<String, SpcodeEntity>,
    private val witcomLog: WitcomLog,
    private val performanceSettings: PerformanceSettings,
    private val context: ApplicationContext,
    private val smsQLibImpl: SmsQLibImpl,
    private val liveReloadCLibraryFile: LiveReloadCLibraryFile,
    private val callInfoRepository: CallInfoRepository,
    private val telePrefixRepository:
    com.infra.mo.skt_giphttp_mo.db.altibase.repository.TELEPrefixRepository
) {
    private val log: Logger = LoggerFactory.getLogger(SENDThreadPool::class.java)

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
                    "[ERROR] DBReadW2PCALLINFO() Altibase Error: msgIdServer(%s), destCallNo(%s), error(%s)",
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
                    "[DEBUG] CheckTelecom() TelePrefix[%s] DestCID[%s]",
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
                        "[DEBUG] CheckTelecom() SELECT TELE PREFIX[%s] SUCCESS!!",
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
                                "[DEBUG] CheckTelecom() SELECT TELE PREFIX[%s] SUCCESS!! (LIKE)",
                                szTelePre
                            )
                        )
                        ALTI_SUCCESS
                    } else {
                        witcomLog.p_write(
                            Level.WARN,
                            String.format(
                                "[WARNING] CheckTelecom() No TelePrefix[%s] in table of TELE_PREFIX",
                                szTelePre
                            )
                        )
                        ALTI_NODATA
                    }
                } else {
                    witcomLog.p_write(
                        Level.WARN,
                        String.format(
                            "[WARNING] CheckTelecom() No TelePrefix[%s] in table of TELE_PREFIX",
                            szTelePre
                        )
                    )
                    ALTI_NODATA
                }
            }
        } catch (e: Exception) {
            witcomLog.p_write(
                Level.ERROR,
                String.format("[ERROR] CheckTelecom() SELECT TELE_PREFIX FAIL :: %s", e.message)
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
                    "[DEBUG] GetTelecom_New() TelePrefix(%s) Prefix(%s)",
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
                        "[DEBUG] GetTelecom_New() SELECT TELECOM[%d] SUCCESS!!",
                        telecom
                    )
                )
                telecom
            } else {
                witcomLog.p_write(
                    Level.WARN,
                    String.format(
                        "[WARNING] GetTelecom_New() No prefix <%s> in table of TELE_PREFIX",
                        szPrefix
                    )
                )
                ALTI_NODATA
            }
        } catch (e: Exception) {
            witcomLog.p_write(
                Level.ERROR,
                String.format(
                    "[ERROR] GetTelecom_New() SELECT TELE_PREFIX FAIL Prefix(%s) :: %s",
                    if (destCallNo.length >= 4) destCallNo.substring(0, 4) else destCallNo,
                    e.message
                )
            )
            ALTI_FAIL
        }
    }

    /** TODO Enqueue All 시뮬레이션 및 비지니스 로직을 수행합니다. */
    fun executeEventHandlerTask(poolSize: Int) {
        val dispatcher = Executors.newFixedThreadPool(poolSize).asCoroutineDispatcher()
        val coroutineCacheCheckPool = CoroutineScope(dispatcher)
        val isActive = true
        val partitionedList =
            gipHttpAccessList.chunked((gipHttpAccessList.size + poolSize - 1) / poolSize)
        val gServerID = System.getenv("SMSS_NO").trim().toInt()
        println("🔹 Partitioned result:")
        partitionedList.forEachIndexed { index, list ->
            println("  Worker[$index] -> ${list.joinToString()}")
        }
        repeat(poolSize) { index ->
            val assignedCids = partitionedList.getOrNull(index) ?: emptyList()
            coroutineCacheCheckPool.launch {
                while (isActive) {
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
                        val gSMS_MANAGER: Int = dbReadCfgSpcode(cid)

                        // 큐에서 데이터 가져오기
                        val formatted =
                            String.format("[GIPHTTP_SEND_TR] Get CP Qno(%s) ==", queueNo)
                        witcomLog.p_write(Level.DEBUG, formatted)
                        val gstQResultObj: QueueResult =
                            QItemServiceUtil.fetchAndConvert2(queueNo, smsQLib, witcomLog) as
                                    QueueResult // -> ### Print QITEM
                        val gstQItemTrans = gstQResultObj.result
                        var gstQItem = gstQResultObj.qItem as? QITEM
                        gstQItem = gstQItem?.let { QItemServiceUtil.qItemToMsgHdr(it) }

                        // TR 처리
                        latencyNs = measureNanoTime {
                            gstQItemTrans?.let { // 불러온 값이 null이 아니라면
                                when (gstQItemTrans.msgSubCode.toInt()) {
                                    SM_REQ_TRANS_RESULT -> {
                                        // C 코드 LINE 770-773: 메시지 변환 및 전송 준비
                                        qItem =
                                            SM_REQ_TRANS_RESULT_PROCESSOR.checking(
                                                gstQItemTrans,
                                                qItem,
                                                entity,
                                                witcomLog,
                                                cfgEtcMap
                                            ) as
                                                    QITEM.ByReference
                                        qItem.uMsgSerialNo =
                                            QItemServiceUtil.getSerialNo(entity.logNo.toInt())

                                        witcomLog.p_write(
                                            Level.DEBUG,
                                            String.format(
                                                "GetSerialNo() : gSerialNo(%s)",
                                                qItem.uMsgSerialNo
                                            )
                                        )

                                        val smReqTransResult: SMReqTransResult =
                                            QItemServiceUtil.fetchAndConvert(qItem)
                                        val dataBody: DataBody = DataBody()
                                        dataBody.srcCID = smReqTransResult.srcCid
                                        dataBody.srcCallNo = smReqTransResult.srcMinNo
                                        dataBody.destCID = smReqTransResult.destCid
                                        dataBody.destCallNo = smReqTransResult.destMinNo
                                        dataBody.msgCode = smReqTransResult.msgCode
                                        dataBody.msgSubCode = smReqTransResult.msgSubCode
                                        dataBody.termtype = smReqTransResult.termType.toString()
                                        dataBody.dataEncoding =
                                            smReqTransResult.dataEncoding.toInt()
                                        val converted =
                                            smReqTransResult.rsv4Protocol.map { value ->
                                                Rsv4ProtocolItem().apply {
                                                    data = value.toChar()
                                                }
                                            }
                                        dataBody.rsv4Protocol = converted

                                        var responseTRTemp: ResponseTR = ResponseTR()
                                        responseTRTemp.msgVerId = DEFINE_GIPVERID_510
                                        responseTRTemp.encFlag = smReqTransResult.msgStatus.toInt()
                                        responseTRTemp.data = dataBody
                                        val responseRenewVO =
                                            ResponseRenewVO().apply {
                                                status = HttpStatus.OK.value()
                                                //
                                                // msgStatus =
                                                // smReqTransResult.msgStatus.toInt() <<- 성공이면
                                                // 필요가 없음
                                                msgId = smReqTransResult.msgId
                                                serverTime =
                                                    LocalDateTime.now(
                                                        ZoneId.of("Asia/Seoul")
                                                    )
                                                responseTR = responseTRTemp
                                            }
                                        val gServerID = System.getenv("SMSS_NO").trim().toInt()
                                        val formatted =
                                            String.format(
                                                """
                                               SMSS_NO#$gServerID
                                               ==================HTTP PACKET DATA [MT-TR]=================== 
                                               ${responseRenewVO.toString()}
                                               ==================HTTP PACKET DATA END======================
                                            """.trimIndent()
                                            )
                                        witcomLog.p_write(Level.DEBUG, formatted)
                                        // 출력 확인 후 HTTP 전송 및 응답 정보 출력

                                        // ========== C 코드 실행 순서 ==========
                                        // 1. LINE 770-772: 메시지 변환 및 전송 준비 (완료)
                                        // 2. LINE 773: SendTcpMsg 호출 (CP 서버로 HTTP POST 전송)
                                        val sendSuccess = sendTrResultToCp(responseRenewVO, entity)

                                        // 3. LINE 776-793: SendTcpMsg 호출 후 InsqStat 분기
                                        if (gstQItemTrans.msgStatus.toInt() == 2 ||
                                            (gstQItemTrans.szFree2[0].toInt() !=
                                                    SM_STATE_MRMSPAM &&
                                                    gstQItemTrans.msgStatus.toInt() ==
                                                    SM_STATE_SPAMERR)
                                        ) {
                                            //InsqStat 호출 필요 (C 코드 LINE 777-789)
                                            val resultInsqStat = smsQLib.InsqStat(
                                                gstQItem,
                                                MESSAGE_TR,
                                                0,
                                                gServerID,
                                                MODULEID_GIPALL_C,
                                                SERVICEID_GIPALL,
                                                ERRORID_CP_TR_SUCCESS,
                                                ST_GIPALL_MTTR_SEND_OK,
                                                IF_NULL,
                                                TID_NO_SAVE,
                                                LT_BOTH,
                                                0
                                            )

                                            witcomLog.p_write(
                                                Level.DEBUG,
                                                String.format(
                                                    "InsqStat ID --> [%s] resultInsqStat --> [%s]",
                                                    ERRORID_CP_TR_SUCCESS,
                                                    resultInsqStat
                                                )
                                            )
                                        } else {
                                            //InsqStat 호출 필요 (C 코드 LINE 791-792)
                                            smsQLib.InsqStat(
                                                gstQItem,
                                                MESSAGE_TR,
                                                0,
                                                gServerID,
                                                MODULEID_GIPALL_C,
                                                SERVICEID_GIPALL,
                                                ERRORID_CP_TR_FAIL,
                                                ST_GIPALL_MTTR_SEND_OK,
                                                IF_NULL,
                                                TID_NO_SAVE,
                                                LT_BOTH,
                                                0
                                            )
                                            witcomLog.p_write(
                                                Level.DEBUG,
                                                String.format(
                                                    "InsqStat ID --> [%s]",
                                                    ERRORID_CP_TR_FAIL
                                                )
                                            )
                                        }
                                        /*통계 TR 정보 센터로 전송 완료*/
                                        // 4. LINE 796-907: SMS Manager 처리 (아래 latencyNs_SMS_MANAGER 블록에서 실행)
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
                        }

                        // ========== C 코드 실행 순서 4단계 ==========
                        // LINE 796-907: 문자매니저 SMS Manager 처리 (CP SEND 및 InsqStat 후 실행)
                        latencyNs_SMS_MANAGER = measureNanoTime {
                            gstQItemTrans?.let {
                                val gstQItem = it
                                gstQItem.serverType = VSMSS_TYPE
                                val msgStatus = gstQItem.msgStatus.toInt()
                                val gSmsMgrFlag = cfgEtcMap[APPLY_NEW_SMSMANAGER]?.pvalue ?: 0
                                val usSource = gstQItem.usSource.toInt()
                                val gOCSQNo = cfgEtcMap["OCS_QNO"]?.pvalue ?: 0

                                when (gstQItemTrans.msgSubCode.toInt()) {
                                    SM_REQ_TRANS_RESULT -> {
                                        if ((gSMS_MANAGER == 1 && msgStatus == 2) ||
                                            (gSMS_MANAGER == 1 &&
                                                    gSmsMgrFlag > 0 &&
                                                    (msgStatus != SM_STATE_PORTED_OUT &&
                                                            msgStatus !=
                                                            SM_STATE_PORTEDOUT_KTF &&
                                                            msgStatus !=
                                                            SM_STATE_PORTEDOUT_LGT &&
                                                            msgStatus !=
                                                            SM_STATE_PORTEDOUT_SKT))
                                        ) {
                                            //C 코드 LINE 796-907 SMS Manager 블록 내부에는
                                            // InsqStat 호출이 없음
                                            // InsqStat은 InsertIntoASPQ 함수 내부에서만 호출됨 (C 코드 LINE
                                            // 3540, 3737, 3750, 3757)
                                            // TODO: 문자매니저 CP 처리
                                            var ret = 0
                                            var nQNo = 0
                                            val szFWD_NO = CharArray(21)
                                            szFWD_NO[21] = '\u0000'
                                            val callInfoEntity = CallInfoEntity() // <- CallInfo 채우기
                                            if (usSource != SMSMOR && usSource != gOCSQNo) {
                                                if (gstQItem.usSource == NPDB) {
                                                    callInfoEntity.srccallno = gstQItem.srcMinNo
                                                    callInfoEntity.rgtdlvflag =
                                                        gstQItem.termType.toInt()
                                                    callInfoEntity.vldperiod =
                                                        gstQItem.vldPrd.toLong()
                                                    callInfoEntity.traceId =
                                                        gstQItem.msgCodeReserved0.toString()
                                                    callInfoEntity.msgidserver = gstQItem.msgId
                                                    callInfoEntity.cb = gstQItem.callback
                                                    callInfoEntity.msg = gstQItem.msg
                                                    ret = ALTI_SUCCESS
                                                } else {
                                                    // DBReadW2PCALLINFO (C 코드 LINE 834-836)
                                                    // C 코드 LINE 4063-4072 참고: szTempDestCallNo 생성 및
                                                    // szVirtualNum 설정
                                                    // 050 AI Survey Service
                                                    val szTempDestCallNo =
                                                        if (gstQItem.destMinNo == "0") {
                                                            gstQItem.destCid
                                                        } else {
                                                            "${gstQItem.destCid}${gstQItem.destMinNo}"
                                                        }

                                                    // AI_SURVEY_NUMBER로 시작하면 szVirtualNum에 설정
                                                    val virtualNum =
                                                        if (szTempDestCallNo.startsWith(
                                                                AI_SURVEY_NUMBER
                                                            )
                                                        ) {
                                                            witcomLog.p_write(
                                                                Level.DEBUG,
                                                                String.format(
                                                                    "[DEBUG] Check 050 AI Survey Service! DestCID[%s] DestCallNo[%s] ==> VirtualNum[%s]",
                                                                    gstQItem.destCid,
                                                                    gstQItem.destMinNo,
                                                                    szTempDestCallNo
                                                                )
                                                            )
                                                            szTempDestCallNo
                                                        } else {
                                                            ""
                                                        }

                                                    // VirtualNum이 있고 FWD_NO가 비어있으면 VirtualNum을
                                                    // FWD_NO에 복사
                                                    val fwdNo = gstQItem.fwdNo
                                                    if (virtualNum.isNotEmpty() && fwdNo.isEmpty()
                                                    ) {
                                                        gstQItem.fwdNo = virtualNum
                                                    }

                                                    // FWD_NO가 있으면 3번째 인덱스부터, 없으면 srcMinNo 사용
                                                    val szSrcMinNo =
                                                        gstQItem.fwdNo
                                                            .takeIf { it.length > 3 }
                                                            ?.substring(3)
                                                            ?: gstQItem.srcMinNo

                                                    /*
                                                       #DBReadW2PCALLINFO 반환 값 정의
                                                        const val ALTI_NODATA = 0
                                                        const val ALTI_SUCCESS = 1
                                                        const val ALTI_FAIL = -1
                                                    * */
                                                    ret =
                                                        DBReadW2PCALLINFO(
                                                            gstQItem.msgId,
                                                            szSrcMinNo,
                                                            callInfoEntity,
                                                            szFWD_NO
                                                        )
                                                }
                                            } else {
                                                // DbReadCallInfo (C 코드 LINE 845-847)
                                                // C 코드 LINE 4063-4072 참고: szTempDestCallNo 생성 및
                                                // szVirtualNum 설정
                                                // 050 AI Survey Service
                                                val szTempDestCallNo =
                                                    if (gstQItem.destMinNo == "0") {
                                                        gstQItem.destCid
                                                    } else {
                                                        "${gstQItem.destCid}${gstQItem.destMinNo}"
                                                    }

                                                // AI_SURVEY_NUMBER로 시작하면 szVirtualNum에 설정
                                                val virtualNum =
                                                    if (szTempDestCallNo.startsWith(
                                                            AI_SURVEY_NUMBER
                                                        )
                                                    ) {
                                                        witcomLog.p_write(
                                                            Level.DEBUG,
                                                            String.format(
                                                                "[DEBUG] Check 050 AI Survey Service! DestCID[%s] DestCallNo[%s] ==> VirtualNum[%s]",
                                                                gstQItem.destCid,
                                                                gstQItem.destMinNo,
                                                                szTempDestCallNo
                                                            )
                                                        )
                                                        szTempDestCallNo
                                                    } else {
                                                        ""
                                                    }

                                                // VirtualNum이 있고 FWD_NO가 비어있으면 VirtualNum을 FWD_NO에
                                                // 복사
                                                val fwdNo = gstQItem.fwdNo
                                                if (virtualNum.isNotEmpty() && fwdNo.isEmpty()) {
                                                    gstQItem.fwdNo = virtualNum
                                                }

                                                // FWD_NO가 있으면 3번째 인덱스부터, 없으면 srcMinNo 사용
                                                val szSrcMinNo =
                                                    gstQItem.fwdNo
                                                        .takeIf { it.length > 3 }
                                                        ?.substring(3)
                                                        ?: gstQItem.srcMinNo

                                                // CallInfo 테이블에서 조회
                                                val msgIdServer = gstQItem.msgId
                                                val srcCId = gstQItem.srcCid
                                                val foundCallInfo =
                                                    callInfoRepository
                                                        .findByMsgIdServerAndSrcCIdAndDestCallNo(
                                                            msgIdServer,
                                                            srcCId,
                                                            szSrcMinNo
                                                        )

                                                if (foundCallInfo.isPresent) {
                                                    val dbCallInfo = foundCallInfo.get()
                                                    // 복합 키 설정 (삭제를 위해 필요)
                                                    callInfoEntity.msgidcenter =
                                                        dbCallInfo.msgidcenter
                                                    callInfoEntity.destcallno =
                                                        dbCallInfo.destcallno
                                                    callInfoEntity.srccallno = dbCallInfo.srccallno
                                                    callInfoEntity.rgtdlvflag =
                                                        dbCallInfo.rgtdlvflag
                                                    callInfoEntity.vldperiod = dbCallInfo.vldperiod
                                                    callInfoEntity.termtype = dbCallInfo.termtype
                                                    callInfoEntity.traceId = dbCallInfo.traceId
                                                    callInfoEntity.msgidserver =
                                                        dbCallInfo.msgidserver
                                                    callInfoEntity.cb = dbCallInfo.cb
                                                    callInfoEntity.msg = dbCallInfo.msg
                                                    nQNo = dbCallInfo.recvQno?.toInt() ?: 0
                                                    // szFWD_NO에 FWD_SRC 복사
                                                    if (dbCallInfo.fwdSrc != null &&
                                                        dbCallInfo.fwdSrc.isNotEmpty()
                                                    ) {
                                                        dbCallInfo
                                                            .fwdSrc
                                                            .toCharArray()
                                                            .copyInto(
                                                                szFWD_NO,
                                                                0,
                                                                0,
                                                                minOf(
                                                                    dbCallInfo
                                                                        .fwdSrc
                                                                        .length,
                                                                    szFWD_NO.size - 1
                                                                )
                                                            )
                                                    }
                                                    ret = ALTI_SUCCESS
                                                } else {
                                                    ret = ALTI_FAIL // 조회 실패
                                                    witcomLog.p_write(
                                                        Level.WARN,
                                                        String.format(
                                                            "DbReadCallInfo: CallInfo not found - msgIdServer(%s), srcCId(%s), destCallNo(%s)",
                                                            msgIdServer,
                                                            srcCId,
                                                            szSrcMinNo
                                                        )
                                                    )
                                                }
                                            } // END -> ALTI_SUCCESS or ALTI_FAIL 결과값 정의

                                            //C 코드 LINE 850에서 ret == ALTI_SUCCESS 분기
                                            // 이 블록 내부에는 InsqStat 호출이 없음 (InsertIntoASPQ 함수 내부에서 처리)
                                            if (ret == ALTI_SUCCESS) {
                                                // C 코드 LINE 852: MakeSPQItem 호출
                                                val aspQItem = QITEM()
                                                QItemServiceUtil.makeSPQItem(
                                                    gstQItemTrans,
                                                    callInfoEntity,
                                                    aspQItem,
                                                    nQNo,
                                                    szFWD_NO
                                                )

                                                // C 코드 LINE 853-854: usMsgCode와 usMsgSubCode 설정
                                                aspQItem.usMsgCode =
                                                    SmsDef.MSG_CODE_SM_REQ.toShort()
                                                aspQItem.usMsgSubCode =
                                                    SmsDef.SM_REQ_SIMPLE.toShort()

                                                /*여기부터 문자매니저 전송 패킷 구성*/
                                                // C 코드 LINE 856-872: CheckTelecom 및 usMsgSubCode
                                                // 조건부 변경
                                                var Ret = 0
                                                val szDestCID =
                                                    QItemServiceUtil.byteArrayToKString(
                                                        aspQItem.szCId
                                                    )

                                                // C 코드 LINE 859-860: szDestCID 추출 (앞 3자리)
                                                val szDestCID3 =
                                                    if (szDestCID.length >= 3) {
                                                        szDestCID.substring(0, 3)
                                                    } else {
                                                        szDestCID
                                                    }

                                                // C 코드 LINE 862: CheckTelecom 호출
                                                Ret = checkTelecom(szDestCID3)

                                                // C 코드 LINE 864-872: usMsgSubCode 조건부 변경
                                                val aspCId =
                                                    QItemServiceUtil.byteArrayToKString(
                                                        aspQItem.szCId
                                                    )
                                                if ((Ret == ALTI_SUCCESS) ||
                                                    (aspCId == "011") ||
                                                    (aspCId == "010") ||
                                                    (aspCId == "012")
                                                ) {
                                                    // C 코드 LINE 866: GetTelecom_New 호출 및 조건 확인
                                                    val aspMinNo =
                                                        QItemServiceUtil.byteArrayToKString(
                                                            aspQItem.szMinNo
                                                        )
                                                    val telecom = getTelecomNew(aspCId, aspMinNo)
                                                    if (((aspCId == "010") ||
                                                                (aspCId == "012") ||
                                                                (Ret == ALTI_SUCCESS)) &&
                                                        (telecom != SmsDef.SKT_TP)
                                                    ) {
                                                        aspQItem.usMsgSubCode =
                                                            SmsDef.SM_REQ_PORTED.toShort()
                                                    }
                                                } else if (aspCId == "017") {
                                                    // C 코드 LINE 869-870: 017인 경우 아무것도 하지 않음
                                                    // do nothing
                                                } else {
                                                    // C 코드 LINE 872: 그 외의 경우 SM_REQ_PORTED로 설정
                                                    aspQItem.usMsgSubCode =
                                                        SmsDef.SM_REQ_PORTED.toShort()
                                                }

                                                // C 코드 LINE 874-882: gstQItem.szFWD_NO가 있으면
                                                // ASPQItem 재설정
                                                if (gstQItemTrans.fwdNo.isNotEmpty()) {
                                                    // C 코드 LINE 877-879: ASPQItem.szSrcMinNo에
                                                    // destMinNo 설정 (숫자로)
                                                    val destMinNoInt =
                                                        gstQItemTrans.destMinNo.toIntOrNull()
                                                            ?: 0
                                                    val destMinNoBytes =
                                                        destMinNoInt
                                                            .toString()
                                                            .toByteArray(
                                                                java.nio.charset.Charset
                                                                    .forName(
                                                                        "CP949"
                                                                    )
                                                            )
                                                    System.arraycopy(
                                                        destMinNoBytes,
                                                        0,
                                                        aspQItem.szSrcMinNo,
                                                        0,
                                                        minOf(
                                                            destMinNoBytes.size,
                                                            aspQItem.szSrcMinNo.size - 1
                                                        )
                                                    )
                                                    // null terminator 설정
                                                    if (destMinNoBytes.size <
                                                        aspQItem.szSrcMinNo.size
                                                    ) {
                                                        aspQItem.szSrcMinNo[destMinNoBytes.size] =
                                                            0x00
                                                    }

                                                    // C 코드 LINE 881: ASPQItem.szSrcCId에
                                                    // gstQItem.destCid 복사
                                                    val destCidBytes =
                                                        gstQItemTrans.destCid.toByteArray(
                                                            java.nio.charset.Charset
                                                                .forName("CP949")
                                                        )
                                                    System.arraycopy(
                                                        destCidBytes,
                                                        0,
                                                        aspQItem.szSrcCId,
                                                        0,
                                                        minOf(
                                                            destCidBytes.size,
                                                            aspQItem.szSrcCId.size - 1
                                                        )
                                                    )
                                                    // null terminator 설정
                                                    if (destCidBytes.size < aspQItem.szSrcCId.size
                                                    ) {
                                                        aspQItem.szSrcCId[destCidBytes.size] = 0x00
                                                    }
                                                }

                                                // C 코드 LINE 883: PrintMsgQueueForASP 호출
                                                QItemServiceUtil.printQItem3(aspQItem, witcomLog)

                                                // C 코드 LINE 885-888: InsertIntoASPQ 호출 (조건부)
                                                val aspSrcMinNoStr =
                                                    QItemServiceUtil.byteArrayToKString(
                                                        aspQItem.szSrcMinNo
                                                    )
                                                if (aspSrcMinNoStr.length > 1) {
                                                    // C 코드 LINE 887: InsertIntoASPQ 호출
                                                    QItemServiceUtil.insertIntoASPQ(
                                                        aspQItem,
                                                        smsQLib,
                                                        witcomLog,
                                                        gServerID,
                                                        spcodeMap
                                                    )
                                                }

                                                // C 코드 LINE 890-905: CALLINFO 삭제 로직
                                                if (gstQItemTrans.usSource != SMSMOR) {
                                                    if (gstQItemTrans.usSource != NPDB) {
                                                        // C 코드 LINE 895: DBDelW2PCALLINFO 호출
                                                        try {
                                                            // CallInfoEntity의 ID가 설정되어 있는지 확인
                                                            if (callInfoEntity.msgidcenter !=
                                                                null &&
                                                                callInfoEntity
                                                                    .destcallno !=
                                                                null
                                                            ) {
                                                                callInfoRepository.delete(
                                                                    callInfoEntity
                                                                )
                                                                witcomLog.p_write(
                                                                    Level.DEBUG,
                                                                    "[DEBUG] DELETE CALLINFO W2P MSG ============="
                                                                )
                                                            } else {
                                                                witcomLog.p_write(
                                                                    Level.WARN,
                                                                    "[WARNING] DBDelW2PCALLINFO() Cannot delete: msgidcenter or destcallno is null"
                                                                )
                                                            }
                                                        } catch (e: Exception) {
                                                            log.error(
                                                                "DBDelW2PCALLINFO() Delete Error",
                                                                e
                                                            )
                                                            witcomLog.p_write(
                                                                Level.ERROR,
                                                                String.format(
                                                                    "[ERROR] DBDelW2PCALLINFO() Delete Error: %s",
                                                                    e.message
                                                                )
                                                            )
                                                        }
                                                    }
                                                } else {
                                                    // C 코드 LINE 901: DbDelCallInfo 호출
                                                    try {
                                                        // CallInfoEntity의 ID가 설정되어 있는지 확인
                                                        if (callInfoEntity.msgidcenter != null &&
                                                            callInfoEntity.destcallno !=
                                                            null
                                                        ) {
                                                            callInfoRepository.delete(
                                                                callInfoEntity
                                                            )
                                                            witcomLog.p_write(
                                                                Level.DEBUG,
                                                                "[DEBUG] DELETE CALLINFO ============="
                                                            )
                                                            // C 코드 LINE 904: DbCommit() - JPA는 트랜잭션
                                                            // 커밋이 자동으로 처리되므로 명시적 호출 불필요
                                                        } else {
                                                            witcomLog.p_write(
                                                                Level.WARN,
                                                                "[WARNING] DbDelCallInfo() Cannot delete: msgidcenter or destcallno is null"
                                                            )
                                                        }
                                                    } catch (e: Exception) {
                                                        log.error("DbDelCallInfo() Delete Error", e)
                                                        witcomLog.p_write(
                                                            Level.ERROR,
                                                            String.format(
                                                                "[ERROR] DbDelCallInfo() Delete Error: %s",
                                                                e.message
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    else -> {
                                        val formatted =
                                            String.format(
                                                "SMS Manager: Invalid Message SubCode(%s)",
                                                gstQItemTrans.msgSubCode
                                            )
                                        witcomLog.p_write(Level.ERROR, formatted)
                                        /*
                                                 Lvdprintf(LOG_ERROR,"[ERROR] << Invalid Message SubCode(%d) >> \n",ptrMsgHdr->usMsgSubCode);
                                        sprintf(gTempProcessNm,"%s INVALID_MESSAGE_SUBCODE HEADER",szProcessName);
                                        PrintHeaderBuf((char *)ptrMsgHdr,(char*)gTempProcessNm);
                                                 */
                                    }
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
}
