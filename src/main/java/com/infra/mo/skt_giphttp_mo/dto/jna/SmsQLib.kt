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

    @JvmField var  szOSFI = ByteArray(OSFI_SIZE)
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

    /**
     * src/dest 번호를 스왑한 새로운 QITEM을 반환합니다.
     * 기존 QITEM은 변경되지 않으며, InsqStat 호출 등 특정 상황에서 사용하기 위한 일시적인 복사본을 생성합니다.
     * 
     * 스왑되는 필드:
     * - szSrcCId <-> szCId
     * - szSrcMinNo <-> szMinNo
     * 
     * @return src/dest가 스왑된 새로운 QITEM 인스턴스
     */
    fun createSwappedSrcDest(): QITEM {
        val swapped = QITEM()
        
        // 모든 필드 복사
        swapped.ucServerType = this.ucServerType
        swapped.nMsgVerId = this.nMsgVerId
        
        // src/dest 스왑: szSrcCId <-> szCId
        System.arraycopy(this.szCId, 0, swapped.szSrcCId, 0, QITEM_SIZE_CID)
        System.arraycopy(this.szSrcCId, 0, swapped.szCId, 0, QITEM_SIZE_CID)
        
        // src/dest 스왑: szSrcMinNo <-> szMinNo
        System.arraycopy(this.szMinNo, 0, swapped.szSrcMinNo, 0, QITEM_SIZE_MINNO)
        System.arraycopy(this.szSrcMinNo, 0, swapped.szMinNo, 0, QITEM_SIZE_MINNO)
        
        swapped.usSource = this.usSource
        swapped.nModuleNo = this.nModuleNo
        
        swapped.usMsgCode = this.usMsgCode
        swapped.usMsgSubCode = this.usMsgSubCode
        System.arraycopy(this.usMsgCodeReserved, 0, swapped.usMsgCodeReserved, 0, this.usMsgCodeReserved.size)
        
        swapped.ucMsgLen = this.ucMsgLen
        swapped.uMsgSerialNo = this.uMsgSerialNo
        swapped.ucTermType = this.ucTermType
        swapped.uSplitSeq = this.uSplitSeq
        
        swapped.ucDataEncoding = this.ucDataEncoding
        System.arraycopy(this.ucRsv, 0, swapped.ucRsv, 0, this.ucRsv.size)
        System.arraycopy(this.nRsv4Protocol, 0, swapped.nRsv4Protocol, 0, this.nRsv4Protocol.size)
        
        swapped.nVldPrd = this.nVldPrd
        swapped.ucPriority = this.ucPriority
        swapped.ucRepFlag = this.ucRepFlag
        swapped.ucRgtDlvFlg = this.ucRgtDlvFlg
        
        swapped.ucMsgStatus = this.ucMsgStatus
        swapped.ucGSMErrCode = this.ucGSMErrCode
        System.arraycopy(this.ucFlagReserved, 0, swapped.ucFlagReserved, 0, this.ucFlagReserved.size)
        swapped.ucAgingCnt = this.ucAgingCnt
        swapped.ucDoNotFwd = this.ucDoNotFwd
        
        System.arraycopy(this.ucMsgId, 0, swapped.ucMsgId, 0, this.ucMsgId.size)
        System.arraycopy(this.ucRsv4Dlv, 0, swapped.ucRsv4Dlv, 0, this.ucRsv4Dlv.size)
        
        System.arraycopy(this.szMsg, 0, swapped.szMsg, 0, this.szMsg.size)
        swapped.tCreateTime = this.tCreateTime
        System.arraycopy(this.szCB, 0, swapped.szCB, 0, this.szCB.size)
        System.arraycopy(this.szFree2, 0, swapped.szFree2, 0, this.szFree2.size)
        
        System.arraycopy(this.ucLocation, 0, swapped.ucLocation, 0, this.ucLocation.size)
        swapped.ucRsvLocation = this.ucRsvLocation
        System.arraycopy(this.szFWD_NO, 0, swapped.szFWD_NO, 0, this.szFWD_NO.size)
        System.arraycopy(this.szFWD_NO2, 0, swapped.szFWD_NO2, 0, this.szFWD_NO2.size)
        swapped.ReturnQ_No = this.ReturnQ_No
        
        System.arraycopy(this.szOSFI, 0, swapped.szOSFI, 0, this.szOSFI.size)
        System.arraycopy(this.szOrgCallingNumber, 0, swapped.szOrgCallingNumber, 0, this.szOrgCallingNumber.size)
        swapped.unMsgAddNum = this.unMsgAddNum
        
        swapped.msgRefID = this.msgRefID
        swapped.totalSeg = this.totalSeg
        swapped.segSeq = this.segSeq
        
        swapped.callback_noti = this.callback_noti
        swapped.n010_plus = this.n010_plus
        swapped.msg_org = this.msg_org
        swapped.callback_check = this.callback_check
        System.arraycopy(this.szTraceId, 0, swapped.szTraceId, 0, this.szTraceId.size)
        
        System.arraycopy(this.szTraceTime, 0, swapped.szTraceTime, 0, this.szTraceTime.size)
        System.arraycopy(this.szOrigMvnoInformation, 0, swapped.szOrigMvnoInformation, 0, this.szOrigMvnoInformation.size)
        System.arraycopy(this.szDestMvnoInformation, 0, swapped.szDestMvnoInformation, 0, this.szDestMvnoInformation.size)
        
        swapped.cBillType = this.cBillType
        System.arraycopy(this.szFullRN, 0, swapped.szFullRN, 0, this.szFullRN.size)
        System.arraycopy(this.szVMBackupCID, 0, swapped.szVMBackupCID, 0, this.szVMBackupCID.size)
        
        System.arraycopy(this.RcsTag, 0, swapped.RcsTag, 0, this.RcsTag.size)
        swapped.RcsResult = this.RcsResult
        swapped.uOrgMsgLen = this.uOrgMsgLen
        
        swapped.ChildNumOrd = this.ChildNumOrd
        swapped.usAuthFlag = this.usAuthFlag
        System.arraycopy(this.szSMS_OSFI, 0, swapped.szSMS_OSFI, 0, this.szSMS_OSFI.size)
        System.arraycopy(this.szMoRecvTime, 0, swapped.szMoRecvTime, 0, this.szMoRecvTime.size)
        
        swapped.nTransQNo = this.nTransQNo
        System.arraycopy(this.szOrigCID, 0, swapped.szOrigCID, 0, this.szOrigCID.size)
        System.arraycopy(this.szRelayCID, 0, swapped.szRelayCID, 0, this.szRelayCID.size)
        
        return swapped
    }

    /**
     * QITEM 구조체의 네이티브 레이아웃 전체 크기(MsgBodyLen).
     * 인스턴스가 작은 버퍼를 가리킬 때 size()가 버퍼 크기를 반환할 수 있으므로,
     * 로깅 시 BodyDataLen에는 이 상수 사용을 권장.
     */
    companion object {
        @JvmStatic
        val LAYOUT_SIZE: Int by lazy { QITEM().size() }
    }

    class ByReference : QITEM(), Structure.ByReference
    class ByValue : QITEM(), Structure.ByValue
}
