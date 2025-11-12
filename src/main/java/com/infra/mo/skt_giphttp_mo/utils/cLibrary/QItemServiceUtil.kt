package com.infra.mo.skt_giphttp_mo.utils.cLibrary

import ch.qos.logback.classic.Level
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DEFINE_GIPVERID_510
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_INSERTQ_FAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_INVALID_SUBSCRIBER
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_SUCCESS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.IF_NULL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LT_BOTH
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LT_TRACE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MESSAGE_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MODULEID_GIPALL_C
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SERVICEID_GIPALL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIPALL_SMSMGR_OK
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIP_INVALID_SMIN_SMSMANAGER
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_Q_FULL_SMSMANAGER
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_Q_INSERT_FAIL_SMSMANAGER
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.TID_NO_SAVE
import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.dto.jna.QItemConverter
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult
import com.infra.mo.skt_giphttp_mo.utils.SmsEncodingTypeAnalyzer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

object QItemServiceUtil {

    // GSM 7bit 기본 문자셋 (GSM 03.38)
    private val gsm7bitMap: Map<Char, Int> =
            mapOf(
                    '@' to 0x00,
                    '£' to 0x01,
                    '$' to 0x02,
                    '¥' to 0x03,
                    'è' to 0x04,
                    'é' to 0x05,
                    'ù' to 0x06,
                    'ì' to 0x07,
                    'ò' to 0x08,
                    'Ç' to 0x09,
                    '\n' to 0x0A,
                    'Ø' to 0x0B,
                    'ø' to 0x0C,
                    '\r' to 0x0D,
                    'Å' to 0x0E,
                    'å' to 0x0F,
                    'Δ' to 0x10,
                    '_' to 0x11,
                    'Φ' to 0x12,
                    'Γ' to 0x13,
                    'Λ' to 0x14,
                    'Ω' to 0x15,
                    'Π' to 0x16,
                    'Ψ' to 0x17,
                    'Σ' to 0x18,
                    'Θ' to 0x19,
                    'Ξ' to 0x1A,
                    'Æ' to 0x1C,
                    'æ' to 0x1D,
                    'ß' to 0x1E,
                    'É' to 0x1F,
                    // 0x20 ~ 0x7A: ASCII 호환 문자들
                    *(' '.code..'z'.code).map { it.toChar() to it }.toTypedArray(),
                    // 특수문자
                    '^' to 0x1B,
                    '{' to 0x28,
                    '}' to 0x29,
                    '\\' to 0x2F,
                    '[' to 0x3C,
                    '~' to 0x3D,
                    ']' to 0x3E,
                    '|' to 0x40,
                    '€' to 0x65
            )

    // gsm7bitMap의 역매핑 (code -> char)
    private val gsm7bitReverseMap = gsm7bitMap.entries.associate { (k, v) -> v to k }

    /** GSM 7bit packed 바이트 배열 → 문자열 복호화 */
    private fun gsm7bitDecodeToString(data: ByteArray): String {
        val result = StringBuilder()
        var carry = 0
        var carryBits = 0

        for (i in data.indices) {
            val current = data[i].toInt() and 0xFF

            val septet = ((current shl carryBits) and 0x7F) or carry
            val ch = gsm7bitReverseMap[septet] ?: '?'
            result.append(ch)

            carry = (current shr (7 - carryBits)) and 0xFF
            carryBits++

            if (carryBits == 7) {
                carryBits = 0
                if (i + 1 < data.size) {
                    val ch2 = gsm7bitReverseMap[carry] ?: '?'
                    result.append(ch2)
                }
                carry = 0
            }
        }
        return result.toString()
    }

    /** 문자열 → GSM 7bit packed 바이트 배열 인코딩 */
    private fun textTogsm7bitEncode(text: String): ByteArray {
        val septets = mutableListOf<Int>()
        for (c in text) {
            val value = gsm7bitMap[c] ?: gsm7bitMap['?'] ?: 0x3F
            septets.add(value)
        }

        val packed = mutableListOf<Byte>()
        var carry = 0
        var carryBits = 0

        for (septet in septets) {
            val current = ((septet and 0x7F) shl carryBits) or carry
            packed.add((current and 0xFF).toByte())
            carry = (septet and 0x7F) shr (8 - carryBits)
            carryBits++

            if (carryBits == 7) {
                packed.add(carry.toByte())
                carry = 0
                carryBits = 0
            }
        }

        if (carryBits > 0) {
            packed.add(carry.toByte())
        }

        return packed.toByteArray()
    }

    /** 바이트 배열과 메시지 타입에 따라 적절히 문자열로 디코딩 */
    private fun decode(bytes: ByteArray, type: SmsEncodingTypeAnalyzer.MessageType): String {
        return when (type) {
            SmsEncodingTypeAnalyzer.MessageType.UCS2_BIGENDIAN ->
                    String(bytes, Charset.forName("UTF-16BE"))
            SmsEncodingTypeAnalyzer.MessageType.KSC5601_CP949 ->
                    String(bytes, Charset.forName("EUC-KR"))
            SmsEncodingTypeAnalyzer.MessageType.ASCII_7BIT ->
                    String(bytes, StandardCharsets.US_ASCII)
            SmsEncodingTypeAnalyzer.MessageType.BINARY_8BIT ->
                    String(bytes, StandardCharsets.ISO_8859_1)
            else -> String(bytes, Charset.forName("UTF-16BE")) // 기본값
        }
    }

    /** 큐에서 QITEM 읽고 SM_REQ_TRANS_RESULT 반환 */
    fun fetchAndConvert(qItem: QITEM): SMReqTransResult {
        val smReqTransResult = QItemConverter.toSMReqTransResult(qItem)

        return smReqTransResult
    }

    fun fetchAndConvert3(qItem: QITEM, witcomLog: WitcomLog) {
        val printBuffer = printQItem3(qItem, witcomLog)
        witcomLog.p_write(Level.INFO, printBuffer.toString())
    }

    data class QueueResult(val result: SMReqTransResult?, val qItem: QITEM?)

    fun fetchAndConvert2(queueNo: Int, smsQLib: SmsQLib, witcomLog: WitcomLog): QueueResult {
        val qItem = QITEM()
        val result = smsQLib.GetAMsgFromSmsQ(queueNo, qItem)

        return when (result) {
            SmsDef.Q_DELETE_FAIL_Q_EMPTY -> {
                witcomLog.p_write(Level.INFO, "Q_EMPTY for queueNo=$queueNo")
                QueueResult(null, null)
            }
            SmsDef.SMS_Q_LOCK_FAIL_TRY_AGAIN -> {
                witcomLog.p_write(Level.INFO, "LOCK_FAIL_TRY_AGAIN for queueNo=$queueNo")
                QueueResult(null, null)
            }
            SmsDef.SMS_Q_LOCK_FAIL_INVALID_SEMID -> {
                witcomLog.p_write(Level.INFO, "LOCK_FAIL_INVALID_SEMID for queueNo=$queueNo")
                QueueResult(null, null)
            }
            else -> {
                qItem.read()
                val smReqTransResult = QItemConverter.toSMReqTransResult(qItem)
                //                printQItem2(qItem)
                val printBuffer = printQItem3(qItem, witcomLog)
                witcomLog.p_write(Level.INFO, printBuffer.toString())

                QueueResult(smReqTransResult, qItem)
            }
        }
    }

    private fun printQItem2(qItem: QITEM) {
        println("================== Print QITEM  ==================")
        println("Server Type             : ${qItem.ucServerType.toChar()}")
        println("Message VersionID       : ${qItem.nMsgVerId}")
        println("=========== Source Address ============")
        println("SourceCID               : ${qItem.szSrcCId.toKString()}")
        println("SourceCallNo            : ${qItem.szSrcMinNo.toKString().toIntOrNull() ?: 0}")
        println("Source Return QNO       : ${qItem.ReturnQ_No}")
        println("========= Destination Address =========")
        println("DestCID                 : ${qItem.szCId.toKString()}")
        println("DestCallNo              : ${qItem.szMinNo.toKString().toIntOrNull() ?: 0}")
        println("ModuleNo                : ${qItem.nModuleNo}")
        println("============= Message Code =============")
        println("Message Code            : ${qItem.usMsgCode}")
        println("Message SubCode         : ${qItem.usMsgSubCode}")
        println("TeleService ID          : ${qItem.usMsgCodeReserved.getOrNull(0) ?: 0}")
        println("usMsgCodeReserved[1]    : ${qItem.usMsgCodeReserved.getOrNull(1) ?: 0}")
        println("Msg Length              : ${qItem.ucMsgLen}")
        println("MsgSerialNo             : ${qItem.uMsgSerialNo}")
        println("TermType                : ${qItem.ucTermType}")
        println("SplitSeq                : ${qItem.uSplitSeq}")
        println("DataEnconding           : ${qItem.ucDataEncoding}:CP949")
        qItem.nRsv4Protocol.forEachIndexed { i, v ->
            val label =
                    when (i) {
                        10 -> "nRsv4Protocol[10] / RD / Roaming PMN"
                        11 -> "nRsv4Protocol[11] / EsmClass"
                        else -> "nRsv4Protocol[$i]"
                    }
            println("$label   : $v")
        }
        println("=========== Sending Flags ============")
        println("VldPrd                  : ${qItem.nVldPrd}")
        println("Priority                : ${qItem.ucPriority}")
        println("RepFlag                 : ${qItem.ucRepFlag}")
        println("RgtDlvFlg TR/Receipt flag   : ${qItem.ucRgtDlvFlg}")
        println("SmDefaultMsgID / callforward count   : ${qItem.ucMsgId.firstOrNull() ?: 0}")
        println("=========== Receive Flags ============")
        println("Message Status          : ${qItem.ucMsgStatus}")
        println("GSMErrCode              : ${qItem.ucGSMErrCode}")
        println(
                "FlagReserved[0] / Center Number(VSMSS)   : ${qItem.ucFlagReserved.getOrNull(0) ?: 0}"
        )
        println("FlagReserved[1]         : ${qItem.ucFlagReserved.getOrNull(1) ?: 0}")
        println("FlagReserved[2]         : ${qItem.ucMsgId.getOrNull(0) ?: 0}")
        println("SRC_TYPE(1:Phone, 2:Web): ${qItem.ucMsgId.getOrNull(0) ?: 0}")
        println("DoNotForward            : ${qItem.ucDoNotFwd}")
        println("MessageID               : ${qItem.ucMsgId.toHexString()}")
        println("Rsv4Dlv[1] / W-Zone Flag: ${qItem.ucRsv4Dlv.getOrNull(1) ?: 0}")
        println("Rsv4Dlv[2] / NCHANGE    : ${qItem.ucRsv4Dlv.getOrNull(2) ?: 0}")
        println("Message                 : ${qItem.szMsg.toKString()}")
        println("CreateTime              : ${qItem.tCreateTime}")
        println("CallBack                : ${qItem.szCB.toKString()}")
        qItem.szFree2.forEachIndexed { i, v ->
            val label = if (i == 0) "szFree2[0] / MRM SM_STATE_MRMSPAM(1)" else "szFree2[$i]"
            println("$label              : $v")
        }
        println("ucLocation              : ${qItem.ucLocation.toKString()}")
        println("ucRsvLocation           : ${qItem.ucRsvLocation}")
        println("Foward No               : ${qItem.szFWD_NO.toKString()}")
        println("Foward No2              : ${qItem.szFWD_NO2.toKString()}")
        println("ReturnQ_No              : ${qItem.ReturnQ_No}")
        println("ConcatenateFlag         : ${qItem.totalSeg}")
        println("ConcatenateInfo         : ${qItem.segSeq}")
        println(
                "OSFI[1~8]               : ${qItem.szOSFI.joinToString("/") { "0x%02X".format(it) }}"
        )
        println("OrgCallingNumber        : ${qItem.szOrgCallingNumber.toKString()}")
        println("OrigMvnoInformation     : ${qItem.szOrigMvnoInformation.toKString()}")
        println("DestMvnoInformation     : ${qItem.szDestMvnoInformation.toKString()}")
        println("BillType                : ${qItem.cBillType.toChar()}")
        println("FullRN                  : ${qItem.szFullRN.toKString()}")
        println("RcsTag                  : ${qItem.RcsTag.toKString()}")
        println("RcsResult               : ${qItem.RcsResult}")
        println("MsgAddNum               : ${qItem.unMsgAddNum}")
        println("sar_msg_ref_num         : ${qItem.msgRefID}")
        println("sar_total_segment       : ${qItem.totalSeg}")
        println("sar_segment_seqnum      : ${qItem.segSeq}")
        println("CallBack_Noti           : ${qItem.callback_noti}")
        println("010Plus                 : ${qItem.n010_plus}")
        println("Message Origin          : ${qItem.msg_org}")
        println("CallBack_Check          : ${qItem.callback_check}")
        println("TraceID                 : ${qItem.szTraceId.toKString()}")
        println("OrgMsgLen               : ${qItem.uOrgMsgLen}")
        println("ChildNumOrd(Multinumber Service)  : ${qItem.ChildNumOrd}")
        println("Auth_Flag               : ${qItem.usAuthFlag}")
        println(
                "SMS_OSFI[1~5]           : ${qItem.szSMS_OSFI.take(5).joinToString("/") { "0x%02X".format(it) }}"
        )
        println("MoRecvTime              : ${qItem.szMoRecvTime.toKString()}")
        println("VirtualNum              : ${qItem.szRelayCID.toKString()}")
        println("================== Print QITEM  END ==================")
    }

    fun printQItem3(qItem: QITEM, witcomLog: WitcomLog) {
        val sb = StringBuilder()

        fun log(msg: String) = sb.appendLine("### $msg")

        sb.appendLine("### Print QITEM  ####################################################")
        log("ServerType<${qItem.ucServerType.toChar()}> MsgVerID<${qItem.nMsgVerId}>")
        log(
                "Source Address : SrcCID<${qItem.szSrcCId.toKString()}> SrcCallNo<${
                qItem.szSrcMinNo.toKString().toIntOrNull() ?: 0
            }> Source ReturnQNo<${qItem.ReturnQ_No}>"
        )
        log(
                "Destination Address : DestCID<${qItem.szCId.toKString()}> DestCallNo<${
                qItem.szMinNo.toKString().toIntOrNull() ?: 0
            }> ModuleNo<${qItem.nModuleNo}>"
        )
        log(
                "Message Code : MessageCode<${qItem.usMsgCode}> MessageSubCode<${qItem.usMsgSubCode}> MsgCodeReserved[0](TID)<${
                qItem.usMsgCodeReserved.getOrNull(
                    0
                ) ?: 0
            }>"
        )
        log(
                "MsgLen<${qItem.ucMsgLen}> MsgSerialNo<${qItem.uMsgSerialNo}> TermType<${qItem.ucTermType}> DataEncoding<CP949>"
        )
        log(
                "Sending Flags : VldPrd<${qItem.nVldPrd}> Priority<${qItem.ucPriority}> RepFlag<${qItem.ucRepFlag}> RgtDlvFlg<${qItem.ucRgtDlvFlg}>"
        )
        log(
                "ConcatenateFlag<${qItem.totalSeg}> ConcatenateInfo<${qItem.segSeq}> SmDefaultMsgID/CALLFW count<${qItem.ucMsgId.firstOrNull() ?: 0}>"
        )

        val msg = qItem.szMsg.toKString()
        log("Message : <${if (msg.isBlank()) "*".repeat(100) else msg}>")
        sb.appendLine("[MSG_DEBUG] Message<${msg}>")

        log(
                "CallBack<${qItem.szCB.toKString()}> CallBack_Noti<${qItem.callback_noti}> CallBack_Check<${qItem.callback_check}> Location<${qItem.ucLocation.toKString()}>"
        )
        log(
                "OrigCID<${qItem.szSrcCId.toKString()}> RelayCID<${qItem.szRelayCID.toKString()}> FowardNo<${qItem.szFWD_NO.toKString()}> ForwardNo2<${qItem.szFWD_NO2.toKString()}> ReturnQ_No<${qItem.ReturnQ_No}> OrgMsgLen<${qItem.uOrgMsgLen}> AuthFlag<${qItem.usAuthFlag}> VirtualNum<${qItem.szRelayCID.toKString()}>"
        )

        // ✅ Rsv4Protocol 배열 상세 출력
        qItem.nRsv4Protocol.forEachIndexed { i, v ->
            val label = "nRsv4Protocol[$i]"

            log("$label   : $v")
        }

        sb.appendLine("### Print QITEM END #################################################")

        // 🔹 WitcomLog 로 최종 출력
        //        witcomLog.p_write(Level.DEBUG, sb.toString())
    }

    private fun formatQItem4(q: QITEM): String {
        val time = SimpleDateFormat("HH:mm:ss:SSSS").format(Date())

        fun bArrToStr(bytes: ByteArray): String =
                String(bytes, Charset.forName("CP949")).trim { it <= ' ' || it == '\u0000' }

        val sb = StringBuffer()

        sb.appendLine(
                "[$time] [DEBUG] ### Print QITEM  ####################################################"
        )
        sb.appendLine(
                "[$time] [DEBUG] ### ServerType<${
                q.ucServerType.toInt().toChar()
            }> MsgVerID<${DEFINE_GIPVERID_510}>"
        )
        sb.appendLine(
                "[$time] [DEBUG] ### Source Address : SrcCID<${bArrToStr(q.szSrcCId)}> SrcCallNo<${bArrToStr(q.szSrcMinNo)}> Source ReturnQNo<${q.usSource}>"
        )
        sb.appendLine(
                "[$time] [DEBUG] ### Destination Address : DestCID<${bArrToStr(q.szCId)}> DestCallNo<${bArrToStr(q.szMinNo)}> ModuleNo<${q.nModuleNo}>"
        )
        sb.appendLine(
                "[$time] [DEBUG] ### Message Code : MessageCode<${q.usMsgCode}> MessageSubCode<${q.usMsgSubCode}> MsgCodeReserved[0](TID)<${q.usMsgCodeReserved[0]}>"
        )
        sb.appendLine(
                "[$time] [DEBUG] ### MsgLen<${q.ucMsgLen}> MsgSerialNo<${q.uMsgSerialNo}> TermType<${q.ucTermType.toInt()}> DataEncoding<${q.ucDataEncoding}>"
        )
        sb.appendLine(
                "[$time] [DEBUG] ### Sending Flags : VldPrd<${q.nVldPrd}> Priority<${q.ucPriority}> RepFlag<${q.ucRepFlag}> RgtDlvFlg<${q.ucRgtDlvFlg}>"
        )
        sb.appendLine(
                "[$time] [DEBUG] ### ConcatenateFlag<${q.totalSeg}> ConcatenateInfo<${q.segSeq}> SmDefaultMsgID/CALLFW count<${q.ucFlagReserved[1].toInt()}>"
        )
        sb.appendLine("[$time] [DEBUG] ### Message : <${bArrToStr(q.szMsg)}>")
        sb.appendLine(
                "[$time] [DEBUG] ### CallBack<${bArrToStr(q.szCB)}> CallBack_Noti<${q.callback_noti}> CallBack_Check<${q.callback_check}> Location<${
                bArrToStr(
                    q.ucLocation
                )
            }>"
        )
        sb.appendLine(
                "[$time] [DEBUG] ### OrigCID<${bArrToStr(q.szOrigCID)}> RelayCID<${bArrToStr(q.szRelayCID)}> FowardNo<${
                bArrToStr(
                    q.szFWD_NO
                )
            }> ForwardNo2<${bArrToStr(q.szFWD_NO2)}> ReturnQ_No<${q.ReturnQ_No}> OrgMsgLen<${q.uOrgMsgLen}> AuthFlag<${q.usAuthFlag}> VirtualNum<>"
        )
        sb.appendLine("[$time] [DEBUG] ### Rsv4protocol (${q.nRsv4Protocol.joinToString(")(")})")
        sb.appendLine(
                "[$time] [DEBUG] ### Print QITEM END ####################################################"
        )

        return sb.toString()
    }

    private fun ByteArray.toKString(): String = String(this, Charsets.UTF_8).trimEnd('\u0000')

    /**
     * ByteArray를 String으로 변환합니다 (null 문자 제거)
     * @param byteArray 변환할 ByteArray
     * @return 변환된 String
     */
    fun byteArrayToKString(byteArray: ByteArray): String =
            String(byteArray, Charsets.UTF_8).trimEnd('\u0000')

    private fun ByteArray.toHexString(): String = joinToString("") { "%02X".format(it) }

    private var gSeqNo = 0

    fun getSerialNo(iProcNo: Int): Int {
        val now = LocalTime.now()
        val currentSec = now.toSecondOfDay() // 시, 분, 초를 초 단위로 환산

        if (gSeqNo > 99) {
            gSeqNo = 1
        }

        val procNo = iProcNo % 10
        val serialStr = String.format("%01d%05d%02d", procNo, currentSec, gSeqNo++)
        return serialStr.toInt()
    }

    /**
     * QITEM 구조체의 데이터를 기반으로 새로운 QITEM을 생성합니다. C 코드의 QItemToMsgHdr 함수 로직을 참고하여 QITEM의 필드를 채웁니다.
     *
     * @param ptrQItem 입력: QITEM 구조체 포인터
     * @return 새로운 QITEM 구조체 (입력 QITEM의 데이터로 채워짐)
     *
     * C 코드 참고: GIPALL/GIPALL_c.c LINE 4095-4129
     */
    fun qItemToMsgHdr(ptrQItem: QITEM): QITEM {
        // 새로운 QITEM 인스턴스 생성
        val destQItem = QITEM()

        // C 코드 LINE 4097: nMsgVerId 복사
        destQItem.nMsgVerId = ptrQItem.nMsgVerId

        // C 코드 LINE 4100-4101: szSrcCId 복사
        System.arraycopy(
                ptrQItem.szSrcCId,
                0,
                destQItem.szSrcCId,
                0,
                minOf(ptrQItem.szSrcCId.size, destQItem.szSrcCId.size)
        )

        // C 코드 LINE 4103: szSrcMinNo 복사 (strtoul 변환 없이 그대로 복사)
        System.arraycopy(
                ptrQItem.szSrcMinNo,
                0,
                destQItem.szSrcMinNo,
                0,
                minOf(ptrQItem.szSrcMinNo.size, destQItem.szSrcMinNo.size)
        )

        // C 코드 LINE 4105: usSource 복사
        destQItem.usSource = ptrQItem.usSource

        // C 코드 LINE 4108-4109: szCId (DestCId) 복사
        System.arraycopy(
                ptrQItem.szCId,
                0,
                destQItem.szCId,
                0,
                minOf(ptrQItem.szCId.size, destQItem.szCId.size)
        )

        // C 코드 LINE 4111: szMinNo (DestMinNo) 복사 (strtoul 변환 없이 그대로 복사)
        System.arraycopy(
                ptrQItem.szMinNo,
                0,
                destQItem.szMinNo,
                0,
                minOf(ptrQItem.szMinNo.size, destQItem.szMinNo.size)
        )

        // C 코드 LINE 4112: nModuleNo 복사
        destQItem.nModuleNo = ptrQItem.nModuleNo

        // C 코드 LINE 4113-4114: usMsgCode, usMsgSubCode 복사
        destQItem.usMsgCode = ptrQItem.usMsgCode
        destQItem.usMsgSubCode = ptrQItem.usMsgSubCode

        // C 코드 LINE 4116-4117: usMsgCodeReserved 배열 복사
        System.arraycopy(
                ptrQItem.usMsgCodeReserved,
                0,
                destQItem.usMsgCodeReserved,
                0,
                minOf(ptrQItem.usMsgCodeReserved.size, destQItem.usMsgCodeReserved.size)
        )

        // C 코드 LINE 4119: ucMsgLen 복사
        destQItem.ucMsgLen = ptrQItem.ucMsgLen

        // C 코드 LINE 4120: uMsgSerialNo 복사
        destQItem.uMsgSerialNo = ptrQItem.uMsgSerialNo

        // C 코드 LINE 4121: ucTermType 복사
        destQItem.ucTermType = ptrQItem.ucTermType

        // C 코드 LINE 4123: ucDataEncoding 복사
        destQItem.ucDataEncoding = ptrQItem.ucDataEncoding

        // C 코드 LINE 4124-4125: ucRsv 배열 복사
        System.arraycopy(
                ptrQItem.ucRsv,
                0,
                destQItem.ucRsv,
                0,
                minOf(ptrQItem.ucRsv.size, destQItem.ucRsv.size)
        )

        // C 코드 LINE 4127: nRsv4Protocol 배열 복사 (12개)
        System.arraycopy(
                ptrQItem.nRsv4Protocol,
                0,
                destQItem.nRsv4Protocol,
                0,
                minOf(ptrQItem.nRsv4Protocol.size, destQItem.nRsv4Protocol.size, 12)
        )

        // C 코드 LINE 4128: nVldPrd 복사 (ucData에 복사하는 대신 직접 복사)
        destQItem.nVldPrd = ptrQItem.nVldPrd

        return destQItem
    }

    /**
     * MinNo 길이를 체크하고 null 종료 문자를 설정합니다. C 코드의 CheckMinNoLen 함수와 동일한 동작을 수행합니다.
     *
     * @param szMinNo MinNo 문자열 (ByteArray)
     * @param iLen 실제 길이
     *
     * C 코드 참고: GIPALL/GIPALL_c.c LINE 4030-4040
     */
    private fun checkMinNoLen(szMinNo: ByteArray, iLen: Int) {
        if (iLen < SmsDef.QITEM_SIZE_MINNO) {
            if (iLen < szMinNo.size) {
                szMinNo[iLen] = 0x00
            }
        } else {
            if (SmsDef.QITEM_SIZE_MINNO - 1 < szMinNo.size) {
                szMinNo[SmsDef.QITEM_SIZE_MINNO - 1] = 0x00
            }
        }
    }

    /**
     * TR QITEM과 CallInfo를 기반으로 새로운 ASP QITEM을 생성합니다. C 코드의 MakeSPQItem 함수와 동일한 동작을 수행합니다.
     *
     * @param ptrTRQItem 입력: TR 결과 QITEM (SMReqTransResult)
     * @param ptrCallInfo 입력: CallInfo 엔티티
     * @param ptrDestQItem 출력: 생성될 ASP QITEM
     * @param recvQNo 수신 큐 번호
     * @param szFWDNO FWD_NO 문자열
     *
     * C 코드 참고: GIPALL/GIPALL_c.c LINE 4132-4188
     */
    fun makeSPQItem(
            ptrTRQItem: com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult,
            ptrCallInfo: com.infra.mo.skt_giphttp_mo.db.altibase.entity.CallInfoEntity,
            ptrDestQItem: QITEM,
            recvQNo: Int,
            szFWDNO: CharArray
    ) {
        // C 코드 LINE 4134-4140: szFWDNO가 있으면 FWD_NO에서 CID와 MinNo 추출
        val szFWDNOStr = String(szFWDNO).trimEnd('\u0000')
        if (szFWDNOStr.isNotEmpty()) {
            // C 코드 LINE 4136: strncpy(ptrDestQItem->szSrcCId, szFWDNO, 3)
            val cidBytes =
                    szFWDNOStr
                            .substring(0, minOf(3, szFWDNOStr.length))
                            .toByteArray(Charset.forName("CP949"))
            val cidCopySize = minOf(cidBytes.size, 3, ptrDestQItem.szSrcCId.size - 1)
            System.arraycopy(cidBytes, 0, ptrDestQItem.szSrcCId, 0, cidCopySize)
            // C 코드 LINE 4137: ptrDestQItem->szSrcCId[3] = '\0'
            ptrDestQItem.szSrcCId[3] = 0x00

            // C 코드 LINE 4138: strcpy(ptrDestQItem->szSrcMinNo, &(szFWDNO[3]))
            if (szFWDNOStr.length > 3) {
                val minNoStr = szFWDNOStr.substring(3)
                val minNoBytes = minNoStr.toByteArray(Charset.forName("CP949"))
                val minNoCopySize = minOf(minNoBytes.size, ptrDestQItem.szSrcMinNo.size - 1)
                System.arraycopy(minNoBytes, 0, ptrDestQItem.szSrcMinNo, 0, minNoCopySize)
                ptrDestQItem.szSrcMinNo[minNoCopySize] = 0x00
                // C 코드 LINE 4139: CheckMinNoLen(ptrDestQItem->szSrcMinNo, strlen(&(szFWDNO[3])))
                checkMinNoLen(ptrDestQItem.szSrcMinNo, minNoStr.length)
            } else {
                ptrDestQItem.szSrcMinNo[0] = 0x00
            }
        } else {
            // C 코드 LINE 4143: strcpy(ptrDestQItem->szSrcCId, ptrTRQItem->szCId)
            val ptrTRQItem_szCId = ptrTRQItem.destCid.toByteArray(Charset.forName("CP949"))
            val cidCopySize = minOf(ptrTRQItem_szCId.size, ptrDestQItem.szSrcCId.size - 1)
            System.arraycopy(ptrTRQItem_szCId, 0, ptrDestQItem.szSrcCId, 0, cidCopySize)
            ptrDestQItem.szSrcCId[cidCopySize] = 0x00

            // C 코드 LINE 4145: strcpy(ptrDestQItem->szSrcMinNo, (char *)ptrCallInfo->szSrcCallNo)
            val ptrCallInfo_szSrcCallNo = ptrCallInfo.srccallno ?: ""
            val srcCallNoBytes = ptrCallInfo_szSrcCallNo.toByteArray(Charset.forName("CP949"))
            val minNoCopySize = minOf(srcCallNoBytes.size, ptrDestQItem.szSrcMinNo.size - 1)
            System.arraycopy(srcCallNoBytes, 0, ptrDestQItem.szSrcMinNo, 0, minNoCopySize)
            ptrDestQItem.szSrcMinNo[minNoCopySize] = 0x00
            // C 코드 LINE 4146: CheckMinNoLen(ptrDestQItem->szSrcMinNo,strlen((char
            // *)ptrCallInfo->szSrcCallNo))
            checkMinNoLen(ptrDestQItem.szSrcMinNo, ptrCallInfo_szSrcCallNo.length)
        }

        // C 코드 LINE 4149: strcpy(ptrDestQItem->szCId, ptrTRQItem->szSrcCId)
        val ptrTRQItem_szSrcCId = ptrTRQItem.srcCid.toByteArray(Charset.forName("CP949"))
        val destCidCopySize = minOf(ptrTRQItem_szSrcCId.size, ptrDestQItem.szCId.size - 1)
        System.arraycopy(ptrTRQItem_szSrcCId, 0, ptrDestQItem.szCId, 0, destCidCopySize)
        ptrDestQItem.szCId[destCidCopySize] = 0x00

        // C 코드 LINE 4150: strcpy(ptrDestQItem->szMinNo,ptrTRQItem->szSrcMinNo)
        val ptrTRQItem_szSrcMinNo = ptrTRQItem.srcMinNo.toByteArray(Charset.forName("CP949"))
        val destMinNoCopySize = minOf(ptrTRQItem_szSrcMinNo.size, ptrDestQItem.szMinNo.size - 1)
        System.arraycopy(ptrTRQItem_szSrcMinNo, 0, ptrDestQItem.szMinNo, 0, destMinNoCopySize)
        ptrDestQItem.szMinNo[destMinNoCopySize] = 0x00
        // C 코드 LINE 4151: CheckMinNoLen(ptrDestQItem->szMinNo,strlen(ptrTRQItem->szSrcMinNo))
        checkMinNoLen(ptrDestQItem.szMinNo, ptrTRQItem.srcMinNo.length)

        // C 코드 LINE 4153: ptrDestQItem->usSource = Recv_QNo
        ptrDestQItem.usSource = recvQNo

        // C 코드 LINE 4155: strcpy(ptrDestQItem->szFWD_NO, ptrTRQItem->szFWD_NO)
        val ptrTRQItem_szFWD_NO = ptrTRQItem.fwdNo.toByteArray(Charset.forName("CP949"))
        val fwdNoCopySize = minOf(ptrTRQItem_szFWD_NO.size, ptrDestQItem.szFWD_NO.size - 1)
        System.arraycopy(ptrTRQItem_szFWD_NO, 0, ptrDestQItem.szFWD_NO, 0, fwdNoCopySize)
        ptrDestQItem.szFWD_NO[fwdNoCopySize] = 0x00

        // C 코드 LINE 4157: ptrDestQItem->ucRgtDlvFlg = ptrCallInfo->ucRgtDlvFlg
        ptrDestQItem.ucRgtDlvFlg = (ptrCallInfo.rgtdlvflag ?: 0).toByte()

        // C 코드 LINE 4159: ptrDestQItem->ucTermType = ptrCallInfo->ucTermType
        ptrDestQItem.ucTermType = (ptrCallInfo.termtype ?: 0).toByte()

        // C 코드 LINE 4161: ptrDestQItem->ucDataEncoding = ptrCallInfo->ucDataEncoding
        ptrDestQItem.ucDataEncoding = (ptrCallInfo.dcsType ?: 14).toByte()

        // C 코드 LINE 4165: nVldPrd = 0
        ptrDestQItem.nVldPrd = 0

        // C 코드 LINE 4167: ptrDestQItem->usMsgCodeReserved[0] = atoi((char *)ptrCallInfo->szTID)
        ptrDestQItem.usMsgCodeReserved[0] = (ptrCallInfo.tid ?: 0).toShort()

        // C 코드 LINE 4171: strncpy((char *)ptrDestQItem->ucMsgId, (char
        // *)ptrCallInfo->szMsgIdServer,QITEM_SIZE_MSGID)
        val ptrCallInfo_szMsgIdServer = ptrCallInfo.msgidserver ?: ""
        val msgIdServerBytes = ptrCallInfo_szMsgIdServer.toByteArray(Charset.forName("CP949"))
        // QITEM_SIZE_MSGID는 ucMsgId의 크기와 동일하다고 가정
        val msgIdCopySize = minOf(msgIdServerBytes.size, ptrDestQItem.ucMsgId.size)
        System.arraycopy(msgIdServerBytes, 0, ptrDestQItem.ucMsgId, 0, msgIdCopySize)
        // strncpy는 null terminator를 보장하지 않으므로 명시적으로 설정하지 않음

        // C 코드 LINE 4174: strcpy((char *)ptrDestQItem->szCB,ptrCallInfo->szCB)
        val ptrCallInfo_szCB = ptrCallInfo.cb ?: ""
        val cbBytes = ptrCallInfo_szCB.toByteArray(Charset.forName("CP949"))
        val cbCopySize = minOf(cbBytes.size, ptrDestQItem.szCB.size - 1)
        System.arraycopy(cbBytes, 0, ptrDestQItem.szCB, 0, cbCopySize)
        ptrDestQItem.szCB[cbCopySize] = 0x00

        // C 코드 LINE 4180: ptrDestQItem->ucMsgLen = ptrCallInfo->ucMsgLen
        ptrDestQItem.ucMsgLen = ptrCallInfo.msglen ?: 0

        // C 코드 LINE 4181: memcpy(ptrDestQItem->szMsg, ptrCallInfo->szMsg, ptrCallInfo->ucMsgLen)
        val ptrCallInfo_szMsg = ptrCallInfo.msg ?: ""
        val msgBytes = ptrCallInfo_szMsg.toByteArray(Charset.forName("CP949"))
        val msgCopySize = minOf(msgBytes.size, ptrDestQItem.szMsg.size, ptrDestQItem.ucMsgLen)
        System.arraycopy(msgBytes, 0, ptrDestQItem.szMsg, 0, msgCopySize)

        // C 코드 LINE 4184: ptrDestQItem->ucRsv4Dlv[0] = ptrTRQItem->ucRsv4Dlv[0]
        ptrDestQItem.ucRsv4Dlv[0] = ptrTRQItem.rsv4Dlv.getOrNull(0) ?: 0

        // C 코드 LINE 4185: ptrDestQItem->nRsv4Protocol[11] = ptrTRQItem->nRsv4Protocol[11]
        if (ptrTRQItem.rsv4Protocol.size > 11 && ptrDestQItem.nRsv4Protocol.size > 11) {
            ptrDestQItem.nRsv4Protocol[11] = ptrTRQItem.rsv4Protocol[11]
        }

        // C 코드 LINE 4187: ptrDestQItem->uOrgMsgLen = ptrCallInfo->uOrgMsgLen
        ptrDestQItem.uOrgMsgLen = ptrCallInfo.orgMsglen ?: 0
    }

    /**
     * C 코드의 InsertIntoASPQ 함수와 동일한 동작을 수행합니다. ASP QITEM을 SMS Manager 큐에 삽입합니다.
     *
     * @param ptrQItem ASP QITEM
     * @param smsQLib SmsQLib 인스턴스
     * @param witcomLog WitcomLog 인스턴스
     * @param gServerID 서버 ID
     * @param spcodeMap SpcodeMap (SMS Manager 서비스 멤버 확인용)
     *
     * C 코드 참고: GIPALL/GIPALL_c.c LINE 3486-3759
     */
    fun insertIntoASPQ(
            ptrQItem: QITEM,
            smsQLib: SmsQLib,
            witcomLog: WitcomLog,
            gServerID: Int,
            spcodeMap:
                    java.util.concurrent.ConcurrentHashMap<
                            String, com.infra.mo.skt_giphttp_mo.db.altibase.entity.SpcodeEntity>
    ) {
        // C 코드 LINE 3502: QItemToMsgHdr 호출 (이미 qItemToMsgHdr로 변환된 상태라고 가정)
        // C 코드 LINE 3504: ucServerType 설정
        ptrQItem.ucServerType = SmsDef.VSMSS_TYPE.toByte()

        // C 코드 LINE 3511: szSrcMinNo에서 CID와 MinNo 분리
        val minTmp = byteArrayToKString(ptrQItem.szSrcMinNo)
        if (minTmp.length >= 3) {
            val szCID = minTmp.substring(0, 3)
            // CheckNpa는 간단히 CID가 유효한 전화번호 형식인지 확인
            // 010, 011, 012, 016, 017, 018, 019 등으로 시작하는지 확인
            if (szCID.startsWith("0") &&
                            (szCID == "010" ||
                                    szCID == "011" ||
                                    szCID == "012" ||
                                    szCID == "016" ||
                                    szCID == "017" ||
                                    szCID == "018" ||
                                    szCID == "019")
            ) {
                // C 코드 LINE 3523-3526: CID와 MinNo 분리
                val cidBytes = szCID.toByteArray(Charset.forName("CP949"))
                System.arraycopy(
                        cidBytes,
                        0,
                        ptrQItem.szSrcCId,
                        0,
                        minOf(cidBytes.size, ptrQItem.szSrcCId.size - 1)
                )
                ptrQItem.szSrcCId[minOf(cidBytes.size, ptrQItem.szSrcCId.size - 1)] = 0x00

                val minNoStr = if (minTmp.length > 3) minTmp.substring(3) else ""
                val minNoBytes = minNoStr.toByteArray(Charset.forName("CP949"))
                System.arraycopy(
                        minNoBytes,
                        0,
                        ptrQItem.szSrcMinNo,
                        0,
                        minOf(minNoBytes.size, ptrQItem.szSrcMinNo.size - 1)
                )
                checkMinNoLen(ptrQItem.szSrcMinNo, minNoStr.length)
            } else if (szCID.length >= 2 &&
                            !szCID.startsWith("0") &&
                            (szCID.startsWith("10") ||
                                    szCID.startsWith("11") ||
                                    szCID.startsWith("12") ||
                                    szCID.startsWith("16") ||
                                    szCID.startsWith("17") ||
                                    szCID.startsWith("18") ||
                                    szCID.startsWith("19"))
            ) {
                // C 코드 LINE 3530-3534: 10, 12, 11 등으로 시작하는 경우
                val cidTmp = minTmp.substring(0, 2)
                val cidBytes = "0$cidTmp".toByteArray(Charset.forName("CP949"))
                System.arraycopy(
                        cidBytes,
                        0,
                        ptrQItem.szSrcCId,
                        0,
                        minOf(cidBytes.size, ptrQItem.szSrcCId.size - 1)
                )
                ptrQItem.szSrcCId[minOf(cidBytes.size, ptrQItem.szSrcCId.size - 1)] = 0x00

                val minNoStr = if (minTmp.length > 2) minTmp.substring(2) else ""
                val minNoBytes = minNoStr.toByteArray(Charset.forName("CP949"))
                System.arraycopy(
                        minNoBytes,
                        0,
                        ptrQItem.szSrcMinNo,
                        0,
                        minOf(minNoBytes.size, ptrQItem.szSrcMinNo.size - 1)
                )
                checkMinNoLen(ptrQItem.szSrcMinNo, minNoStr.length)
            } else {
                // C 코드 LINE 3538-3543: 유효하지 않은 전화번호
                witcomLog.p_write(
                        Level.DEBUG,
                        String.format(
                                "[DEBUG] InsertIntoASPQ() Invalid SourceCallNo (%s).  Not SKT phone number!",
                                minTmp
                        )
                )
                //InsqStat 호출 필요 (C 코드 LINE 3540-3541)
                smsQLib.InsqStat(
                        ptrQItem,
                        MESSAGE_MO,
                        0,
                        gServerID,
                        MODULEID_GIPALL_C,
                        SERVICEID_GIPALL,
                        ERRORID_CP_INVALID_SUBSCRIBER,
                        ST_GIP_INVALID_SMIN_SMSMANAGER,
                        IF_NULL,
                        TID_NO_SAVE,
                        LT_TRACE,
                        0
                )
                // InsertHistory
                return
            }
        } else {
            witcomLog.p_write(
                    Level.DEBUG,
                    String.format(
                            "[DEBUG] InsertIntoASPQ() Invalid SourceCallNo (%s).  Not SKT phone number!",
                            minTmp
                    )
            )
            return
        }

        // C 코드 LINE 3547-3549: DBSMSManagerSelect_ALTIBASE 호출 (spcodeMap으로 확인)
        val srcCId = byteArrayToKString(ptrQItem.szSrcCId)
        val srcMinNo = byteArrayToKString(ptrQItem.szSrcMinNo)

        // spcodeMap에서 CID로 확인 (DBSMSManagerSelect_ALTIBASE 대체)
        val ret =
                if (spcodeMap.containsKey(srcCId)) {
                    SmsDef.ALTI_SUCCESS
                } else {
                    SmsDef.ALTI_NODATA
                }

        if (ret != SmsDef.ALTI_SUCCESS) {
            // C 코드 LINE 3553-3555: SMS Manager 서비스 멤버가 아님
            witcomLog.p_write(
                    Level.WARN,
                    String.format(
                            "[WARNING] InsertIntoASPQ() OK! Not SMS Manager Service member SourceCID(%s) SourceCallNo(%s)",
                            srcCId,
                            srcMinNo
                    )
            )
            return
        }

        witcomLog.p_write(
                Level.INFO,
                String.format(
                        "[NORMAL] InsertIntoASPQ() OK! SMS Manager Service member SourceCID(%s) SourceCallNo(%s)",
                        srcCId,
                        srcMinNo
                )
        )

        // C 코드 LINE 3567-3574: Dest Addr 설정
        val destCId = byteArrayToKString(ptrQItem.szCId)
        val destMinNo = byteArrayToKString(ptrQItem.szMinNo)
        val destMinTmp = "$destCId$destMinNo"
        val destMinBytes = destMinTmp.toByteArray(Charset.forName("CP949"))
        System.arraycopy(
                destMinBytes,
                0,
                ptrQItem.szMinNo,
                0,
                minOf(destMinBytes.size, ptrQItem.szMinNo.size - 1)
        )
        checkMinNoLen(ptrQItem.szMinNo, destMinTmp.length)

        // C 코드 LINE 3573: szCId에 SPCode 설정 (spcodeMap에서 가져옴)
        val spcodeEntity = spcodeMap[srcCId]
        if (spcodeEntity != null && spcodeEntity.spcode != null) {
            val spcode = spcodeEntity.spcode
            val spcodeBytes = spcode.toByteArray(Charset.forName("CP949"))
            System.arraycopy(
                    spcodeBytes,
                    0,
                    ptrQItem.szCId,
                    0,
                    minOf(spcodeBytes.size, ptrQItem.szCId.size - 1)
            )
            ptrQItem.szCId[minOf(spcodeBytes.size, ptrQItem.szCId.size - 1)] = 0x00
        }

        // C 코드 LINE 3576-3581: 현재 시간 설정
        val timeNew = System.currentTimeMillis()
        val tpNew = Calendar.getInstance()
        tpNew.timeInMillis = timeNew
        val curTime =
                String.format(
                        "%04d%02d%02d%02d%02d%02d",
                        tpNew.get(Calendar.YEAR),
                        tpNew.get(Calendar.MONTH) + 1,
                        tpNew.get(Calendar.DAY_OF_MONTH),
                        tpNew.get(Calendar.HOUR_OF_DAY),
                        tpNew.get(Calendar.MINUTE),
                        tpNew.get(Calendar.SECOND)
                )
        // C 코드 LINE 3627: strcat(curTime, "6")
        val curTimeWith6 = "${curTime}6"

        // C 코드 LINE 3583-3641: UCS2 인코딩 처리
        if (ptrQItem.ucDataEncoding.toInt() == SmsDef.DCS_TYPE_DEC_UCS2) {
            // C 코드 LINE 3585: memcpy(msg, ptrQItem->szMsg, ptrQItem->ucMsgLen)
            val msg = ptrQItem.szMsg.sliceArray(0 until ptrQItem.ucMsgLen)

            // UCS2 바이트 배열을 문자열로 디코딩
            val msgStr = decode(msg, SmsEncodingTypeAnalyzer.MessageType.UCS2_BIGENDIAN)

            // C 코드 LINE 3588: nTok = WcsStr(msg, ucsFW)
            var nTok = msgStr.indexOf("[FW]")
            if (nTok < 0) nTok = 0

            // C 코드 LINE 3590-3599: Resultmsg 설정
            val Resultmsg =
                    if (nTok != 0) {
                        msgStr.substring(0, nTok)
                    } else {
                        msgStr
                    }

            // C 코드 LINE 3601-3603: ptrQItem->szMsg에 Resultmsg 복사
            val ResultmsgBytes = Resultmsg.toByteArray(Charset.forName("UTF-16BE"))
            val resultMsgLen = minOf(ResultmsgBytes.size, ptrQItem.szMsg.size)
            System.arraycopy(ResultmsgBytes, 0, ptrQItem.szMsg, 0, resultMsgLen)
            ptrQItem.ucMsgLen = (resultMsgLen / 2).toInt() // UCS2는 2바이트당 1문자

            // C 코드 LINE 3605-3619: [N+] 태그 제거
            val msgAfterFW =
                    String(
                            ptrQItem.szMsg.sliceArray(0 until (ptrQItem.ucMsgLen * 2)),
                            Charset.forName("UTF-16BE")
                    )
            nTok = msgAfterFW.indexOf("[N+]")
            if (nTok < 0) nTok = 0

            val Resultmsg2 =
                    if (nTok != 0) {
                        msgAfterFW.substring(0, nTok)
                    } else {
                        msgAfterFW
                    }

            val Resultmsg2Bytes = Resultmsg2.toByteArray(Charset.forName("UTF-16BE"))
            val resultMsg2Len = minOf(Resultmsg2Bytes.size, ptrQItem.szMsg.size)
            System.arraycopy(Resultmsg2Bytes, 0, ptrQItem.szMsg, 0, resultMsg2Len)
            ptrQItem.ucMsgLen = (resultMsg2Len / 2).toInt()

            // C 코드 LINE 3621-3623: Original Short Message
            val msgOriginal = ptrQItem.szMsg.sliceArray(0 until (ptrQItem.ucMsgLen * 2))

            // C 코드 LINE 3625-3634: curTimeUCS2 생성
            val curTimeUCS2 = curTimeWith6.toByteArray(Charset.forName("UTF-16BE"))

            // C 코드 LINE 3636-3637: 시간 + 메시지 합침
            val curTimeUCS2Len = curTimeUCS2.size / 2 // UCS2 문자 개수
            val msgOriginalLen = msgOriginal.size / 2 // UCS2 문자 개수
            val combinedLen = minOf(curTimeUCS2Len + msgOriginalLen, SmsDef.MAX_SHORT_MSG_LEN / 2)
            val combinedUCS2 = ByteArray(combinedLen * 2)
            System.arraycopy(
                    curTimeUCS2,
                    0,
                    combinedUCS2,
                    0,
                    minOf(curTimeUCS2.size, combinedUCS2.size)
            )
            if (curTimeUCS2.size < combinedUCS2.size) {
                val remainingSize = minOf(msgOriginal.size, combinedUCS2.size - curTimeUCS2.size)
                System.arraycopy(msgOriginal, 0, combinedUCS2, curTimeUCS2.size, remainingSize)
            }

            // C 코드 LINE 3639: ptrQItem->ucMsgLen = WcsLen(...)
            ptrQItem.ucMsgLen = (combinedUCS2.size / 2).toInt()
            System.arraycopy(
                    combinedUCS2,
                    0,
                    ptrQItem.szMsg,
                    0,
                    minOf(combinedUCS2.size, ptrQItem.szMsg.size)
            )
        }
        // C 코드 LINE 3642-3668: GSM7/ASCII7 인코딩 처리
        else if (ptrQItem.ucDataEncoding.toInt() == SmsDef.DCS_TYPE_DEC_GSM7 ||
                        ptrQItem.ucDataEncoding.toInt() == SmsDef.DCS_TYPE_DEC_ASCII7
        ) {
            // C 코드 LINE 3643: memset(msg, 0x00, MAX_SHORT_MSG_LEN)
            val msg = ByteArray(SmsDef.MAX_SHORT_MSG_LEN)

            // C 코드 LINE 3645-3651: Unpack (7bit → 8bit) - msg[15]부터 시작
            val msgBytes = ptrQItem.szMsg.sliceArray(0 until ptrQItem.ucMsgLen)
            val unpackedMsgStr =
                    if (ptrQItem.ucDataEncoding.toInt() == SmsDef.DCS_TYPE_DEC_GSM7) {
                        gsm7bitDecodeToString(msgBytes)
                    } else {
                        // ASCII7은 US_ASCII로 디코딩
                        String(msgBytes, StandardCharsets.US_ASCII)
                    }
            // msg[15]부터 unpacked 메시지 복사
            val unpackedMsgBytes = unpackedMsgStr.toByteArray(StandardCharsets.US_ASCII)
            System.arraycopy(
                    unpackedMsgBytes,
                    0,
                    msg,
                    15,
                    minOf(unpackedMsgBytes.size, msg.size - 15)
            )

            // C 코드 LINE 3654-3655: memcpy(msg, curTime,14); msg[14] = '6'
            val curTimeBytes = curTime.toByteArray(Charsets.US_ASCII)
            System.arraycopy(curTimeBytes, 0, msg, 0, minOf(14, curTimeBytes.size))
            msg[14] = '6'.code.toByte()

            // C 코드 LINE 3657: ptrQItem->uOrgMsgLen += 15
            ptrQItem.uOrgMsgLen = ptrQItem.uOrgMsgLen + 15

            // C 코드 LINE 3660: memset(ptrQItem->szMsg,0x00,MAX_SHORT_MSG_LEN)
            // C 코드 LINE 3662-3667: Packing (8bit → 7bit)
            val msgLen = unpackedMsgStr.length + 15 // 시간(15) + 원본 메시지
            val packedMsg =
                    if (ptrQItem.ucDataEncoding.toInt() == SmsDef.DCS_TYPE_DEC_GSM7) {
                        val msgStr =
                                String(msg.sliceArray(0 until msgLen), StandardCharsets.US_ASCII)
                        textTogsm7bitEncode(msgStr)
                    } else {
                        // ASCII7은 US_ASCII로 인코딩
                        msg.sliceArray(0 until msgLen)
                    }

            // C 코드 LINE 3664/3667: ptrQItem->ucMsgLen = Packing8bitTo7bit(...)
            val copySize = minOf(packedMsg.size, ptrQItem.szMsg.size)
            System.arraycopy(packedMsg, 0, ptrQItem.szMsg, 0, copySize)
            ptrQItem.ucMsgLen = copySize.toInt()
        }
        // C 코드 LINE 3669-3713: 기타 인코딩 처리
        else {
            // C 코드 LINE 3671: memcpy(msg, ptrQItem->szMsg, ptrQItem->ucMsgLen + 1)
            val msg = ptrQItem.szMsg.sliceArray(0 until (ptrQItem.ucMsgLen + 1))
            val msgStr = String(msg, Charset.forName("CP949"))

            // C 코드 LINE 3673: tok = strstr(msg, "[FW]")
            var tok = msgStr.indexOf("[FW]")

            // C 코드 LINE 3674-3685: Resultmsg 설정
            val Resultmsg =
                    if (tok >= 0) {
                        msgStr.substring(0, tok)
                    } else {
                        msgStr
                    }

            // C 코드 LINE 3687-3690: msg와 ptrQItem->szMsg 업데이트
            val ResultmsgBytes = Resultmsg.toByteArray(Charset.forName("CP949"))
            val resultMsgLen = minOf(ResultmsgBytes.size, ptrQItem.szMsg.size)
            System.arraycopy(ResultmsgBytes, 0, ptrQItem.szMsg, 0, resultMsgLen)
            ptrQItem.ucMsgLen = resultMsgLen

            // C 코드 LINE 3692: tok = strstr(msg, "[N+]")
            val msgAfterFW =
                    String(
                            ptrQItem.szMsg.sliceArray(0 until ptrQItem.ucMsgLen),
                            Charset.forName("CP949")
                    )
            tok = msgAfterFW.indexOf("[N+]")

            // C 코드 LINE 3693-3704: Resultmsg 설정
            val Resultmsg2 =
                    if (tok >= 0) {
                        msgAfterFW.substring(0, tok)
                    } else {
                        msgAfterFW
                    }

            val Resultmsg2Bytes = Resultmsg2.toByteArray(Charset.forName("CP949"))
            val resultMsg2Len = minOf(Resultmsg2Bytes.size, ptrQItem.szMsg.size)
            System.arraycopy(Resultmsg2Bytes, 0, ptrQItem.szMsg, 0, resultMsg2Len)
            ptrQItem.ucMsgLen = resultMsg2Len

            // C 코드 LINE 3707-3711: 시간 + "6" + 메시지
            val msgFinal =
                    String(
                            ptrQItem.szMsg.sliceArray(0 until ptrQItem.ucMsgLen),
                            Charset.forName("CP949")
                    )
            val finalMsg = "${curTime}6$msgFinal"
            val finalMsgBytes = finalMsg.toByteArray(Charset.forName("CP949"))
            val copySize = minOf(finalMsgBytes.size, ptrQItem.szMsg.size)
            System.arraycopy(finalMsgBytes, 0, ptrQItem.szMsg, 0, copySize)
            ptrQItem.ucMsgLen = copySize
        }

        // C 코드 LINE 3715-3720: MO Message 설정
        ptrQItem.usSource = SmsDef.SMSMOR
        ptrQItem.usMsgCodeReserved[0] = 4098
        ptrQItem.usMsgCode = SmsDef.QTYPE_SM_MO.toShort()
        ptrQItem.usMsgSubCode = SmsDef.SUB_QTYPE_REQ_SEND.toShort()
        if (ptrQItem.nRsv4Protocol.size > 11) {
            ptrQItem.nRsv4Protocol[11] = 1
        }

        // C 코드 LINE 3723: InsertIntoSmsQnQNo 호출 (InsertIntoSmsQnQNo 사용)
        // 큐 번호는 내부적으로 결정되므로 InsertIntoSmsQ 사용
        val nQueueNo = 0
        val formatted = String.format("[GIPHTTP_MESSAGE_MANAGER_TR] Get CP Qno(%s) ==", nQueueNo)
        witcomLog.p_write(Level.DEBUG, formatted)

        fetchAndConvert3(ptrQItem, witcomLog) // -> 문자매니저 전문 출력

        val insertResult = smsQLib.InsertIntoSmsQnQNo(ptrQItem, nQueueNo)

        // C 코드 LINE 3726-3757: 결과 처리
        if (insertResult == SmsDef.Q_INSERT_FAIL_Q_FULL) {
            witcomLog.p_write(Level.ERROR, "[ERROR] InsertIntoASPQ ERROR : InsertIntoSmsQ [Q_FULL]")
            val szCIdInt = byteArrayToKString(ptrQItem.szCId).toIntOrNull() ?: 0
            //InsqStat 호출 필요 (C 코드 LINE 3737-3738)
            smsQLib.InsqStat(
                    ptrQItem,
                    MESSAGE_MO,
                    0,
                    gServerID,
                    MODULEID_GIPALL_C,
                    SERVICEID_GIPALL,
                    ERRORID_CP_INSERTQ_FAIL,
                    ST_Q_FULL_SMSMANAGER,
                    szCIdInt,
                    TID_NO_SAVE,
                    LT_TRACE,
                    0
            )
            // InsertHistory(gCallHistory, ptrQItem, NULL, MODULE_GIPALL, MANAGERQ_FULL, __LINE__,
            // atoi(gszSPCode));
        } else if (insertResult < 0) {
            witcomLog.p_write(
                    Level.ERROR,
                    String.format(
                            "[ERROR] InsertIntoASPQ ERROR : InsertIntoSmsQ %d[Q_FAIL]",
                            insertResult
                    )
            )
            val szCIdInt = byteArrayToKString(ptrQItem.szCId).toIntOrNull() ?: 0
            //InsqStat 호출 필요 (C 코드 LINE 3750-3751)
            smsQLib.InsqStat(
                    ptrQItem,
                    MESSAGE_MO,
                    0,
                    gServerID,
                    MODULEID_GIPALL_C,
                    SERVICEID_GIPALL,
                    ERRORID_CP_INSERTQ_FAIL,
                    ST_Q_INSERT_FAIL_SMSMANAGER,
                    szCIdInt,
                    TID_NO_SAVE,
                    LT_TRACE,
                    0
            )
            //            InsertHistory(gCallHistory, ptrQItem, NULL, MODULE_GIPALL,
            // MANAGERQ_INSERT_FAIL, __LINE__, atoi(gszSPCode));
        } else {
            witcomLog.p_write(
                    Level.INFO,
                    String.format(
                            "[NORMAL] InsertIntoASPQ() OK! SMS Manager Service member SourceCID(%s) SourceCallNo(%s)",
                            srcCId,
                            srcMinNo
                    )
            )
            val szCIdInt = byteArrayToKString(ptrQItem.szCId).toIntOrNull() ?: 0
            //InsqStat 호출 필요 (C 코드 LINE 3757-3758)
            smsQLib.InsqStat(
                    ptrQItem,
                    MESSAGE_MO,
                    0,
                    gServerID,
                    MODULEID_GIPALL_C,
                    SERVICEID_GIPALL,
                    ERRORID_CP_MO_SUCCESS,
                    ST_GIPALL_SMSMGR_OK,
                    szCIdInt,
                    TID_NO_SAVE,
                    LT_BOTH,
                    0
            )
        }
    }
}
