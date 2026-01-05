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
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_SMSMOR_MO_INSQ_OK
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseMO
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseMO.DataBody as MODataBody
import com.infra.mo.skt_giphttp_mo.dto.smsController.RequestMOTR
import com.infra.mo.skt_giphttp_mo.dto.smsController.RequestMOTR.DataBody as MOTRDataBody
import com.infra.mo.skt_giphttp_mo.dto.smsController.Rsv4ProtocolItem
import com.infra.mo.skt_giphttp_mo.config.application.LiveReloadCLibraryFile
import com.infra.mo.skt_giphttp_mo.config.application.PerformanceSettings
import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.CfgEtcEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.SpcodeEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.MOCallInfoEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.MONotISendEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.CallInfoRepository
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.MOCallInfoRepository
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.MONotISendRepository
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
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.QITEM_SIZE_MSGID
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LEN_TRACE_ID
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.QTYPE_SM_REQ
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.Q_INSERT_SUCCESS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.RCS_RESULT_INCALIDDST
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_SEND
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_SIMPLE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_TRANS_RESULT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_MRMSPAM
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
import com.infra.mo.skt_giphttp_mo.service.SmsResService
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseTR
import io.netty.channel.ChannelOption
import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.awaitSingle
import java.net.URI
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
    private val smsResService: SmsResService
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
        val gServerID = System.getenv("SMSS_NO")?.trim()?.toIntOrNull() ?: 0

        witcomLog.p_write(Level.DEBUG, "🔹 MOThreadPool Partitioned result:")
        partitionedList.forEachIndexed { index, list ->
            witcomLog.p_write(
                Level.DEBUG,
                String.format("  Worker[%d] -> %s", index, list.joinToString())
            )
        }

        repeat(poolSize) { index ->
            val assignedCids = partitionedList.getOrNull(index) ?: emptyList()
            coroutineCacheCheckPool.launch {
                witcomLog.p_write(
                    Level.INFO,
                    String.format("🔹[MOThreadPool Worker-%d] 쓰레드 시작 - 이름: %s", index, Thread.currentThread().name)
                )

                while (coroutineCacheCheckPool.isActive) {
                    val smsQLib: SmsQLib = liveReloadCLibraryFile.getPreInitializedLibrary(index)
                    assignedCids.forEach { entity ->
                        val latencyNs: Long
                        val result: Int
                        var qItem = QITEM.ByReference()
                        val queueNo = entity.queueNo
                        val cpUrl = entity.cpUrl
                        val logNo = entity.logNo
                        val cid = entity.cid

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
//                            witcomLog.p_write(
//                                Level.DEBUG,
//                                String.format(
//                                    "Queue is empty. queueNo=%d",
//                                    queueNo,
//                                    if (gstQItemTrans == null) "null" else "not null",
//                                    if (gstQItem == null) "null" else "not null"
//                                )
//                            )
                            delay(500L)
                            return@forEach
                        }

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
                        
                        gstQItem = QItemServiceUtil.qItemToMsgHdr(gstQItem)
                        
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
                        val startNs = System.nanoTime()
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
                                    val nRet = checkLimitMO(gstQItemTrans, entity, witcomLog)
                                    if (nRet) {
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
                                            IF_NULL,
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

                                    val gESMCLASS = gstQItem.nRsv4Protocol[11]
                                    val gBILLTYPE = entity.billType?.trim()?.firstOrNull() ?: '0'
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
                                    
                                    // QITEM 업데이트: msgID, TraceID 생성 및 QITEM 업데이트 (HTTP 전송 전)
                                    // 큐에서 가져온 메시지의 traceID를 먼저 확인
                                    val existingTraceId = QItemServiceUtil.byteArrayToKString(gstQItem.szTraceId)
                                    val hasExistingTraceId = existingTraceId.isNotBlank() && !existingTraceId.all { it == '\u0000' }
                                    
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
                                    
                                    // 1. msgId 생성 및 QITEM 업데이트
                                    val cacheKey = "${entity.ipAddr}-${entity.portNo}"
                                    val procNo = entity.logNo?.toIntOrNull() ?: 0
                                    val msgId = msgIdCenterGenerator.generateMsgIdCenterWithTerminator(cacheKey, procNo)
                                    val msgIdBytes = msgId.toByteArray(Charsets.UTF_8)
                                    val msgIdByteArray = ByteArray(QITEM_SIZE_MSGID) { index ->
                                        if (index < msgIdBytes.size) msgIdBytes[index] else 0
                                    }
                                    System.arraycopy(msgIdByteArray, 0, gstQItem.ucMsgId, 0, QITEM_SIZE_MSGID)
                                    
                                    // 2. traceId 확인 및 검증
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
                                            IF_NULL,
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

                                    // C 코드 LINE 1769: MO 전송 성공 통계 기록 (성공(MO))
                                    // C 코드와 동일: BILLTYPE 조건 없이 LT_TRACE로 한 번만 호출
                                    witcomLog.c_write(
                                        loggerName,
                                        Level.DEBUG,
                                        String.format(
                                            "[MO 전송 성공] InsqStat 호출 시작: MODULEID_GIPEVENT_C(%d), SERVICEID_GIPEVENT(%d), ERRORID_CP_MO_SUCCESS(%d), ST_GIPEVENT_MO_OK(%d), MESSAGE_MO(%d), gServerID(%d), TraceId(%s), BILLTYPE(%c)",
                                            MODULEID_GIPEVENT_C,
                                            SERVICEID_GIPEVENT,
                                            ERRORID_CP_MO_SUCCESS,
                                            ST_GIPEVENT_MO_OK,
                                            MESSAGE_MO,
                                            gServerID,
                                            QItemServiceUtil.byteArrayToKString(gstQItem.szTraceId),
                                            gBILLTYPE
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
                                        IF_NULL,
                                        TID_NO_SAVE,  // TID_SAVE → TID_NO_SAVE (C 코드와 일치)
                                        LT_TRACE,  // C 코드와 동일하게 LT_TRACE 사용
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


                                    if (!moSendSuccess) {
                                        // 전송 실패 시 Queue 재저장 (DB Insert 금지: 중복 MO 방지)
                                        requeueMessage(gstQResultObj, queueNo, smsQLib, loggerName, workerThreadId, gstQItemTrans)
                                        return@processing
                                    }
                                    // ===== 전송 성공 후 DB Insert =====
                                    var dbInsertOk = true

                                    if (gBILLTYPE != '1' && (isNotiPlusType || isNotiType)) {
                                        // NOTI_PLUS(20,21) 또는 NOTI(90,91) && BILL_TYPE != '1' : MO_NOTISEND만 INSERT
                                        val notiRes = insertMO_NOTISEND(gstQItem, gstQItemTrans, entity, segmentInfo, workerThreadId)
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
                                                IF_NULL,
                                                TID_NO_SAVE,
                                                LT_TRACE,
                                                0
                                            )
                                        }
                                    } else if (isRelayMo) {
                                        // Relay MO: (현재 insertRelayMOCallInfo는 TODO지만, 전송 성공 후 호출)
                                        val relayRes = insertRelayMOCallInfo(gstQItem, gstQItemTrans, entity, serialNo)
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
                                                IF_NULL,
                                                TID_NO_SAVE,
                                                LT_TRACE,
                                                0
                                            )
                                        } else {
                                            gstQItem.ucServerType = VSMSS_TYPE.code.toByte()
                                        }
                                    } else {
                                        // 일반 MO(예: esm_class=1 등): MOCALLINFO
                                        // 디버깅: MOTRBILL 값 확인
                                        val gMOTRBILLForInsert = entity.moTrBill?.trim()?.equals("Y", ignoreCase = true) == true
                                        val actualMsgIdForInsert = QItemServiceUtil.byteArrayToKString(gstQItem.ucMsgId)
                                        witcomLog.c_write(
                                            loggerName,
                                            Level.DEBUG,
                                            String.format(
                                                "[DB Insert] insertGIPMOCallInfo 호출 전: MOTRBILL(%s), gMOTRBILL(%b), msgId(%s), srcCID(%s), destCID(%s)",
                                                entity.moTrBill ?: "null",
                                                gMOTRBILLForInsert,
                                                actualMsgIdForInsert,
                                                QItemServiceUtil.byteArrayToKString(gstQItem.szSrcCId),
                                                QItemServiceUtil.byteArrayToKString(gstQItem.szCId)
                                            ),
                                            workerThreadId
                                        )
                                        
                                        val callRes = insertGIPMOCallInfo(gstQItem, gstQItemTrans, entity, workerThreadId)
                                        
                                        witcomLog.c_write(
                                            loggerName,
                                            if (callRes >= 0) Level.INFO else Level.ERROR,
                                            String.format(
                                                "[DB Insert] insertGIPMOCallInfo 호출 결과: 반환값(%d), MOTRBILL(%s), msgId(%s), 성공여부(%b)",
                                                callRes,
                                                entity.moTrBill ?: "null",
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
                                                IF_NULL,
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
                                    
                                    // ===== MOTRBILL='N'일 때 즉시 과금 처리 (HTTP 전송 성공 = MO-ACK 성공으로 간주) =====
                                    // C 코드 참고: GIPEVENT_c.c LINE 2110-2244 (!gMOTRBILL 블록)
                                    // HTTP 환경에서는 MO-ACK 요청을 별도로 받지 않고, HTTP 전송 성공 시점에 MO-ACK 처리 로직을 실행
                                    val gMOTRBILL = entity.moTrBill?.trim()?.equals("Y", ignoreCase = true) == true
                                    
                                    // 디버깅: MOTRBILL 값과 조건 확인
                                    val actualMsgId = QItemServiceUtil.byteArrayToKString(gstQItem.ucMsgId)
                                    witcomLog.c_write(
                                        loggerName,
                                        Level.INFO,
                                        String.format(
                                            "[MO 전송 성공] MOTRBILL 분기 체크: moTrBill(%s), gMOTRBILL(%b), dbInsertOk(%b), moSendSuccess(%b), MsgId(%s)",
                                            entity.moTrBill ?: "null",
                                            gMOTRBILL,
                                            dbInsertOk,
                                            moSendSuccess,
                                            actualMsgId
                                        ),
                                        workerThreadId
                                    )
                                    
                                    if (!gMOTRBILL && dbInsertOk && moSendSuccess) {
                                        // HTTP 전송 성공 = MO-ACK 성공으로 간주
                                        // C 코드 LINE 2110-2244: !gMOTRBILL 블록의 과금 처리 로직 실행
                                        witcomLog.c_write(
                                            loggerName,
                                            Level.INFO,
                                            String.format(
                                                "[MO 전송 성공] MOTRBILL='N' 분기: 즉시 과금 처리 시작 - MsgId(%s), srcCID(%s), destCID(%s)",
                                                actualMsgId,
                                                QItemServiceUtil.byteArrayToKString(gstQItem.szSrcCId),
                                                QItemServiceUtil.byteArrayToKString(gstQItem.szCId)
                                            ),
                                            workerThreadId
                                        )
                                        
                                        try {
                                            // processMOBilling() 호출을 위한 ResponseTR 객체 생성
                                            // HTTP 전송 성공 = MO-ACK 성공 (GI_RES_NO_ERR = 0)
                                            val dataBody = ResponseTR.DataBody()
                                            dataBody.srcCID = QItemServiceUtil.byteArrayToKString(gstQItem.szSrcCId)
                                            dataBody.srcCallNo = QItemServiceUtil.byteArrayToKString(gstQItem.szMinNo)
                                            dataBody.srcAddrRsv = 0
                                            dataBody.destCID = QItemServiceUtil.byteArrayToKString(gstQItem.szCId)
                                            dataBody.destCallNo = QItemServiceUtil.byteArrayToKString(gstQItem.szMinNo)
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
                                            dataBody.msgId = actualMsgId  // msgId 필수 (MOCALLINFO 조회용) - ucMsgId에서 실제 msgId 사용
                                            
                                            val moAckRequest = ResponseTR()
                                            moAckRequest.msgVerId = 1
                                            moAckRequest.encFlag = 0
                                            moAckRequest.data = dataBody
                                            
                                            // processMOBilling() 호출 (과금 처리 + 삭제)
                                            smsResService.processMOBilling(
                                                gstQItem,
                                                moAckRequest,
                                                entity,
                                                smsQLib,
                                                loggerName
                                            )
                                            
                                            witcomLog.c_write(
                                                loggerName,
                                                Level.INFO,
                                                String.format(
                                                    "[MO 전송 성공] MOTRBILL='N' 분기: 즉시 과금 처리 완료 - MsgId(%s)",
                                                    actualMsgId
                                                ),
                                                workerThreadId
                                            )
                                        } catch (e: Exception) {
                                            witcomLog.c_write(
                                                loggerName,
                                                Level.ERROR,
                                                String.format(
                                                    "[MO 전송 성공] MOTRBILL='N' 분기: 즉시 과금 처리 실패 - MsgId(%s), error(%s)",
                                                    actualMsgId,
                                                    e.message
                                                ),
                                                workerThreadId
                                            )
                                        }
                                    } else if (!gMOTRBILL && (!dbInsertOk || !moSendSuccess)) {
                                        witcomLog.c_write(
                                            loggerName,
                                            Level.INFO,  // DEBUG → INFO로 변경하여 항상 출력
                                            String.format(
                                                "[MO 전송 성공] MOTRBILL='N' 분기: 즉시 과금 처리 스킵 - dbInsertOk(%b), moSendSuccess(%b), MsgId(%s)",
                                                dbInsertOk,
                                                moSendSuccess,
                                                actualMsgId
                                            ),
                                            workerThreadId
                                        )
                                    } else if (gMOTRBILL) {
                                        // MOTRBILL='Y'인 경우 로그 추가
                                        // 저장된 MOCALLINFO 확인 (MO-TR 요청 시 조회 가능 여부 확인)
                                        val verifyMoCallInfo = moCallInfoRepository.findByMsgId(actualMsgId)
                                        witcomLog.c_write(
                                            loggerName,
                                            Level.INFO,
                                            String.format(
                                                "[MO 전송 성공] MOTRBILL='Y' 분기: 즉시 과금 처리 스킵 (MO-TR 요청 대기) - MsgId(%s), MOCALLINFO 존재여부(%b)",
                                                actualMsgId,
                                                verifyMoCallInfo != null
                                            ),
                                            workerThreadId
                                        )
                                        if (verifyMoCallInfo == null) {
                                            witcomLog.c_write(
                                                loggerName,
                                                Level.ERROR,
                                                String.format(
                                                    "[MO 전송 성공] ⚠️ MOTRBILL='Y' 경고: MOCALLINFO가 DB에 없음 - MsgId(%s), MO-TR 요청 시 조회 불가능",
                                                    actualMsgId
                                                ),
                                                workerThreadId
                                            )
                                        }
                                    }

                                    // C 코드 LINE 2163-2195: VCDR bprintf 과금 데이터 출력
                                    // MOTRBILL='Y'인 경우에만 호출 (MOTRBILL='N'인 경우는 processMOBilling에서 이미 호출함)
                                    // 주의: GIPHTTP 프로젝트에서는 COLORSMS("200"), AVATASMS("1584") 지원하지 않음 (계약상 불필요)
                                    if (gMOTRBILL) {
                                        val destCId = gstQItemTrans.destCid
                                        val srcCId = gstQItemTrans.srcCid
                                        val srcCallNo = gstQItemTrans.srcMinNo.toLongOrNull() ?: 0L
                                        val msgSerialNo = gstQItem.uMsgSerialNo.toInt()
                                        
                                        // C 코드 LINE 2115-2116: buf (시간 포맷)
                                        val now = Calendar.getInstance()
                                        val tempTime = now.timeInMillis / 1000 - 15
                                        val tempDate = Date(tempTime * 1000)
                                        val buf = SimpleDateFormat("yyMMddHHmmss").format(tempDate)
                                        
                                        // C 코드 LINE 2116: szUsec (마이크로초)
                                        val szUsec = String.format("%.4f", (now.get(Calendar.MILLISECOND) / 1000.0)).substring(2)
                                        
                                        // C 코드 LINE 2094-2114: CDRCId (DestCID에서 가져옴)
                                        val CDRCId = destCId
                                        
                                        // C 코드 LINE 2096: szServerNo (환경변수에서 가져오거나 빈 문자열)
                                        val szServerNo = System.getenv("SMSS_NO") ?: ""
                                        
                                        // C 코드 LINE 2003-2004, 2122-2123: SelectGIPMOCallInfo에서 가져오는 값들
                                        // 이미 insertGIPMOCallInfo에서 준비된 값들을 사용
                                        val moSubTime = try {
                                            val cal = Calendar.getInstance()
                                            val sdf = SimpleDateFormat("yyMMddHHmmss")
                                            sdf.format(cal.time) + String.format("%02d", cal.get(Calendar.MILLISECOND) / 10)
                                        } catch (e: Exception) {
                                            ""
                                        }
                                        
                                        val moRecvTimeRaw = QItemServiceUtil.byteArrayToKString(gstQItem.szMoRecvTime)
                                        val MORecvTime = if (moRecvTimeRaw.isBlank()) {
                                            val calRecv = Calendar.getInstance()
                                            val sdfRecv = SimpleDateFormat("yyMMddHHmmss")
                                            sdfRecv.format(calRecv.time) + String.format("%02d", calRecv.get(Calendar.MILLISECOND) / 10)
                                        } else {
                                            moRecvTimeRaw
                                        }
                                        
                                        // C 코드 LINE 2122: nRoaming, RoamPMN, CallBack, cWZone
                                        val RoamingIndFlag = RoamingIndChk(gstQItem.szSMS_OSFI[0])
                                        var nRoaming = 0
                                        if (gstQItem.nRsv4Protocol[11] == CDMA_ROAMING || gstQItem.nRsv4Protocol[11] == PORTED_CDMA_ROAMING) {
                                            nRoaming = if (RoamingIndFlag == 1) 19 else 17
                                        } else if (gstQItem.nRsv4Protocol[11] == GSM_WCDMA_ROAMING || gstQItem.nRsv4Protocol[11] == PORTED_GSM_WCDMA_ROAMING) {
                                            nRoaming = if (RoamingIndFlag == 1) 20 else 18
                                        }
                                        
                                        val RoamPMN = if (nRoaming != 0 && gstQItem.nRsv4Protocol[10] != 0) {
                                            gstQItem.nRsv4Protocol[10].toString()
                                        } else {
                                            ""
                                        }
                                        
                                        val CallBack = QItemServiceUtil.byteArrayToKString(gstQItem.szCB)
                                        val cWZone = if (gstQItem.ucRsv4Dlv.isNotEmpty()) {
                                            gstQItem.ucRsv4Dlv[1].toInt().toChar()
                                        } else {
                                            '0'
                                        }
                                        
                                        // C 코드 LINE 2149-2154: nRcs
                                        val rcsTag = QItemServiceUtil.byteArrayToKString(gstQItem.RcsTag)
                                        val nRcs = if (rcsTag.isNotEmpty()) {
                                            RCS_TO_SMS
                                        } else {
                                            COMMON_SMS
                                        }
                                        
                                        // C 코드 LINE 2190: bprintf 호출 (일반 DestCID)
                                        // GIPHTTP 프로젝트에서는 COLORSMS/AVATASMS 구분 없이 항상 일반 포맷 사용
                                        smsQLib.bprintf(
                                            ";%d;;;%d;%s;%s%d;%s;;11;%08d;%s;%s;%s%s;%d;%d;%d;;;;;;%s;%s;0;%c;;%s;%s;%d;0;;\n",
                                            CALL_TYPE_MO,
                                            gstQItem.nModuleNo,
                                            szServerNo,
                                            srcCId,
                                            srcCallNo,
                                            CDRCId,
                                            msgSerialNo,
                                            MORecvTime,
                                            moSubTime,
                                            buf,
                                            szUsec,
                                            MSG_DELEVER_OK,
                                            gstQItem.ucMsgLen.toInt(),
                                            nRoaming,
                                            RoamPMN,
                                            CallBack,
                                            cWZone,
                                            QItemServiceUtil.byteArrayToKString(gstQItem.szOrigMvnoInformation),
                                            QItemServiceUtil.byteArrayToKString(gstQItem.szDestMvnoInformation),
                                            nRcs
                                        )
                                    } else {
                                        // MOTRBILL='N'인 경우: processMOBilling에서 이미 bprintf 호출했으므로 여기서는 스킵
                                        witcomLog.c_write(
                                            loggerName,
                                            Level.DEBUG,
                                            String.format(
                                                "[MO 전송 성공] MOTRBILL='N' 분기: bprintf 스킵 (processMOBilling에서 이미 호출됨) - MsgId(%s)",
                                                actualMsgId
                                            ),
                                            workerThreadId
                                        )
                                    }




                                    val insqStatinsqStatST_GIPEVENT_MO_OK = smsQLib.InsqStat(
                                        gstQItem,
                                        MESSAGE_MO,
                                        0,
                                        gServerID,
                                        MODULEID_GIPEVENT_C,
                                        SERVICEID_GIPEVENT,
                                        ERRORID_CP_MO_SUCCESS,
                                        ST_GIPEVENT_MOACK_BILL_OK,
                                        IF_NULL,
                                        TID_NO_SAVE,
                                        LT_BOTH,  // HTTP 전송 성공 시 통계에 15 값 기록
                                        0
                                    )

                                    // 디버그 로그: InsqStat 호출 결과 상세 확인
                                    val insqStatResultIntinsqStatST_GIPEVENT_MO_OK = insqStatinsqStatST_GIPEVENT_MO_OK
                                    val isInsqStatSuccessinsqStatST_GIPEVENT_MO_OK = insqStatResultIntinsqStatST_GIPEVENT_MO_OK == 1
                                    val ucServerTypeAfterCallinsqStatST_GIPEVENT_MO_OK = gstQItem.ucServerType.toInt().toChar()

                                    witcomLog.c_write(
                                        loggerName,
                                        if (isInsqStatSuccessinsqStatST_GIPEVENT_MO_OK) Level.DEBUG else Level.ERROR,
                                        String.format(
                                            "[TRC 전송 성공] ST_GIPEVENT_MO_OK 호출 결과 상세: 반환값(%d), 성공여부(%b), ucServerType(%c), DestCID(%s), SrcCID(%s), MessageType(%d)",
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
                                                "[TRC 전송 성공] ❌ ST_GIPEVENT_MO_OK 실패: 반환값(%d) - 통계 큐 삽입 실패 가능성. C 라이브러리 에러 로그 확인 필요: [ERROR] InsertIntoSmsQStat : invalid nQNo 또는 [ERROR] GetQNoFromStatRouteTblWithCid() : Unknown ServerType",
                                                insqStatResultIntinsqStatST_GIPEVENT_MO_OK
                                            ),
                                            workerThreadId
                                        )
                                    } else {
                                        witcomLog.c_write(
                                            loggerName,
                                            Level.DEBUG,
                                            String.format(
                                                "[TRC 전송 성공] ✅ ST_GIPEVENT_MO_OK 성공: MODULEID_GIPEVENT_C(%d), ST_GIPEVENT_MO_OK(%d) - 통계 큐 삽입 완료, GIPEVENT_C 성공(MO) 로그 생성 예상 (smstrc.sh에서 확인 필요)",
                                                MODULEID_GIPEVENT_C,
                                                ST_GIPEVENT_MO_OK
                                            ),
                                            workerThreadId
                                        )
                                    }
                                }

                                SM_REQ_TRANS_RESULT -> {
                                    // HTTP POST로 CP 서버에 MO-TR 결과 전송
                                    // C 오리지널: MO-TR 전송은 gMOTRBILL과 gBILLTYPE에 따라 결정됨 (REPLY_FLAG와 무관)
                                    val moTrSendSuccess = sendMoTrToCp(gstQItem, gstQItemTrans, entity, loggerName, workerThreadId)

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
                                            IF_NULL,
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
                                        requeueMessage(gstQResultObj, queueNo, smsQLib, loggerName, workerThreadId, gstQItemTrans)
                                    }
                                }
                            }
                        }
                        latencyNs = System.nanoTime() - startNs
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
            DCS_TYPE_DEC_GSM7, DCS_TYPE_GSM7.toInt() -> "GSM7"
            DCS_TYPE_DEC_ASCII7, DCS_TYPE_ASCII7.toInt() -> "ASCII7"
            DCS_TYPE_DEC_8BIT, DCS_TYPE_8BIT.toInt() -> "8BIT"
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
        val gBILLTYPE = entity.billType?.trim()?.firstOrNull() ?: '0'
        // val gREPLY_FLAG = entity.replyFlag?.trim()?.firstOrNull() ?: 'Y'  // 제거: REPLY_FLAG 변수
        val gMOTRBILL = entity.moTrBill?.trim()?.firstOrNull() ?: 'N'
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
     */
    private fun checkLimitMO(
        msgHdr: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        witcomLog: WitcomLog
    ): Boolean {
        val gMOLimit = (entity.limitCheckFlag == "Y" || entity.limitCheckFlag == "y") &&
                entity.billType == BILLTYPE_SRC.toString()

        if (!gMOLimit) {
            return false //한도차단여부가 비활성화됨
        }

        val szMdn = "${msgHdr.srcCid}${msgHdr.srcMinNo}"
        val loggerName = "${entity.cid}-${entity.ipAddr}-${entity.portNo}"

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
            val srcCId = QItemServiceUtil.byteArrayToKString(qItem.szSrcCId)
            val srcCallNo = QItemServiceUtil.byteArrayToKString(qItem.szSrcMinNo)
            val destCId = QItemServiceUtil.byteArrayToKString(qItem.szCId)
            val destCallNo = QItemServiceUtil.byteArrayToKString(qItem.szMinNo)
            
            // MSGID: HTTP 전송 전에 이미 생성되어 qItem.ucMsgId에 설정되어 있으므로, 그것을 사용
            // 만약 비어있다면 새로 생성 (안전장치)
            var msgId = QItemServiceUtil.byteArrayToKString(qItem.ucMsgId)
            if (msgId.isBlank()) {
                // 안전장치: msgId가 비어있으면 새로 생성
                val cacheKey = "${entity.ipAddr}-${entity.portNo}"
                val procNo = entity.logNo?.toIntOrNull() ?: 0
                msgId = msgIdCenterGenerator.generateMsgIdCenterWithTerminator(cacheKey, procNo)
                // 새로 생성한 msgId를 qItem.ucMsgId에 설정
                val msgIdBytes = msgId.toByteArray(Charsets.UTF_8)
                val msgIdByteArray = ByteArray(QITEM_SIZE_MSGID) { index ->
                    if (index < msgIdBytes.size) msgIdBytes[index] else 0
                }
                System.arraycopy(msgIdByteArray, 0, qItem.ucMsgId, 0, QITEM_SIZE_MSGID)
            }

            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyMMddHHmmss")
            val moSubTime = sdf.format(cal.time) + String.format("%02d", cal.get(Calendar.MILLISECOND) / 10)

            val msgLen = qItem.ucMsgLen.toInt()

            // C 코드 LINE 1851-1864: RoamingIndFlag 기반 로밍 ID 결정
            val RoamingIndFlag = RoamingIndChk(qItem.szSMS_OSFI[0])
            var v_ucRoaming: Int = 0

            if (qItem.nRsv4Protocol[11] == CDMA_ROAMING || qItem.nRsv4Protocol[11] == PORTED_CDMA_ROAMING) {
                if (RoamingIndFlag == 1)
                    v_ucRoaming = 19  // CDMA 로밍 Roaming_ind
                else
                    v_ucRoaming = 17  // CDMA 로밍
            } else if (qItem.nRsv4Protocol[11] == GSM_WCDMA_ROAMING || qItem.nRsv4Protocol[11] == PORTED_GSM_WCDMA_ROAMING) {
                if (RoamingIndFlag == 1)
                    v_ucRoaming = 20  // GSM/WCDMA 로밍 Roaming_ind
                else
                    v_ucRoaming = 18  // GSM/WCDMA 로밍
            } else {
                v_ucRoaming = 0
            }

            val cb = QItemServiceUtil.byteArrayToKString(qItem.szCB)
            val roamPMN = if (v_ucRoaming != 0 && qItem.nRsv4Protocol[10] != 0) {
                qItem.nRsv4Protocol[10].toString()
            } else {
                "0"
            }

            val wZone = if (qItem.ucRsv4Dlv.isNotEmpty()) {
                qItem.ucRsv4Dlv[1].toInt().toChar().toString()
            } else {
                ""
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
            val origMvnoInfo = QItemServiceUtil.byteArrayToKString(qItem.szOrigMvnoInformation)
            val destMvnoInfo = QItemServiceUtil.byteArrayToKString(qItem.szDestMvnoInformation)
            val rcs = QItemServiceUtil.byteArrayToKString(qItem.RcsTag)

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
            }

            moCallInfoRepository.save(moCallInfo)
            moCallInfoRepository.flush()  // 즉시 DB에 반영하여 다른 트랜잭션에서 조회 가능하도록 함

            // 저장 후 즉시 조회하여 검증
            val savedMoCallInfo = moCallInfoRepository.findByMsgId(msgId)
            if (savedMoCallInfo != null) {
                witcomLog.c_write(
                    loggerName,
                    Level.INFO,
                    String.format(
                        "InsertGIPMOCallInfo OK : MsgId(%s) TraceId(%s) SrcCallNo(%s) DestCID(%s) - 저장 후 조회 성공 (검증 완료)",
                        msgId,
                        traceId,
                        srcCallNo,
                        destCId
                    ),
                    workerThreadId
                )
            } else {
                witcomLog.c_write(
                    loggerName,
                    Level.ERROR,
                    String.format(
                        "InsertGIPMOCallInfo FAIL : MsgId(%s) TraceId(%s) SrcCallNo(%s) DestCID(%s) - 저장 후 조회 실패 (DB에 저장되지 않음)",
                        msgId,
                        traceId,
                        srcCallNo,
                        destCId
                    ),
                    workerThreadId
                )
                return -1  // 저장 후 조회 실패 시 에러 반환
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
            witcomLog.c_write(
                loggerName,
                Level.ERROR,
                String.format(
                    "InsertGIPMOCallInfo() Insert Error: srcCID(%s), srcCallNo(%s), destCID(%s), destCallNo(%s)",
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
            
            // MSGID: HTTP 전송 전에 이미 생성되어 qItem.ucMsgId에 설정되어 있으므로, 그것을 사용
            var msgId = QItemServiceUtil.byteArrayToKString(qItem.ucMsgId)
            if (msgId.isBlank()) {
                // 안전장치: msgId가 비어있으면 새로 생성
                val cacheKey = "${entity.ipAddr}-${entity.portNo}"
                val procNo = entity.logNo?.toIntOrNull() ?: 0
                msgId = msgIdCenterGenerator.generateMsgIdCenterWithTerminator(cacheKey, procNo)
                // 새로 생성한 msgId를 qItem.ucMsgId에 설정
                val msgIdBytes = msgId.toByteArray(Charsets.UTF_8)
                val msgIdByteArray = ByteArray(QITEM_SIZE_MSGID) { index ->
                    if (index < msgIdBytes.size) msgIdBytes[index] else 0
                }
                System.arraycopy(msgIdByteArray, 0, qItem.ucMsgId, 0, QITEM_SIZE_MSGID)
            }
            val node = System.getenv("SMSS_NODE") ?: System.getenv("HOSTNAME") ?: "UNKNOWN"

            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyMMddHHmmss")
            val usec = String.format("%02d", cal.get(Calendar.MILLISECOND) / 10)
            val moSubTime = sdf.format(cal.time) + usec

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
        gstQItem: QITEM,
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
        System.arraycopy(rcsQItem.szSrcCId, 0, rcsQItem.szCId, 0, minOf(rcsQItem.szSrcCId.size, rcsQItem.szCId.size))
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
        val originalQItemForRequeue = gstQResultObj.qItem as? QITEM
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
                val responseMO = ResponseMO().apply {
                    msgVerId = moResult.msgVerId
                    encFlag = 0

                    data = MODataBody().apply {
                        srcCID = moResult.srcCid
                        srcCallNo = moResult.srcMinNo
                        srcAddrRsv = 0
                        destCID = moResult.destCid
                        destCallNo = moResult.destMinNo
                        destAddrRsv = 0
                        // QITEM에서 변경된 MsgCode, MsgSubCode 사용 (MOCALLINFO 저장을 위해 변경된 값)
                        msgCode = moResult.msgCode  // QTYPE_SM_REQ (11)
                        msgSubCode = moResult.msgSubCode  // SM_REQ_SIMPLE (10)
                        bodyDataLen = moResult.msgLen
                        msgSeqNo = if (qItem != null && qItem.uMsgSerialNo > 0) qItem.uMsgSerialNo.toInt() else 0
                        termtype = moResult.termType.toString()
                        dataType = 0.toByte()
                        dataEncoding = moResult.dataEncoding.toInt()

                        // Segment 정보 설정 (C 코드 LINE 1601-1613 대응)
                        concatenateflag = if (segmentInfo != null && segmentInfo.isValid) {
                            segmentInfo.msgRefId.toString()
                        } else {
                            "255"  // 0xFF (무효)
                        }
                        concatenateInfo = if (segmentInfo != null && segmentInfo.isValid) {
                            "${segmentInfo.totalSeg}:${segmentInfo.segSeq}"
                        } else {
                            "15:15"  // 0x0F:0x0F (무효)
                        }

                        rsv4Protocol = moResult.rsv4Protocol.map { value ->
                            Rsv4ProtocolItem(value)
                        }
                        nVldPrd = moResult.vldPrd
                        // C 오리지널 의미(REPLY_FLAG='N'이면 receipt 미요청) 반영: CP에도 ucRgtDlvFlg=0으로 전달
                        // REPLY_FLAG 제거로 인해 항상 원본 메시지의 rgtDlvFlg 사용
                        // val gREPLY_FLAG = entity.replyFlag?.trim()?.firstOrNull() ?: 'Y'  // 제거: REPLY_FLAG 변수
                        // ucRgtDlvFlg = if (gREPLY_FLAG == 'N' || gREPLY_FLAG == 'n') {
                        //     0
                        // } else {
                        //     moResult.rgtDlvFlg
                        // }
                        ucRgtDlvFlg = moResult.rgtDlvFlg  // REPLY_FLAG 제거로 인해 항상 원본 메시지의 rgtDlvFlg 사용
                        callback = moResult.callback
                        msgLen = moResult.msgLen.toByte()
                        msg = moResult.msg
                        orgMsgTotalLen = if (qItem != null && qItem.uOrgMsgLen > 0) {
                            qItem.uOrgMsgLen.toByte()
                        } else {
                            1.toByte()
                        }
                        // MOCALLINFO 저장 시 사용된 msgId 추가 (CP가 mo-report 응답 시 조회용)
                        msgId = if (qItem != null) {
                            QItemServiceUtil.byteArrayToKString(qItem.ucMsgId)
                        } else {
                            ""  // qItem이 null인 경우는 없어야 하지만 안전장치
                        }
                    }
                }

                // MO 전송 DTO JSON 로깅 (dequeue 시점의 loggerName과 workerThreadId 재사용)
                val objectMapper = ObjectMapper()
                val jsonPayload = objectMapper.writeValueAsString(responseMO)
                witcomLog.c_write(
                    loggerName,
                    Level.DEBUG,
                    String.format("MO Send Request JSON: %s", jsonPayload),
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
                        jsonPayload.length
                    ),
                    workerThreadId
                )
                
                val response = client.post()
                    .uri(URI(entity.cpUrl))
                    .bodyValue(responseMO)
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
            val success = doSendMoTr(qItem, moResult, entity, connectionTimeoutSeconds, attempt, totalAttempts, loggerName, workerThreadId, gServerID)
            if (success) {
                return true
            }
        }
        return false
    }

    /**
     * 실제 MO-TR 결과 HTTP POST 전송 수행
     */
    private suspend fun doSendMoTr(
        qItem: QITEM,
        moResult: SMReqTransResult,
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
                val requestMOTR = RequestMOTR().apply {
                    msgVerId = moResult.msgVerId
                    encFlag = 0

                    data = MOTRDataBody().apply {
                        srcCID = moResult.srcCid
                        srcCallNo = moResult.srcMinNo
                        srcAddrRsv = 0
                        destCID = moResult.destCid
                        destCallNo = moResult.destMinNo
                        destAddrRsv = 0
                        msgCode = SM_REQ_MO.toShort()
                        msgSubCode = SM_REQ_SEND.toShort()
                        bodyDataLen = 11
                        msgSeqNo = if (qItem.uMsgSerialNo > 0) qItem.uMsgSerialNo.toInt() else 0
                        termtype = moResult.termType.toString()
                        dataType = 0.toByte()
                        dataEncoding = moResult.dataEncoding.toInt()
                        concatenateflag = (qItem.ucRsv[0].toInt() and 0xFF).toString()
                        concatenateInfo = (qItem.ucRsv[1].toInt() and 0xFF).toString()
                        rsv4Protocol = moResult.rsv4Protocol.map { value ->
                            Rsv4ProtocolItem(value)
                        }
                        val now = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                        time = String.format(
                            "%02d%02d%02d%02d%02d",
                            now.year % 100,
                            now.monthValue,
                            now.dayOfMonth,
                            now.hour,
                            now.minute
                        )
                        msgStatus = qItem.ucMsgStatus
                        rsv = qItem.ucGSMErrCode
                        msgId = QItemServiceUtil.byteArrayToKString(qItem.ucMsgId)
                    }
                }

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

                val response = client.post()
                    .uri(URI(entity.cpUrl))
                    .bodyValue(requestMOTR)
                    .exchangeToMono { clientResponse ->
                        // 응답 본문을 읽어서 RES_TRANS_RESULT 로그 출력
                        clientResponse.bodyToMono(String::class.java)
                            .defaultIfEmpty("")
                            .map { body ->
                                // [RES_TRANS_RESULT] 형식으로 응답 로그 출력
                                val logNo = entity.logNo?.let {
                                    String.format("%04d", it.toIntOrNull() ?: 0)
                                } ?: "0000"
                                val cpName = entity.cpName ?: ""
                                
                                // DataEncoding 포맷팅
                                val dataEncoding = formatDataEncoding(moResult.dataEncoding)
                                
                                // Status를 문자열로 변환
                                val statusStr = when (qItem.ucMsgStatus.toInt()) {
                                    2 -> "2:DELIVERED"
                                    3 -> "3:EXPIRED"
                                    5 -> "5:UNDELIVERED"
                                    14 -> "14:FWDFAIL"
                                    16 -> "16:SPAMERR"
                                    17 -> "17:USERDEL"
                                    19 -> "19:NPREFIX"
                                    20 -> "20:ADMCANC"
                                    else -> "${qItem.ucMsgStatus.toInt()}:UNKNOWN"
                                }
                                
                                // Result 값 (응답 본문에서 파싱하거나 기본값 사용)
                                val resultValue = if (body.isNotBlank()) {
                                    try {
                                        val objectMapper = ObjectMapper()
                                        val jsonNode = objectMapper.readTree(body)
                                        jsonNode.get("data")?.get("result")?.asInt() ?: 0
                                    } catch (e: Exception) {
                                        0
                                    }
                                } else {
                                    0
                                }
                                val resultStr = when (resultValue) {
                                    0 -> "0:GI_RES_NO_ERROR"
                                    else -> "$resultValue:ERROR"
                                }
                                
                                val resTransResultLog = String.format(
                                    "[GIPALL_C_%s] [RES_TRANS_RESULT] [VSMSS#%d->%s] MsgVerId(%d) SrcCId(%s) SrcCallNo(%s) DestCId(%s) DestCallNo(%s) MsgCode(12) MsgSubCode(9) TId(%d) BodyDataLen(11) MsgSeqNo(%d) DataEncoding(%s) TermType(%d) ConcatenateFlag(%s) ConcatenateInfo(%s) Result(%s)",
                                    logNo,
                                    gServerID,
                                    cpName,
                                    moResult.msgVerId,
                                    moResult.srcCid,
                                    moResult.srcMinNo,
                                    moResult.destCid,
                                    moResult.destMinNo,
                                    qItem.usMsgCodeReserved[0].toInt(),
                                    if (qItem.uMsgSerialNo > 0) qItem.uMsgSerialNo.toInt() else 0,
                                    dataEncoding,
                                    moResult.termType.toInt(),
                                    (qItem.ucRsv[0].toInt() and 0xFF).toString(),
                                    (qItem.ucRsv[1].toInt() and 0xFF).toString(),
                                    resultStr
                                )
                                witcomLog.c_write(loggerName, Level.DEBUG, resTransResultLog, workerThreadId)
                                clientResponse.statusCode()
                            }
                    }
                    .timeout(Duration.ofSeconds(connectionTimeoutSeconds.toLong()))
                    .awaitSingle()

                response.is2xxSuccessful
            } catch (e: Exception) {
                // dequeue 시점의 loggerName과 workerThreadId 재사용
                witcomLog.c_write(
                    loggerName,
                    Level.ERROR,
                    String.format("MO-TR 결과 전송 중 예외 발생: CP_URL(%s), 시도(%d/%d)", entity.cpUrl, attempt, totalAttempts),
                    workerThreadId
                )
                false
            }
        }
    }

    /**
     * 애플리케이션 종료 시 코루틴 스코프를 취소하여 리소스 정리
     */
    @PreDestroy
    fun cleanup() {
        witcomLog.p_write(Level.INFO, "MOThreadPool cleanup: 코루틴 스코프 취소 중...")
        coroutineScope?.cancel()
        coroutineScope = null
        witcomLog.p_write(Level.INFO, "MOThreadPool cleanup: 코루틴 스코프 취소 완료")
    }
}

