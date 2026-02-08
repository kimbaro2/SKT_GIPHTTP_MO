package com.infra.mo.skt_giphttp_mo.service.impl

import ch.qos.logback.classic.Level
import com.infra.mo.skt_giphttp_mo.dto.SegmentInfo
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_GIVEBILL_LIMIT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_FAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_LIMIT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MESSAGE_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MSG_CODE_SM_RES
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MODULEID_GIPEVENT_C
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SERVICEID_GIPEVENT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_SIMPLE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIP_MO_LIMIT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.TID_NO_SAVE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LT_BOTH
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LT_TRACE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.VSMSS_TYPE
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_DB_INS_FAIL_GIPMOCALLINFO
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_DB_NO_DATA_MONOTISEND
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_GIP_MT_LIMIT_GIFT
import com.infra.mo.skt_giphttp_mo.dto.smsController.Rsv4ProtocolItem
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseTR
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.CfgEtcEntity
import com.infra.mo.skt_giphttp_mo.service.MoBillTypeService
import com.infra.mo.skt_giphttp_mo.service.MoBlockNotificationService
import com.infra.mo.skt_giphttp_mo.service.MoDbInsertService
import com.infra.mo.skt_giphttp_mo.service.MoGipEventLogService
import com.infra.mo.skt_giphttp_mo.service.MoLimitCheckService
import com.infra.mo.skt_giphttp_mo.service.MoQItemUtilService
import com.infra.mo.skt_giphttp_mo.service.MoRequeueService
import com.infra.mo.skt_giphttp_mo.service.MoRcsTrService
import com.infra.mo.skt_giphttp_mo.service.MoSendToCpService
import com.infra.mo.skt_giphttp_mo.service.SmsResService
import com.infra.mo.skt_giphttp_mo.service.handler.MoServiceType
import com.infra.mo.skt_giphttp_mo.service.handler.MoServiceTypeResolver
import com.infra.mo.skt_giphttp_mo.service.mo.IMoProcessorOps
import com.infra.mo.skt_giphttp_mo.service.mo.MoServiceContext
import com.infra.mo.skt_giphttp_mo.service.mo.MoServiceHandlerRegistry
import com.infra.mo.skt_giphttp_mo.service.mo.MoServiceTypeAwareHandler
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil.QueueResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

/**
 * SM_REQ_SIMPLE(MO 전송) 비즈니스 로직 서비스.
 * ESMClass별 핸들러에서 이 서비스를 주입받아 processSMReqSimple를 호출한다.
 * MOThreadPool은 ESMClass 구분 + getHandler + handle만 담당.
 */
@Service
class MoSmReqSimpleProcessServiceImpl(
    private val moBillTypeService: MoBillTypeService,
    private val moServiceTypeResolver: MoServiceTypeResolver,
    private val moServiceHandlerRegistry: MoServiceHandlerRegistry,
    private val moLimitCheckService: MoLimitCheckService,
    private val moQItemUtilService: MoQItemUtilService,
    private val moBlockNotificationService: MoBlockNotificationService,
    private val moRcsTrService: MoRcsTrService,
    private val moGipEventLogService: MoGipEventLogService,
    private val moSendToCpService: MoSendToCpService,
    private val moRequeueService: MoRequeueService,
    private val moDbInsertService: MoDbInsertService,
    private val smsResService: SmsResService
) : IMoProcessorOps {

    override fun processSMReqSimple(qItem: QITEM, context: MoServiceContext) {
        runBlocking { doProcessSMReqSimpleBody(qItem, context) }
    }

    /**
     * SM_REQ_SIMPLE(MO 전송) 본문 로직 (레거시 INSERT 분기: NOTI/Relay/GIPMO).
     * 도메인별 핸들러는 handle() 내부에서 자체 흐름으로 처리하므로, 이 경로는 SmsResServiceImpl 등 공통 호출용.
     */
    private suspend fun doProcessSMReqSimpleBody(
        qItem: QITEM,
        context: MoServiceContext
    ) {
        val gstQItem = qItem
        val gstQItemTrans = context.gstQItemTrans as? SMReqTransResult ?: return
        val actualEntity = context.entity ?: return
        val loggerName = context.loggerName
        val workerThreadId = context.workerThreadId
        val queueNo = context.queueNo
        val witcomLog = context.witcomLog ?: return
        val smsQLib = context.smsQLib ?: return
        val gServerID = context.gServerID ?: return
        val cfgEtcMap = context.cfgEtcMap as? HashMap<String, CfgEtcEntity> ?: return
        val gstQResultObj = context.gstQResultObj as? QueueResult ?: return
        val gESMCLASS = context.esmClass
        val gBILLTYPE = moBillTypeService.getBillTypeChar(context.entity, '0')
        val destCidForEsmClass = gstQItemTrans.destCid ?: ""
        val serviceTypeForBody = moServiceTypeResolver.resolve(gESMCLASS, destCidForEsmClass)
        witcomLog.c_write(
            loggerName,
            Level.INFO,
            String.format(
                "[doProcessSMReqSimpleBody] ESMClass 체크 최우선: gESMCLASS(%d), serviceType(%s), DestCID(%s)",
                gESMCLASS,
                serviceTypeForBody.name,
                destCidForEsmClass
            ),
            workerThreadId
        )

        val contextForBody = MoServiceContext(
            destCID = destCidForEsmClass,
            esmClass = gESMCLASS,
            loggerName = loggerName,
            workerThreadId = workerThreadId,
            entity = actualEntity,
            queueNo = queueNo,
            gstQItemTrans = gstQItemTrans,
            witcomLog = witcomLog,
            smsQLib = smsQLib,
            gServerID = gServerID,
            cfgEtcMap = cfgEtcMap,
            gstQResultObj = gstQResultObj,
            moProcessorOps = null
        )
        val handlerForBody = moServiceHandlerRegistry.getHandler(serviceTypeForBody) as MoServiceTypeAwareHandler
        if (!handlerForBody.checkQItemVariables(gstQItem, contextForBody)) {
            return
        }

        val existingMsgId = QItemServiceUtil.byteArrayToKString(gstQItem.ucMsgId)
        val existingTraceId = QItemServiceUtil.byteArrayToKString(gstQItem.szTraceId)
        val traceId = existingTraceId

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

        val nRetMO = moLimitCheckService.checkLimitMO(gstQItemTrans, actualEntity)
        if (nRetMO) {
            val gGIPEVENT_BLK_NOTI =
                cfgEtcMap["GIPEVENT_BLK_NOTI"]?.pvalue?.let { it != 0 } ?: true
            if (gGIPEVENT_BLK_NOTI) {
                val blockNotiQItem = moQItemUtilService.copyQItem(gstQItem)
                moBlockNotificationService.processBlockNotification(
                    blockNotiQItem,
                    gstQItem,
                    smsQLib,
                    gServerID,
                    loggerName,
                    workerThreadId
                )
            }
            smsQLib.InsqStat(
                gstQItem,
                MESSAGE_MO,
                0,
                gServerID,
                MODULEID_GIPEVENT_C,
                SERVICEID_GIPEVENT,
                ERRORID_CP_MO_LIMIT,
                ST_GIP_MO_LIMIT,
                moQItemUtilService.getNInforNo(gstQItem),
                TID_NO_SAVE,
                LT_BOTH,
                0
            )
            val rcsTag = QItemServiceUtil.byteArrayToKString(gstQItem.RcsTag)
            if (rcsTag.isNotBlank()) {
                gstQItem.nVldPrd = 43200
                val rcsQItem = moQItemUtilService.copyQItem(gstQItem)
                moRcsTrService.processRcsTrMessage(rcsQItem, smsQLib, loggerName, workerThreadId)
            }
            return
        }

        val nRetGIVE = if (moBillTypeService.isGiveBill(gBILLTYPE)) {
            moLimitCheckService.checkLimitGIVE(gstQItemTrans, actualEntity)
        } else {
            false
        }
        if (nRetGIVE) {
            val gGIPEVENT_BLK_NOTI =
                cfgEtcMap["GIPEVENT_BLK_NOTI"]?.pvalue?.let { it != 0 } ?: true
            if (gGIPEVENT_BLK_NOTI) {
                val blockNotiQItem = moQItemUtilService.copyQItem(gstQItem)
                moBlockNotificationService.processBlockNotification(
                    blockNotiQItem,
                    gstQItem,
                    smsQLib,
                    gServerID,
                    loggerName,
                    workerThreadId
                )
            }
            smsQLib.InsqStat(
                gstQItem,
                MESSAGE_MO,
                0,
                gServerID,
                MODULEID_GIPEVENT_C,
                SERVICEID_GIPEVENT,
                ERRORID_CP_GIVEBILL_LIMIT,
                ST_GIP_MT_LIMIT_GIFT,
                moQItemUtilService.getNInforNo(gstQItem),
                TID_NO_SAVE,
                LT_BOTH,
                0
            )
            val rcsTag = QItemServiceUtil.byteArrayToKString(gstQItem.RcsTag)
            if (rcsTag.isNotBlank()) {
                gstQItem.nVldPrd = 43200
                val rcsQItem = moQItemUtilService.copyQItem(gstQItem)
                moRcsTrService.processRcsTrMessage(rcsQItem, smsQLib, loggerName, workerThreadId)
            }
            return
        }

        val serviceTypeForInsert = moServiceTypeResolver.resolve(gESMCLASS, gstQItemTrans.destCid ?: "")
        val isNotiPlusType = serviceTypeForInsert == MoServiceType.NOTI_PLUS
        val isNotiType = serviceTypeForInsert == MoServiceType.NOTI_REGISTERED
        val isRelayMo = gstQItem.nRsv4Protocol[0] != 0 && gstQItem.nRsv4Protocol[1] != 0
        val serialNo = gstQItem.uMsgSerialNo.toLong()
        witcomLog.c_write(
            loggerName,
            Level.INFO,
            String.format("GetSerialNo() : gSerialNo(%d)", serialNo),
            workerThreadId
        )
        val reqSimpleLog = moGipEventLogService.formatGipEventLog(actualEntity, gServerID, gstQItemTrans, gstQItem)
        witcomLog.c_write(loggerName, Level.INFO, reqSimpleLog, workerThreadId)
        witcomLog.c_write(
            loggerName,
            Level.INFO,
            String.format(
                "MO HTTP 전송 시작: CP_URL(%s), MsgSeqNo(%d), 재시도횟수(%d), Timeout(%d초)",
                actualEntity.cpUrl,
                gstQItem.uMsgSerialNo.toInt(),
                actualEntity.rc,
                actualEntity.tc
            ),
            workerThreadId
        )
        witcomLog.c_write(
            loggerName,
            Level.INFO,
            String.format(
                "QITEM 변수체크 통과: msgId(%s) TraceId(%s) CP_URL(%s) MsgSeqNo(%d)",
                existingMsgId,
                traceId,
                actualEntity.cpUrl,
                gstQItem.uMsgSerialNo.toInt()
            ),
            workerThreadId
        )
        val moSendSuccess = withContext(Dispatchers.IO) {
            moSendToCpService.sendMoMessageToCp(
                gstQItemTrans,
                actualEntity,
                gstQItem,
                smsQLib,
                gServerID,
                loggerName,
                workerThreadId,
                segmentInfo
            )
        }
        witcomLog.c_write(
            loggerName,
            if (moSendSuccess) Level.INFO else Level.INFO,
            String.format(
                "MO HTTP 전송 완료: 결과(%s), CP_URL(%s), MsgSeqNo(%d), SrcCID(%s), DestCID(%s)",
                if (moSendSuccess) "SUCCESS" else "FAILED",
                actualEntity.cpUrl,
                gstQItem.uMsgSerialNo.toInt(),
                QItemServiceUtil.byteArrayToKString(gstQItem.szSrcCId),
                QItemServiceUtil.byteArrayToKString(gstQItem.szCId)
            ),
            workerThreadId
        )
        val ucServerTypeChar = gstQItem.ucServerType.toInt().toChar()
        val expectedServerType = VSMSS_TYPE.code.toChar()
        witcomLog.c_write(
            loggerName,
            Level.INFO,
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
        if (ucServerTypeChar != expectedServerType) {
            witcomLog.c_write(
                loggerName,
                Level.INFO,
                String.format(
                    "[MO 전송 성공] ⚠️ ServerType 불일치: ucServerType(%c) != Expected(%c) - QLIB64 GetQNoFromStatRouteTblWithCid에서 Unknown ServerType 에러 발생 가능",
                    ucServerTypeChar,
                    expectedServerType
                ),
                workerThreadId
            )
        }
        val handlerForStat = moServiceHandlerRegistry.getHandler(
            moServiceTypeResolver.resolve(
                context.esmClass,
                context.destCID
            )
        ) as MoServiceTypeAwareHandler
        witcomLog.c_write(
            loggerName,
            Level.INFO,
            String.format(
                "[MO 전송 성공] 통계 분기: ESMCLASS(%d), serviceType(%s), DestCID(%s)",
                gESMCLASS,
                handlerForStat.serviceType().name,
                context.destCID
            ),
            workerThreadId
        )
        gstQItem.ucServerType = VSMSS_TYPE.code.toByte()
        handlerForStat.recordMoSuccessInsqStat(gstQItem, context)
        val destCIDForSendCheck = QItemServiceUtil.byteArrayToKString(gstQItem.szCId)
        val isCid1584ForSendCheck = destCIDForSendCheck.startsWith("1584")
        witcomLog.c_write(
            loggerName,
            if (moSendSuccess) Level.INFO else Level.INFO,
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
            moRequeueService.requeueMessage(
                gstQResultObj,
                queueNo,
                smsQLib,
                loggerName,
                workerThreadId,
                gstQItemTrans
            )
            return
        }
        var dbInsertOk = true
        val destCIDForDBInsert = QItemServiceUtil.byteArrayToKString(gstQItem.szCId)
        val isCid1584ForDBInsert = destCIDForDBInsert.startsWith("1584")
        witcomLog.c_write(
            loggerName,
            Level.INFO,
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
        if (!moBillTypeService.isFreeBill(gBILLTYPE) && (isNotiPlusType || isNotiType)) {
            witcomLog.c_write(
                loggerName,
                Level.INFO,
                String.format(
                    "[DB Insert 분기] NOTI 타입 분기 진입: destCID(%s), isCid1584(%b), MO_NOTISEND 저장",
                    destCIDForDBInsert,
                    isCid1584ForDBInsert
                ),
                workerThreadId
            )
            val notiRes = moDbInsertService.insertMO_NOTISEND(
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
                    ST_DB_NO_DATA_MONOTISEND,
                    moQItemUtilService.getNInforNo(gstQItem),
                    TID_NO_SAVE,
                    LT_TRACE,
                    0
                )
            }
        } else if (isRelayMo) {
            witcomLog.c_write(
                loggerName,
                Level.INFO,
                String.format(
                    "[DB Insert 분기] Relay MO 분기 진입: destCID(%s), isCid1584(%b), RelayMOCallInfo 저장",
                    destCIDForDBInsert,
                    isCid1584ForDBInsert
                ),
                workerThreadId
            )
            val relayRes =
                moDbInsertService.insertRelayMOCallInfo(gstQItem, gstQItemTrans, actualEntity, serialNo)
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
                    moQItemUtilService.getNInforNo(gstQItem),
                    TID_NO_SAVE,
                    LT_TRACE,
                    0
                )
            } else {
                gstQItem.ucServerType = VSMSS_TYPE.code.toByte()
            }
        } else {
            witcomLog.c_write(
                loggerName,
                Level.INFO,
                String.format(
                    "[DB Insert 분기] 일반 MO 분기 진입 (MOCALLINFO 저장): destCID(%s), isCid1584(%b)",
                    destCIDForDBInsert,
                    isCid1584ForDBInsert
                ),
                workerThreadId
            )
            val gMOTRBILLForInsert = moBillTypeService.isMoTrBillEnabled(actualEntity)
            val actualMsgIdForInsert =
                QItemServiceUtil.byteArrayToKString(gstQItem.ucMsgId)
            val destCIDForInsert = QItemServiceUtil.byteArrayToKString(gstQItem.szCId)
            val isCid1584ForInsert = destCIDForInsert.startsWith("1584")
            witcomLog.c_write(
                loggerName,
                Level.INFO,
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
            val callRes = moDbInsertService.insertGIPMOCallInfo(
                gstQItem,
                gstQItemTrans,
                actualEntity,
                workerThreadId
            )
            witcomLog.c_write(
                loggerName,
                if (callRes >= 0) Level.INFO else Level.INFO,
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
                    moQItemUtilService.getNInforNo(gstQItem),
                    TID_NO_SAVE,
                    LT_TRACE,
                    0
                )
            }
        }
        if (!dbInsertOk) {
            witcomLog.c_write(
                loggerName,
                Level.INFO,
                String.format(
                    "DB Insert After-Send Failed: billType=%d, esmClass=%d, isNotiPlus=%b, isNoti=%b, isRelay=%b, srcCID(%s), srcCallNo(%s), destCID(%s), destCallNo(%s)",
                    gBILLTYPE.code,
                    gESMCLASS,
                    (!moBillTypeService.isFreeBill(gBILLTYPE) && isNotiPlusType),
                    (!moBillTypeService.isFreeBill(gBILLTYPE) && isNotiType),
                    isRelayMo,
                    gstQItemTrans.srcCid,
                    gstQItemTrans.srcMinNo,
                    gstQItemTrans.destCid,
                    gstQItemTrans.destMinNo
                ),
                workerThreadId
            )
        }
        val moTrBill = actualEntity.moTrBill ?: 0
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
            if (moTrBill == 1) {
                witcomLog.c_write(
                    loggerName,
                    Level.INFO,
                    String.format(
                        "[사용자 요청 대기] MO-TR 요청 기다리는중: msgId(%s), traceId(%s), moTrBill(Y), destCID(%s), cpUrl(%s)",
                        actualMsgId,
                        traceId,
                        QItemServiceUtil.byteArrayToKString(gstQItem.szCId),
                        actualEntity.cpUrl
                    ),
                    workerThreadId
                )
            } else {
                witcomLog.c_write(
                    loggerName,
                    Level.INFO,
                    String.format(
                        "[로직 종료] MOTRBILL=N : MO-TR 콜백(/sms/mo-report) 대기 없음. msgId(%s), traceId(%s), destCID(%s), cpUrl(%s)",
                        actualMsgId,
                        traceId,
                        QItemServiceUtil.byteArrayToKString(gstQItem.szCId),
                        actualEntity.cpUrl
                    ),
                    workerThreadId
                )
            }
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
                val dataBody = ResponseTR.DataBody()
                dataBody.srcCID = QItemServiceUtil.byteArrayToKString(gstQItem.szSrcCId)
                dataBody.srcCallNo =
                    QItemServiceUtil.byteArrayToKString(gstQItem.szSrcMinNo)
                dataBody.srcAddrRsv = 0
                dataBody.destCID = QItemServiceUtil.byteArrayToKString(gstQItem.szCId)
                dataBody.destCallNo =
                    QItemServiceUtil.byteArrayToKString(gstQItem.szMinNo)
                dataBody.destAddrRsv = 0
                dataBody.msgCode = MSG_CODE_SM_RES.toShort()
                dataBody.msgSubCode = SM_REQ_SIMPLE.toShort()
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
                dataBody.ackResult = 0
                dataBody.result = null
                dataBody.msgStatus = null
                dataBody.msgId = actualMsgId
                val moAckRequest = ResponseTR()
                moAckRequest.msgVerId = 1
                moAckRequest.encFlag = 0
                moAckRequest.data = dataBody
                runBlocking {
                    smsResService.processSMRes(
                        moAckRequest,
                        actualEntity.ipAddr,
                        actualEntity.portNo,
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
                    Level.INFO,
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
        witcomLog.c_write(
            loggerName,
            Level.INFO,
            String.format(
                "[MO 전송 성공] bprintf 스킵: processSMReqSimple/processMOBilling에서 이미 호출됨 - MsgId(%s)",
                actualMsgId
            ),
            workerThreadId
        )
        if (moBillTypeService.isFreeBill(gBILLTYPE)) {
            witcomLog.c_write(
                loggerName,
                Level.INFO,
                "[TRC 전송 성공] BILLTYPE='1' 비과금: ST_GIPEVENT_MOACK_BILL_OK TRC 호출 스킵 - 과금성공(MOACK) TRC 미출력"
            )
        } else {
            gstQItem.ucServerType = VSMSS_TYPE.code.toByte()
            (moServiceHandlerRegistry.getHandler(
                moServiceTypeResolver.resolve(
                    context.esmClass,
                    context.destCID
                )
            ) as MoServiceTypeAwareHandler).recordMoAckBilling(gstQItem, context, gBILLTYPE)
        }
    }
}
