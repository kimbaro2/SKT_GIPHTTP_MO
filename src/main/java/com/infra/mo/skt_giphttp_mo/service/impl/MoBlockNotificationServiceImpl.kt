package com.infra.mo.skt_giphttp_mo.service.impl

import ch.qos.logback.classic.Level
import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.GIPEVENT_BLOCK_NOTI_CALLBACK
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.GIPEVENT_BLOCK_NOTI_CID
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MODULEID_GIPEVENT_C
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SERVICEID_GIPEVENT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_SUCCESS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIPEVENT_INSQ_BLOCKNOTI
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_Q_INSERT_FAIL_BLOCKNOTI
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_SIMPLE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.QTYPE_SM_REQ
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.Q_INSERT_SUCCESS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MESSAGE_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.TID_NO_SAVE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LT_TRACE
import com.infra.mo.skt_giphttp_mo.service.MoBlockNotificationService
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib
import org.springframework.stereotype.Service
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 블록 알림 메시지 처리 서비스 구현.
 * MOThreadPool 공통 호출 함수(processBlockNotification)를 서비스 내부에서 사용할 수 있도록 의존성만 주입.
 */
@Service
class MoBlockNotificationServiceImpl(
    private val witcomLog: WitcomLog
) : MoBlockNotificationService {

    override fun processBlockNotification(
        stQItem: QITEM,
        gstQItem: QITEM,
        smsQLib: SmsQLib,
        gServerID: Int,
        loggerName: String,
        workerThreadId: Long
    ) {
        val nQueueNo = 0
        val dateFormat = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
        val strtime = dateFormat.format(Date())

        stQItem.ucTermType = '1'.code.toByte()
        stQItem.usMsgCode = QTYPE_SM_REQ.toShort()
        stQItem.usMsgSubCode = SM_REQ_SIMPLE.toShort()

        val blockNotiMsgFormat = "[%s] %s로부터 차단된 메시지입니다."
        val blockNotiMsg = String.format(blockNotiMsgFormat, strtime, QItemServiceUtil.byteArrayToKString(stQItem.szCId))

        val tempCid = stQItem.szCId.clone()
        System.arraycopy(stQItem.szSrcCId, 0, stQItem.szCId, 0, minOf(stQItem.szSrcCId.size, stQItem.szCId.size))
        System.arraycopy(tempCid, 0, stQItem.szSrcCId, 0, minOf(tempCid.size, stQItem.szSrcCId.size))

        val tempMin = stQItem.szMinNo.clone()
        System.arraycopy(stQItem.szSrcMinNo, 0, stQItem.szMinNo, 0, minOf(stQItem.szSrcMinNo.size, stQItem.szMinNo.size))
        System.arraycopy(tempMin, 0, stQItem.szSrcMinNo, 0, minOf(tempMin.size, stQItem.szSrcMinNo.size))

        val blockNotiCidBytes = GIPEVENT_BLOCK_NOTI_CID.toByteArray(Charset.forName("CP949"))
        System.arraycopy(blockNotiCidBytes, 0, stQItem.szSrcCId, 0, minOf(blockNotiCidBytes.size, stQItem.szSrcCId.size - 1))
        if (blockNotiCidBytes.size < stQItem.szSrcCId.size) stQItem.szSrcCId[blockNotiCidBytes.size] = 0x00

        stQItem.szSrcMinNo.fill(0x00)

        val blockNotiCallbackBytes = GIPEVENT_BLOCK_NOTI_CALLBACK.toByteArray(Charset.forName("CP949"))
        System.arraycopy(blockNotiCallbackBytes, 0, stQItem.szCB, 0, minOf(blockNotiCallbackBytes.size, stQItem.szCB.size - 1))
        if (blockNotiCallbackBytes.size < stQItem.szCB.size) stQItem.szCB[blockNotiCallbackBytes.size] = 0x00

        val blockNotiMsgBytes = blockNotiMsg.toByteArray(Charset.forName("CP949"))
        val msgLen = minOf(blockNotiMsgBytes.size, stQItem.szMsg.size)
        System.arraycopy(blockNotiMsgBytes, 0, stQItem.szMsg, 0, msgLen)
        stQItem.ucMsgLen = msgLen
        stQItem.nVldPrd = 86400
        stQItem.ucRgtDlvFlg = 0

        this.witcomLog.c_write(loggerName, Level.INFO, "[MO] 최종 큐 삽입: nQueueNo=$nQueueNo (Block Noti 메시지)", workerThreadId)

        val insertResult = smsQLib.InsertIntoSmsQnQNo(stQItem, nQueueNo)
        if (insertResult == Q_INSERT_SUCCESS) {
            smsQLib.InsqStat(stQItem, MESSAGE_MO, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT, ERRORID_CP_MO_SUCCESS, ST_GIPEVENT_INSQ_BLOCKNOTI, nQueueNo, TID_NO_SAVE, LT_TRACE, 0)
        } else {
            smsQLib.InsqStat(stQItem, MESSAGE_MO, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT, ERRORID_CP_MO_SUCCESS, ST_Q_INSERT_FAIL_BLOCKNOTI, nQueueNo, TID_NO_SAVE, LT_TRACE, 0)
        }
    }
}
