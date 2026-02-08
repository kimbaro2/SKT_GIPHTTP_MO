package com.infra.mo.skt_giphttp_mo.service.impl

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_8BIT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_ASCII7
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_8BIT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_ASCII7
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_GSM7
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_KSC5601
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_UCS2
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_GSM7
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_KSC5601
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_UCS2
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MSG_CODE_SM_REQ
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MSG_CODE_SM_RES
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MAX_VAILD_PERIOD
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_SIMPLE
import com.infra.mo.skt_giphttp_mo.service.MoBillTypeService
import com.infra.mo.skt_giphttp_mo.service.MoGipEventLogService
import org.springframework.stereotype.Service
import java.nio.charset.Charset

/**
 * GIP 이벤트 로그 포맷 서비스 구현.
 * MOThreadPool 공통 호출 함수(formatGipEventLog, getMessageType, formatDataEncoding)를 서비스 내부에서 사용할 수 있도록 의존성만 주입.
 */
@Service
class MoGipEventLogServiceImpl(
    private val moBillTypeService: MoBillTypeService
) : MoGipEventLogService {

    override fun getMessageType(msgCode: Short, msgSubCode: Short): String = when {
        msgSubCode == SM_REQ_SIMPLE.toShort() && msgCode == MSG_CODE_SM_REQ.toShort() -> "REQ_SIMPLE"
        msgSubCode == SM_REQ_SIMPLE.toShort() && msgCode == MSG_CODE_SM_RES.toShort() -> "RES_SIMPLE"
        msgSubCode == SM_REQ_SIMPLE.toShort() -> "REQ_SIMPLE"
        else -> "UNKNOWN"
    }

    override fun formatDataEncoding(dataEncoding: Byte): String {
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

    override fun formatGipEventLog(
        entity: GipHttpMoAccessEntity,
        gServerID: Int,
        msgHdr: SMReqTransResult,
        qItem: QITEM?
    ): String {
        val logNo = entity.logNo?.let { String.format("%04d", it.toIntOrNull() ?: 0) } ?: "0000"
        val messageType = getMessageType(msgHdr.msgCode, msgHdr.msgSubCode)
        val cpName = entity.cpName ?: ""
        val serverInfo = String.format("VSMSS#%d->%s", gServerID, cpName)
        val dataEncoding = formatDataEncoding(msgHdr.dataEncoding)
        val gBILLTYPE = moBillTypeService.getBillTypeChar(entity, '0')
        val gMOTRBILL = if (moBillTypeService.isMoTrBillEnabled(entity)) '1' else '0'
        val gESMCLASS = if (qItem != null) qItem.nRsv4Protocol[11] else 0
        // RECV와 동일: VldPrd(보낸값:보정값). 보낸값 = nRsv4Protocol[9], 보정값 = 실제 적용값(nVldPrd>0이면 nVldPrd, 0이면 기본값 MAX_VAILD_PERIOD)
        val vldPrd = if (qItem != null) {
            val sent = qItem.nRsv4Protocol.getOrNull(9) ?: 0
            val effective = if (qItem.nVldPrd > 0) qItem.nVldPrd else MAX_VAILD_PERIOD
            "${sent}:${effective}"
        } else "0:$MAX_VAILD_PERIOD"
        val orgMsgTotalLen = if (qItem != null && qItem.uOrgMsgLen > 0) qItem.uOrgMsgLen else 0
        return String.format(
            "[GIPHTTPMO_C_%s] [%s] [%s] MsgVerId(%d) SrcCId(%s) SrcCallNo(%s) DestCId(%s) DestCallNo(%s) MsgCode(%d) MsgSubCode(%d) TId(%d) BodyDataLen(%d) MsgSeqNo(%d) DataEncoding(%s) TermType(%d) ConcatenateFlag(%d) ConcatenateInfo(%d) VldPrd(%s) RgtDlvFlg(%d) CallBack(%s) MsgLen(%d) OrgMsgTotalLen(%d) BILLTYPE(%c) REPLY_FLAG(%c) MO_TR_BILL(%c) ESMCLASS(%d)",
            logNo, messageType, serverInfo,
            msgHdr.msgVerId, msgHdr.srcCid, msgHdr.srcMinNo, msgHdr.destCid, msgHdr.destMinNo,
            msgHdr.msgCode.toInt(), msgHdr.msgSubCode.toInt(), msgHdr.msgCodeReserved0.toInt(),
            msgHdr.msgLen, msgHdr.msgSerialNo, dataEncoding, msgHdr.termType.toInt(),
            if (qItem != null) qItem.totalSeg.toInt() else 0, if (qItem != null) qItem.segSeq.toInt() else 0,
            vldPrd, msgHdr.rgtDlvFlg.toInt(), msgHdr.callback, msgHdr.msgLen, orgMsgTotalLen,
            gBILLTYPE, 'Y', gMOTRBILL, gESMCLASS
        )
    }
}
