package com.infra.mo.skt_giphttp_mo.utils.cLibrary

import ch.qos.logback.classic.Level
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_ADMCANC
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_FWDFAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_MRMSPAM
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_NPREFIX
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_PORTEDOUT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_PORTEDOUT_KTF
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_PORTEDOUT_LGT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_PORTEDOUT_SKT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_SPAMERR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_UNDELIVERABLE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_STATE_USERDEL
import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.CfgEtcEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpAccessEntity
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult

object SM_REQ_TRANS_RESULT_PROCESSOR {

    private const val MAX_SHORT_MSG_LEN = 180
    private const val MAX_SHORT_MSG_LEN_462_MO = 80
    private const val DCS_TYPE_GSM7: Byte = 0x01
    private const val DCS_TYPE_ASCII7: Byte = 0x02
    private const val DCS_TYPE_UCS2: Byte = 0x08
    private const val DCS_TYPE_KSC5601: Byte = 0x10
    private const val DATA_TYPE_TEXT_KOR: Byte = 0x20
    private const val DATA_TYPE_BINARY: Byte = 0x30

    // 과금을 위한 통계로직
    // SM_REQ_TRANS_RESULT -> OK
    fun checking(
        ptrQItem: SMReqTransResult,
        ptrTransRes: QITEM,
        entity: GipHttpAccessEntity,
        witcomLog: WitcomLog,
        cfgEtcMap: HashMap<String, CfgEtcEntity>
    ): QITEM {
        /*1.*/
        when (ptrQItem.msgStatus.toInt()) { /*LINE 1107*/
            SM_STATE_PORTEDOUT_KTF,
            SM_STATE_PORTEDOUT_LGT,
            SM_STATE_PORTEDOUT_SKT -> {
                ptrTransRes.ucMsgStatus = SM_STATE_PORTEDOUT.toByte();
            }

            else -> {
                /* DELIVRD(2),EXPIRED(3),UNDELIVRD(5),FWDFAIL(14),SPAMERR(16),USERDEL(17),NPREFIX(19),ADMCANC(20) */
                ptrTransRes.ucMsgStatus = ptrQItem.msgStatus
            }
        }
        /*2.*/
        if (ptrQItem.dataEncoding == DCS_TYPE_KSC5601) {
            ptrTransRes.ucDataEncoding = DATA_TYPE_TEXT_KOR;
        } else {
            ptrTransRes.ucDataEncoding = DATA_TYPE_BINARY;
        }

        ptrTransRes.szSrcCId = ptrQItem.srcCid.toByteArray();
        ptrTransRes.szSrcMinNo = ptrQItem.srcMinNo.toByteArray();
        ptrTransRes.szCId = ptrQItem.destCid.toByteArray();
        ptrTransRes.szMinNo = ptrQItem.destMinNo.toByteArray();

        /*3.2.1 MessageStatus 분기처리 부*/
        when (ptrTransRes.ucMsgStatus.toInt()) { /*LINE 1142*/
            SM_STATE_SPAMERR -> {
                if (ptrQItem.szFree2[0] == SM_STATE_MRMSPAM.toByte()) {
                    ptrTransRes.ucMsgStatus = SM_STATE_UNDELIVERABLE.toByte();
//                    println("[NORMAL] TRANS_RESULT_CHANGE : SourceCID(${ptrQItem.srcCid}) SourceCallNo(${ptrQItem.srcMinNo}) " +
//                            "szDestCId(${ptrQItem.destCid}) uDestCallNo(${ptrQItem.destMinNo}) " +
//                            "Before Status(${ptrQItem.msgStatus}) => After Status(${ptrTransRes.ucMsgStatus})")
                    val formatted = String.format(
                        "TRANS_RESULT_CHANGE : SourceCID(%s) SourceCallNo(%s) szDestCId(%s) uDestCallNo(%s) Before Status(%s) => After Status(%s)",
                        ptrTransRes.szSrcCId,
                        ptrTransRes.szSrcMinNo,
                        ptrTransRes.szCId,
                        ptrTransRes.szMinNo,
                        ptrQItem.msgStatus,
                        ptrTransRes.ucMsgStatus
                    )
                    witcomLog.p_write(Level.INFO, formatted)
                } else {
                    ptrTransRes.ucMsgStatus = 2;
//                    println("[NORMAL] TRANS_RESULT_CHANGE : SourceCID(${ptrQItem.srcCid}) SourceCallNo(${ptrQItem.srcMinNo}) " +
//                            "szDestCId(${ptrQItem.destCid}) uDestCallNo(${ptrQItem.destMinNo}) " +
//                            "Before Status(${ptrQItem.msgStatus}) => After Status(${ptrTransRes.ucMsgStatus})")
//
                    val formatted = String.format(
                        "TRANS_RESULT_CHANGE : SourceCID(%s) SourceCallNo(%s) szDestCId(%s) uDestCallNo(%s) Before Status(%s) => After Status(%s)",
                        ptrTransRes.szSrcCId,
                        ptrTransRes.szSrcMinNo,
                        ptrTransRes.szCId,
                        ptrTransRes.szMinNo,
                        ptrQItem.msgStatus,
                        ptrTransRes.ucMsgStatus
                    )
                    witcomLog.p_write(Level.INFO, formatted)

                    ptrQItem.msgStatus = 2
                }
            }

            SM_STATE_FWDFAIL, SM_STATE_USERDEL, SM_STATE_NPREFIX, SM_STATE_ADMCANC -> {
                ptrTransRes.ucMsgStatus = SM_STATE_UNDELIVERABLE.toByte();
                val formatted = String.format(
                    "TRANS_RESULT_CHANGE : SourceCID(%s) SourceCallNo(%s) szDestCId(%s) uDestCallNo(%s) Before Status(%s) => After Status(%s)",
                    ptrTransRes.szSrcCId,
                    ptrTransRes.szSrcMinNo,
                    ptrTransRes.szCId,
                    ptrTransRes.szMinNo,
                    ptrQItem.msgStatus,
                    ptrTransRes.ucMsgStatus
                )
                witcomLog.p_write(Level.INFO, formatted)
            }

            else -> {

//                ucMsgStatus 값 확인해서 성공, 실패 여부 확인하고 밖으로 나와서 응답 패킷 구성할때 성공 실패 여부 처리 해야함!!!!

                val formatted = String.format(
                    "TRANS_RESULT_CHANGE : SourceCID(%s) SourceCallNo(%s) szDestCId(%s) uDestCallNo(%s) Before Status(%s) => After Status(%s)",
                    ptrTransRes.szSrcCId,
                    ptrTransRes.szSrcMinNo,
                    ptrTransRes.szCId,
                    ptrTransRes.szMinNo,
                    ptrQItem.msgStatus,
                    ptrTransRes.ucMsgStatus
                )
                witcomLog.p_write(Level.INFO, formatted)
            }
        }
        ptrTransRes.ucGSMErrCode = ptrQItem.gsmErrCode;
        ptrTransRes.ucMsgId = ptrQItem.msgId.toByteArray()
        for (i in ptrQItem.rsv4Protocol.indices) {
            if (i < ptrTransRes.nRsv4Protocol.size) {
                ptrTransRes.nRsv4Protocol[i] = ptrQItem.rsv4Protocol[i]
            }
        }
        /*
        [REQ_TRANS_RESULT] [VSMSS#1->JWS_TESTP] MsgVerId(5) SrcCId(010) SrcCallNo(25001611) DestCId(1571700310) DestCallNo(0) MsgCode(11) MsgSubCode(9) TId(0) BodyDataLen(11) MsgSeqNo(3649001) DataEncoding(0E:CP949) TermType(49) ConcatenateFlag(0) ConcatenateInfo(0) Status(2:DELIVERED) MsgId(0037AD85)
        * */


        return ptrTransRes;
    }
}



