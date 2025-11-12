package com.infra.mo.skt_giphttp_mo.dto.jna

import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.FULL_RN_SIZE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LEN_TRACE_ID
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MAX_AUTH_CALLBACK_LEN
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MAX_LOCATION
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MAX_SHORT_MSG_LEN
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MVNO_INFO_SIZE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ORIGCID_SIZE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.OSFI_SIZE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.QITEM_SIZE_CID
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.QITEM_SIZE_MINNO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.QITEM_SIZE_MSGID
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.RCS_REFERENCE_SIZE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.RELAYCID_SIZE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SMS_OSFI_SIZE
import com.sun.jna.Structure


@Structure.FieldOrder(
    "ucServerType", "nMsgVerId",
    "szSrcCId", "szSrcMinNo", "usSource",
    "szCId", "szMinNo", "nModuleNo",
    "usMsgCode", "usMsgSubCode", "usMsgCodeReserved",
    "ucMsgLen", "uMsgSerialNo", "ucTermType", "uSplitSeq",
    "ucDataEncoding", "ucRsv", "nRsv4Protocol",
    "nVldPrd", "ucPriority", "ucRepFlag", "ucRgtDlvFlg",
    "ucMsgStatus", "ucGSMErrCode", "ucFlagReserved", "ucAgingCnt", "ucDoNotFwd",
    "ucMsgId", "ucRsv4Dlv",
    "szMsg", "tCreateTime", "szCB", "szFree2",
    "ucLocation", "ucRsvLocation", "szFWD_NO", "szFWD_NO2", "ReturnQ_No",
    "szOSFI", "szOrgCallingNumber", "unMsgAddNum",
    "msgRefID", "totalSeg", "segSeq",
    "callback_noti", "n010_plus", "msg_org", "callback_check", "szTraceId",
    "szTraceTime",
    "szOrigMvnoInformation", "szDestMvnoInformation",
    "cBillType", "szFullRN", "szVMBackupCID",
    "RcsTag", "RcsResult", "uOrgMsgLen",
    "ChildNumOrd", "usAuthFlag", "szSMS_OSFI", "szMoRecvTime",
    "nTransQNo", "szOrigCID", "szRelayCID"
)
open class QITEM : Structure() {
    // unsigned char -> Byte
    @JvmField var ucServerType: Byte = 0
    @JvmField var nMsgVerId: Int = 0

    @JvmField var szSrcCId = ByteArray(QITEM_SIZE_CID)
    @JvmField var szSrcMinNo = ByteArray(QITEM_SIZE_MINNO)
    @JvmField var usSource: Int = 0

    @JvmField var szCId = ByteArray(QITEM_SIZE_CID)
    @JvmField var szMinNo = ByteArray(QITEM_SIZE_MINNO)
    @JvmField var nModuleNo: Int = 0

    @JvmField var usMsgCode: Short = 0
    @JvmField var usMsgSubCode: Short = 0
    @JvmField var usMsgCodeReserved = ShortArray(2)

    @JvmField var ucMsgLen: Int = 0
    @JvmField var uMsgSerialNo: Int = 0
    @JvmField var ucTermType: Byte = 0
    @JvmField var uSplitSeq: Int = 0

    @JvmField var ucDataEncoding: Byte = 0
    @JvmField var ucRsv = ByteArray(2)
    @JvmField var nRsv4Protocol = IntArray(12)

    @JvmField var nVldPrd: Int = 0
    @JvmField var ucPriority: Byte = 0
    @JvmField var ucRepFlag: Byte = 0
    @JvmField var ucRgtDlvFlg: Byte = 0

    @JvmField var ucMsgStatus: Byte = 0
    @JvmField var ucGSMErrCode: Byte = 0
    @JvmField var ucFlagReserved = ByteArray(2)
    @JvmField var ucAgingCnt: Byte = 0
    @JvmField var ucDoNotFwd: Byte = 0

    @JvmField var ucMsgId = ByteArray(QITEM_SIZE_MSGID)
    @JvmField var ucRsv4Dlv = ByteArray(3)

    @JvmField var szMsg = ByteArray(MAX_SHORT_MSG_LEN)  // 180바이트 (CP949: 80바이트 + 100바이트 여분, 그 외: 140바이트 + 40바이트 여분)
    @JvmField var tCreateTime: Int = 0
    @JvmField var szCB = ByteArray(MAX_AUTH_CALLBACK_LEN)
    @JvmField var szFree2 = ByteArray(11)

    @JvmField var ucLocation = ByteArray(MAX_LOCATION + 1)  // C: MAX_LOCATION+1
    @JvmField var ucRsvLocation: Byte = 0
    @JvmField var szFWD_NO = ByteArray(21)
    @JvmField var szFWD_NO2 = ByteArray(21)
    @JvmField var ReturnQ_No: Int = 0

    @JvmField var szOSFI = ByteArray(OSFI_SIZE)
    @JvmField var szOrgCallingNumber = ByteArray(21)
    @JvmField var unMsgAddNum: Int = 0

    @JvmField var msgRefID: Short = 0
    @JvmField var totalSeg: Byte = 0
    @JvmField var segSeq: Byte = 0

    @JvmField var callback_noti: Short = 0
    @JvmField var n010_plus: Short = 0
    @JvmField var msg_org: Short = 0
    @JvmField var callback_check: Short = 0
    @JvmField var szTraceId = ByteArray(LEN_TRACE_ID + 1)  // C: LEN_TRACE_ID+1

    @JvmField var szTraceTime = ByteArray(17)
    @JvmField var szOrigMvnoInformation = ByteArray(MVNO_INFO_SIZE)
    @JvmField var szDestMvnoInformation = ByteArray(MVNO_INFO_SIZE)

    @JvmField var cBillType: Byte = 0
    @JvmField var szFullRN = ByteArray(FULL_RN_SIZE)
    @JvmField var szVMBackupCID = ByteArray(QITEM_SIZE_CID + 1)  // C: QITEM_SIZE_CID+1

    @JvmField var RcsTag = ByteArray(RCS_REFERENCE_SIZE)
    @JvmField var RcsResult: Short = 0
    @JvmField var uOrgMsgLen: Int = 0

    @JvmField var ChildNumOrd: Short = 0
    @JvmField var usAuthFlag: Short = 0
    @JvmField var szSMS_OSFI = ByteArray(SMS_OSFI_SIZE)
    @JvmField var szMoRecvTime = ByteArray(17)

    @JvmField var nTransQNo: Int = 0
    @JvmField var szOrigCID = ByteArray(ORIGCID_SIZE)
    @JvmField var szRelayCID = ByteArray(RELAYCID_SIZE)

    class ByReference : QITEM(), Structure.ByReference
    class ByValue : QITEM(), Structure.ByValue
}
