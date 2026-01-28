package com.infra.mo.skt_giphttp_mo.dto.jna

import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.utils.SmsEncodingTypeAnalyzer
import lombok.Data
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

// SM_REQ_TRANS_RESULT 데이터 클래스
@Data
data class SMReqTransResult(
    var serverType: Char,           // ucServerType
    var msgVerId: Int,              // nMsgVerId
    var srcCid: String,             // szSrcCId
    var srcMinNo: String,           // szSrcMinNo
    var returnQNo: Int,             // ReturnQ_No
    var destCid: String,            // szCId
    var destMinNo: String,          // szMinNo
    var moduleNo: Int,              // nModuleNo
    var msgCode: Short,             // usMsgCode
    var msgSubCode: Short,          // usMsgSubCode
    var msgCodeReserved0: Short,    // usMsgCodeReserved[0]
    var msgCodeReserved1: Short,    // usMsgCodeReserved[1]
    var msgLen: Int,                // ucMsgLen
    var msgSerialNo: Int,           // uMsgSerialNo
    var termType: Byte,             // ucTermType
    var splitSeq: Int,              // uSplitSeq
    var dataEncoding: Byte,         // ucDataEncoding
    var rsv4Protocol: List<Int>,    // nRsv4Protocol
    var vldPrd: Int,                // nVldPrd
    var priority: Byte,             // ucPriority
    var repFlag: Byte,              // ucRepFlag
    var rgtDlvFlg: Byte,            // ucRgtDlvFlg
    var msgStatus: Byte,            // ucMsgStatus
    var gsmErrCode: Byte,           // ucGSMErrCode
    var flagReserved: List<Byte>,   // ucFlagReserved
    var doNotFwd: Byte,             // ucDoNotFwd
    var msgId: String,              // ucMsgId -> Hex String
    var rsv4Dlv: List<Byte>,        // ucRsv4Dlv
    var msg: String,                // szMsg
    var createTime: Int,            // tCreateTime
    var callback: String,           // szCB
    var szFree2: List<Byte>,        // szFree2
    var ucLocation: String,         // ucLocation
    var ucRsvLocation: Byte,        // ucRsvLocation
    var fwdNo: String,              // szFWD_NO
    var fwdNo2: String,             // szFWD_NO2
    var osfi: String,               // szOSFI
    var orgCallingNumber: String,   // szOrgCallingNumber
    var origMvnoInformation: String,// szOrigMvnoInformation
    var destMvnoInformation: String,// szDestMvnoInformation
    var billType: Char,             // cBillType
    var fullRN: String,             // szFullRN
    var rcsTag: String,             // RcsTag
    var rcsResult: Short,           // RcsResult
    var msgAddNum: Int,             // unMsgAddNum
    var msgRefID: Short,            // msgRefID
    var totalSeg: Byte,             // totalSeg
    var segSeq: Byte,               // segSeq
    var callbackNoti: Short,        // callback_noti
    var plus010: Short,             // n010_plus
    var msgOrigin: Short,           // msg_org
    var callbackCheck: Short,       // callback_check
    var traceId: String,            // szTraceId
    var orgMsgLen: Int,             // uOrgMsgLen
    var childNumOrd: Short,         // ChildNumOrd
    var authFlag: Short,            // usAuthFlag
    var smsOsfi: String,            // szSMS_OSFI
    var moRecvTime: String,         // szMoRecvTime
    var relayCID: String,           // szRelayCID
    var resultCode: Int = 0,         // 처리 상태 코드 (0=OK)
    var usSource: Int = 0,          // usSource
)


object QItemConverter {
    fun toSMReqTransResult(qitem: QITEM): SMReqTransResult {
        // ✅ 인코딩 타입 분석
        val encodingType = SmsEncodingTypeAnalyzer.analyze(qitem.ucDataEncoding.toInt())
        
        // ✅ 메시지 바이트 배열 준비 (ucMsgLen만큼만)
        val msgBytes = qitem.szMsg.sliceArray(0 until qitem.ucMsgLen)
        
        return SMReqTransResult(
            serverType = qitem.ucServerType.toChar(),
            msgVerId = qitem.nMsgVerId,
            srcCid = qitem.szSrcCId.decodeCP949(),
            srcMinNo = qitem.szSrcMinNo.decodeCP949(),
            returnQNo = qitem.ReturnQ_No,
            destCid = qitem.szCId.decodeCP949(),
            destMinNo = qitem.szMinNo.decodeCP949(),
            moduleNo = qitem.nModuleNo,
            msgCode = qitem.usMsgCode,
            msgSubCode = qitem.usMsgSubCode,
            msgCodeReserved0 = qitem.usMsgCodeReserved.getOrNull(0) ?: 0,
            msgCodeReserved1 = qitem.usMsgCodeReserved.getOrNull(1) ?: 0,
            msgLen = qitem.ucMsgLen,
            msgSerialNo = qitem.uMsgSerialNo,
            termType = qitem.ucTermType,
            splitSeq = qitem.uSplitSeq,
            dataEncoding = qitem.ucDataEncoding,
            rsv4Protocol = qitem.nRsv4Protocol.toList(),
            vldPrd = qitem.nVldPrd,
            priority = qitem.ucPriority,
            repFlag = qitem.ucRepFlag,
            rgtDlvFlg = qitem.ucRgtDlvFlg,
            msgStatus = qitem.ucMsgStatus,
            gsmErrCode = qitem.ucGSMErrCode,
            flagReserved = qitem.ucFlagReserved.toList(),
            doNotFwd = qitem.ucDoNotFwd,
            msgId = qitem.ucMsgId.toHexString(),
            rsv4Dlv = qitem.ucRsv4Dlv.toList(),
            msg = msgBytes.decodeMessage(encodingType),
            createTime = qitem.tCreateTime,
            callback = qitem.szCB.decodeCP949(),
            szFree2 = qitem.szFree2.toList(),
            ucLocation = qitem.ucLocation.decodeCP949(),
            ucRsvLocation = qitem.ucRsvLocation,
            fwdNo = qitem.szFWD_NO.decodeCP949(),
            fwdNo2 = qitem.szFWD_NO2.decodeCP949(),
            osfi = qitem.szOSFI.joinToString("/") { "0x%02X".format(it) },
            orgCallingNumber = qitem.szOrgCallingNumber.decodeCP949(),
            origMvnoInformation = qitem.szOrigMvnoInformation.decodeCP949(),
            destMvnoInformation = qitem.szDestMvnoInformation.decodeCP949(),
            billType = qitem.cBillType.toChar(),
            fullRN = qitem.szFullRN.decodeCP949(),
            rcsTag = qitem.RcsTag.decodeCP949(),
            rcsResult = qitem.RcsResult,
            msgAddNum = qitem.unMsgAddNum,
            msgRefID = qitem.msgRefID,
            totalSeg = qitem.totalSeg,
            segSeq = qitem.segSeq,
            callbackNoti = qitem.callback_noti,
            plus010 = qitem.n010_plus,
            msgOrigin = qitem.msg_org,
            callbackCheck = qitem.callback_check,
            traceId = qitem.szTraceId.decodeCP949(),
            orgMsgLen = qitem.uOrgMsgLen,
            childNumOrd = qitem.ChildNumOrd,
            authFlag = qitem.usAuthFlag,
            smsOsfi = qitem.szSMS_OSFI.joinToString("/") { "0x%02X".format(it) },
            moRecvTime = qitem.szMoRecvTime.decodeCP949(),
            relayCID = qitem.szRelayCID.decodeCP949(),
            usSource = qitem.usSource
        )
    }

    private fun ByteArray.toHexString(): String =
        joinToString("") { "%02X".format(it) }

    /**
     * CP949 인코딩으로 ByteArray를 String으로 변환
     * CID, CallNo, Callback 등 일반 필드용
     */
    private fun ByteArray.decodeCP949(): String {
        val bytes = this.takeWhile { it != 0.toByte() }.toByteArray()
        return try {
            String(bytes, Charset.forName("CP949"))
        } catch (e: Exception) {
            // 디코딩 실패 시 UTF-8로 폴백
            String(bytes, StandardCharsets.UTF_8)
        }.trim()
    }

    /**
     * 인코딩 타입에 맞게 메시지를 디코딩
     * @param encodingType 인코딩 타입
     * @return 디코딩된 문자열
     */
    private fun ByteArray.decodeMessage(encodingType: SmsEncodingTypeAnalyzer.MessageType): String {
        val msgBytes = this.takeWhile { it != 0.toByte() }.toByteArray()
        return try {
            when (encodingType) {
                // C 원본: 메시지는 최종적으로 KSC5601/CP949로 변환됨
                SmsEncodingTypeAnalyzer.MessageType.KSC5601_CP949 ->
                    String(msgBytes, Charset.forName("CP949"))
                SmsEncodingTypeAnalyzer.MessageType.UCS2_BIGENDIAN ->
                    String(msgBytes, Charset.forName("UTF-16BE"))
                SmsEncodingTypeAnalyzer.MessageType.ASCII_7BIT ->
                    String(msgBytes, StandardCharsets.US_ASCII)
                SmsEncodingTypeAnalyzer.MessageType.BINARY_8BIT ->
                    String(msgBytes, StandardCharsets.ISO_8859_1)
                else -> String(msgBytes, Charset.forName("CP949")) // 기본값 CP949
            }
        } catch (e: Exception) {
            // 디코딩 실패 시 CP949로 폴백
            try {
                String(msgBytes, Charset.forName("CP949"))
            } catch (e2: Exception) {
                String(msgBytes, StandardCharsets.UTF_8)
            }
        }.trim()
    }
}

