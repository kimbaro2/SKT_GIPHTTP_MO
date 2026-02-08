package com.infra.mo.skt_giphttp_mo.service.impl

import ch.qos.logback.classic.Level
import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_KSC5601
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.QTYPE_SM_REQ
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SUB_QTYPE_RCS_TR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.TERM_TYPE_KOR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.RCS_RESULT_INCALIDDST
import com.infra.mo.skt_giphttp_mo.service.MoRcsTrService
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib
import org.springframework.stereotype.Service

/**
 * RCS TR 메시지 처리 서비스 구현.
 * MOThreadPool 공통 호출 함수(processRcsTrMessage)를 서비스 내부에서 사용할 수 있도록 의존성만 주입.
 */
@Service
class MoRcsTrServiceImpl(
    private val witcomLog: WitcomLog
) : MoRcsTrService {

    override fun processRcsTrMessage(
        gstQItem: QITEM,
        smsQLib: SmsQLib,
        loggerName: String,
        workerThreadId: Long
    ) {
        val rcsQItem = QItemServiceUtil.qItemToMsgHdr(gstQItem)

        val tempCidRcs = rcsQItem.szCId.clone()
        System.arraycopy(rcsQItem.szSrcCId, 0, rcsQItem.szCId, 0, minOf(rcsQItem.szSrcCId.size, rcsQItem.szCId.size))
        System.arraycopy(tempCidRcs, 0, rcsQItem.szSrcCId, 0, minOf(tempCidRcs.size, rcsQItem.szSrcCId.size))

        val tempMinRcs = rcsQItem.szMinNo.clone()
        System.arraycopy(rcsQItem.szSrcMinNo, 0, rcsQItem.szMinNo, 0, minOf(rcsQItem.szSrcMinNo.size, rcsQItem.szMinNo.size))
        System.arraycopy(tempMinRcs, 0, rcsQItem.szSrcMinNo, 0, minOf(tempMinRcs.size, rcsQItem.szSrcMinNo.size))

        rcsQItem.usMsgCode = QTYPE_SM_REQ.toShort()
        rcsQItem.usMsgSubCode = SUB_QTYPE_RCS_TR.toShort()
        rcsQItem.ucTermType = TERM_TYPE_KOR.code.toByte()
        rcsQItem.ucDataEncoding = DCS_TYPE_KSC5601
        rcsQItem.nVldPrd = 43200
        rcsQItem.RcsResult = RCS_RESULT_INCALIDDST.toShort()

        val rcsQueueNo = 0
        witcomLog.c_write(loggerName, Level.INFO, "[MO] 최종 큐 삽입: rcsQueueNo=$rcsQueueNo (RCS 메시지)", workerThreadId)
        smsQLib.InsertIntoSmsQnQNo(rcsQItem, rcsQueueNo)
    }
}
