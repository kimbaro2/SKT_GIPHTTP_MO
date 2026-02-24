package com.infra.mo.skt_giphttp_mo.config.threadPool

import ch.qos.logback.classic.Level
import com.fasterxml.jackson.databind.ObjectMapper
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.GIPEVENT_BLOCK_NOTI_CALLBACK
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.GIPEVENT_BLOCK_NOTI_CID
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MODULEID_GIPEVENT_C
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MODULEID_SMSMOR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SERVICEID_GIPEVENT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SERVICEID_GIPM
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CENTER_MO_SUCCESS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIPEVENT_INSQ_BLOCKNOTI
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIPEVENT_MO_OK
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIP_MO_LIMIT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_Q_INSERT_FAIL_BLOCKNOTI
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_DB_INS_FAIL_GIPMOCALLINFO
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_GIP_MT_LIMIT_GIFT
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_SMSMOR_MO_INSQ_OK
import com.infra.mo.skt_giphttp_mo.dto.SegmentInfo
import com.infra.mo.skt_giphttp_mo.dto.smsController.Rsv4ProtocolItem
import com.infra.mo.skt_giphttp_mo.config.application.LiveReloadCLibraryFile
import com.infra.mo.skt_giphttp_mo.config.application.PerformanceSettings
import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.CfgEtcEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.SpcodeEntity
import com.infra.mo.skt_giphttp_mo.utils.LimitCheckFlags
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.CallInfoRepository
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.MOCallInfoRepository
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.MONotISendRepository
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.GipHttpMoAccessRepository
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult
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
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_FAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_GIVEBILL_LIMIT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_LIMIT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_SUCCESS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_TR_FAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_TR_SUCCESS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.IF_NULL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LT_BOTH
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LT_TRACE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MESSAGE_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MESSAGE_TR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.QITEM_SIZE_MSGID
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LEN_TRACE_ID
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.QTYPE_SM_REQ
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.Q_INSERT_SUCCESS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.RCS_RESULT_INCALIDDST
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_SEND
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_SIMPLE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_TRANS_RESULT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_MRMSPAM
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_DELIVERED
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_SPAMERR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIPALL_MTTR_SEND_OK
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIPEVENT_MOACK_BILL_OK
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_GIP_SOCK_MAX_RETRY_FAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_GIP_SOCK_SEND_FAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SUB_QTYPE_RCS_TR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.TERM_TYPE_KOR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.TID_NO_SAVE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.TID_SAVE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.VSMSS_TYPE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.CALL_TYPE_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MSG_DELEVER_OK
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.RCS_TO_SMS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.COMMON_SMS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DEFINE_GIPVERID_510
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil.QueueResult
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.impl.SmsQLibImpl
import com.infra.mo.skt_giphttp_mo.utils.MsgIdCenterGenerator
import com.infra.mo.skt_giphttp_mo.utils.TraceUtil
import com.infra.mo.skt_giphttp_mo.utils.SmsEncodingTypeAnalyzer
import com.infra.mo.skt_giphttp_mo.service.MoBillTypeService
import com.infra.mo.skt_giphttp_mo.service.MoBlockNotificationService
import com.infra.mo.skt_giphttp_mo.service.MoDbInsertService
import com.infra.mo.skt_giphttp_mo.service.MoGipEventLogService
import com.infra.mo.skt_giphttp_mo.service.MoLimitCheckService
import com.infra.mo.skt_giphttp_mo.service.MoQItemUtilService
import com.infra.mo.skt_giphttp_mo.service.MoRegisteredServerIpService
import com.infra.mo.skt_giphttp_mo.service.MoRequeueService
import com.infra.mo.skt_giphttp_mo.service.MoRcsTrService
import com.infra.mo.skt_giphttp_mo.service.MoSendToCpService
import com.infra.mo.skt_giphttp_mo.service.SmsResService
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseTR
import com.infra.mo.skt_giphttp_mo.service.handler.MoServiceType
import com.infra.mo.skt_giphttp_mo.service.handler.MoServiceTypeResolver
import com.infra.mo.skt_giphttp_mo.service.mo.MoServiceContext
import com.infra.mo.skt_giphttp_mo.service.mo.MoServiceHandlerRegistry
import com.infra.mo.skt_giphttp_mo.service.mo.MoServiceTypeAwareHandler
import io.netty.channel.ChannelOption
import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.awaitSingle
import java.net.NetworkInterface
import java.net.Inet4Address
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import javax.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/*TODO SENDThreadPool을 대체하는 새로운 스레드풀입니다.*/
@Component("MOThreadPool")
open class MOThreadPool(
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
    private val moCallInfoRepository: MOCallInfoRepository,
    private val moNotISendRepository: MONotISendRepository,
    private val telePrefixRepository:
    com.infra.mo.skt_giphttp_mo.db.altibase.repository.TELEPrefixRepository,
    private val msgIdCenterGenerator: MsgIdCenterGenerator,
    private val moServiceTypeResolver: MoServiceTypeResolver,
    private val moServiceHandlerRegistry: MoServiceHandlerRegistry,
    private val smsResService: SmsResService,
    private val moDbInsertService: MoDbInsertService,
    private val moLimitCheckService: MoLimitCheckService,
    private val moRegisteredServerIpService: MoRegisteredServerIpService,
    private val moGipEventLogService: MoGipEventLogService,
    private val moQItemUtilService: MoQItemUtilService,
    private val moBlockNotificationService: MoBlockNotificationService,
    private val moRcsTrService: MoRcsTrService,
    private val moRequeueService: MoRequeueService,
    private val moSendToCpService: MoSendToCpService,
    private val moBillTypeService: MoBillTypeService,
    private val gipHttpMoAccessRepository: GipHttpMoAccessRepository
) {

    // 코루틴 스코프 저장 (애플리케이션 종료 시 취소하기 위함)
    private var coroutineScope: CoroutineScope? = null

    // 스레드풀 상태 추적
    @Volatile
    private var threadPoolActive: Boolean = false

    @Volatile
    private var workerCount: Int = 0

    @Volatile
    private var lastDequeueTime: Long = 0

    /**
     * TODO: SMSS 큐 전달 프로세스를 실행합니다.
     *
     * @param poolSize 스레드 풀 크기
     */
    open fun executeEventHandlerTask(poolSize: Int) {
        // poolSize가 0 이하이면 에러
        require(poolSize > 0) { "poolSize must be greater than zero" }

        witcomLog.p_write(
            Level.INFO,
            String.format("MOThreadPool: executeEventHandlerTask 시작 - poolSize: %d", poolSize)
        )

        threadPoolActive = true
        workerCount = poolSize
        lastDequeueTime = System.currentTimeMillis()

        val dispatcher = Executors.newFixedThreadPool(poolSize).asCoroutineDispatcher()
        coroutineScope = CoroutineScope(dispatcher)
        val coroutineCacheCheckPool = coroutineScope!!

        val initialListSnapshot = gipHttpMoAccessEntityList.toList()
        // gipHttpMoAccessEntityList가 비어있어도 스케줄러는 계속 동작해야 함
        // chunked size가 0이 되지 않도록 최소값을 1로 설정
        val chunkSize = if (initialListSnapshot.isEmpty()) {
            1 // 빈 리스트일 때도 chunked(1)로 처리하여 스케줄러는 동작하되 forEach는 실행되지 않음
        } else {
            maxOf(1, (initialListSnapshot.size + poolSize - 1) / poolSize)
        }
        val partitionedList = initialListSnapshot.chunked(chunkSize)
        val gServerID: Int = run {
            val smssNo = System.getenv("SMSS_NO")?.trim()
            if (smssNo.isNullOrBlank()) {
                witcomLog.p_write(Level.INFO, "❌ 환경변수 SMSS_NO가 설정되지 않았습니다. SMSS_NO는 필수 환경변수입니다.")
                throw IllegalStateException("환경변수 SMSS_NO가 설정되지 않았습니다.")
            }
            val serverId = smssNo.toIntOrNull()
            if (serverId == null || serverId <= 0) {
                witcomLog.p_write(Level.INFO, "❌ 환경변수 SMSS_NO 값이 유효하지 않습니다. SMSS_NO=$smssNo (1 이상의 정수여야 합니다)")
                throw IllegalStateException("환경변수 SMSS_NO 값이 유효하지 않습니다. SMSS_NO=$smssNo")
            }
            witcomLog.p_write(Level.INFO, "✅ 환경변수 SMSS_NO 확인: gServerID=$serverId")
            serverId
        }

        witcomLog.p_write(Level.INFO, "🔹 MOThreadPool Partitioned result:")
        partitionedList.forEachIndexed { index, list ->
            witcomLog.p_write(
                Level.INFO,
                String.format("  Worker[%d] -> %s", index, list.joinToString())
            )
        }

        repeat(poolSize) { index ->
            coroutineCacheCheckPool.launch {
                var wasEmpty = false
                witcomLog.p_write(
                    Level.INFO,
                    String.format("🔹[MOThreadPool Worker-%d] 쓰레드 시작 - 이름: %s", index, Thread.currentThread().name)
                )

                while (coroutineCacheCheckPool.isActive) {
                    val currentListSnapshot = gipHttpMoAccessEntityList.toList()
                    if (currentListSnapshot.isEmpty()) {
                        if (!wasEmpty) {
                            witcomLog.p_write(
                                Level.INFO,
                                String.format(
                                    "🔹[MOThreadPool Worker-%d] GIPHTTP_MO_ACCESS 캐시가 비어있음 - dequeue 대기",
                                    index
                                )
                            )
                            wasEmpty = true
                        }
                        delay(1000L)
                        continue
                    } else if (wasEmpty) {
                        witcomLog.p_write(
                            Level.INFO,
                            String.format(
                                "🔹[MOThreadPool Worker-%d] GIPHTTP_MO_ACCESS 캐시가 로드됨 - dequeue 재개",
                                index
                            )
                        )
                        wasEmpty = false
                    }

                    val currentChunkSize =
                        maxOf(1, (currentListSnapshot.size + poolSize - 1) / poolSize)
                    val currentPartitionedList = currentListSnapshot.chunked(currentChunkSize)
                    val assignedCids = currentPartitionedList.getOrNull(index) ?: emptyList()
                    if (assignedCids.isEmpty()) {
                        delay(200L)
                        continue
                    }

                    val smsQLib: SmsQLib = liveReloadCLibraryFile.getPreInitializedLibrary(index)
                    assignedCids.forEach { entity ->
                        var startNs: Long = 0
                        try {
                            val queueNo = entity.queueNo
                            val logNo = entity.logNo

                            // loggerName 생성 (entity 기반)
                            val loggerName = "${entity.cid}-${entity.ipAddr}-${entity.portNo}"
                            // dequeue 시점의 workerThreadId 저장 (비즈니스 로직 완료 시점까지 재사용)
                            val workerThreadId = Thread.currentThread().getId()

                            // 큐에서 데이터 가져오기
                            val gstQResultObj: QueueResult =
                                QItemServiceUtil.fetchAndConvert2(
                                    queueNo,
                                    smsQLib,
                                    witcomLog
                                ) as QueueResult // GetAMsgFromSmsQ
                            val gstQItemTrans = gstQResultObj.result
                            var gstQItem = gstQResultObj.qItem as? QITEM

                            // null 체크: 큐에서 데이터를 가져오지 못한 경우 처리하지 않음
                            if (gstQItemTrans == null || gstQItem == null) {
                                // 큐가 비어있거나 dequeue 실패 - 정상적인 경우
                                delay(500L)
                                return@forEach
                            }

                            // dequeue 성공 시 시간 업데이트
                            lastDequeueTime = System.currentTimeMillis()

                            // msgId 생성: dequeue 직후 즉시 생성 (HTTP 전송 전이 아닌 dequeue 직후)
                            val cacheKey = "${entity.ipAddr}-${entity.portNo}"
                            val procNo = entity.logNo?.toIntOrNull() ?: 0
                            val msgId = msgIdCenterGenerator.generateMsgIdCenterWithTerminator(cacheKey, procNo)
                            val msgIdBytes = msgId.toByteArray(Charsets.UTF_8)
                            val msgIdByteArray = ByteArray(QITEM_SIZE_MSGID) { index ->
                                if (index < msgIdBytes.size) msgIdBytes[index] else 0
                            }
                            System.arraycopy(msgIdByteArray, 0, gstQItem.ucMsgId, 0, QITEM_SIZE_MSGID)

                            witcomLog.c_write(
                                loggerName,
                                Level.INFO,
                                String.format(
                                    "[Dequeue 직후] msgId 생성 완료: msgId(%s) CacheKey(%s) ProcNo(%d) QueueNo(%d) MsgSeqNo(%d)",
                                    msgId,
                                    cacheKey,
                                    procNo,
                                    queueNo,
                                    gstQItem.uMsgSerialNo.toInt()
                                ),
                                workerThreadId
                            )

                            // 큐에서 가져온 직후 traceID 확인 (qItemToMsgHdr 호출 전)
                            val traceIdBeforeConvert = QItemServiceUtil.byteArrayToKString(gstQItem.szTraceId)

                            // qItemToMsgHdr 호출 전에 필수 필드들 백업 (qItemToMsgHdr가 일부 필드를 복사하지 않거나 덮어쓸 수 있음)
                            val cbBeforeConvert = QItemServiceUtil.byteArrayToKString(gstQItem.szCB)
                            val msgBeforeConvert = gstQItem.szMsg.copyOf()  // ByteArray 복사
                            val msgLenBeforeConvert = gstQItem.ucMsgLen

                            // msgId 백업 (dequeue 직후 생성된 msgId 보존)
                            val msgIdBeforeConvert = gstQItem.ucMsgId.copyOf()

                            // nRsv4Protocol 배열 전체 백업 (ESMClass 포함)
                            val nRsv4ProtocolBeforeConvert = gstQItem.nRsv4Protocol.copyOf()

                            // 추가 필드들 백업
                            val totalSegBeforeConvert = gstQItem.totalSeg
                            val segSeqBeforeConvert = gstQItem.segSeq
                            val uOrgMsgLenBeforeConvert = gstQItem.uOrgMsgLen
                            val uMsgSerialNoBeforeConvert = gstQItem.uMsgSerialNo
                            val nVldPrdBeforeConvert = gstQItem.nVldPrd
                            val szOrigMvnoBeforeConvert = gstQItem.szOrigMvnoInformation.copyOf()
                            val szDestMvnoBeforeConvert = gstQItem.szDestMvnoInformation.copyOf()
                            var gESMCLASS = gstQItem.nRsv4Protocol[11]

                            gstQItem = QItemServiceUtil.qItemToMsgHdr(gstQItem)

                            // qItemToMsgHdr 호출 후 백업한 값 복원
                            System.arraycopy(
                                cbBeforeConvert.toByteArray(Charset.forName("CP949")), 0, gstQItem.szCB, 0,
                                minOf(cbBeforeConvert.toByteArray(Charset.forName("CP949")).size, gstQItem.szCB.size)
                            )
                            System.arraycopy(
                                msgBeforeConvert,
                                0,
                                gstQItem.szMsg,
                                0,
                                minOf(msgBeforeConvert.size, gstQItem.szMsg.size)
                            )
                            gstQItem.ucMsgLen = msgLenBeforeConvert

                            // msgId 복원 (dequeue 직후 생성된 msgId 보존)
                            System.arraycopy(
                                msgIdBeforeConvert,
                                0,
                                gstQItem.ucMsgId,
                                0,
                                minOf(msgIdBeforeConvert.size, gstQItem.ucMsgId.size)
                            )

                            // nRsv4Protocol 배열 전체 복원
                            System.arraycopy(
                                nRsv4ProtocolBeforeConvert, 0, gstQItem.nRsv4Protocol, 0,
                                minOf(nRsv4ProtocolBeforeConvert.size, gstQItem.nRsv4Protocol.size)
                            )

                            // 추가 필드들 복원
                            gstQItem.totalSeg = totalSegBeforeConvert
                            gstQItem.segSeq = segSeqBeforeConvert
                            gstQItem.uOrgMsgLen = uOrgMsgLenBeforeConvert
                            // uMsgSerialNo와 nVldPrd는 복사되지만, 값이 0이면 원본 값으로 복원
                            if (gstQItem.uMsgSerialNo == 0 && uMsgSerialNoBeforeConvert != 0) {
                                gstQItem.uMsgSerialNo = uMsgSerialNoBeforeConvert
                            }
                            if (gstQItem.nVldPrd == 0 && nVldPrdBeforeConvert != 0) {
                                gstQItem.nVldPrd = nVldPrdBeforeConvert
                            }
                            // MVNO 정보 복원 (dequeue 시점 값 보존)
                            System.arraycopy(
                                szOrigMvnoBeforeConvert, 0, gstQItem.szOrigMvnoInformation, 0,
                                minOf(szOrigMvnoBeforeConvert.size, gstQItem.szOrigMvnoInformation.size)
                            )
                            System.arraycopy(
                                szDestMvnoBeforeConvert, 0, gstQItem.szDestMvnoInformation, 0,
                                minOf(szDestMvnoBeforeConvert.size, gstQItem.szDestMvnoInformation.size)
                            )

                            // gstQItemTrans(Dequeue 시점 toSMReqTransResult) 기준으로 QITEM 보정 — DCS/기타 누락 방지
                            gstQItem.ucDataEncoding = gstQItemTrans.dataEncoding
                            gstQItem.ucTermType = gstQItemTrans.termType
                            gstQItem.nVldPrd = gstQItemTrans.vldPrd
                            gstQItem.ucPriority = gstQItemTrans.priority
                            gstQItem.ucRepFlag = gstQItemTrans.repFlag
                            gstQItem.ucRgtDlvFlg = gstQItemTrans.rgtDlvFlg
                            gstQItem.ucMsgStatus = gstQItemTrans.msgStatus

                            // qItemToMsgHdr 호출 후 traceID 확인
                            val traceIdAfterConvert = QItemServiceUtil.byteArrayToKString(gstQItem.szTraceId)
                            witcomLog.c_write(
                                loggerName,
                                Level.INFO,
                                String.format(
                                    "[qItemToMsgHdr 호출 후] traceID 확인: TraceId(%s) QueueNo(%d) MsgSeqNo(%d)",
                                    if (traceIdAfterConvert.isBlank() || traceIdAfterConvert.all { it == '\u0000' }) "비어있음" else traceIdAfterConvert,
                                    queueNo,
                                    gstQItem.uMsgSerialNo.toInt()
                                ),
                                workerThreadId
                            )

                            // dequeue 직후 QITEM의 msgVerId = HTTP_MOSEND_ACCESS(GIPHTTP_MO_ACCESS)의 GIPVERID (print QITEM에서 Version ID로 출력됨)
                            gstQItem.nMsgVerId = if (entity.gipverid > 0) entity.gipverid else DEFINE_GIPVERID_510

                            witcomLog.c_write(loggerName, Level.INFO, "== Get CP Qno(${queueNo}) ==", workerThreadId)
                            // C 코드 LINE 892: PrintMsgQueue 호출
                            QItemServiceUtil.printQItem3(gstQItem, witcomLog, loggerName, workerThreadId)

                            // ESMClass 선행 검증 후 서비스 타입(번호규칙 포함) 결정 → 해당 핸들러 호출. 1584 entity 재조회는 로밍 핸들러 내부에서 처리.
                            val destCID = QItemServiceUtil.byteArrayToKString(gstQItem.szCId)
                            val serviceType = moServiceTypeResolver.resolve(gESMCLASS.toInt(), destCID) //등기문자인지 일반문자인지 등 구분.
                            witcomLog.c_write(
                                loggerName,
                                Level.INFO,
                                String.format(
                                    "[ESMCLASS 분기 진입] ESMCLASS(%d) -> serviceType(%s), DestCID(%s)",
                                    gESMCLASS,
                                    serviceType.name,
                                    destCID
                                ),
                                workerThreadId
                            )
                            // MO 전송(SM_REQ_SIMPLE) / MO-TR(SM_REQ_TRANS_RESULT) 모두 서비스(핸들러)에서 수행. usMsgSubCode 덮어쓰기 전에 분기.
                            gstQItem.ucServerType = VSMSS_TYPE.code.toByte()
                            val msgSubCodeBeforeOverwrite = gstQItem.usMsgSubCode.toInt()
                            val fullContext = MoServiceContext(
                                destCID = destCID,
                                esmClass = gESMCLASS.toInt(),
                                loggerName = loggerName,
                                workerThreadId = workerThreadId,
                                entity = entity,
                                queueNo = queueNo,
                                gstQItemTrans = gstQItemTrans,
                                witcomLog = witcomLog,
                                smsQLib = smsQLib,
                                gServerID = gServerID,
                                cfgEtcMap = cfgEtcMap,
                                gstQResultObj = gstQResultObj,
                                moProcessorOps = null
                            )
                            startNs = System.nanoTime()
                            run processing@{
                                when (msgSubCodeBeforeOverwrite) {
                                    SM_REQ_TRANS_RESULT -> {
                                        moServiceHandlerRegistry.getHandler(serviceType).handle(gstQItem, fullContext)
                                        return@processing
                                    }
                                    SM_REQ_SIMPLE, SM_REQ_SEND.toInt() -> {
                                        if (gstQItem.usMsgSubCode == SM_REQ_SEND.toShort()) {
                                            gstQItem.usMsgSubCode = SM_REQ_SIMPLE.toShort()
                                            gstQItemTrans.msgSubCode = SM_REQ_SIMPLE.toShort()
                                        }
                                        gstQItem.usMsgCode = QTYPE_SM_REQ.toShort()
                                        gstQItem.usMsgSubCode = SM_REQ_SIMPLE.toShort()
                                        gstQItemTrans.msgCode = QTYPE_SM_REQ.toShort()
                                        gstQItemTrans.msgSubCode = SM_REQ_SIMPLE.toShort()
                                        moServiceHandlerRegistry.getHandler(serviceType).handle(gstQItem, fullContext)
                                        return@processing
                                    }
                                    else -> return@processing
                                }
                            }
                            witcomLog.c_write(
                                loggerName,
                                Level.INFO,
                                String.format("[서비스 분기] serviceType(%s) -> handler 호출 완료", serviceType.name),
                                workerThreadId
                            )
                        } catch (e: Exception) {
                            // forEach 블록 전체 예외 처리 - 예외 발생 시에도 다음 entity 처리 계속 진행
                            val errorLoggerName = "${entity.cid}-${entity.ipAddr}-${entity.portNo}"
                            val errorWorkerThreadId = Thread.currentThread().getId()
                            witcomLog.c_write(
                                errorLoggerName,
                                Level.INFO,
                                String.format(
                                    "[⚠️ forEach 블록 예외 발생] QueueNo(%d), CID(%s), 예외타입(%s), 예외메시지(%s)",
                                    entity.queueNo,
                                    entity.cid,
                                    e.javaClass.simpleName,
                                    e.message ?: "null"
                                ),
                                errorWorkerThreadId
                            )
                            witcomLog.c_write(
                                errorLoggerName,
                                Level.INFO,
                                String.format(
                                    "[⚠️ forEach 블록 예외 스택] %s",
                                    e.stackTraceToString()
                                ),
                                errorWorkerThreadId
                            )
                            // 예외 발생해도 다음 entity 처리 계속 진행
                        }
                        // latencyNs = System.nanoTime() - startNs // 사용되지 않음
                    }
                }

                witcomLog.p_write(
                    Level.INFO,
                    String.format("🔹[MOThreadPool Worker-%d] 쓰레드 종료 - 이름: %s", index, Thread.currentThread().name)
                )
            }
        }

        witcomLog.p_write(
            Level.INFO,
            String.format("MOThreadPool: executeEventHandlerTask 완료 - %d 개의 워커 시작됨", poolSize)
        )
    }

    /**
     * 스레드풀 상태 정보 클래스
     */
    data class ThreadPoolStatus(
        val active: Boolean,
        val workerCount: Int,
        val lastDequeueTime: Long,
        val timeSinceLastDequeue: Long,
        val healthy: Boolean,
        val coroutineScopeActive: Boolean
    )

    /**
     * 스레드풀 상태 조회
     */
    fun getThreadPoolStatus(): ThreadPoolStatus {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastDequeue = if (lastDequeueTime > 0) {
            currentTime - lastDequeueTime
        } else {
            -1L
        }

        // 스레드풀이 활성화되어 있고, 마지막 dequeue가 5분 이내인 경우 healthy
        val healthy = threadPoolActive &&
                (timeSinceLastDequeue < 0 || timeSinceLastDequeue < 300000)

        return ThreadPoolStatus(
            active = threadPoolActive,
            workerCount = workerCount,
            lastDequeueTime = lastDequeueTime,
            timeSinceLastDequeue = timeSinceLastDequeue,
            healthy = healthy,
            coroutineScopeActive = coroutineScope?.isActive ?: false
        )
    }

    /**
     * 애플리케이션 종료 시 코루틴 스코프를 취소하여 리소스 정리
     */
    @PreDestroy
    fun cleanup() {
        threadPoolActive = false
        witcomLog.p_write(Level.INFO, "MOThreadPool cleanup: 코루틴 스코프 취소 중...")
        coroutineScope?.cancel()
        coroutineScope = null
        witcomLog.p_write(Level.INFO, "MOThreadPool cleanup: 코루틴 스코프 취소 완료")
    }
}

