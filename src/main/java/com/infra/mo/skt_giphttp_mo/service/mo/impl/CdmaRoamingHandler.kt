package com.infra.mo.skt_giphttp_mo.service.mo.impl

import ch.qos.logback.classic.Level
import com.infra.mo.skt_giphttp_mo.dto.SegmentInfo
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_GIVEBILL_LIMIT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_FAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_LIMIT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LT_BOTH
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LT_TRACE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MESSAGE_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MSG_CODE_SM_RES
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MODULEID_GIPEVENT_C
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SERVICEID_GIPEVENT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_SIMPLE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIPEVENT_MOACK_BILL_OK
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIPEVENT_MO_OK
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.TID_NO_SAVE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.VSMSS_TYPE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_TRANS_RESULT
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_DB_INS_FAIL_GIPMOCALLINFO
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_GIP_MO_LIMIT
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_GIP_MT_LIMIT_GIFT
import com.infra.mo.skt_giphttp_mo.dto.smsController.Rsv4ProtocolItem
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseTR
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.CfgEtcEntity
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_SUCCESS
import com.infra.mo.skt_giphttp_mo.service.Mo1584EntityResolveService
import com.infra.mo.skt_giphttp_mo.service.MoBillTypeService
import com.infra.mo.skt_giphttp_mo.service.MoBlockNotificationService
import com.infra.mo.skt_giphttp_mo.service.MoDbInsertService
import com.infra.mo.skt_giphttp_mo.service.MoGipEventLogService
import com.infra.mo.skt_giphttp_mo.service.MoLimitCheckService
import com.infra.mo.skt_giphttp_mo.service.MoQItemUtilService
import com.infra.mo.skt_giphttp_mo.service.MoRequeueService
import com.infra.mo.skt_giphttp_mo.service.MoRcsTrService
import com.infra.mo.skt_giphttp_mo.service.MoSendInsqStatService
import com.infra.mo.skt_giphttp_mo.service.MoSendToCpService
import com.infra.mo.skt_giphttp_mo.service.MoTrSendService
import com.infra.mo.skt_giphttp_mo.service.SmsResService
import com.infra.mo.skt_giphttp_mo.service.handler.MoServiceType
import com.infra.mo.skt_giphttp_mo.service.mo.MoServiceContext
import com.infra.mo.skt_giphttp_mo.service.mo.MoServiceTypeAwareHandler
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil.QueueResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component

/**
 * 5. CDMA 로밍 — handle()에서 흐름 전부 구현. 1584 entity 재조회 후 context 전달, InsqStat·DB Insert(insertGIPMOCallInfo) 등은 handle() 내부에서 호출.
 * 1584일 때 recordMoAckBilling 스킵.
 */
@Component
class CdmaRoamingHandler(
    private val moBillTypeService: MoBillTypeService,
    private val moLimitCheckService: MoLimitCheckService,
    private val moQItemUtilService: MoQItemUtilService,
    private val moBlockNotificationService: MoBlockNotificationService,
    private val moRcsTrService: MoRcsTrService,
    private val moGipEventLogService: MoGipEventLogService,
    private val moSendToCpService: MoSendToCpService,
    private val moRequeueService: MoRequeueService,
    private val moSendInsqStatService: MoSendInsqStatService,
    private val mo1584EntityResolveService: Mo1584EntityResolveService,
    private val moDbInsertService: MoDbInsertService,
    private val smsResService: SmsResService,
    private val moTrSendService: MoTrSendService
) : MoServiceTypeAwareHandler {

    override fun serviceType(): MoServiceType = MoServiceType.CDMA_ROAMING

    override fun handle(qItem: QITEM, context: MoServiceContext) {
        val contextToUse = if (context.destCID.startsWith("1584") && context.entity != null) {
            val actualEntity = mo1584EntityResolveService.resolveEntityFor1584(
                context.destCID,
                context.entity!!,
                context.queueNo,
                context.loggerName,
                context.workerThreadId,
                context.witcomLog!!
            )
            context.copy(entity = actualEntity)
        } else context
        runBlocking { doProcessSMReqSimpleInHandler(qItem, contextToUse) }
    }

    private suspend fun doProcessSMReqSimpleInHandler(qItem: QITEM, context: MoServiceContext) {
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
        val destCID = context.destCID
        val gBILLTYPE = moBillTypeService.getBillTypeChar(context.entity, '0')

        if (!checkQItemVariables(gstQItem, context)) return

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
            val gGIPEVENT_BLK_NOTI = cfgEtcMap["GIPEVENT_BLK_NOTI"]?.pvalue?.let { it != 0 } ?: true
            if (gGIPEVENT_BLK_NOTI) {
                val blockNotiQItem = moQItemUtilService.copyQItem(gstQItem)
                moBlockNotificationService.processBlockNotification(
                    blockNotiQItem, gstQItem, smsQLib, gServerID, loggerName, workerThreadId
                )
            }
            smsQLib.InsqStat(
                gstQItem, MESSAGE_MO, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT,
                ERRORID_CP_MO_LIMIT, ST_GIP_MO_LIMIT, moQItemUtilService.getNInforNo(gstQItem),
                TID_NO_SAVE, LT_BOTH, 0
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
        } else false
        if (nRetGIVE) {
            val gGIPEVENT_BLK_NOTI = cfgEtcMap["GIPEVENT_BLK_NOTI"]?.pvalue?.let { it != 0 } ?: true
            if (gGIPEVENT_BLK_NOTI) {
                val blockNotiQItem = moQItemUtilService.copyQItem(gstQItem)
                moBlockNotificationService.processBlockNotification(
                    blockNotiQItem, gstQItem, smsQLib, gServerID, loggerName, workerThreadId
                )
            }
            smsQLib.InsqStat(
                gstQItem, MESSAGE_MO, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT,
                ERRORID_CP_GIVEBILL_LIMIT, ST_GIP_MT_LIMIT_GIFT, moQItemUtilService.getNInforNo(gstQItem),
                TID_NO_SAVE, LT_BOTH, 0
            )
            val rcsTag = QItemServiceUtil.byteArrayToKString(gstQItem.RcsTag)
            if (rcsTag.isNotBlank()) {
                gstQItem.nVldPrd = 43200
                val rcsQItem = moQItemUtilService.copyQItem(gstQItem)
                moRcsTrService.processRcsTrMessage(rcsQItem, smsQLib, loggerName, workerThreadId)
            }
            return
        }

        val reqSimpleLog = moGipEventLogService.formatGipEventLog(actualEntity, gServerID, gstQItemTrans, gstQItem)
        witcomLog.c_write(loggerName, Level.INFO, reqSimpleLog, workerThreadId)

        val moSendSuccess = withContext(Dispatchers.IO) {
            moSendToCpService.sendMoMessageToCp(
                gstQItemTrans, actualEntity, gstQItem, smsQLib, gServerID,
                loggerName, workerThreadId, segmentInfo
            )
        }

        if (!moSendSuccess) {
            moRequeueService.requeueMessage(
                gstQResultObj, queueNo, smsQLib, loggerName, workerThreadId, gstQItemTrans
            )
            moSendInsqStatService.recordMoFailedInsqStat(gstQItem, context)
            return
        }

        gstQItem.ucServerType = VSMSS_TYPE.code.toByte()
        recordMoSuccessInsqStat(gstQItem, context)
        val callRes = moDbInsertService.insertGIPMOCallInfo(
            gstQItem, gstQItemTrans, actualEntity, workerThreadId
        )
        val dbInsertOk = callRes >= 0
        if (!dbInsertOk) {
            smsQLib.InsqStat(
                gstQItem, MESSAGE_MO, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT,
                ERRORID_CP_MO_FAIL, ST_DB_INS_FAIL_GIPMOCALLINFO,
                moQItemUtilService.getNInforNo(gstQItem), TID_NO_SAVE, LT_TRACE, 0
            )
        }

        val actualMsgId = QItemServiceUtil.byteArrayToKString(gstQItem.ucMsgId)
        if (dbInsertOk && moSendSuccess) {
            try {
                val dataBody = ResponseTR.DataBody().apply {
                    srcCID = QItemServiceUtil.byteArrayToKString(gstQItem.szSrcCId)
                    srcCallNo = QItemServiceUtil.byteArrayToKString(gstQItem.szSrcMinNo)
                    srcAddrRsv = 0
                    this.destCID = QItemServiceUtil.byteArrayToKString(gstQItem.szCId)
                    destCallNo = QItemServiceUtil.byteArrayToKString(gstQItem.szMinNo)
                    destAddrRsv = 0
                    msgCode = MSG_CODE_SM_RES.toShort()
                    msgSubCode = SM_REQ_SIMPLE.toShort()
                    bodyDataLen = 0
                    msgSeqNo = gstQItem.uMsgSerialNo.toInt()
                    termtype = ""
                    dataType = 0
                    dataEncoding = gstQItem.ucDataEncoding.toInt()
                    concatenateflag = ""
                    concatenateInfo = ""
                    rsv4Protocol = gstQItemTrans.rsv4Protocol.map { Rsv4ProtocolItem(it) }
                    teleServiceID = 0
                    msgCodeRsv = 0
                    reserved2 = emptyList()
                    time = ""
                    ackResult = 0
                    this.result = null
                    this.msgStatus = null
                    msgId = actualMsgId
                }
                val moAckRequest = ResponseTR().apply {
                    msgVerId = 1
                    encFlag = 0
                    data = dataBody
                }
                runBlocking {
                    smsResService.processSMRes(
                        moAckRequest, actualEntity.ipAddr, actualEntity.portNo, queueNo
                    )
                }
            } catch (e: Exception) {
                witcomLog.c_write(
                    loggerName, Level.INFO,
                    String.format("[MO 전송 성공] MO-ACK 처리 실패 - MsgId(%s), error(%s)", actualMsgId, e.message),
                    workerThreadId
                )
            }
        }

        if (!moBillTypeService.isFreeBill(gBILLTYPE)) {
            gstQItem.ucServerType = VSMSS_TYPE.code.toByte()
            recordMoAckBilling(gstQItem, context, gBILLTYPE)
        }
    }

    override fun checkQItemVariables(qItem: QITEM, context: MoServiceContext): Boolean {
        val witcomLog = context.witcomLog ?: return false
        val smsQLib = context.smsQLib ?: return false
        val gServerID = context.gServerID ?: return false
        val loggerName = context.loggerName
        val workerThreadId = context.workerThreadId
        val entity = context.entity
        val st = serviceType()
        val msgId = QItemServiceUtil.byteArrayToKString(qItem.ucMsgId)
        if (msgId.isBlank()) {
            witcomLog.c_write(loggerName, Level.INFO, String.format("[CRITICAL] QITEM 변수체크 실패(msgId): msgId 비어있음. serviceType(%s) CP_URL(%s) MsgSeqNo(%d) SrcCID(%s) DestCID(%s)", st.name, entity?.cpUrl, qItem.uMsgSerialNo.toInt(), QItemServiceUtil.byteArrayToKString(qItem.szSrcCId), QItemServiceUtil.byteArrayToKString(qItem.szCId)), workerThreadId)
            smsQLib.InsqStat(qItem, MESSAGE_MO, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT, ERRORID_CP_MO_FAIL, ST_GIPEVENT_MO_OK, qItem.usSource, TID_NO_SAVE, LT_BOTH, 0)
            return false
        }
        val traceId = QItemServiceUtil.byteArrayToKString(qItem.szTraceId)
        val hasTraceId = traceId.isNotBlank() && !traceId.all { it == '\u0000' }
        if (!hasTraceId) {
            witcomLog.c_write(loggerName, Level.INFO, String.format("[CRITICAL] QITEM 변수체크 실패(traceId): traceId 없음. serviceType(%s) CP_URL(%s) MsgSeqNo(%d) SrcCID(%s) DestCID(%s)", st.name, entity?.cpUrl, qItem.uMsgSerialNo.toInt(), QItemServiceUtil.byteArrayToKString(qItem.szSrcCId), QItemServiceUtil.byteArrayToKString(qItem.szCId)), workerThreadId)
            smsQLib.InsqStat(qItem, MESSAGE_MO, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT, ERRORID_CP_MO_FAIL, ST_GIPEVENT_MO_OK, qItem.usSource, TID_NO_SAVE, LT_BOTH, 0)
            return false
        }
        return true
    }

    override fun recordMoSuccessInsqStat(qItem: QITEM, context: MoServiceContext) {
        val smsQLib = context.smsQLib ?: return
        val gServerID = context.gServerID ?: return
        smsQLib.InsqStat(
            qItem,
            MESSAGE_MO,
            0,
            gServerID,
            MODULEID_GIPEVENT_C,
            SERVICEID_GIPEVENT,
            ERRORID_CP_MO_SUCCESS,
            ST_GIPEVENT_MO_OK,
            moQItemUtilService.getNInforNo(qItem),
            TID_NO_SAVE,
            LT_BOTH,
            0
        )
    }

    override fun recordMoAckBilling(qItem: QITEM, context: MoServiceContext, billType: Char) {
        if (context.destCID.startsWith("1584")) {
            val witcomLog = context.witcomLog ?: return
            witcomLog.c_write(
                context.loggerName, Level.INFO,
                String.format(
                    "[TRC 전송 성공] 서비스타입(CDMA_ROAMING)+1584: ST_GIPEVENT_MOACK_BILL_OK 스킵 - ESM(%d), DestCID(%s)",
                    context.esmClass, context.destCID
                ),
                context.workerThreadId
            )
            return
        }
        val smsQLib = context.smsQLib ?: return
        val gServerID = context.gServerID ?: return
        smsQLib.InsqStat(qItem, MESSAGE_MO, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT, com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_SUCCESS, ST_GIPEVENT_MOACK_BILL_OK, qItem.usSource, TID_NO_SAVE, LT_TRACE, 0)
    }

    override fun shouldSkipBprintf(): Boolean = false
}
