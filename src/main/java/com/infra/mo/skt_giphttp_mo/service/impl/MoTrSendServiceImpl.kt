package com.infra.mo.skt_giphttp_mo.service.impl

import ch.qos.logback.classic.Level
import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_TR_FAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_TR_SUCCESS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LT_BOTH
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MESSAGE_TR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MODULEID_GIPEVENT_C
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SERVICEID_GIPEVENT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_MRMSPAM
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_SPAMERR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIPALL_MTTR_SEND_OK
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.TID_NO_SAVE
import com.infra.mo.skt_giphttp_mo.service.MoSendToCpService
import com.infra.mo.skt_giphttp_mo.service.MoTrSendService
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil.QueueResult
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib
import com.infra.mo.skt_giphttp_mo.service.MoRequeueService
import com.infra.mo.skt_giphttp_mo.service.MoQItemUtilService
import org.springframework.stereotype.Service

/**
 * MO-TR 결과 전송 오케스트레이션 서비스 구현.
 * CP로 MO-TR 전송(sendMoTrToCp) + 성공 시 InsqStat, 실패 시 재큐 처리.
 */
@Service
class MoTrSendServiceImpl(
    private val moSendToCpService: MoSendToCpService,
    private val moRequeueService: MoRequeueService,
    private val moQItemUtilService: MoQItemUtilService,
    private val witcomLog: WitcomLog
) : MoTrSendService {

    override suspend fun processMoTrResult(
        qItem: QITEM,
        moResult: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        smsQLib: SmsQLib,
        gServerID: Int,
        queueResult: QueueResult,
        queueNo: Int,
        loggerName: String,
        workerThreadId: Long
    ): Boolean {
        val moTrSendSuccess = moSendToCpService.sendMoTrToCp(
            qItem,
            moResult,
            entity,
            loggerName,
            workerThreadId
        )

        if (moTrSendSuccess) {
            witcomLog.c_write(
                loggerName,
                Level.INFO,
                String.format(
                    "MO-TR Result Send Success : SrcCId(%s),SrcCallNo(%s),DestCId(%s),DestCallNo(%s),MsgStatus(%d),MsgId(%s)",
                    moResult.srcCid,
                    moResult.srcMinNo,
                    moResult.destCid,
                    moResult.destMinNo,
                    qItem.ucMsgStatus.toInt(),
                    QItemServiceUtil.byteArrayToKString(qItem.ucMsgId)
                ),
                workerThreadId
            )

            val msgStatus = qItem.ucMsgStatus.toInt()
            val isSuccess = (msgStatus == 2) ||
                (qItem.szFree2[0].toInt() != SM_STATE_MRMSPAM && msgStatus == SM_STATE_SPAMERR)

            smsQLib.InsqStat(
                qItem,
                MESSAGE_TR,
                0,
                gServerID,
                MODULEID_GIPEVENT_C,
                SERVICEID_GIPEVENT,
                if (isSuccess) ERRORID_CP_TR_SUCCESS else ERRORID_CP_TR_FAIL,
                ST_GIPALL_MTTR_SEND_OK,
                moQItemUtilService.getNInforNo(qItem),
                TID_NO_SAVE,
                LT_BOTH,
                0
            )
            return true
        }

        witcomLog.c_write(
            loggerName,
            Level.INFO,
            String.format(
                "MO-TR Result Send Fail : SrcCId(%s),SrcCallNo(%s),DestCId(%s),DestCallNo(%s),MsgStatus(%d),MsgId(%s)",
                moResult.srcCid,
                moResult.srcMinNo,
                moResult.destCid,
                moResult.destMinNo,
                qItem.ucMsgStatus.toInt(),
                QItemServiceUtil.byteArrayToKString(qItem.ucMsgId)
            ),
            workerThreadId
        )

        moRequeueService.requeueMessage(
            queueResult,
            queueNo,
            smsQLib,
            loggerName,
            workerThreadId,
            moResult
        )
        return false
    }
}
