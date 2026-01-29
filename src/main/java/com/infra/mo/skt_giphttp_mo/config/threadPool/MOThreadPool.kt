package com.infra.mo.skt_giphttp_mo.config.threadPool

import ch.qos.logback.classic.Level
import com.fasterxml.jackson.databind.ObjectMapper
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.BILLTYPE_SRC
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
import com.infra.mo.skt_giphttp_mo.dto.smsController.MoReportRequest
import com.infra.mo.skt_giphttp_mo.dto.smsController.Rsv4ProtocolItem
import com.infra.mo.skt_giphttp_mo.config.application.LiveReloadCLibraryFile
import com.infra.mo.skt_giphttp_mo.config.application.PerformanceSettings
import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.CfgEtcEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.SpcodeEntity
import com.infra.mo.skt_giphttp_mo.utils.LimitCheckFlags
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.MOCallInfoEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.MONotISendEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.CallInfoRepository
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.MOCallInfoRepository
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.MONotISendRepository
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.GipHttpMoAccessRepository
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.CDMA_ROAMING
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
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.GSM_WCDMA_ROAMING
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.IF_NULL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LT_BOTH
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LT_TRACE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MESSAGE_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MESSAGE_TR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NOTI_PLUS_NORMAL_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NOTI_PLUS_PORTED_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NOTI_NORMAL_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NOTI_PORTED_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.PORTED_CDMA_ROAMING
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.PORTED_GSM_WCDMA_ROAMING
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.FORWARD_CDMA_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.FORWARD_GSM_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NUMBER_PLUS_CDMA_ROAMING
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NUMBER_PLUS_GSM_ROAMING
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.BIZ_NUMBER_CDMA_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.BIZ_NUMBER_GSM_ROAMING_MO
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
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil.QueueResult
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.impl.SmsQLibImpl
import com.infra.mo.skt_giphttp_mo.utils.MsgIdCenterGenerator
import com.infra.mo.skt_giphttp_mo.utils.TraceUtil
import com.infra.mo.skt_giphttp_mo.utils.SmsEncodingTypeAnalyzer
import com.infra.mo.skt_giphttp_mo.service.SmsResService
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseTR
import com.infra.mo.skt_giphttp_mo.service.handler.EsmClassHandler
import com.infra.mo.skt_giphttp_mo.service.handler.EsmClassDomainResolver
import io.netty.channel.ChannelOption
import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.awaitSingle
import java.net.URI
import java.net.NetworkInterface
import java.net.Inet4Address
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.time.Duration
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
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

/**
 * Segment 정보를 담는 데이터 클래스 (C 코드의 SEGMENT 구조체 대응)
 * C 코드 LINE 1601-1613 참조
 */
data class SegmentInfo(
    val msgRefId: Int,      // ucRsv[0]
    val totalSeg: Int,      // (ucRsv[1] & 0xF0) >> 4
    val segSeq: Int,        // ucRsv[1] & 0x0F
    val isValid: Boolean    // MMS/PUSH 타입 여부
)

/*TODO SENDThreadPool을 대체하는 새로운 스레드풀입니다.*/
@Component("MOThreadPool")
open class MOThreadPool(
    @Qualifier("ApplicationCacheMap") private val cacheMap: ConcurrentHashMap<String, Any>,
    @Qualifier("ApplicationCacheQueue") private val cacheQueue: ConcurrentLinkedQueue<Any>,
    @Qualifier("GipHttpMoAccessList")
    private val gipHttpMoAccessEntityList: CopyOnWriteArrayList<GipHttpMoAccessEntity>,
    @Qualifier("LimitCheckFlagsMap") private val limitCheckFlagsMap: ConcurrentHashMap<String, LimitCheckFlags>,
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
    private val msgLimitListRepository:
    com.infra.mo.skt_giphttp_mo.db.altibase.repository.MsgLimitListRepository,
    private val msgIdCenterGenerator: MsgIdCenterGenerator,
    private val esmClassDomainResolver: EsmClassDomainResolver,
    private val smsResService: SmsResService,
    private val gipHttpMoAccessRepository: GipHttpMoAccessRepository
) {
    /**
     * JNA Structure(QITEM) 딥카피 유틸.
     * - 차단 처리 시 블록노티/RCSTR 생성 과정에서 원본 QITEM이 변형되지 않도록 하기 위함
     */
    private fun copyQItem(src: QITEM): QITEM {
        src.write() // Java -> native memory sync
        val copy = QITEM()
        copy.write() // allocate native memory
        val bytes = src.pointer.getByteArray(0, src.size())
        copy.pointer.write(0, bytes, 0, bytes.size)
        copy.read() // native memory -> Java sync
        return copy
    }

    // 코루틴 스코프 저장 (애플리케이션 종료 시 취소하기 위함)
    private var coroutineScope: CoroutineScope? = null

    // 등록된 서버 IP 캐시 (네트워크 인터페이스 IP 중 DB에 등록된 IP)
    @Volatile
    private var registeredServerIp: String? = null

    // 스레드풀 상태 추적
    @Volatile
    private var threadPoolActive: Boolean = false

    @Volatile
    private var workerCount: Int = 0

    @Volatile
    private var lastDequeueTime: Long = 0

    /**
     * 네트워크 인터페이스 IP 중에서 DB에 등록된 IP를 찾아 반환
     * CDMA 케이스에서 정확한 LOG_NO를 찾기 위해 사용
     */
    private fun findRegisteredServerIp(): String? {
        // 캐시된 값이 있으면 반환
        if (registeredServerIp != null) {
            return registeredServerIp
        }

        try {
            // 1. 서버의 모든 네트워크 인터페이스 IP 수집
            val networkInterfaceIps = mutableListOf<String>()
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()

            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                val addresses = networkInterface.inetAddresses

                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    // IPv4 주소만 사용 (로컬호스트 제외)
                    if (!address.isLoopbackAddress && address is Inet4Address) {
                        networkInterfaceIps.add(address.hostAddress)
                    }
                }
            }

            if (networkInterfaceIps.isEmpty()) {
                witcomLog.p_write(Level.WARN, "[findRegisteredServerIp] 네트워크 인터페이스 IP를 찾을 수 없습니다.")
                return null
            }

            // 2. HTTP_MOSEND_ACCESS 테이블에서 등록된 IP 목록 조회
            // NOTE: MSG_TYPE 컬럼 삭제됨 → 전체 조회 후 IP 목록만 추출
            val registeredIps = gipHttpMoAccessRepository.findAllEntity()
                .orElse(emptyList())
                .map { it.ipAddr }
                .distinct()
                .filterNotNull()

            if (registeredIps.isEmpty()) {
                witcomLog.p_write(Level.WARN, "[findRegisteredServerIp] HTTP_MOSEND_ACCESS 테이블에 등록된 IP가 없습니다.")
                return null
            }

            // 3. 네트워크 인터페이스 IP 중에서 DB에 등록된 IP 찾기
            val matchedIp = networkInterfaceIps.firstOrNull { networkIp ->
                registeredIps.contains(networkIp)
            }

            if (matchedIp != null) {
                registeredServerIp = matchedIp
                witcomLog.p_write(
                    Level.INFO,
                    "[findRegisteredServerIp] 등록된 서버 IP 찾음: $matchedIp (네트워크 인터페이스 IP: ${networkInterfaceIps.joinToString()}, DB 등록 IP: ${registeredIps.joinToString()})"
                )
                return matchedIp
            } else {
                witcomLog.p_write(
                    Level.WARN,
                    "[findRegisteredServerIp] 네트워크 인터페이스 IP(${networkInterfaceIps.joinToString()}) 중에서 DB에 등록된 IP(${registeredIps.joinToString()})를 찾을 수 없습니다."
                )
                return null
            }
        } catch (e: Exception) {
            witcomLog.p_write(Level.ERROR, "[findRegisteredServerIp] 오류 발생: ${e.message}, 예외: ${e.toString()}")
            return null
        }
    }

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
                witcomLog.p_write(Level.ERROR, "❌ 환경변수 SMSS_NO가 설정되지 않았습니다. SMSS_NO는 필수 환경변수입니다.")
                throw IllegalStateException("환경변수 SMSS_NO가 설정되지 않았습니다.")
            }
            val serverId = smssNo.toIntOrNull()
            if (serverId == null || serverId <= 0) {
                witcomLog.p_write(Level.ERROR, "❌ 환경변수 SMSS_NO 값이 유효하지 않습니다. SMSS_NO=$smssNo (1 이상의 정수여야 합니다)")
                throw IllegalStateException("환경변수 SMSS_NO 값이 유효하지 않습니다. SMSS_NO=$smssNo")
            }
            witcomLog.p_write(Level.INFO, "✅ 환경변수 SMSS_NO 확인: gServerID=$serverId")
            serverId
        }

        /**
         * qItem.usSource를 nInforNo로 반환
         * dequeue된 QITEM의 usSource는 항상 유효한 값이므로 직접 사용
         * IF_NULL은 에러 케이스이므로 사용하지 않음
         */
        fun getNInforNo(qItem: QITEM): Int {
            return qItem.usSource
        }

        witcomLog.p_write(Level.DEBUG, "🔹 MOThreadPool Partitioned result:")
        partitionedList.forEachIndexed { index, list ->
            witcomLog.p_write(
                Level.DEBUG,
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
                                Level.WARN,
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
                                Level.DEBUG,
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
                            witcomLog.c_write(
                                loggerName,
                                Level.DEBUG,
                                String.format(
                                    "[큐에서 가져온 직후] traceID 확인 (qItemToMsgHdr 호출 전): TraceId(%s) QueueNo(%d) MsgSeqNo(%d)",
                                    if (traceIdBeforeConvert.isBlank() || traceIdBeforeConvert.all { it == '\u0000' }) "비어있음" else traceIdBeforeConvert,
                                    queueNo,
                                    gstQItem.uMsgSerialNo.toInt()
                                ),
                                workerThreadId
                            )

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
                            var gESMCLASS = gstQItem.nRsv4Protocol[11]

                            // 디버깅: 백업 전 값 확인
                            witcomLog.c_write(
                                loggerName,
                                Level.DEBUG,
                                String.format(
                                    "[qItemToMsgHdr 호출 전 백업] ESMClass(%d), totalSeg(%d), segSeq(%d), uOrgMsgLen(%d), uMsgSerialNo(%d), nVldPrd(%d)",
                                    gstQItem.nRsv4Protocol[11],
                                    gstQItem.totalSeg,
                                    gstQItem.segSeq,
                                    gstQItem.uOrgMsgLen,
                                    gstQItem.uMsgSerialNo,
                                    gstQItem.nVldPrd
                                ),
                                workerThreadId
                            )

                            // QITEM dequeue 직후 전문 전체 출력 (스레드 풀 컨텍스트에서)
                            // - qItemToMsgHdr 호출 전에 원본 QITEM 상태를 그대로 남기기 위함
                            QItemServiceUtil.printQItem3(
                                gstQItem,
                                witcomLog,
                                loggerName,
                                workerThreadId
                            )

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

                            // 디버깅: 복원 후 값 확인
                            witcomLog.c_write(
                                loggerName,
                                Level.DEBUG,
                                String.format(
                                    "[qItemToMsgHdr 호출 후 복원] ESMClass(%d), totalSeg(%d), segSeq(%d), uOrgMsgLen(%d), uMsgSerialNo(%d), nVldPrd(%d)",
                                    gstQItem.nRsv4Protocol[11],
                                    gstQItem.totalSeg,
                                    gstQItem.segSeq,
                                    gstQItem.uOrgMsgLen,
                                    gstQItem.uMsgSerialNo,
                                    gstQItem.nVldPrd
                                ),
                                workerThreadId
                            )

                            // qItemToMsgHdr 호출 후 traceID 확인
                            val traceIdAfterConvert = QItemServiceUtil.byteArrayToKString(gstQItem.szTraceId)
                            witcomLog.c_write(
                                loggerName,
                                Level.DEBUG,
                                String.format(
                                    "[qItemToMsgHdr 호출 후] traceID 확인: TraceId(%s) QueueNo(%d) MsgSeqNo(%d)",
                                    if (traceIdAfterConvert.isBlank() || traceIdAfterConvert.all { it == '\u0000' }) "비어있음" else traceIdAfterConvert,
                                    queueNo,
                                    gstQItem.uMsgSerialNo.toInt()
                                ),
                                workerThreadId
                            )

                            witcomLog.c_write(loggerName, Level.DEBUG, "== Get CP Qno(${queueNo}) ==", workerThreadId)
                            // C 코드 LINE 892: PrintMsgQueue 호출
                            QItemServiceUtil.printQItem3(gstQItem, witcomLog, loggerName, workerThreadId)

                            // DestCID 확인 및 ESMCLASS 유형별 진입 분기
                            val destCID = QItemServiceUtil.byteArrayToKString(gstQItem.szCId)
                            val serviceDomainByEsmClass = when (gESMCLASS) {
                                NOTI_PLUS_NORMAL_MO, NOTI_PLUS_PORTED_MO -> EsmClassDomainResolver.ServiceDomain.NOTI_PLUS
                                CDMA_ROAMING, PORTED_CDMA_ROAMING, FORWARD_CDMA_ROAMING_MO,
                                NUMBER_PLUS_CDMA_ROAMING, BIZ_NUMBER_CDMA_ROAMING_MO,
                                GSM_WCDMA_ROAMING, PORTED_GSM_WCDMA_ROAMING, FORWARD_GSM_ROAMING_MO,
                                NUMBER_PLUS_GSM_ROAMING, BIZ_NUMBER_GSM_ROAMING_MO -> EsmClassDomainResolver.ServiceDomain.ROAMING

                                else -> EsmClassDomainResolver.ServiceDomain.NORMAL
                            }

                            witcomLog.c_write(
                                loggerName,
                                Level.INFO,
                                String.format(
                                    "[ESMCLASS 분기 진입] ESMCLASS(%d) -> domain(%s), DestCID(%s)",
                                    gESMCLASS,
                                    serviceDomainByEsmClass.name,
                                    destCID
                                ),
                                workerThreadId
                            )

                            val isCid1584Prefix =
                                serviceDomainByEsmClass == EsmClassDomainResolver.ServiceDomain.ROAMING &&
                                        destCID.startsWith("1584")

                            // CDMA 케이스에서 정확한 LOG_NO 찾기
                            var actualLogNo: String? = logNo
                            var actualMoTrBill: Int = entity.moTrBill ?: 0
                            var actualEntity = entity
                            var gBILLTYPE = com.infra.mo.skt_giphttp_mo.utils.BillTypeValidator.toChar(actualEntity.billType, '0')
                            if (isCid1584Prefix) {
                                // 네트워크 인터페이스 IP 중에서 DB에 등록된 IP 찾기
                                val registeredServerIp = findRegisteredServerIp()

                                if (registeredServerIp != null) {
                                    // DestCID(1584) + 큐 번호 + 등록된 IP로 HTTP_MOSEND_ACCESS 재조회 (MSG_TYPE 제외, MOTRBILL로 로직 분기)
                                    val matchedEntity = try {
                                        gipHttpMoAccessRepository.findByCidAndIpAddrAndPortNo(
                                            destCID,              // 1584로 시작하는 CID
                                            registeredServerIp,   // DB에 등록된 서버 IP
                                            entity.portNo        // 포트 번호
                                        ).orElse(null)
                                    } catch (e: Exception) {
                                        // IP만으로 조회
                                        try {
                                            gipHttpMoAccessRepository.findByCidAndIpAddr(
                                                destCID,
                                                registeredServerIp
                                            ).orElse(null)
                                        } catch (e2: Exception) {
                                            // IP만으로 조회
                                            gipHttpMoAccessRepository.findByCidAndIpAddr(
                                                destCID,
                                                registeredServerIp
                                            ).orElse(null)
                                        }
                                    }

                                    if (matchedEntity != null) {
                                        actualLogNo = matchedEntity.logNo
                                        actualMoTrBill = matchedEntity.moTrBill
                                        actualEntity = matchedEntity
                                        gBILLTYPE = com.infra.mo.skt_giphttp_mo.utils.BillTypeValidator.toChar(actualEntity.billType, '0')
                                        witcomLog.c_write(
                                            loggerName,
                                            Level.INFO,
                                            String.format(
                                                "[CDMA 케이스] DB 등록 IP로 정확한 LOG_NO 찾음: DestCID(%s), QueueNo(%d), RegisteredIP(%s), LogNo(%s), MOTRBILL(%d)",
                                                destCID,
                                                queueNo,
                                                registeredServerIp,
                                                actualLogNo,
                                                actualMoTrBill ?: 0
                                            ),
                                            workerThreadId
                                        )
                                    } else {
                                        witcomLog.c_write(
                                            loggerName,
                                            Level.WARN,
                                            String.format(
                                                "[CDMA 케이스] DB 등록 IP로 레코드를 찾지 못함: DestCID(%s), QueueNo(%d), RegisteredIP(%s), 기존 LogNo(%s) 사용",
                                                destCID,
                                                queueNo,
                                                registeredServerIp,
                                                logNo
                                            ),
                                            workerThreadId
                                        )
                                    }
                                } else {
                                    witcomLog.c_write(
                                        loggerName,
                                        Level.WARN,
                                        String.format(
                                            "[CDMA 케이스] 등록된 서버 IP를 찾을 수 없음: DestCID(%s), QueueNo(%d), 기존 LogNo(%s) 사용",
                                            destCID,
                                            queueNo,
                                            logNo
                                        ),
                                        workerThreadId
                                    )
                                }
                            }

                            // C 코드 LINE 893: ucServerType 설정
                            gstQItem.ucServerType = VSMSS_TYPE.code.toByte()

                            // C 코드 LINE 1173: MakePacketFromQItem에서 SM_REQ_ SEND -> SM_REQ_SIMPLE로 변경
                            if (gstQItem.usMsgSubCode == SM_REQ_SEND.toShort()) {
                                gstQItem.usMsgSubCode = SM_REQ_SIMPLE.toShort()
                                gstQItemTrans.msgSubCode = SM_REQ_SIMPLE.toShort()
                            }

                            // MOCALLINFO 저장을 위해 QITEM의 MsgCode, MsgSubCode 변경
                            // Dequeue 시점: MessageCode=31, MessageSubCode=2
                            // MOCALLINFO 저장을 위해: MsgCode=11, MsgSubCode=10으로 변경
                            // 이 변경된 값이 QITEM에 저장되며, CP에 HTTP 전달 시에도 사용됨
                            gstQItem.usMsgCode = QTYPE_SM_REQ.toShort()  // 11
                            gstQItem.usMsgSubCode = SM_REQ_SIMPLE.toShort()  // 10
                            gstQItemTrans.msgCode = QTYPE_SM_REQ.toShort()  // 11
                            gstQItemTrans.msgSubCode = SM_REQ_SIMPLE.toShort()  // 10

                            // MO 전송을 위한..
                            startNs = System.nanoTime()
                            run processing@{
                                // C 코드 LINE 937: switch(ptrMsgHdr->usMsgSubCode)
                                when (gstQItem.usMsgSubCode.toInt()) {
                                    SM_REQ_SIMPLE -> {
                                        // Segment 정보 계산 (C 코드 LINE 1601-1613 대응)
                                        val msgRefId = gstQItem.ucRsv[0].toInt() and 0xFF
                                        val segByte = gstQItem.ucRsv[1].toInt() and 0xFF
                                        val totalSeg = (segByte and 0xF0) shr 4
                                        val segSeq = segByte and 0x0F
                                        val isValidSegment = totalSeg in 1..15 && segSeq in 1..15 && totalSeg >= segSeq
                                        val segmentInfo = SegmentInfo(
                                            msgRefId = msgRefId,
                                            totalSeg = totalSeg,
                                            segSeq = segSeq,
                                            isValid = isValidSegment
                                        )

                                        // 한도차단 체크 (true면 차단 대상)
                                        // BILL_TYPE='2' (SRC): CheckLimitMO 호출
                                        val nRetMO = checkLimitMO(gstQItemTrans, actualEntity, witcomLog)
                                        if (nRetMO) {
                                            // C 코드: gGIPEVENT_BLK_NOTI (CFG_ETC.PRO_NM="GIPEVENT_BLK_NOTI", P_VALUE!=0)
                                            val gGIPEVENT_BLK_NOTI =
                                                cfgEtcMap["GIPEVENT_BLK_NOTI"]?.pvalue?.let { it != 0 } ?: true

                                            // 차단 알림 큐 적재 (전송/DB Insert 하지 않음)
                                            if (gGIPEVENT_BLK_NOTI) {
                                                val blockNotiQItem = copyQItem(gstQItem)
                                                processBlockNotification(
                                                    blockNotiQItem,
                                                    gstQItem,
                                                    smsQLib,
                                                    gServerID,
                                                    witcomLog,
                                                    loggerName,
                                                    workerThreadId
                                                )
                                            }

                                            // MO_LIMIT 통계 (C: ERRORID_CP_MO_LIMIT, ST_GIP_MO_LIMIT)
                                            smsQLib.InsqStat(
                                                gstQItem,
                                                MESSAGE_MO,
                                                0,
                                                gServerID,
                                                MODULEID_GIPEVENT_C,
                                                SERVICEID_GIPEVENT,
                                                ERRORID_CP_MO_LIMIT,
                                                ST_GIP_MO_LIMIT,
                                                getNInforNo(gstQItem),  // qItem.usSource (nInforNo)
                                                TID_NO_SAVE,  // TID_SAVE → TID_NO_SAVE (C 코드와 일치)
                                                LT_BOTH,
                                                0
                                            )

                                            // 차단 케이스에서도 RCS TAG가 있으면 RCS_TR 큐 적재 (C 코드 LINE 1026~1049)
                                            val rcsTag = QItemServiceUtil.byteArrayToKString(gstQItem.RcsTag)
                                            if (rcsTag.isNotBlank()) {
                                                val rcsQItem = copyQItem(gstQItem)
                                                processRcsTrMessage(rcsQItem, smsQLib, loggerName, workerThreadId)
                                            }
                                            return@processing
                                        }

                                        // CheckLimitMO 호출 후, BILL_TYPE='4' (GIVE)인 경우 CheckLimitGIVE 호출
                                        val nRetGIVE = if (gBILLTYPE == '4') {
                                            checkLimitGIVE(gstQItemTrans, actualEntity, witcomLog)
                                        } else {
                                            false
                                        }
                                        if (nRetGIVE) {
                                            // C 코드: gGIPEVENT_BLK_NOTI (CFG_ETC.PRO_NM="GIPEVENT_BLK_NOTI", P_VALUE!=0)
                                            val gGIPEVENT_BLK_NOTI =
                                                cfgEtcMap["GIPEVENT_BLK_NOTI"]?.pvalue?.let { it != 0 } ?: true

                                            // 차단 알림 큐 적재 (전송/DB Insert 하지 않음)
                                            if (gGIPEVENT_BLK_NOTI) {
                                                val blockNotiQItem = copyQItem(gstQItem)
                                                processBlockNotification(
                                                    blockNotiQItem,
                                                    gstQItem,
                                                    smsQLib,
                                                    gServerID,
                                                    witcomLog,
                                                    loggerName,
                                                    workerThreadId
                                                )
                                            }

                                            // GIVE_LIMIT 통계 (C 코드: ERRORID_CP_GIVEBILL_LIMIT, ST_GIP_MT_LIMIT_GIFT)
                                            smsQLib.InsqStat(
                                                gstQItem,
                                                MESSAGE_MO,
                                                0,
                                                gServerID,
                                                MODULEID_GIPEVENT_C,
                                                SERVICEID_GIPEVENT,
                                                ERRORID_CP_GIVEBILL_LIMIT,
                                                ST_GIP_MT_LIMIT_GIFT,
                                                getNInforNo(gstQItem),  // qItem.usSource (nInforNo)
                                                TID_NO_SAVE,
                                                LT_BOTH,
                                                0
                                            )

                                            // 차단 케이스에서도 RCS TAG가 있으면 RCS_TR 큐 적재
                                            val rcsTag = QItemServiceUtil.byteArrayToKString(gstQItem.RcsTag)
                                            if (rcsTag.isNotBlank()) {
                                                val rcsQItem = copyQItem(gstQItem)
                                                processRcsTrMessage(rcsQItem, smsQLib, loggerName, workerThreadId)
                                            }
                                            return@processing
                                        }
//                                    val gREPLY_FLAG = entity.replyFlag?.trim()?.firstOrNull() ?: 'Y'

                                        // C 오리지널(GIPEVENT_c.c) 기준:
                                        // - BILL_TYPE != '1' 인 경우에만 NOTI_PLUS 2종(20/21) 또는 NOTI 2종(90/91)일 때 MO_NOTISEND에 저장
                                        // - 그 외(예: esm_class=1 포함)는 MOCALLINFO에 저장
                                        val isNotiPlusType =
                                            gESMCLASS == NOTI_PLUS_NORMAL_MO ||
                                                    gESMCLASS == NOTI_PLUS_PORTED_MO

                                        // NOTI 체크 (ESMClass 90, 91)
                                        val isNotiType =
                                            gESMCLASS == NOTI_NORMAL_MO ||
                                                    gESMCLASS == NOTI_PORTED_MO

                                        // Relay MO 체크
                                        val isRelayMo = gstQItem.nRsv4Protocol[0] != 0 && gstQItem.nRsv4Protocol[1] != 0

                                        // GetSerialNo 로그
                                        val serialNo = gstQItem.uMsgSerialNo.toLong()
                                        witcomLog.c_write(
                                            loggerName,
                                            Level.DEBUG,
                                            String.format("GetSerialNo() : gSerialNo(%d)", serialNo),
                                            workerThreadId
                                        )

                                        // 2. [GIPHTTPMO_C_<LogNo>] [REQ_SIMPLE] 로그 출력 (전송 전)
                                        val reqSimpleLog = formatGipEventLog(entity, gServerID, gstQItemTrans, gstQItem)
                                        witcomLog.c_write(
                                            loggerName,
                                            Level.DEBUG,
                                            reqSimpleLog,
                                            workerThreadId
                                        )

                                        // HTTP 전송 시작 전 로그
                                        witcomLog.c_write(
                                            loggerName,
                                            Level.DEBUG,
                                            String.format(
                                                "MO HTTP 전송 시작: CP_URL(%s), MsgSeqNo(%d), 재시도횟수(%d), Timeout(%d초)",
                                                entity.cpUrl,
                                                gstQItem.uMsgSerialNo.toInt(),
                                                entity.rc,
                                                entity.tc
                                            ),
                                            workerThreadId
                                        )

                                        // msgId 확인: dequeue 직후에 이미 생성되어 있어야 함
                                        val existingMsgId = QItemServiceUtil.byteArrayToKString(gstQItem.ucMsgId)
                                        if (existingMsgId.isBlank()) {
                                            witcomLog.c_write(
                                                loggerName,
                                                Level.ERROR,
                                                String.format(
                                                    "[CRITICAL] msgId가 비어있음: dequeue 직후에 msgId가 생성되어야 합니다. CP_URL(%s) MsgSeqNo(%d) SrcCID(%s) DestCID(%s) - 메시지 처리를 중단합니다.",
                                                    entity.cpUrl,
                                                    gstQItem.uMsgSerialNo.toInt(),
                                                    QItemServiceUtil.byteArrayToKString(gstQItem.szSrcCId),
                                                    QItemServiceUtil.byteArrayToKString(gstQItem.szCId)
                                                ),
                                                workerThreadId
                                            )

                                            // 통계 로그: msgId 없음 에러
                                            smsQLib.InsqStat(
                                                gstQItem,
                                                MESSAGE_MO,
                                                0,
                                                gServerID,
                                                MODULEID_GIPEVENT_C,
                                                SERVICEID_GIPEVENT,
                                                ERRORID_CP_MO_FAIL,
                                                ST_GIPEVENT_MO_OK,
                                                getNInforNo(gstQItem),  // qItem.usSource (nInforNo)
                                                TID_NO_SAVE,
                                                LT_BOTH,
                                                0
                                            )
                                            return@processing
                                        }

                                        witcomLog.c_write(
                                            loggerName,
                                            Level.DEBUG,
                                            String.format(
                                                "[HTTP 전송 전] msgId 확인: msgId(%s) CP_URL(%s) MsgSeqNo(%d) - dequeue 직후 생성된 msgId 사용",
                                                existingMsgId,
                                                entity.cpUrl,
                                                gstQItem.uMsgSerialNo.toInt()
                                            ),
                                            workerThreadId
                                        )

                                        // traceId 확인 및 검증
                                        // 큐에서 가져온 메시지의 traceID를 먼저 확인
                                        val existingTraceId = QItemServiceUtil.byteArrayToKString(gstQItem.szTraceId)
                                        val hasExistingTraceId =
                                            existingTraceId.isNotBlank() && !existingTraceId.all { it == '\u0000' }

                                        if (hasExistingTraceId) {
                                            witcomLog.c_write(
                                                loggerName,
                                                Level.DEBUG,
                                                String.format(
                                                    "[TraceID 재사용] 큐에서 가져온 메시지에 이미 traceID가 설정되어 있음: TraceId(%s) CP_URL(%s) MsgSeqNo(%d) - 재생성하지 않고 재사용",
                                                    existingTraceId,
                                                    entity.cpUrl,
                                                    gstQItem.uMsgSerialNo.toInt()
                                                ),
                                                workerThreadId
                                            )
                                        }

                                        // traceId 확인 및 검증
                                        // traceID는 반드시 큐에서 가져온 메시지에 이미 설정되어 있어야 함
                                        // 새로 생성하면 과금이 되지 않기 때문에 절대 새로 생성하면 안됨
                                        if (!hasExistingTraceId) {
                                            // traceID가 없으면 에러 로그 출력하고 처리 중단
                                            witcomLog.c_write(
                                                loggerName,
                                                Level.ERROR,
                                                String.format(
                                                    "[CRITICAL] TraceID가 없음: 큐에서 가져온 메시지에 traceID가 설정되어 있지 않습니다. CP_URL(%s) MsgSeqNo(%d) SrcCID(%s) DestCID(%s) - 과금 처리를 위해 traceID는 필수입니다. 메시지 처리를 중단합니다.",
                                                    entity.cpUrl,
                                                    gstQItem.uMsgSerialNo.toInt(),
                                                    QItemServiceUtil.byteArrayToKString(gstQItem.szSrcCId),
                                                    QItemServiceUtil.byteArrayToKString(gstQItem.szCId)
                                                ),
                                                workerThreadId
                                            )

                                            // 통계 로그: traceID 없음 에러
                                            smsQLib.InsqStat(
                                                gstQItem,
                                                MESSAGE_MO,
                                                0,
                                                gServerID,
                                                MODULEID_GIPEVENT_C,
                                                SERVICEID_GIPEVENT,
                                                ERRORID_CP_MO_FAIL,
                                                ST_GIPEVENT_MO_OK,  // 적절한 에러 코드로 변경 필요할 수 있음
                                                getNInforNo(gstQItem),  // qItem.usSource (nInforNo)
                                                TID_NO_SAVE,
                                                LT_BOTH,
                                                0
                                            )

                                            // 메시지 처리 중단
                                            return@processing
                                        }

                                        // traceID가 설정되어 있으면 재사용
                                        val traceId = existingTraceId

                                        witcomLog.c_write(
                                            loggerName,
                                            Level.DEBUG,
                                            String.format(
                                                "QITEM 업데이트 완료: msgId(%s) TraceId(%s) CP_URL(%s) MsgSeqNo(%d) %s",
                                                msgId,
                                                traceId,
                                                entity.cpUrl,
                                                gstQItem.uMsgSerialNo.toInt(),
                                                if (hasExistingTraceId) "(재사용)" else "(새로 생성)"
                                            ),
                                            workerThreadId
                                        )

                                        // 요구사항: 전송 성공(2xx) 후에만 DB Insert
                                        val moSendSuccess = withContext(Dispatchers.IO) {
                                            sendMoMessageToCp(
                                                gstQItemTrans,
                                                entity,
                                                gstQItem,
                                                smsQLib,
                                                gServerID,
                                                loggerName,
                                                workerThreadId,
                                                segmentInfo
                                            )
                                        }

                                        // 전송 결과 상세 로그
                                        witcomLog.c_write(
                                            loggerName,
                                            if (moSendSuccess) Level.DEBUG else Level.ERROR,
                                            String.format(
                                                "MO HTTP 전송 완료: 결과(%s), CP_URL(%s), MsgSeqNo(%d), SrcCID(%s), DestCID(%s)",
                                                if (moSendSuccess) "SUCCESS" else "FAILED",
                                                entity.cpUrl,
                                                gstQItem.uMsgSerialNo.toInt(),
                                                QItemServiceUtil.byteArrayToKString(gstQItem.szSrcCId),
                                                QItemServiceUtil.byteArrayToKString(gstQItem.szCId)
                                            ),
                                            workerThreadId
                                        )


                                        // 디버그 로그: InsqStat 호출 전 상태 확인
                                        val ucServerTypeChar = gstQItem.ucServerType.toInt().toChar()
                                        val expectedServerType = VSMSS_TYPE.code.toChar()
                                        witcomLog.c_write(
                                            loggerName,
                                            Level.DEBUG,
                                            String.format(
                                                "[MO 전송 성공] InsqStat 호출 전 상태 확인: moSendSuccess(%b), gESMCLASS(%d), gBILLTYPE(%c), isNotiPlusType(%b), isNotiType(%b), isRelayMo(%b), SrcCID(%s), DestCID(%s), MsgSeqNo(%d), ucServerType(%c), ExpectedServerType(%c), ServerTypeMatch(%b)",
                                                moSendSuccess,
                                                gESMCLASS,
                                                gBILLTYPE,
                                                isNotiPlusType,
                                                isNotiType,
                                                isRelayMo,
                                                QItemServiceUtil.byteArrayToKString(gstQItem.szSrcCId),
                                                QItemServiceUtil.byteArrayToKString(gstQItem.szCId),
                                                gstQItem.uMsgSerialNo.toInt(),
                                                ucServerTypeChar,
                                                expectedServerType,
                                                ucServerTypeChar == expectedServerType
                                            ),
                                            workerThreadId
                                        )

                                        // QLIB64 버전 확인: GetQNoFromStatRouteTblWithCid에서 사용하는 파라미터 확인
                                        if (ucServerTypeChar != expectedServerType) {
                                            witcomLog.c_write(
                                                loggerName,
                                                Level.WARN,
                                                String.format(
                                                    "[MO 전송 성공] ⚠️ ServerType 불일치: ucServerType(%c) != Expected(%c) - QLIB64 GetQNoFromStatRouteTblWithCid에서 Unknown ServerType 에러 발생 가능",
                                                    ucServerTypeChar,
                                                    expectedServerType
                                                ),
                                                workerThreadId
                                            )
                                        }

                                        // traceId는 HTTP 전송 전에 이미 생성되어 gstQItem.szTraceId에 설정되어 있음

                                        // ESMCLASS 기반 도메인 분기 (서비스 판단 기준)
                                        // gstQItemTrans.destCid 사용 (DB 조회 없이 내부 코드에서 직접 체크)
                                        val destCidValue = gstQItemTrans.destCid ?: ""
                                        val serviceDomain = esmClassDomainResolver.resolve(gESMCLASS)

                                        witcomLog.c_write(
                                            loggerName,
                                            Level.INFO,
                                            String.format(
                                                "[MO 전송 성공] 도메인 분기: ESMCLASS(%d), domain(%s), DestCID(%s)",
                                                gESMCLASS,
                                                serviceDomain.name,
                                                destCidValue
                                            ),
                                            workerThreadId
                                        )

                                        /**
                                         * ESM 값을 기준으로 문자메신저서비스(2580) 판단
                                         * @param esmClass ESM 값
                                         * @return true: 문자메신저서비스, false: 그 외
                                         */
                                        fun isSmsMessengerService(esmClass: Int): Boolean {
                                            // TODO: ESM 값으로 문자메신저서비스(2580)를 구분하는 로직 구현 필요
                                            // 현재는 ESM 값 기준으로 판단하도록 변경됨
                                            // 실제 ESM 값 매핑은 요구사항에 따라 수정 필요
                                            return false
                                        }

                                        /**
                                         * ESM 값을 기준으로 문자매니저서비스(6381) 판단
                                         * @param esmClass ESM 값
                                         * @return true: 문자매니저서비스, false: 그 외
                                         */
                                        fun isSmsManagerService(esmClass: Int): Boolean {
                                            // TODO: ESM 값으로 문자매니저서비스(6381)를 구분하는 로직 구현 필요
                                            // 현재는 ESM 값 기준으로 판단하도록 변경됨
                                            // 실제 ESM 값 매핑은 요구사항에 따라 수정 필요
                                            return false
                                        }

                                        fun recordMoSuccessInsqStat() {
                                            // C 코드 LINE 1768-1769: InsqStat 호출 직전에 ucServerType을 VSMSS_TYPE으로 설정
                                            // C 코드와 동일: InsqStat 호출 직전에 반드시 ucServerType 설정
                                            gstQItem.ucServerType = VSMSS_TYPE.code.toByte()

                                            // C 코드 LINE 1769: MO 전송 성공 통계 기록 (성공(MO))
                                            // C 코드와 동일: BILLTYPE 조건 없이 LT_TRACE로 한 번만 호출
                                            witcomLog.c_write(
                                                loggerName,
                                                Level.DEBUG,
                                                String.format(
                                                    "[MO 전송 성공] InsqStat 호출 시작: MODULEID_GIPEVENT_C(%d), SERVICEID_GIPEVENT(%d), ERRORID_CP_MO_SUCCESS(%d), ST_GIPEVENT_MO_OK(%d), MESSAGE_MO(%d), gServerID(%d), TraceId(%s), BILLTYPE(%c), DestCID(%s)",
                                                    MODULEID_GIPEVENT_C,
                                                    SERVICEID_GIPEVENT,
                                                    ERRORID_CP_MO_SUCCESS,
                                                    ST_GIPEVENT_MO_OK,
                                                    MESSAGE_MO,
                                                    gServerID,
                                                    QItemServiceUtil.byteArrayToKString(gstQItem.szTraceId),
                                                    gBILLTYPE,
                                                    QItemServiceUtil.byteArrayToKString(gstQItem.szCId)
                                                ),
                                                workerThreadId
                                            )

                                            val insqStatResult = smsQLib.InsqStat(
                                                gstQItem,
                                                MESSAGE_MO,
                                                0,
                                                gServerID,
                                                MODULEID_GIPEVENT_C,
                                                SERVICEID_GIPEVENT,
                                                ERRORID_CP_MO_SUCCESS,
                                                ST_GIPEVENT_MO_OK,
                                                getNInforNo(gstQItem),  // qItem.usSource (nInforNo)
                                                TID_NO_SAVE,  // TID_SAVE → TID_NO_SAVE (C 코드와 일치)
                                                LT_BOTH,  // C 코드와 동일하게 LT_TRACE 사용
                                                0
                                            )

                                            // 디버그 로그: InsqStat 호출 결과 상세 확인
                                            val insqStatResultInt = insqStatResult
                                            val isInsqStatSuccess = insqStatResultInt == 1
                                            val ucServerTypeAfterCall = gstQItem.ucServerType.toInt().toChar()

                                            witcomLog.c_write(
                                                loggerName,
                                                if (isInsqStatSuccess) Level.DEBUG else Level.ERROR,
                                                String.format(
                                                    "[MO 전송 성공] InsqStat 호출 결과 상세: 반환값(%d), 성공여부(%b), ucServerType(%c), DestCID(%s), SrcCID(%s), MessageType(%d)",
                                                    insqStatResultInt,
                                                    isInsqStatSuccess,
                                                    ucServerTypeAfterCall,
                                                    QItemServiceUtil.byteArrayToKString(gstQItem.szCId),
                                                    QItemServiceUtil.byteArrayToKString(gstQItem.szSrcCId),
                                                    MESSAGE_MO
                                                ),
                                                workerThreadId
                                            )

                                            if (!isInsqStatSuccess) {
                                                witcomLog.c_write(
                                                    loggerName,
                                                    Level.ERROR,
                                                    String.format(
                                                        "[MO 전송 성공] ❌ InsqStat 실패: 반환값(%d) - 통계 큐 삽입 실패 가능성. C 라이브러리 에러 로그 확인 필요: [ERROR] InsertIntoSmsQStat : invalid nQNo 또는 [ERROR] GetQNoFromStatRouteTblWithCid() : Unknown ServerType",
                                                        insqStatResultInt
                                                    ),
                                                    workerThreadId
                                                )
                                            } else {
                                                witcomLog.c_write(
                                                    loggerName,
                                                    Level.DEBUG,
                                                    String.format(
                                                        "[MO 전송 성공] ✅ InsqStat 성공: MODULEID_GIPEVENT_C(%d), ST_GIPEVENT_MO_OK(%d) - 통계 큐 삽입 완료, GIPEVENT_C 성공(MO) 로그 생성 예상 (smstrc.sh에서 확인 필요)",
                                                        MODULEID_GIPEVENT_C,
                                                        ST_GIPEVENT_MO_OK
                                                    ),
                                                    workerThreadId
                                                )
                                            }
                                        }

                                        when (serviceDomain) {
                                            EsmClassDomainResolver.ServiceDomain.NORMAL -> {
                                                // ESM 값을 기준으로 서비스 구분
                                                // 2580 = 문자메신저서비스, 6381 = 문자매니저서비스
                                                when {
                                                    // ESM 값으로 문자메신저서비스(2580) 판단
                                                    isSmsMessengerService(gESMCLASS) -> {
                                                        // 문자메신저서비스: MO-ACK 단계에서 InsqStat 호출하므로 여기서는 스킵
                                                        witcomLog.c_write(
                                                            loggerName,
                                                            Level.INFO,
                                                            String.format(
                                                                "[MO 전송 성공] NORMAL 도메인 + 문자메신저서비스(ESM=%d): InsqStat 스킵 (MO-ACK 단계에서 호출 예정) - DestCID(%s)",
                                                                gESMCLASS,
                                                                destCidValue
                                                            ),
                                                            workerThreadId
                                                        )
                                                    }

                                                    // ESM 값으로 문자매니저서비스(6381) 판단
                                                    isSmsManagerService(gESMCLASS) -> {
                                                        // 문자매니저서비스: MO-ACK 단계에서 InsqStat 호출하므로 여기서는 스킵
                                                        witcomLog.c_write(
                                                            loggerName,
                                                            Level.INFO,
                                                            String.format(
                                                                "[MO 전송 성공] NORMAL 도메인 + 문자매니저서비스(ESM=%d): InsqStat 스킵 (MO-ACK 단계에서 호출 예정) - DestCID(%s)",
                                                                gESMCLASS,
                                                                destCidValue
                                                            ),
                                                            workerThreadId
                                                        )
                                                    }

                                                    else -> {
                                                        witcomLog.c_write(
                                                            loggerName,
                                                            Level.INFO,
                                                            String.format(
                                                                "[MO 전송 성공] NORMAL 도메인 일반 처리 - ESM(%d), DestCID(%s)",
                                                                gESMCLASS,
                                                                destCidValue
                                                            ),
                                                            workerThreadId
                                                        )
                                                        recordMoSuccessInsqStat()
                                                    }
                                                }
                                            }

                                            else -> {
                                                witcomLog.c_write(
                                                    loggerName,
                                                    Level.INFO,
                                                    String.format(
                                                        "[MO 전송 성공] %s 도메인 일반 처리 - DestCID(%s)",
                                                        serviceDomain.name,
                                                        destCidValue
                                                    ),
                                                    workerThreadId
                                                )
                                                recordMoSuccessInsqStat()
                                            }
                                        }


                                        // HTTP 전송 성공 여부 확인 로그
                                        val destCIDForSendCheck = QItemServiceUtil.byteArrayToKString(gstQItem.szCId)
                                        val isCid1584ForSendCheck = destCIDForSendCheck.startsWith("1584")
                                        witcomLog.c_write(
                                            loggerName,
                                            if (moSendSuccess) Level.INFO else Level.ERROR,
                                            String.format(
                                                "[HTTP 전송 결과] moSendSuccess(%b), destCID(%s), isCid1584(%b), DB Insert 분기 진입 여부(%s)",
                                                moSendSuccess,
                                                destCIDForSendCheck,
                                                isCid1584ForSendCheck,
                                                if (moSendSuccess) "예정" else "스킵(전송 실패)"
                                            ),
                                            workerThreadId
                                        )

                                        if (!moSendSuccess) {
                                            // 전송 실패 시 Queue 재저장 (DB Insert 금지: 중복 MO 방지)
                                            requeueMessage(
                                                gstQResultObj,
                                                queueNo,
                                                smsQLib,
                                                loggerName,
                                                workerThreadId,
                                                gstQItemTrans
                                            )
                                            return@processing
                                        }
                                        // ===== 전송 성공 후 DB Insert =====
                                        var dbInsertOk = true

                                        // CDMA 케이스에서 DB Insert 분기 확인 로그
                                        val destCIDForDBInsert = QItemServiceUtil.byteArrayToKString(gstQItem.szCId)
                                        val isCid1584ForDBInsert = destCIDForDBInsert.startsWith("1584")
                                        witcomLog.c_write(
                                            loggerName,
                                            Level.DEBUG,
                                            String.format(
                                                "[DB Insert 분기 체크] destCID(%s), isCid1584(%b), gBILLTYPE(%c), isNotiPlusType(%b), isNotiType(%b), isRelayMo(%b), actualEntity.logNo(%s), actualEntity.moTrBill(%d)",
                                                destCIDForDBInsert,
                                                isCid1584ForDBInsert,
                                                gBILLTYPE,
                                                isNotiPlusType,
                                                isNotiType,
                                                isRelayMo,
                                                actualEntity.logNo,
                                                actualEntity.moTrBill ?: 0
                                            ),
                                            workerThreadId
                                        )

                                        if (gBILLTYPE != '1' && (isNotiPlusType || isNotiType)) {
                                            // NOTI_PLUS(20,21) 또는 NOTI(90,91) && BILL_TYPE != '1' : MO_NOTISEND만 INSERT
                                            witcomLog.c_write(
                                                loggerName,
                                                Level.DEBUG,
                                                String.format(
                                                    "[DB Insert 분기] NOTI 타입 분기 진입: destCID(%s), isCid1584(%b), MO_NOTISEND 저장",
                                                    destCIDForDBInsert,
                                                    isCid1584ForDBInsert
                                                ),
                                                workerThreadId
                                            )
                                            val notiRes = insertMO_NOTISEND(
                                                gstQItem,
                                                gstQItemTrans,
                                                actualEntity,
                                                segmentInfo,
                                                workerThreadId
                                            )
                                            if (notiRes < 0) {
                                                dbInsertOk = false
                                                smsQLib.InsqStat(
                                                    gstQItem,
                                                    MESSAGE_MO,
                                                    0,
                                                    gServerID,
                                                    MODULEID_GIPEVENT_C,
                                                    SERVICEID_GIPEVENT,
                                                    ERRORID_CP_MO_FAIL,
                                                    // C 오리지널(GIPEVENT_c.c): InsertMO_NOTISEND 실패 시 ST_DB_NO_DATA_MONOTISEND 사용
                                                    com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_DB_NO_DATA_MONOTISEND,
                                                    getNInforNo(gstQItem),  // qItem.usSource (nInforNo)
                                                    TID_NO_SAVE,
                                                    LT_TRACE,
                                                    0
                                                )
                                            }
                                        } else if (isRelayMo) {
                                            // Relay MO: (현재 insertRelayMOCallInfo는 TODO지만, 전송 성공 후 호출)
                                            witcomLog.c_write(
                                                loggerName,
                                                Level.DEBUG,
                                                String.format(
                                                    "[DB Insert 분기] Relay MO 분기 진입: destCID(%s), isCid1584(%b), RelayMOCallInfo 저장",
                                                    destCIDForDBInsert,
                                                    isCid1584ForDBInsert
                                                ),
                                                workerThreadId
                                            )
                                            val relayRes =
                                                insertRelayMOCallInfo(gstQItem, gstQItemTrans, actualEntity, serialNo)
                                            if (relayRes < 0) {
                                                dbInsertOk = false
                                                smsQLib.InsqStat(
                                                    gstQItem,
                                                    MESSAGE_MO,
                                                    0,
                                                    gServerID,
                                                    MODULEID_GIPEVENT_C,
                                                    SERVICEID_GIPEVENT,
                                                    ERRORID_CP_MO_FAIL,
                                                    ST_DB_INS_FAIL_GIPMOCALLINFO,
                                                    getNInforNo(gstQItem),  // qItem.usSource (nInforNo)
                                                    TID_NO_SAVE,
                                                    LT_TRACE,
                                                    0
                                                )
                                            } else {
                                                gstQItem.ucServerType = VSMSS_TYPE.code.toByte()
                                            }
                                        } else {
                                            // 일반 MO(예: esm_class=1 등): MOCALLINFO
                                            witcomLog.c_write(
                                                loggerName,
                                                Level.DEBUG,
                                                String.format(
                                                    "[DB Insert 분기] 일반 MO 분기 진입 (MOCALLINFO 저장): destCID(%s), isCid1584(%b)",
                                                    destCIDForDBInsert,
                                                    isCid1584ForDBInsert
                                                ),
                                                workerThreadId
                                            )
                                            // 디버깅: MOTRBILL 값 확인
                                            val gMOTRBILLForInsert = (actualEntity.moTrBill == 1)
                                            val actualMsgIdForInsert =
                                                QItemServiceUtil.byteArrayToKString(gstQItem.ucMsgId)
                                            val destCIDForInsert = QItemServiceUtil.byteArrayToKString(gstQItem.szCId)
                                            val isCid1584ForInsert = destCIDForInsert.startsWith("1584")

                                            witcomLog.c_write(
                                                loggerName,
                                                Level.DEBUG,
                                                String.format(
                                                    "[DB Insert] insertGIPMOCallInfo 호출 전: MOTRBILL(%d), gMOTRBILL(%b), msgId(%s), srcCID(%s), destCID(%s), isCid1584(%b), gBILLTYPE(%c), isNotiPlusType(%b), isNotiType(%b), isRelayMo(%b)",
                                                    actualEntity.moTrBill ?: 0,
                                                    gMOTRBILLForInsert,
                                                    actualMsgIdForInsert,
                                                    QItemServiceUtil.byteArrayToKString(gstQItem.szSrcCId),
                                                    destCIDForInsert,
                                                    isCid1584ForInsert,
                                                    gBILLTYPE,
                                                    isNotiPlusType,
                                                    isNotiType,
                                                    isRelayMo
                                                ),
                                                workerThreadId
                                            )

                                            // insertGIPMOCallInfo 호출 직전 로그
                                            witcomLog.c_write(
                                                loggerName,
                                                Level.INFO,
                                                String.format(
                                                    "[DB Insert] insertGIPMOCallInfo 호출 직전: destCID(%s), isCid1584(%b), msgId(%s)",
                                                    destCIDForInsert,
                                                    isCid1584ForInsert,
                                                    actualMsgIdForInsert
                                                ),
                                                workerThreadId
                                            )

                                            val callRes = smsResService.insertGIPMOCallInfo(
                                                gstQItem,
                                                gstQItemTrans,
                                                actualEntity,
                                                workerThreadId
                                            )

                                            witcomLog.c_write(
                                                loggerName,
                                                if (callRes >= 0) Level.INFO else Level.ERROR,
                                                String.format(
                                                    "[DB Insert] insertGIPMOCallInfo 호출 결과: 반환값(%d), MOTRBILL(%d), msgId(%s), 성공여부(%b)",
                                                    callRes,
                                                    actualEntity.moTrBill ?: 0,
                                                    actualMsgIdForInsert,
                                                    callRes >= 0
                                                ),
                                                workerThreadId
                                            )

                                            if (callRes < 0) {
                                                dbInsertOk = false
                                                smsQLib.InsqStat(
                                                    gstQItem,
                                                    MESSAGE_MO,
                                                    0,
                                                    gServerID,
                                                    MODULEID_GIPEVENT_C,
                                                    SERVICEID_GIPEVENT,
                                                    ERRORID_CP_MO_FAIL,
                                                    ST_DB_INS_FAIL_GIPMOCALLINFO,
                                                    getNInforNo(gstQItem),  // qItem.usSource (nInforNo)
                                                    TID_NO_SAVE,
                                                    LT_TRACE,
                                                    0
                                                )
                                            }
                                        }

                                        // 전송은 성공했는데 DB Insert 실패한 경우: 재전송(requeue) 금지
                                        if (!dbInsertOk) {
                                            witcomLog.c_write(
                                                loggerName,
                                                Level.ERROR,
                                                String.format(
                                                    "DB Insert After-Send Failed: billType=%d, esmClass=%d, isNotiPlus=%b, isNoti=%b, isRelay=%b, srcCID(%s), srcCallNo(%s), destCID(%s), destCallNo(%s)",
                                                    gBILLTYPE.code,
                                                    gESMCLASS,
                                                    (gBILLTYPE != '1' && isNotiPlusType),
                                                    (gBILLTYPE != '1' && isNotiType),
                                                    isRelayMo,
                                                    gstQItemTrans.srcCid,
                                                    gstQItemTrans.srcMinNo,
                                                    gstQItemTrans.destCid,
                                                    gstQItemTrans.destMinNo
                                                ),
                                                workerThreadId
                                            )
                                        }

                                        // ===== MO_TR_BILL에 따른 처리 로직 분기 =====
                                        // MO_TR_BILL=0: MO-ACK만 처리 (즉시 과금)
                                        // MO_TR_BILL=1: MO-ACK 처리 후 MO-TR 대기 (과금은 MO-TR에서)
                                        val moTrBill = entity.moTrBill ?: 0
                                        val actualMsgId = QItemServiceUtil.byteArrayToKString(gstQItem.ucMsgId)

                                        witcomLog.c_write(
                                            loggerName,
                                            Level.INFO,
                                            String.format(
                                                "[MO 전송 성공] MO_TR_BILL 분기 체크: moTrBill(%d), dbInsertOk(%b), moSendSuccess(%b), MsgId(%s)",
                                                moTrBill,
                                                dbInsertOk,
                                                moSendSuccess,
                                                actualMsgId
                                            ),
                                            workerThreadId
                                        )

                                        if (dbInsertOk && moSendSuccess) {
                                            // HTTP 전송 성공 = MO-ACK 성공으로 간주
                                            // 과금/업데이트 분기는 processSMRes 내부에서 MO_TR_BILL로 결정

                                            witcomLog.c_write(
                                                loggerName,
                                                Level.INFO,
                                                String.format(
                                                    "[MO 전송 성공] MO-ACK 처리 시작 - MsgId(%s), srcCID(%s), destCID(%s), moTrBill(%d)",
                                                    actualMsgId,
                                                    QItemServiceUtil.byteArrayToKString(gstQItem.szSrcCId),
                                                    QItemServiceUtil.byteArrayToKString(gstQItem.szCId),
                                                    moTrBill
                                                ),
                                                workerThreadId
                                            )

                                            try {
                                                // processSMRes() 호출을 위한 ResponseTR 객체 생성
                                                // HTTP 전송 성공 = MO-ACK 성공 (GI_RES_NO_ERR = 0)
                                                val dataBody = ResponseTR.DataBody()
                                                dataBody.srcCID = QItemServiceUtil.byteArrayToKString(gstQItem.szSrcCId)
                                                dataBody.srcCallNo =
                                                    QItemServiceUtil.byteArrayToKString(gstQItem.szSrcMinNo)
                                                dataBody.srcAddrRsv = 0
                                                dataBody.destCID = QItemServiceUtil.byteArrayToKString(gstQItem.szCId)
                                                dataBody.destCallNo =
                                                    QItemServiceUtil.byteArrayToKString(gstQItem.szMinNo)
                                                dataBody.destAddrRsv = 0
                                                dataBody.msgCode = 12.toShort()  // RES_SIMPLE
                                                dataBody.msgSubCode = SM_REQ_SIMPLE.toShort()  // SM_REQ_SIMPLE (10)
                                                dataBody.bodyDataLen = 0
                                                dataBody.msgSeqNo = gstQItem.uMsgSerialNo.toInt()
                                                dataBody.termtype = ""
                                                dataBody.dataType = 0
                                                dataBody.dataEncoding = gstQItem.ucDataEncoding.toInt()
                                                dataBody.concatenateflag = ""
                                                dataBody.concatenateInfo = ""
                                                dataBody.rsv4Protocol = gstQItemTrans.rsv4Protocol.map {
                                                    Rsv4ProtocolItem(it)
                                                }
                                                dataBody.teleServiceID = 0
                                                dataBody.msgCodeRsv = 0
                                                dataBody.reserved2 = emptyList()
                                                dataBody.time = ""
                                                dataBody.ackResult = 0  // GI_RES_NO_ERR = 0 (HTTP 전송 성공 = ACK 성공)
                                                dataBody.result = null  // SM_REQ_TRANS_RESULT용 (MO-ACK에서는 사용 안 함)
                                                dataBody.msgStatus = null  // SM_REQ_TRANS_RESULT용 (MO-ACK에서는 사용 안 함)
                                                dataBody.msgId =
                                                    actualMsgId  // msgId 필수 (MOCALLINFO 조회용) - ucMsgId에서 실제 msgId 사용

                                                val moAckRequest = ResponseTR()
                                                moAckRequest.msgVerId = 1
                                                moAckRequest.encFlag = 0
                                                moAckRequest.data = dataBody

                                                // C 코드 흐름: ProcessSMRes -> ProcessSMReqSimple (VSTAT 35, 15 기록) -> ProcessMOBilling
                                                // loggerName 일치를 위해 entity.ipAddr와 entity.portNo 전달
                                                runBlocking {
                                                    smsResService.processSMRes(
                                                        moAckRequest,
                                                        entity.ipAddr,
                                                        entity.portNo,
                                                        queueNo
                                                    )
                                                }

                                                witcomLog.c_write(
                                                    loggerName,
                                                    Level.INFO,
                                                    String.format(
                                                        "[MO 전송 성공] MO-ACK 처리 완료 - MsgId(%s), moTrBill(%d)",
                                                        actualMsgId,
                                                        moTrBill
                                                    ),
                                                    workerThreadId
                                                )
                                            } catch (e: Exception) {
                                                witcomLog.c_write(
                                                    loggerName,
                                                    Level.ERROR,
                                                    String.format(
                                                        "[MO 전송 성공] MO-ACK 처리 실패 - MsgId(%s), error(%s)",
                                                        actualMsgId,
                                                        e.message
                                                    ),
                                                    workerThreadId
                                                )
                                            }
                                        } else if (!dbInsertOk || !moSendSuccess) {
                                            witcomLog.c_write(
                                                loggerName,
                                                Level.INFO,
                                                String.format(
                                                    "[MO 전송 성공] MO-ACK 처리 스킵 - dbInsertOk(%b), moSendSuccess(%b), MsgId(%s)",
                                                    dbInsertOk,
                                                    moSendSuccess,
                                                    actualMsgId
                                                ),
                                                workerThreadId
                                            )
                                        }

                                        // C 코드 LINE 2163-2195: VCDR bprintf 과금 데이터 출력
                                        // MSG_TYPE='1'인 경우: processMOBilling에서 이미 bprintf 호출함
                                        // MSG_TYPE='4'인 경우: processSMReqSimple에서 VSTAT 15 기록 후 processMOBilling에서 bprintf 호출함
                                        // 따라서 여기서는 bprintf 호출하지 않음
                                        witcomLog.c_write(
                                            loggerName,
                                            Level.DEBUG,
                                            String.format(
                                                "[MO 전송 성공] bprintf 스킵: processSMReqSimple/processMOBilling에서 이미 호출됨 - MsgId(%s)",
                                                actualMsgId
                                            ),
                                            workerThreadId
                                        )

                                        // BILLTYPE == '1'(비과금)인 경우: 과금성공(MOACK) TRC는 생성하지 않음
                                        if (gBILLTYPE == '1') {
                                            witcomLog.c_write(
                                                loggerName,
                                                Level.INFO,
                                                "[TRC 전송 성공] BILLTYPE='1' 비과금: ST_GIPEVENT_MOACK_BILL_OK TRC 호출 스킵 - 과금성공(MOACK) TRC 미출력"
                                            )
                                        } else {
                                            // 문자메신저서비스(2580) 또는 문자매니저서비스(6381), 또는 CID 1584 + CDMA_ROAMING 조합인 경우 ST_GIPEVENT_MOACK_BILL_OK 호출 스킵
                                            // - 문자메신저서비스/문자매니저서비스: ESMClass 기준으로 판단 (CID prefix 기반 구분 제거)
                                            // - CID 1584 + CDMA_ROAMING: processSMReqSimple에서 이미 VSTAT 15 기록했으므로 중복 방지
                                            val destCIDForMOACK = gstQItemTrans.destCid ?: ""
                                            val serviceDomainForMoack = esmClassDomainResolver.resolve(gESMCLASS)
                                            val esmClassHandler = EsmClassHandler()
                                            val isSmsMessengerService = esmClassHandler.isSmsMessengerService(gESMCLASS)
                                            val isSmsManagerService = esmClassHandler.isSmsManagerService(gESMCLASS)
                                            val isCdmaRoamingWithCid1584 = esmClassHandler.isCdmaRoamingWithCid1584(gESMCLASS, destCIDForMOACK)

                                            // ESMClass 기준으로 서비스 판단 (CID prefix 기반 구분 제거)
                                            val shouldSkipMoackInsqStat = when (serviceDomainForMoack) {
                                                EsmClassDomainResolver.ServiceDomain.NORMAL -> {
                                                    // 문자메신저서비스 또는 문자매니저서비스 (ESMClass 기준)
                                                    isSmsMessengerService || isSmsManagerService
                                                }

                                                EsmClassDomainResolver.ServiceDomain.ROAMING -> {
                                                    // CID 1584 + CDMA_ROAMING 조합
                                                    isCdmaRoamingWithCid1584
                                                }

                                                else -> false
                                            }

                                            // 디버깅: ESMCLASS 기준 서비스 판단 확인
                                            witcomLog.c_write(
                                                loggerName,
                                                Level.DEBUG,
                                                String.format(
                                                    "[TRC 전송 성공] ST_GIPEVENT_MOACK_BILL_OK 호출 전 ESMCLASS 체크: destCID(%s), ESMCLASS(%d), isSmsMessengerService(%b), isSmsManagerService(%b), isCdmaRoamingWithCid1584(%b), shouldSkip(%b)",
                                                    destCIDForMOACK,
                                                    gESMCLASS,
                                                    isSmsMessengerService,
                                                    isSmsManagerService,
                                                    isCdmaRoamingWithCid1584,
                                                    shouldSkipMoackInsqStat
                                                ),
                                                workerThreadId
                                            )

                                            if (shouldSkipMoackInsqStat) {
                                                // ESMCLASS 기준 서비스 판단: ST_GIPEVENT_MOACK_BILL_OK 호출 스킵
                                                witcomLog.c_write(
                                                    loggerName,
                                                    Level.INFO,
                                                    String.format(
                                                        "[TRC 전송 성공] ESMCLASS 기준 서비스 판단: ST_GIPEVENT_MOACK_BILL_OK 스킵 - DestCID(%s), ESMCLASS(%d), isSmsMessengerService(%b), isSmsManagerService(%b)",
                                                        destCIDForMOACK,
                                                        gESMCLASS,
                                                        isSmsMessengerService,
                                                        isSmsManagerService
                                                    ),
                                                    workerThreadId
                                                )
                                            } else {
                                                // ESMCLASS 기준 서비스 판단 결과 스킵하지 않는 경우: ST_GIPEVENT_MOACK_BILL_OK 호출
                                                // C 코드 LINE 2110-2244: !gMOTRBILL 블록의 InsqStat 호출
                                                gstQItem.ucServerType = VSMSS_TYPE.code.toByte()

                                                // C 코드 LINE 2219-2220: ST_GIPEVENT_MOACK_BILL_OK 호출 (LT_TRACE 사용)
                                                val insqStatinsqStatST_GIPEVENT_MO_OK = smsQLib.InsqStat(
                                                    gstQItem,
                                                    MESSAGE_MO,
                                                    0,
                                                    gServerID,
                                                    MODULEID_GIPEVENT_C,
                                                    SERVICEID_GIPEVENT,
                                                    ERRORID_CP_MO_SUCCESS,
                                                    ST_GIPEVENT_MOACK_BILL_OK,
                                                    getNInforNo(gstQItem),  // qItem.usSource (nInforNo)
                                                    TID_NO_SAVE,
                                                    LT_TRACE,  // C 코드와 동일: LT_TRACE 사용 (TRC 파일에만 기록)
                                                    0
                                                )

                                                // 디버그 로그: InsqStat 호출 결과 상세 확인
                                                val insqStatResultIntinsqStatST_GIPEVENT_MO_OK =
                                                    insqStatinsqStatST_GIPEVENT_MO_OK
                                                val isInsqStatSuccessinsqStatST_GIPEVENT_MO_OK =
                                                    insqStatResultIntinsqStatST_GIPEVENT_MO_OK == 1
                                                val ucServerTypeAfterCallinsqStatST_GIPEVENT_MO_OK =
                                                    gstQItem.ucServerType.toInt().toChar()

                                                witcomLog.c_write(
                                                    loggerName,
                                                    if (isInsqStatSuccessinsqStatST_GIPEVENT_MO_OK) Level.DEBUG else Level.ERROR,
                                                    String.format(
                                                        "[TRC 전송 성공] ST_GIPEVENT_MOACK_BILL_OK 호출 결과 상세: 반환값(%d), 성공여부(%b), ucServerType(%c), DestCID(%s), SrcCID(%s), MessageType(%d)",
                                                        insqStatResultIntinsqStatST_GIPEVENT_MO_OK,
                                                        isInsqStatSuccessinsqStatST_GIPEVENT_MO_OK,
                                                        ucServerTypeAfterCallinsqStatST_GIPEVENT_MO_OK,
                                                        QItemServiceUtil.byteArrayToKString(gstQItem.szCId),
                                                        QItemServiceUtil.byteArrayToKString(gstQItem.szSrcCId),
                                                        MESSAGE_MO
                                                    ),
                                                    workerThreadId
                                                )

                                                if (!isInsqStatSuccessinsqStatST_GIPEVENT_MO_OK) {
                                                    witcomLog.c_write(
                                                        loggerName,
                                                        Level.ERROR,
                                                        String.format(
                                                            "[TRC 전송 성공] ❌ ST_GIPEVENT_MOACK_BILL_OK 실패: 반환값(%d) - 통계 큐 삽입 실패 가능성. C 라이브러리 에러 로그 확인 필요: [ERROR] InsertIntoSmsQStat : invalid nQNo 또는 [ERROR] GetQNoFromStatRouteTblWithCid() : Unknown ServerType",
                                                            insqStatResultIntinsqStatST_GIPEVENT_MO_OK
                                                        ),
                                                        workerThreadId
                                                    )
                                                } else {
                                                    witcomLog.c_write(
                                                        loggerName,
                                                        Level.DEBUG,
                                                        String.format(
                                                            "[TRC 전송 성공] ✅ ST_GIPEVENT_MOACK_BILL_OK 성공: MODULEID_GIPEVENT_C(%d), ST_GIPEVENT_MOACK_BILL_OK(%d) - 통계 큐 삽입 완료, GIPEVENT_C 과금성공(MOACK) 로그 생성 예상 (smstrc.sh에서 확인 필요)",
                                                            MODULEID_GIPEVENT_C,
                                                            ST_GIPEVENT_MOACK_BILL_OK
                                                        ),
                                                        workerThreadId
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    SM_REQ_TRANS_RESULT -> {
                                        // HTTP POST로 CP 서버에 MO-TR 결과 전송
                                        // C 오리지널: MO-TR 전송은 gMOTRBILL과 gBILLTYPE에 따라 결정됨 (REPLY_FLAG와 무관)
                                        val moTrSendSuccess =
                                            sendMoTrToCp(gstQItem, gstQItemTrans, entity, loggerName, workerThreadId)

                                        if (moTrSendSuccess) {
                                            witcomLog.c_write(
                                                loggerName,
                                                Level.INFO,
                                                String.format(
                                                    "MO-TR Result Send Success : SrcCId(%s),SrcCallNo(%s),DestCId(%s),DestCallNo(%s),MsgStatus(%d),MsgId(%s)",
                                                    gstQItemTrans.srcCid,
                                                    gstQItemTrans.srcMinNo,
                                                    gstQItemTrans.destCid,
                                                    gstQItemTrans.destMinNo,
                                                    gstQItem.ucMsgStatus.toInt(),
                                                    QItemServiceUtil.byteArrayToKString(gstQItem.ucMsgId)
                                                ),
                                                workerThreadId
                                            )

                                            // C 코드 LINE 1066-1072: InsqStat 호출
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
                                                getNInforNo(gstQItem),  // qItem.usSource (nInforNo)
                                                TID_NO_SAVE,
                                                LT_BOTH,
                                                0
                                            )
                                        } else {
                                            witcomLog.c_write(
                                                loggerName,
                                                Level.ERROR,
                                                String.format(
                                                    "MO-TR Result Send Fail : SrcCId(%s),SrcCallNo(%s),DestCId(%s),DestCallNo(%s),MsgStatus(%d),MsgId(%s)",
                                                    gstQItemTrans.srcCid,
                                                    gstQItemTrans.srcMinNo,
                                                    gstQItemTrans.destCid,
                                                    gstQItemTrans.destMinNo,
                                                    gstQItem.ucMsgStatus.toInt(),
                                                    QItemServiceUtil.byteArrayToKString(gstQItem.ucMsgId)
                                                ),
                                                workerThreadId
                                            )

                                            // HTTP 전송 실패 시 Queue 재저장
                                            requeueMessage(
                                                gstQResultObj,
                                                queueNo,
                                                smsQLib,
                                                loggerName,
                                                workerThreadId,
                                                gstQItemTrans
                                            )
                                        }
                                    }
                                }  // run processing@ 람다 블록 닫기
                            }
                        } catch (e: Exception) {
                            // forEach 블록 전체 예외 처리 - 예외 발생 시에도 다음 entity 처리 계속 진행
                            val errorLoggerName = "${entity.cid}-${entity.ipAddr}-${entity.portNo}"
                            val errorWorkerThreadId = Thread.currentThread().getId()
                            witcomLog.c_write(
                                errorLoggerName,
                                Level.ERROR,
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
                                Level.ERROR,
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
     * 로그 메시지 타입 결정 (REQ_SIMPLE 또는 RES_SIMPLE)
     * msgSubCode가 SM_REQ_SIMPLE(10)이면 REQ_SIMPLE 또는 RES_SIMPLE로 판단
     */
    private fun getMessageType(msgCode: Short, msgSubCode: Short): String {
        return when {
            msgSubCode == SM_REQ_SIMPLE.toShort() && msgCode == 11.toShort() -> "REQ_SIMPLE"
            msgSubCode == SM_REQ_SIMPLE.toShort() && msgCode == 12.toShort() -> "RES_SIMPLE"
            msgSubCode == SM_REQ_SIMPLE.toShort() -> "REQ_SIMPLE" // msgSubCode가 10이면 기본적으로 REQ_SIMPLE
            else -> "UNKNOWN"
        }
    }

    /**
     * DataEncoding 값을 문자열로 변환 (예: 08:UCS2, 0E:CP949)
     */
    private fun formatDataEncoding(dataEncoding: Byte): String {
        val encodingValue = dataEncoding.toInt() and 0xFF
        val encodingName = when (encodingValue) {
            DCS_TYPE_DEC_KSC5601, DCS_TYPE_KSC5601.toInt() -> "CP949"
            DCS_TYPE_DEC_UCS2, DCS_TYPE_UCS2.toInt() -> "UCS2"
            DCS_TYPE_DEC_GSM7, DCS_TYPE_GSM7.code -> "GSM7"
            DCS_TYPE_DEC_ASCII7, DCS_TYPE_ASCII7.code -> "ASCII7"
            DCS_TYPE_DEC_8BIT, DCS_TYPE_8BIT.code -> "8BIT"
            else -> "UNKNOWN"
        }
        return String.format("%02X:%s", encodingValue, encodingName)
    }

    /**
     * GIPHTTPMO_C 형식의 로그 메시지 생성
     * 형식: [GIPHTTPMO_C_{logNo}] [REQ_SIMPLE/RES_SIMPLE] [VSMSS#{gServerID}->{cpName}] {메시지 내용}
     */
    private fun formatGipEventLog(
        entity: GipHttpMoAccessEntity,
        gServerID: Int,
        msgHdr: SMReqTransResult,
        qItem: QITEM?
    ): String {
        // logNo를 4자리로 포맷팅
        val logNo = entity.logNo?.let {
            String.format("%04d", it.toIntOrNull() ?: 0)
        } ?: "0000"

        // 메시지 타입 결정
        val messageType = getMessageType(msgHdr.msgCode, msgHdr.msgSubCode)

        // CP 이름 (없으면 빈 문자열)
        val cpName = entity.cpName ?: ""

        // 서버 정보 포맷팅
        val serverInfo = String.format("VSMSS#%d->%s", gServerID, cpName)

        // DataEncoding 포맷팅
        val dataEncoding = formatDataEncoding(msgHdr.dataEncoding)

        // C 스타일 옵션/유형 로그용 값
        val gBILLTYPE = com.infra.mo.skt_giphttp_mo.utils.BillTypeValidator.toChar(entity.billType, '0')
        // val gREPLY_FLAG = entity.replyFlag?.trim()?.firstOrNull() ?: 'Y'  // 제거: REPLY_FLAG 변수
        val gMOTRBILL = if (entity.moTrBill == 1) '1' else '0'  // NUMERIC(1): 0 또는 1
        val gESMCLASS = if (qItem != null) qItem.nRsv4Protocol[11] else 0

        // VldPrd 포맷팅 (0:86400 형식)
        val vldPrd = if (qItem != null && qItem.nVldPrd > 0) {
            String.format("%d:%d", 0, qItem.nVldPrd)
        } else {
            "0:0"
        }

        // OrigCID, RelayCID (QITEM에서 가져오기) - CP949로 디코딩
        val origCID = if (qItem != null) {
            try {
                val bytes = qItem.szOrigCID.takeWhile { it != 0.toByte() }.toByteArray()
                if (bytes.isNotEmpty()) {
                    String(bytes, Charset.forName("CP949")).trim()
                } else {
                    ""
                }
            } catch (e: Exception) {
                ""
            }
        } else {
            ""
        }
        val relayCID = if (qItem != null) {
            try {
                val bytes = qItem.szRelayCID.takeWhile { it != 0.toByte() }.toByteArray()
                if (bytes.isNotEmpty()) {
                    String(bytes, Charset.forName("CP949")).trim()
                } else {
                    ""
                }
            } catch (e: Exception) {
                ""
            }
        } else {
            ""
        }

        // OrgMsgTotalLen
        val orgMsgTotalLen = if (qItem != null && qItem.uOrgMsgLen > 0) {
            qItem.uOrgMsgLen
        } else {
            0
        }

        // 로그 메시지 포맷팅 (GIPHTTPMO_C 형식으로 변경)
        return String.format(
            "[GIPHTTPMO_C_%s] [%s] [%s] MsgVerId(%d) SrcCId(%s) SrcCallNo(%s) DestCId(%s) DestCallNo(%s) MsgCode(%d) MsgSubCode(%d) TId(%d) BodyDataLen(%d) MsgSeqNo(%d) DataEncoding(%s) TermType(%d) ConcatenateFlag(%d) ConcatenateInfo(%d) VldPrd(%s) RgtDlvFlg(%d) CallBack(%s) OrigCID(%s) RelayCID(%s) MsgLen(%d) OrgMsgTotalLen(%d) BILLTYPE(%c) REPLY_FLAG(%c) MO_TR_BILL(%c) ESMCLASS(%d)",
            logNo,
            messageType,
            serverInfo,
            msgHdr.msgVerId,
            msgHdr.srcCid,
            msgHdr.srcMinNo,
            msgHdr.destCid,
            msgHdr.destMinNo,
            msgHdr.msgCode.toInt(),
            msgHdr.msgSubCode.toInt(),
            msgHdr.msgCodeReserved0.toInt(),
            msgHdr.msgLen,
            msgHdr.msgSerialNo,
            dataEncoding,
            msgHdr.termType.toInt(),
            if (qItem != null) qItem.totalSeg.toInt() else 0,
            if (qItem != null) qItem.segSeq.toInt() else 0,
            vldPrd,
            msgHdr.rgtDlvFlg.toInt(),
            msgHdr.callback,
            origCID,
            relayCID,
            msgHdr.msgLen,
            orgMsgTotalLen,
            gBILLTYPE,
            'Y',  // REPLY_FLAG 제거로 인해 기본값 'Y' 사용
            gMOTRBILL,
            gESMCLASS
        )
    }

    /**
     * C 코드의 CheckLimitMO 함수와 동일한 동작을 수행합니다.
     * C 코드 참고: GIPEVENT_c.c LINE 4057-4074
     *
     * 개선사항:
     * - C 코드 패턴 적용: GetLimitCheck()로 계산된 limitFlags를 캐시에서 조회
     * - BILL_TYPE='2' (SRC)일 때만 MO 한도체크 수행
     */
    fun checkLimitMO(
        msgHdr: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        witcomLog: WitcomLog
    ): Boolean {
        // 캐시에서 limitFlags 조회 (C 코드 GetLimitCheck() 패턴)
        val limitFlags = limitCheckFlagsMap[entity.logNo]
            ?: LimitCheckFlags.from(entity) // 캐시 미스 시 즉시 계산

        // C 코드: if(gMOLimit == TRUE) - limitMO 플래그 확인
        if (!limitFlags.limitMO) {
            return false //한도차단여부가 비활성화됨
        }

        // C 코드: sprintf(szMdn, "%s%u", ptrGIMsgHdr->szSrcCId, ptrGIMsgHdr->uSrcCallNo)
        val szMdn = "${msgHdr.srcCid}${msgHdr.srcMinNo}"
        val loggerName = "${entity.cid}-${entity.ipAddr}-${entity.portNo}"

        // C 코드: iLimit = CheckLimitMdn(szMdn)
        return try {
            val count = msgLimitListRepository.countByMdn(szMdn)
            count > 0L
        } catch (e: Exception) {
            witcomLog.c_write(
                loggerName,
                Level.ERROR,
                String.format("CheckLimitMO() DB Error: MDN(%s)", szMdn),
                Thread.currentThread().getId()
            )
            false
        }
    }

    /**
     * C 코드의 CheckLimitGiveBill 함수와 동일한 동작을 수행합니다.
     * C 코드 참고: GIPEVENT_c.c LINE 4076-4093
     *
     * BILL_TYPE='4' (GIVE)일 때 GIVE 한도체크 수행
     * C 코드: sprintf(szMdn, "0%u", ptrGIMsgHdr->uSrcCallNo)
     */
    fun checkLimitGIVE(
        msgHdr: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        witcomLog: WitcomLog
    ): Boolean {
        // 캐시에서 limitFlags 조회 (C 코드 GetLimitCheck() 패턴)
        val limitFlags = limitCheckFlagsMap[entity.logNo]
            ?: LimitCheckFlags.from(entity) // 캐시 미스 시 즉시 계산

        // C 코드: if(gGIVELimit == TRUE) - limitGIVE 플래그 확인
        if (!limitFlags.limitGIVE) {
            return false //한도차단여부가 비활성화됨
        }

        // C 코드: sprintf(szMdn, "0%u", ptrGIMsgHdr->uSrcCallNo)
        val srcCallNoInt = msgHdr.srcMinNo.toIntOrNull() ?: 0
        val szMdn = "0$srcCallNoInt"
        val loggerName = "${entity.cid}-${entity.ipAddr}-${entity.portNo}"

        // C 코드: iLimit = CheckLimitMdn(szMdn)
        return try {
            val count = msgLimitListRepository.countByMdn(szMdn)
            count > 0L
        } catch (e: Exception) {
            witcomLog.c_write(
                loggerName,
                Level.ERROR,
                String.format("CheckLimitGIVE() DB Error: MDN(%s)", szMdn),
                Thread.currentThread().getId()
            )
            false
        }
    }

    /**
     * RoamingIndChk 함수 - C 코드 ComLib.c LINE 397-404 대응
     * SMS_OSFI의 첫번째 바이트에서 0x40 비트가 설정되어 있는지 확인
     *
     * @param RoamingInd szSMS_OSFI[0] 값
     * @return 1: Roaming Ind Flag On, 0: Off
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
     * C 코드: GIDBLib.c LINE 1751-1889 대응
     */
    @Transactional
    open fun insertGIPMOCallInfo(
        qItem: QITEM,
        msgHdr: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        workerThreadId: Long
    ): Int {
        val loggerName = "${entity.cid}-${entity.ipAddr}-${entity.portNo}"
        return try {
            // 함수 진입 확인 로그
            witcomLog.c_write(
                loggerName,
                Level.INFO,
                String.format(
                    "[insertGIPMOCallInfo] 함수 진입: msgId(%s), destCID(%s)",
                    QItemServiceUtil.byteArrayToKString(qItem.ucMsgId),
                    QItemServiceUtil.byteArrayToKString(qItem.szCId)
                ),
                workerThreadId
            )

            // 디버깅: insertGIPMOCallInfo 호출 시점의 qItem 값 확인 (안전하게)
            try {
                witcomLog.c_write(
                    loggerName,
                    Level.DEBUG,
                    String.format(
                        "[insertGIPMOCallInfo] qItem 값 확인: ESMClass(%d), uOrgMsgLen(%d), totalSeg(%d), segSeq(%d), ucRsv4Dlv[1](%d), nRsv4Protocol[10](%d), nRsv4Protocol[11](%d), uMsgSerialNo(%d), nVldPrd(%d)",
                        if (qItem.nRsv4Protocol.size > 11) qItem.nRsv4Protocol[11] else -1,
                        qItem.uOrgMsgLen,
                        qItem.totalSeg,
                        qItem.segSeq,
                        if (qItem.ucRsv4Dlv.isNotEmpty() && qItem.ucRsv4Dlv.size > 1) qItem.ucRsv4Dlv[1].toInt() else -1,
                        if (qItem.nRsv4Protocol.size > 10) qItem.nRsv4Protocol[10] else -1,
                        if (qItem.nRsv4Protocol.size > 11) qItem.nRsv4Protocol[11] else -1,
                        qItem.uMsgSerialNo,
                        qItem.nVldPrd
                    ),
                    workerThreadId
                )
            } catch (logException: Exception) {
                witcomLog.c_write(
                    loggerName,
                    Level.WARN,
                    String.format(
                        "[insertGIPMOCallInfo] qItem 값 확인 중 예외 발생: %s",
                        logException.message ?: "null"
                    ),
                    workerThreadId
                )
            }

            val srcCId = QItemServiceUtil.byteArrayToKString(qItem.szSrcCId)
            val srcCallNo = QItemServiceUtil.byteArrayToKString(qItem.szSrcMinNo)
            val destCId = QItemServiceUtil.byteArrayToKString(qItem.szCId)
            val destCallNo = QItemServiceUtil.byteArrayToKString(qItem.szMinNo)

            // MSGID: HTTP 전송 전에 이미 생성되어 qItem.ucMsgId에 설정되어 있어야 함
            // msgId는 비어있을 수 없으며, 최초 생성 이후 절대 재생성하면 안 됨
            val msgId = QItemServiceUtil.byteArrayToKString(qItem.ucMsgId)
            if (msgId.isBlank()) {
                val errorMsg = String.format(
                    "[insertGIPMOCallInfo] ❌ 치명적 오류: msgId가 비어있음. HTTP 전송 전에 msgId가 생성되어야 합니다. destCID(%s), srcCID(%s), srcCallNo(%s)",
                    destCId,
                    srcCId,
                    srcCallNo
                )
                witcomLog.c_write(
                    loggerName,
                    Level.ERROR,
                    errorMsg,
                    workerThreadId
                )
                return -1  // 실패 반환
            }

            // 저장 전 필수 필드 검증
            if (srcCId.isBlank() || destCId.isBlank() || srcCallNo.isBlank()) {
                val errorMsg = String.format(
                    "[insertGIPMOCallInfo] ❌ 필수 필드 누락: msgId(%s), srcCId(%s), destCId(%s), srcCallNo(%s), destCallNo(%s)",
                    msgId,
                    srcCId,
                    destCId,
                    srcCallNo,
                    destCallNo
                )
                witcomLog.c_write(
                    loggerName,
                    Level.ERROR,
                    errorMsg,
                    workerThreadId
                )
                return -1  // 실패 반환
            }

            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyMMddHHmmss")
            val moSubTime = sdf.format(cal.time) + String.format("%02d", cal.get(Calendar.MILLISECOND) / 10)

            val msgLen = qItem.ucMsgLen.toInt()

            // ESMCLASS: nRsv4Protocol[11]에서 가져옴
            val gESMCLASS = qItem.nRsv4Protocol[11]

            // C 코드 LINE 1851-1864: RoamingIndFlag 기반 로밍 ID 결정
            val RoamingIndFlag = RoamingIndChk(qItem.szSMS_OSFI[0])
            var v_ucRoaming: Int = 0

            // CDMA 로밍 계열 (36, 37, 38, 69, 72)
            if (gESMCLASS == CDMA_ROAMING ||
                gESMCLASS == PORTED_CDMA_ROAMING ||
                gESMCLASS == FORWARD_CDMA_ROAMING_MO ||
                gESMCLASS == NUMBER_PLUS_CDMA_ROAMING ||
                gESMCLASS == BIZ_NUMBER_CDMA_ROAMING_MO
            ) {
                if (RoamingIndFlag == 1)
                    v_ucRoaming = 19  // CDMA 로밍 Roaming_ind
                else
                    v_ucRoaming = 17  // CDMA 로밍
            }
            // GSM/WCDMA 로밍 계열 (40, 41, 42, 70, 73)
            else if (gESMCLASS == GSM_WCDMA_ROAMING ||
                gESMCLASS == PORTED_GSM_WCDMA_ROAMING ||
                gESMCLASS == FORWARD_GSM_ROAMING_MO ||
                gESMCLASS == NUMBER_PLUS_GSM_ROAMING ||
                gESMCLASS == BIZ_NUMBER_GSM_ROAMING_MO
            ) {
                if (RoamingIndFlag == 1)
                    v_ucRoaming = 20  // GSM/WCDMA 로밍 Roaming_ind
                else
                    v_ucRoaming = 18  // GSM/WCDMA 로밍
            }

            val cb = QItemServiceUtil.byteArrayToKString(qItem.szCB)
            val roamPMN = if (v_ucRoaming != 0 && qItem.nRsv4Protocol[10] != 0) {
                qItem.nRsv4Protocol[10].toString()
            } else {
                "0"
            }

            val wZone = if (qItem.ucRsv4Dlv.isNotEmpty() && qItem.ucRsv4Dlv[1] != 0.toByte()) {
                val wZoneChar = qItem.ucRsv4Dlv[1].toInt().toChar()
                if (wZoneChar != '\u0000') {
                    wZoneChar.toString()
                } else {
                    null  // null 문자면 null로 저장
                }
            } else {
                null  // 빈 배열이거나 값이 0이면 null
            }

            // TRACE_ID: HTTP 전송 전에 이미 생성되어 qItem.szTraceId에 설정되어 있으므로, 그것을 사용
            val traceId = QItemServiceUtil.byteArrayToKString(qItem.szTraceId)
            if (traceId.isBlank()) {
                // 안전장치: traceId가 비어있으면 경고 로그 출력 (정상적으로는 발생하지 않아야 함)
                witcomLog.c_write(
                    loggerName,
                    Level.WARN,
                    String.format(
                        "[insertGIPMOCallInfo] ⚠️ TraceId가 비어있음: HTTP 전송 전에 생성되어야 함. msgId(%s)",
                        msgId
                    ),
                    workerThreadId
                )
            }
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

            // QITEM에서 추가 필드 값 추출
            // MSG: 메시지 내용 (szMsg) - DataEncoding에 맞게 인코딩하여 저장 (문자 평문으로 저장)
            val msg: String? = try {
                // QItemServiceUtil의 decodeQItemMessage 사용 (ucMsgLen 기반 정확한 디코딩)
                val decodedMsg = QItemServiceUtil.decodeQItemMessage(qItem)
                if (decodedMsg != null && decodedMsg.isNotBlank()) {
                    // 최대 길이 제한 (300자)
                    decodedMsg.take(300)
                } else {
                    null
                }
            } catch (e: Exception) {
                // 디코딩 실패 시 null
                witcomLog.c_write(
                    loggerName,
                    Level.WARN,
                    String.format(
                        "[insertGIPMOCallInfo] MSG 디코딩 실패: msgLen(%d), dataEncoding(%d), 예외(%s)",
                        qItem.ucMsgLen,
                        qItem.ucDataEncoding.toInt() and 0xFF,
                        e.message ?: "null"
                    ),
                    workerThreadId
                )
                null
            }

            // SEGMENT: 세그먼트 정보 (segSeq)
            val segment = if (qItem.segSeq > 0) qItem.segSeq.toInt() else null

            // TID: usMsgCodeReserved[0]을 String으로 변환 (6자리)
            val tidValue = if (qItem.usMsgCodeReserved.isNotEmpty() && qItem.usMsgCodeReserved[0] != 0.toShort()) {
                String.format("%06d", qItem.usMsgCodeReserved[0].toInt() and 0xFFFF)
            } else {
                null
            }

            // RETURNQNO: Return Queue 번호
            val returnQno = if (qItem.ReturnQ_No > 0) qItem.ReturnQ_No else null

            // EXPIRETIME: nVldPrd를 Date로 변환 (초 단위를 밀리초로 변환)
            val expireTime = if (qItem.nVldPrd > 0) {
                val calExpire = Calendar.getInstance()
                calExpire.timeInMillis = System.currentTimeMillis() + (qItem.nVldPrd * 1000L)
                calExpire.time
            } else {
                null
            }

            // FWD_SRC: 전달 소스 (szFWD_NO)
            val fwdSrc = QItemServiceUtil.byteArrayToKString(qItem.szFWD_NO)
            val fwdSrcValue = if (fwdSrc.isNotBlank() && fwdSrc.any { it != '\u0000' }) {
                fwdSrc.trim { it <= ' ' }.take(20)
            } else {
                null
            }

            // W2PMSGID: Relay MO의 경우 W2P MSGID (szRelayCID 또는 szOrigCID에서 확인)
            val relayCid = QItemServiceUtil.byteArrayToKString(qItem.szRelayCID)
            val origCid = QItemServiceUtil.byteArrayToKString(qItem.szOrigCID)
            val w2pMsgId = if (relayCid.isNotBlank() || origCid.isNotBlank()) {
                // Relay MO인 경우 처리 (필요시 추가 로직)
                null  // TODO: W2PMSGID 생성 로직 필요시 추가
            } else {
                null
            }

            // CENTERNO: Center 번호 (확인 필요, 일단 null)
            val centerno: Int? = null

            // VIRTUAL_NUM: Virtual Number (확인 필요, 일단 null)
            val virtualNum: String? = null

            // NPDB_QUERY_CNT: NPDB 조회 횟수 (확인 필요, 일단 null)
            val npdbQueryCnt: Int? = null

            // C 모듈에서 저장하는 필드들만 설정 (DB 테이블에 실제로 존재하는 컬럼만)
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

                // QITEM에서 가져온 추가 필드들
                this.msg = msg
                this.segment = segment
                this.tid = tidValue
                this.returnQno = returnQno
                this.expireTime = expireTime
                this.fwdSrc = fwdSrcValue
                this.w2pMsgId = w2pMsgId
                this.centerno = centerno
                this.virtualNum = virtualNum
                this.npdbQueryCnt = npdbQueryCnt
                this.esmClass = gESMCLASS  // ESMCLASS 저장
            }

            // CDMA 케이스 확인 로그
            val isCid1584InInsert = destCId.startsWith("1584")
            witcomLog.c_write(
                loggerName,
                Level.DEBUG,
                String.format(
                    "[insertGIPMOCallInfo] MOCALLINFO 저장 시작: msgId(%s), destCID(%s), isCid1584(%b), roamingId(%d), entity.logNo(%s), entity.moTrBill(%d)",
                    msgId,
                    destCId,
                    isCid1584InInsert,
                    v_ucRoaming,
                    entity.logNo,
                    entity.moTrBill ?: 0
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
                moCallInfoRepository.flush()  // 즉시 DB에 반영하여 다른 트랜잭션에서 조회 가능하도록 함
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
                throw saveException  // 상위 catch 블록에서 처리하도록 재던지기
            }

            // 저장 후 즉시 조회하여 검증 (복합키로 조회)
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
                    return -1  // 저장 후 조회 실패 시 에러 반환
                }
            }
            0
        } catch (e: Exception) {
            // catch 블록에서는 qItem에서 직접 읽어오기
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

            // 예외 발생 시 상세 로그 출력
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

    /**
     * InsertMO_NOTISEND 구현 (C 코드 LINE 1626 대응)
     * @param segmentInfo Segment 정보 (MMS/PUSH 타입에만 유효)
     */
    @Transactional
    open fun insertMO_NOTISEND(
        qItem: QITEM,
        msgHdr: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        segmentInfo: SegmentInfo? = null,
        workerThreadId: Long
    ): Int {
        val loggerName = "${entity.cid}-${entity.ipAddr}-${entity.portNo}"
        return try {
            val srcCId = QItemServiceUtil.byteArrayToKString(qItem.szSrcCId)
            val srcCallNo = QItemServiceUtil.byteArrayToKString(qItem.szSrcMinNo)
            val destCId = QItemServiceUtil.byteArrayToKString(qItem.szCId)
            val destCallNo = QItemServiceUtil.byteArrayToKString(qItem.szMinNo)

            // MSGID: HTTP 전송 전에 이미 생성되어 qItem.ucMsgId에 설정되어 있어야 함
            // msgId는 비어있을 수 없으며, 최초 생성 이후 절대 재생성하면 안 됨
            val msgId = QItemServiceUtil.byteArrayToKString(qItem.ucMsgId)
            if (msgId.isBlank()) {
                val errorMsg = String.format(
                    "[insertMO_NOTISEND] ❌ 치명적 오류: msgId가 비어있음. HTTP 전송 전에 msgId가 생성되어야 합니다. destCID(%s), srcCID(%s), srcCallNo(%s)",
                    destCId,
                    srcCId,
                    srcCallNo
                )
                witcomLog.c_write(
                    loggerName,
                    Level.ERROR,
                    errorMsg,
                    workerThreadId
                )
                return -1  // 실패 반환
            }
            val node = System.getenv("SMSS_NODE") ?: System.getenv("HOSTNAME") ?: "UNKNOWN"

            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyMMddHHmmss")
            val usec = String.format("%02d", cal.get(Calendar.MILLISECOND) / 10)
            // val moSubTime = sdf.format(cal.time) + usec // 사용되지 않음

            val moRecvTimeRaw = QItemServiceUtil.byteArrayToKString(qItem.szMoRecvTime)
            val moRecvTime = if (moRecvTimeRaw.isBlank()) {
                sdf.format(cal.time) + usec
            } else {
                moRecvTimeRaw
            }

            val tidInt = qItem.usMsgCodeReserved[0].toInt()
            var tid = tidInt.toString()
            if (tid.length < 5) {
                tid = tid.padEnd(5, 'F')
            }
            if (tid == "0FFFF") {
                tid = "4098F"
            }

            // Segment 정보 설정 (C 코드 LINE 1601-1613 대응)
            // DB 컬럼이 Integer이므로 unsigned byte(0~255) 형태로 보존
            val segment: Int = if (segmentInfo != null && segmentInfo.isValid) {
                qItem.ucRsv[1].toInt() and 0xFF // total_seg + seg_seq
            } else {
                0xFF // 무효
            }
            val esmClass = qItem.nRsv4Protocol[11]

            val wZone = if (qItem.ucRsv4Dlv.isNotEmpty()) {
                qItem.ucRsv4Dlv[1].toInt().toChar().toString()
            } else {
                "0"
            }

            // TRACE_ID: HTTP 전송 전에 이미 생성되어 qItem.szTraceId에 설정되어 있으므로, 그것을 사용
            val traceId = QItemServiceUtil.byteArrayToKString(qItem.szTraceId)
            if (traceId.isBlank()) {
                // 안전장치: traceId가 비어있으면 경고 로그 출력
                witcomLog.c_write(
                    loggerName,
                    Level.WARN,
                    String.format(
                        "[insertMO_NOTISEND] ⚠️ TraceId가 비어있음: HTTP 전송 전에 생성되어야 함. msgId(%s)",
                        msgId
                    ),
                    workerThreadId
                )
            }
            val origMvnoInfo = QItemServiceUtil.byteArrayToKString(qItem.szOrigMvnoInformation)
            val destMvnoInfo = QItemServiceUtil.byteArrayToKString(qItem.szDestMvnoInformation)

            val dcsType = when (qItem.ucDataEncoding) {
                DCS_TYPE_GSM7.code.toByte() -> DCS_TYPE_DEC_GSM7
                DCS_TYPE_ASCII7.code.toByte() -> DCS_TYPE_DEC_ASCII7
                DCS_TYPE_8BIT.code.toByte() -> DCS_TYPE_DEC_8BIT
                DCS_TYPE_UCS2.code.toByte() -> DCS_TYPE_DEC_UCS2
                DCS_TYPE_KSC5601 -> DCS_TYPE_DEC_KSC5601
                else -> DCS_TYPE_DEC_UNKNOWN
            }

            val msgLen = qItem.ucMsgLen.toInt()
            val orgMsgLen = qItem.uOrgMsgLen
            val cb = QItemServiceUtil.byteArrayToKString(qItem.szCB)

            // InsertMO_NOTISEND 상세 로그 (DEBUG)
            val srcType = qItem.ucServerType.toInt()
            val serverType = qItem.ucServerType.toInt().toChar().toString()
            val virtualNum = "" // VirtualNum은 일반적으로 빈 문자열
            witcomLog.c_write(
                loggerName,
                Level.DEBUG,
                String.format(
                    "InsertMO_NOTISEND() ServerType(%s) szSrcCID(%s) szSrcCallNo(%s) szDestCID(%s) szDestCallNo(%s) szMsgId(%s) szCB(%s) EsmClass(%d) DCSType(%d) SrcType(%d) VirtualNum(%s)",
                    serverType,
                    srcCId,
                    srcCallNo,
                    destCId,
                    destCallNo,
                    msgId,
                    cb,
                    esmClass,
                    dcsType,
                    srcType,
                    virtualNum
                ),
                workerThreadId
            )

            val moNotISend = MONotISendEntity().apply {
                this.msgId = msgId
                this.srcCId = srcCId
                this.destCId = destCId
                this.serverType = serverType
                this.node = node
                this.moSubTime = cal.time
                this.srcCallNo = srcCallNo
                this.destCallNo = destCallNo
                val expireCal = Calendar.getInstance()
                expireCal.add(Calendar.DAY_OF_MONTH, 1)
                this.expireTime = expireCal.time
                this.segment = segment
                this.tid = tid
                this.cb = cb
                this.esmClass = esmClass
                this.wZone = wZone
                this.traceId = traceId
                this.origMvnoInfo = origMvnoInfo
                this.destMvnoInfo = destMvnoInfo
                this.msgLen = msgLen
                this.dcsType = dcsType
                this.orgMsgLen = orgMsgLen
                this.moRecvTime = moRecvTime
            }

            moNotISendRepository.save(moNotISend)

            // InsertMO_NOTISEND 성공 로그 (NORMAL)
            // (msgId와 traceId는 HTTP 전송 전에 이미 생성되어 qItem에 설정되어 있으므로 재설정 불필요)
            witcomLog.c_write(
                loggerName,
                Level.INFO,
                String.format(
                    "InsertMO_NOTISEND OK : MsgId(%s) TraceId(%s) SrcCallNo(%s) DestCID(%s)",
                    msgId,
                    traceId,
                    srcCallNo,
                    destCId
                ),
                workerThreadId
            )
            0
        } catch (e: Exception) {
            // catch 블록에서는 qItem에서 직접 읽어오기
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
            witcomLog.c_write(
                loggerName,
                Level.ERROR,
                String.format(
                    "InsertMO_NOTISEND() Insert Error: srcCID(%s), srcCallNo(%s), destCID(%s), destCallNo(%s)",
                    errorSrcCId,
                    errorSrcCallNo,
                    errorDestCId,
                    errorDestCallNo
                ),
                workerThreadId
            )
            -1
        }
    }

    /**
     * Relay MO CallInfo Insert 함수 (C 코드 LINE 1636-1642 대응)
     * Relay MO는 nRsv4Protocol[0]와 [1]이 모두 0이 아닌 경우
     */
    @Transactional
    open fun insertRelayMOCallInfo(
        qItem: QITEM,
        msgHdr: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        serialNo: Long
    ): Int {
        val loggerName = "${entity.cid}-${entity.ipAddr}-${entity.portNo}"
        return try {
            // TODO: RelayMOCallInfo 엔티티 및 Repository 구현 필요
            // 현재는 로그만 출력하고 성공 반환

            witcomLog.c_write(
                loggerName,
                Level.INFO,
                String.format(
                    "InsertRelayMOCallInfo OK : MsgId(%d) SrcCallNo(%s) DestCID(%s) Relay[0]=%d, [1]=%d",
                    serialNo,
                    QItemServiceUtil.byteArrayToKString(qItem.szSrcMinNo),
                    QItemServiceUtil.byteArrayToKString(qItem.szCId),
                    qItem.nRsv4Protocol[0],
                    qItem.nRsv4Protocol[1]
                ),
                Thread.currentThread().getId()
            )

            witcomLog.c_write(
                loggerName,
                Level.WARN,
                "TODO: RelayMOCallInfo 엔티티 및 Repository 구현 필요",
                Thread.currentThread().getId()
            )

            0
        } catch (e: Exception) {
            witcomLog.c_write(
                loggerName,
                Level.ERROR,
                String.format("InsertRelayMOCallInfo() Insert Error: %s", e.message),
                Thread.currentThread().getId()
            )
            -1
        }
    }

    /**
     * 블록 알림 메시지 처리
     */
    private fun processBlockNotification(
        stQItem: QITEM,
        @Suppress("UNUSED_PARAMETER") gstQItem: QITEM,
        smsQLib: SmsQLib,
        gServerID: Int,
        witcomLog: WitcomLog,
        loggerName: String,
        workerThreadId: Long
    ) {
        val nQueueNo = 0
        val currtime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM/dd HH:mm", java.util.Locale.getDefault())
        val strtime = dateFormat.format(java.util.Date(currtime))

        stQItem.ucTermType = '1'.code.toByte()
        stQItem.usMsgCode = QTYPE_SM_REQ.toShort()
        stQItem.usMsgSubCode = SM_REQ_SIMPLE.toShort()

        val blockNotiMsgFormat = "[%s] %s로부터 차단된 메시지입니다."
        val blockNotiMsg =
            String.format(blockNotiMsgFormat, strtime, QItemServiceUtil.byteArrayToKString(stQItem.szCId))

        // Dest와 Src 교환
        val tempCid = stQItem.szCId.clone()
        System.arraycopy(stQItem.szSrcCId, 0, stQItem.szCId, 0, minOf(stQItem.szSrcCId.size, stQItem.szCId.size))
        System.arraycopy(tempCid, 0, stQItem.szSrcCId, 0, minOf(tempCid.size, stQItem.szSrcCId.size))

        val tempMin = stQItem.szMinNo.clone()
        System.arraycopy(
            stQItem.szSrcMinNo,
            0,
            stQItem.szMinNo,
            0,
            minOf(stQItem.szSrcMinNo.size, stQItem.szMinNo.size)
        )
        System.arraycopy(tempMin, 0, stQItem.szSrcMinNo, 0, minOf(tempMin.size, stQItem.szSrcMinNo.size))

        val blockNotiCidBytes = GIPEVENT_BLOCK_NOTI_CID.toByteArray(Charset.forName("CP949"))
        System.arraycopy(
            blockNotiCidBytes,
            0,
            stQItem.szSrcCId,
            0,
            minOf(blockNotiCidBytes.size, stQItem.szSrcCId.size - 1)
        )
        if (blockNotiCidBytes.size < stQItem.szSrcCId.size) {
            stQItem.szSrcCId[blockNotiCidBytes.size] = 0x00
        }

        stQItem.szSrcMinNo.fill(0x00)

        val blockNotiCallbackBytes = GIPEVENT_BLOCK_NOTI_CALLBACK.toByteArray(Charset.forName("CP949"))
        System.arraycopy(
            blockNotiCallbackBytes,
            0,
            stQItem.szCB,
            0,
            minOf(blockNotiCallbackBytes.size, stQItem.szCB.size - 1)
        )
        if (blockNotiCallbackBytes.size < stQItem.szCB.size) {
            stQItem.szCB[blockNotiCallbackBytes.size] = 0x00
        }

        val blockNotiMsgBytes = blockNotiMsg.toByteArray(Charset.forName("CP949"))
        val msgLen = minOf(blockNotiMsgBytes.size, stQItem.szMsg.size)
        System.arraycopy(blockNotiMsgBytes, 0, stQItem.szMsg, 0, msgLen)
        stQItem.ucMsgLen = msgLen
        stQItem.nVldPrd = 86400
        stQItem.ucRgtDlvFlg = 0

        // 최종 큐 삽입 로깅
        witcomLog.c_write(
            loggerName,
            Level.INFO,
            "[MO] 최종 큐 삽입: nQueueNo=$nQueueNo (Block Noti 메시지)",
            workerThreadId
        )

        val insertResult = smsQLib.InsertIntoSmsQnQNo(stQItem, nQueueNo)

        if (insertResult == Q_INSERT_SUCCESS) {
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

    /**
     * RCS TR 메시지 처리
     */
    private fun processRcsTrMessage(gstQItem: QITEM, smsQLib: SmsQLib, loggerName: String, workerThreadId: Long) {
        val rcsQItem = QItemServiceUtil.qItemToMsgHdr(gstQItem)

        val tempCidRcs = rcsQItem.szCId.clone()
        System.arraycopy(
            rcsQItem.szSrcCId,
            0,
            rcsQItem.szCId,
            0,
            minOf(rcsQItem.szSrcCId.size, rcsQItem.szCId.size)
        )
        System.arraycopy(tempCidRcs, 0, rcsQItem.szSrcCId, 0, minOf(tempCidRcs.size, rcsQItem.szSrcCId.size))

        val tempMinRcs = rcsQItem.szMinNo.clone()
        System.arraycopy(
            rcsQItem.szSrcMinNo,
            0,
            rcsQItem.szMinNo,
            0,
            minOf(rcsQItem.szSrcMinNo.size, rcsQItem.szMinNo.size)
        )
        System.arraycopy(tempMinRcs, 0, rcsQItem.szSrcMinNo, 0, minOf(tempMinRcs.size, rcsQItem.szSrcMinNo.size))

        rcsQItem.usMsgCode = QTYPE_SM_REQ.toShort()
        rcsQItem.usMsgSubCode = SUB_QTYPE_RCS_TR.toShort()
        rcsQItem.ucTermType = TERM_TYPE_KOR.code.toByte()
        rcsQItem.ucDataEncoding = DCS_TYPE_KSC5601
        rcsQItem.nVldPrd = 43200
        rcsQItem.RcsResult = RCS_RESULT_INCALIDDST.toShort()

        val rcsQueueNo = 0

        // 최종 큐 삽입 로깅 (RCS 메시지)
        witcomLog.c_write(
            loggerName,
            Level.INFO,
            "[MO] 최종 큐 삽입: rcsQueueNo=$rcsQueueNo (RCS 메시지)",
            workerThreadId
        )

        smsQLib.InsertIntoSmsQnQNo(rcsQItem, rcsQueueNo)
    }

    /**
     * 큐 재저장 처리
     */
    private fun requeueMessage(
        gstQResultObj: QueueResult,
        queueNo: Int,
        smsQLib: SmsQLib,
        loggerName: String,
        workerThreadId: Long,
        gstQItemTrans: SMReqTransResult
    ) {
        val originalQItemForRequeue = gstQResultObj.qItem
        if (originalQItemForRequeue != null) {
            val targetQueueNo = queueNo
            originalQItemForRequeue.write()

            // 최종 큐 삽입 로깅
            witcomLog.c_write(
                loggerName,
                Level.INFO,
                "[MO] 최종 큐 삽입 (재저장): targetQueueNo=$targetQueueNo, srcCID=${gstQItemTrans.srcCid}, destCID=${gstQItemTrans.destCid}",
                workerThreadId
            )

            val requeueResult = smsQLib.InsertIntoSmsQWithQNo(originalQItemForRequeue, targetQueueNo)

            if (requeueResult >= 0) {
                witcomLog.c_write(
                    loggerName,
                    Level.INFO,
                    String.format(
                        "MO 메시지 Queue 재저장 성공: result=%d, targetQueueNo=%d, srcCID(%s), destCID(%s)",
                        requeueResult,
                        targetQueueNo,
                        gstQItemTrans.srcCid,
                        gstQItemTrans.destCid
                    ),
                    workerThreadId
                )
            } else {
                witcomLog.c_write(
                    loggerName,
                    Level.ERROR,
                    String.format(
                        "MO 메시지 Queue 재저장 실패: result=%d, targetQueueNo=%d, srcCID(%s), destCID(%s)",
                        requeueResult,
                        targetQueueNo,
                        gstQItemTrans.srcCid,
                        gstQItemTrans.destCid
                    ),
                    workerThreadId
                )
            }
        }
    }

    /**
     * CP 서버로 MO 메시지를 HTTP POST로 전송합니다.
     */
    private suspend fun sendMoMessageToCp(
        moResult: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        qItem: QITEM?,
        smsQLib: SmsQLib,
        gServerID: Int,
        loggerName: String,
        workerThreadId: Long,
        segmentInfo: SegmentInfo? = null
    ): Boolean {
        val maxRetryCount = entity.rc
        val connectionTimeoutSeconds = entity.tc
        val totalAttempts = if (maxRetryCount == 0) 1 else maxRetryCount + 1

        for (attempt in 1..totalAttempts) {
            // 재시도 시도 로그
            if (attempt > 1) {
                witcomLog.c_write(
                    loggerName,
                    Level.DEBUG,
                    String.format(
                        "MO HTTP 전송 재시도: 시도(%d/%d), CP_URL(%s), MsgSeqNo(%d)",
                        attempt,
                        totalAttempts,
                        entity.cpUrl,
                        qItem?.uMsgSerialNo?.toInt() ?: 0
                    ),
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
                // 성공 시도 로그
                if (attempt > 1) {
                    witcomLog.c_write(
                        loggerName,
                        Level.DEBUG,
                        String.format(
                            "MO HTTP 전송 재시도 성공: 시도(%d/%d), CP_URL(%s)",
                            attempt,
                            totalAttempts,
                            entity.cpUrl
                        ),
                        workerThreadId
                    )
                }
                return true
            }
        }

        // 모든 재시도 실패 시: C 코드 ST_GIP_SOCK_MAX_RETRY_FAIL에 해당하는 통계 기록
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
                maxRetryCount,       // 사용된 retry count
                TID_NO_SAVE,
                LT_TRACE,
                0
            )
        }

        return false
    }

    /**
     * 실제 MO 메시지 HTTP POST 전송 수행
     */
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
        segmentInfo: SegmentInfo? = null
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // MoReportRequest DTO 생성 (CP로 전송용)
                val cid = entity.cid ?: ""
                val msgId = if (qItem != null) {
                    QItemServiceUtil.byteArrayToKString(qItem.ucMsgId)
                } else {
                    ""  // qItem이 null인 경우는 없어야 하지만 안전장치
                }
                val traceId = if (qItem != null) {
                    QItemServiceUtil.byteArrayToKString(qItem.szTraceId)
                } else {
                    ""
                }
                val gipverid = entity.gipverid ?: 510

                // MO Send Request: status는 SM_STATE_DELIVERED (2)로 설정 (전송 요청 시점)
                val moReportRequest = MoReportRequest().apply {
                    msgVerId = gipverid
                    encFlag = 0
                    data = MoReportRequest.DataBody().apply {
                        // logNo는 더 이상 사용하지 않음
                        this.cid = cid
                        this.msgId = msgId
                        this.traceId = traceId
                        this.status = SM_STATE_DELIVERED  // MO Send Request 시점: 전송 요청 상태
                    }
                }

                // MoReportRequest DTO toString 로깅
                witcomLog.c_write(
                    loggerName,
                    Level.DEBUG,
                    String.format("MO Send Request JSON: %s", moReportRequest.toString()),
                    workerThreadId
                )

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

                // HTTP 전송 시작 로그
                witcomLog.c_write(
                    loggerName,
                    Level.DEBUG,
                    String.format(
                        "MO HTTP POST 요청 전송: CP_URL(%s), 시도(%d/%d), MsgSeqNo(%d), BodySize(%d bytes)",
                        entity.cpUrl,
                        attempt,
                        totalAttempts,
                        qItem?.uMsgSerialNo?.toInt() ?: 0,
                        moReportRequest.toString().length
                    ),
                    workerThreadId
                )

                val response = client.post()
                    .uri(URI(entity.cpUrl))
                    .bodyValue(moReportRequest)
                    .exchangeToMono { clientResponse ->
                        // 응답 본문을 읽어서 로그로 출력
                        clientResponse.bodyToMono(String::class.java)
                            .defaultIfEmpty("")
                            .map { body ->
                                // [RES_SIMPLE] 형식으로 응답 로그 출력 (GIPHTTPMO_C 형식)
                                val logNo = entity.logNo?.let {
                                    String.format("%04d", it.toIntOrNull() ?: 0)
                                } ?: "0000"
                                val cpName = entity.cpName ?: ""
                                val resLog = String.format(
                                    "[GIPHTTPMO_C_%s] [RES_SIMPLE] [VSMSS#%d->%s] %s",
                                    logNo,
                                    gServerID,
                                    cpName,
                                    if (body.isNotBlank()) body else "Status: ${clientResponse.statusCode()}"
                                )
                                witcomLog.c_write(loggerName, Level.DEBUG, resLog, workerThreadId)

                                // HTTP 응답 상세 로그
                                val statusCode = clientResponse.statusCode()
                                val isSuccess = statusCode.is2xxSuccessful
                                witcomLog.c_write(
                                    loggerName,
                                    if (isSuccess) Level.DEBUG else Level.ERROR,
                                    String.format(
                                        "MO HTTP POST 응답 수신: CP_URL(%s), StatusCode(%d), 시도(%d/%d), 성공(%s), BodySize(%d bytes)",
                                        entity.cpUrl,
                                        statusCode.value(),
                                        attempt,
                                        totalAttempts,
                                        if (isSuccess) "YES" else "NO",
                                        body.length
                                    ),
                                    workerThreadId
                                )

                                statusCode
                            }
                    }
                    .timeout(Duration.ofSeconds(connectionTimeoutSeconds.toLong()))
                    .awaitSingle()

                val isSuccess = response.is2xxSuccessful

                // 최종 전송 결과 로그
                witcomLog.c_write(
                    loggerName,
                    if (isSuccess) Level.DEBUG else Level.ERROR,
                    String.format(
                        "MO HTTP 전송 최종 결과: CP_URL(%s), 시도(%d/%d), 결과(%s), StatusCode(%d)",
                        entity.cpUrl,
                        attempt,
                        totalAttempts,
                        if (isSuccess) "SUCCESS" else "FAILED",
                        response.value()
                    ),
                    workerThreadId
                )

                isSuccess
            } catch (e: Exception) {
                // dequeue 시점의 loggerName과 workerThreadId 재사용
                witcomLog.c_write(
                    loggerName,
                    Level.ERROR,
                    String.format("MO 메시지 전송 중 예외 발생: CP_URL(%s), 시도(%d/%d)", entity.cpUrl, attempt, totalAttempts),
                    workerThreadId
                )

                // 전송 한 번 실패 시: C 코드 ST_GIP_SOCK_SEND_FAIL에 해당하는 통계 기록
                if (qItem != null) {
                    smsQLib.InsqStat(
                        qItem,
                        MESSAGE_MO,
                        0,
                        gServerID,
                        MODULEID_GIPEVENT_C,
                        SERVICEID_GIPEVENT,
                        ERRORID_CP_MO_FAIL,
                        ST_GIP_SOCK_SEND_FAIL,
                        attempt,          // 현재 재시도 횟수
                        TID_NO_SAVE,
                        LT_TRACE,
                        0
                    )
                }
                false
            }
        }
    }

    /**
     * CP 서버로 MO-TR 결과를 HTTP POST로 전송합니다.
     */
    private suspend fun sendMoTrToCp(
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
            if (success) {
                return true
            }
        }
        return false
    }

    /**
     * 실제 MO-TR 결과 HTTP POST 전송 수행
     */
    suspend fun doSendMoTr(
        qItem: QITEM,
        @Suppress("UNUSED_PARAMETER") moResult: SMReqTransResult, // 파라미터는 유지하되 사용하지 않음 (호출부 호환성)
        entity: GipHttpMoAccessEntity,
        connectionTimeoutSeconds: Int,
        attempt: Int,
        totalAttempts: Int,
        loggerName: String,
        workerThreadId: Long,
        gServerID: Int
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // MoReportRequest 생성
                val msgId = QItemServiceUtil.byteArrayToKString(qItem.ucMsgId)
                val traceId = QItemServiceUtil.byteArrayToKString(qItem.szTraceId)
                val cid = entity.cid ?: ""
                val msgStatus = qItem.ucMsgStatus.toInt()

                val moReportRequest = MoReportRequest().apply {
                    msgVerId = 510
                    encFlag = 0
                    data = MoReportRequest.DataBody().apply {
                        // logNo는 더 이상 사용하지 않음
                        this.cid = cid
                        this.msgId = msgId
                        this.traceId = traceId
                        this.status = msgStatus
                    }
                }

                // MoReportRequest DTO toString 로깅
                witcomLog.c_write(
                    loggerName,
                    Level.DEBUG,
                    String.format("MO-TR Send Request JSON: %s", moReportRequest.toString()),
                    workerThreadId
                )

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

                // HTTP 전송 시작 로그 (MoReportRequest 정보 사용)
                val logNoForLog = entity.logNo?.let { String.format("%04d", it.toIntOrNull() ?: 0) } ?: "0000"
                witcomLog.c_write(
                    loggerName,
                    Level.DEBUG,
                    String.format(
                        "MO-TR HTTP POST 요청 전송: CP_URL(%s), 시도(%d/%d), logNo(%s), cid(%s), msgId(%s), status(%d), traceId(%s), BodySize(%d bytes)",
                        entity.cpUrl,
                        attempt,
                        totalAttempts,
                        logNoForLog,
                        moReportRequest.data.cid,
                        moReportRequest.data.msgId,
                        moReportRequest.data.status ?: -1,
                        moReportRequest.data.traceId,
                        moReportRequest.toString().length
                    ),
                    workerThreadId
                )

                val response = client.post()
                    .uri(URI(entity.cpUrl))
                    .bodyValue(moReportRequest)
                    .exchangeToMono { clientResponse ->
                        // 응답 본문을 읽어서 로그 출력
                        clientResponse.bodyToMono(String::class.java)
                            .defaultIfEmpty("")
                            .map { body ->
                                // MoReportRequest 정보를 사용한 응답 로그
                                val reportLogNo =
                                    entity.logNo?.let { String.format("%04d", it.toIntOrNull() ?: 0) } ?: "0000"
                                val cpName = entity.cpName ?: ""

                                // MSG_STATUS를 문자열로 변환
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
                                    reportLogNo,
                                    gServerID,
                                    cpName,
                                    reportLogNo,
                                    moReportRequest.data.cid,
                                    moReportRequest.data.msgId,
                                    statusStr,
                                    moReportRequest.data.traceId,
                                    clientResponse.statusCode().toString(),
                                    if (body.length > 200) body.substring(0, 200) + "..." else body
                                )
                                witcomLog.c_write(loggerName, Level.DEBUG, resTransResultLog, workerThreadId)
                                clientResponse.statusCode()
                            }
                    }
                    .timeout(Duration.ofSeconds(connectionTimeoutSeconds.toLong()))
                    .awaitSingle()

                response.is2xxSuccessful
            } catch (e: Exception) {
                // 에러 로깅 (MoReportRequest 정보 포함)
                val msgId = QItemServiceUtil.byteArrayToKString(qItem.ucMsgId)
                val traceId = QItemServiceUtil.byteArrayToKString(qItem.szTraceId)
                val errorLogNo = entity.logNo?.let { String.format("%04d", it.toIntOrNull() ?: 0) } ?: "0000"
                val errorCid = entity.cid ?: ""
                val msgStatus = qItem.ucMsgStatus.toInt()

                witcomLog.c_write(
                    loggerName,
                    Level.ERROR,
                    String.format(
                        "MO-TR 결과 전송 중 예외 발생: CP_URL(%s), 시도(%d/%d), LOG_NO(%s), CID(%s), MSG_ID(%s), MSG_STATUS(%d), TRACE_ID(%s), Error(%s)",
                        entity.cpUrl,
                        attempt,
                        totalAttempts,
                        errorLogNo,
                        errorCid,
                        msgId,
                        msgStatus,
                        traceId,
                        e.message
                    ),
                    workerThreadId
                )
                false
            }
        }
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

