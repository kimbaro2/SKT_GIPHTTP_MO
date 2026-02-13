package com.infra.mo.skt_giphttp_mo.service.impl

import ch.qos.logback.classic.Level
import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.MONotISendEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.MONotISendRepository
import com.infra.mo.skt_giphttp_mo.dto.SegmentInfo
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_8BIT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_ASCII7
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_8BIT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_ASCII7
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_GSM7
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_KSC5601
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_UCS2
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_DEC_UNKNOWN
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_GSM7
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_KSC5601
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MAX_VAILD_PERIOD
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_UCS2
import com.infra.mo.skt_giphttp_mo.service.MoDbInsertService
import com.infra.mo.skt_giphttp_mo.service.SmsResService
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * MO DB Insert 전용 서비스 구현.
 * insertMO_NOTISEND, insertRelayMOCallInfo는 MOThreadPool에서 이관된 로직,
 * insertGIPMOCallInfo는 SmsResService에 위임.
 */
@Service
open class MoDbInsertServiceImpl(
    private val witcomLog: WitcomLog,
    private val moNotISendRepository: MONotISendRepository,
    private val smsResService: SmsResService
) : MoDbInsertService {

    override fun insertGIPMOCallInfo(
        qItem: QITEM,
        msgHdr: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        workerThreadId: Long
    ): Int = smsResService.insertGIPMOCallInfo(qItem, msgHdr, entity, workerThreadId)

    /**
     * InsertMO_NOTISEND 구현 (C 코드 LINE 1626 대응)
     * @param segmentInfo Segment 정보 (MMS/PUSH 타입에만 유효)
     */
    @Transactional
    open override fun insertMO_NOTISEND(
        qItem: QITEM,
        msgHdr: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        segmentInfo: SegmentInfo?,
        workerThreadId: Long
    ): Int {
        val loggerName = "${entity.cid}-${entity.ipAddr}-${entity.portNo}"
        return try {
            val srcCId = QItemServiceUtil.byteArrayToKString(qItem.szSrcCId)
            val srcCallNo = QItemServiceUtil.byteArrayToKString(qItem.szSrcMinNo)
            val destCId = QItemServiceUtil.byteArrayToKString(qItem.szCId)
            val destCallNo = QItemServiceUtil.byteArrayToKString(qItem.szMinNo)

            val msgId = QItemServiceUtil.byteArrayToKString(qItem.ucMsgId)
            if (msgId.isBlank()) {
                witcomLog.c_write(
                    loggerName,
                    Level.INFO,
                    String.format(
                        "[insertMO_NOTISEND] ❌ 치명적 오류: msgId가 비어있음. HTTP 전송 전에 msgId가 생성되어야 합니다. destCID(%s), srcCID(%s), srcCallNo(%s)",
                        destCId, srcCId, srcCallNo
                    ),
                    workerThreadId
                )
                return -1
            }
            val node = System.getenv("SMSS_NODE") ?: System.getenv("HOSTNAME") ?: "UNKNOWN"

            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyMMddHHmmss")
            val usec = String.format("%02d", cal.get(Calendar.MILLISECOND) / 10)

            val moRecvTimeRaw = QItemServiceUtil.byteArrayToKString(qItem.szMoRecvTime)
            val moRecvTime = if (moRecvTimeRaw.isBlank()) {
                sdf.format(cal.time) + usec
            } else {
                moRecvTimeRaw
            }

            val tidInt = qItem.usMsgCodeReserved[0].toInt()
            var tid = tidInt.toString()
            if (tid.length < 5) {
                tid = tid.padEnd(5, 'F')
            }
            if (tid == "0FFFF") {
                tid = "4098F"
            }

            val segment: Int = if (segmentInfo != null && segmentInfo.isValid) {
                qItem.ucRsv[1].toInt() and 0xFF
            } else {
                0xFF
            }
            val esmClass = qItem.nRsv4Protocol[11]

            val wZone = if (qItem.ucRsv4Dlv.isNotEmpty()) {
                qItem.ucRsv4Dlv[1].toInt().toChar().toString()
            } else {
                "0"
            }

            val traceId = QItemServiceUtil.byteArrayToKString(qItem.szTraceId)
            if (traceId.isBlank()) {
                witcomLog.c_write(
                    loggerName,
                    Level.INFO,
                    String.format(
                        "[insertMO_NOTISEND] ⚠️ TraceId가 비어있음: HTTP 전송 전에 생성되어야 함. msgId(%s)",
                        msgId
                    ),
                    workerThreadId
                )
            }
            val origMvnoInfo = QItemServiceUtil.byteArrayToKString(qItem.szOrigMvnoInformation)
            val destMvnoInfo = QItemServiceUtil.byteArrayToKString(qItem.szDestMvnoInformation)

            val dcsType = when (qItem.ucDataEncoding) {
                DCS_TYPE_GSM7.code.toByte() -> DCS_TYPE_DEC_GSM7
                DCS_TYPE_ASCII7.code.toByte() -> DCS_TYPE_DEC_ASCII7
                DCS_TYPE_8BIT.code.toByte() -> DCS_TYPE_DEC_8BIT
                DCS_TYPE_UCS2.code.toByte() -> DCS_TYPE_DEC_UCS2
                DCS_TYPE_KSC5601 -> DCS_TYPE_DEC_KSC5601
                else -> DCS_TYPE_DEC_UNKNOWN
            }

            val msgLen = qItem.ucMsgLen.toInt()
            val orgMsgLen = qItem.uOrgMsgLen
            val cb = QItemServiceUtil.byteArrayToKString(qItem.szCB)

            val serverType = qItem.ucServerType.toInt().toChar().toString()
            witcomLog.c_write(
                loggerName,
                Level.INFO,
                String.format(
                    "InsertMO_NOTISEND() ServerType(%s) szSrcCID(%s) szSrcCallNo(%s) szDestCID(%s) szDestCallNo(%s) szMsgId(%s) szCB(%s) EsmClass(%d) DCSType(%d) SrcType(%d) VirtualNum(%s)",
                    serverType,
                    srcCId,
                    srcCallNo,
                    destCId,
                    destCallNo,
                    msgId,
                    cb,
                    esmClass,
                    dcsType,
                    qItem.ucServerType.toInt(),
                    ""
                ),
                workerThreadId
            )

            val moNotISend = MONotISendEntity().apply {
                this.msgId = msgId
                this.srcCId = srcCId
                this.destCId = destCId
                this.serverType = serverType
                this.node = node
                this.moSubTime = cal.time
                this.srcCallNo = srcCallNo
                this.destCallNo = destCallNo
                // expireTime: 무조건 조정된 값 사용 (RECV와 동일: nVldPrd>0이면 nVldPrd, 0이면 MAX_VAILD_PERIOD)
                val effectiveVldPrd = if (qItem.nVldPrd > 0) qItem.nVldPrd else MAX_VAILD_PERIOD
                this.expireTime = java.util.Date(System.currentTimeMillis() + (effectiveVldPrd * 1000L))
                this.segment = segment
                this.tid = tid
                this.cb = cb
                this.esmClass = esmClass
                this.wZone = wZone
                this.traceId = traceId
                this.origMvnoInfo = origMvnoInfo
                this.destMvnoInfo = destMvnoInfo
                this.msgLen = msgLen
                this.dcsType = dcsType
                this.orgMsgLen = orgMsgLen
                this.moRecvTime = moRecvTime
            }

            witcomLog.c_write(
                loggerName,
                Level.INFO,
                String.format(
                    "InsertMO_NOTISEND OK : MsgId(%s) TraceId(%s) SrcCallNo(%s) DestCID(%s)",
                    msgId, traceId, srcCallNo, destCId
                ),
                workerThreadId
            )
            0
        } catch (e: Exception) {
            val errorSrcCId = try {
                QItemServiceUtil.byteArrayToKString(qItem.szSrcCId)
            } catch (_: Exception) {
                "UNKNOWN"
            }
            val errorSrcCallNo = try {
                QItemServiceUtil.byteArrayToKString(qItem.szSrcMinNo)
            } catch (_: Exception) {
                "UNKNOWN"
            }
            val errorDestCId = try {
                QItemServiceUtil.byteArrayToKString(qItem.szCId)
            } catch (_: Exception) {
                "UNKNOWN"
            }
            val errorDestCallNo = try {
                QItemServiceUtil.byteArrayToKString(qItem.szMinNo)
            } catch (_: Exception) {
                "UNKNOWN"
            }
            witcomLog.c_write(
                loggerName,
                Level.INFO,
                String.format(
                    "InsertMO_NOTISEND() Insert Error: srcCID(%s), srcCallNo(%s), destCID(%s), destCallNo(%s)",
                    errorSrcCId, errorSrcCallNo, errorDestCId, errorDestCallNo
                ),
                workerThreadId
            )
            -1
        }
    }

    /**
     * MO_NOTISEND 저장 — 안심문자(ESMClass 20, 21) 전용. 등기문자와 공통 서비스 없이 도메인별 중복 배치.
     */
    @Transactional
    open override fun insertMO_NOTISEND_NotiPlus(
        qItem: QITEM,
        msgHdr: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        segmentInfo: SegmentInfo?,
        workerThreadId: Long
    ): Int = insertMO_NOTISEND_NotiPlusImpl(qItem, msgHdr, entity, segmentInfo, workerThreadId, "NotiPlus")

    /**
     * MO_NOTISEND 저장 — 등기문자(ESMClass 90, 91) 전용. 안심문자와 공통 서비스 없이 도메인별 중복 배치.
     */
    @Transactional
    open override fun insertMO_NOTISEND_NotiRegistered(
        qItem: QITEM,
        msgHdr: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        segmentInfo: SegmentInfo?,
        workerThreadId: Long
    ): Int = insertMO_NOTISEND_NotiRegisteredImpl(qItem, msgHdr, entity, segmentInfo, workerThreadId, "NotiRegistered")

    private fun insertMO_NOTISEND_NotiPlusImpl(
        qItem: QITEM,
        msgHdr: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        segmentInfo: SegmentInfo?,
        workerThreadId: Long,
        domainLabel: String
    ): Int {
        val loggerName = "${entity.cid}-${entity.ipAddr}-${entity.portNo}"
        return try {
            val srcCId = QItemServiceUtil.byteArrayToKString(qItem.szSrcCId)
            val srcCallNo = QItemServiceUtil.byteArrayToKString(qItem.szSrcMinNo)
            val destCId = QItemServiceUtil.byteArrayToKString(qItem.szCId)
            val destCallNo = QItemServiceUtil.byteArrayToKString(qItem.szMinNo)
            val msgId = QItemServiceUtil.byteArrayToKString(qItem.ucMsgId)
            if (msgId.isBlank()) {
                witcomLog.c_write(
                    loggerName, Level.INFO,
                    String.format(
                        "[insertMO_NOTISEND_%s] ❌ msgId 비어있음. destCID(%s), srcCID(%s), srcCallNo(%s)",
                        domainLabel,
                        destCId,
                        srcCId,
                        srcCallNo
                    ), workerThreadId
                )
                return -1
            }
            val node = System.getenv("SMSS_NODE") ?: System.getenv("HOSTNAME") ?: "UNKNOWN"
            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyMMddHHmmss")
            val usec = String.format("%02d", cal.get(Calendar.MILLISECOND) / 10)
            val moRecvTimeRaw = QItemServiceUtil.byteArrayToKString(qItem.szMoRecvTime)
            val moRecvTime = if (moRecvTimeRaw.isBlank()) sdf.format(cal.time) + usec else moRecvTimeRaw
            var tid = qItem.usMsgCodeReserved[0].toInt().toString()
            if (tid.length < 5) tid = tid.padEnd(5, 'F')
            if (tid == "0FFFF") tid = "4098F"
            val segment: Int = if (segmentInfo != null && segmentInfo.isValid) {
                qItem.ucRsv[1].toInt() and 0xFF
            } else {
                0xFF
            }
            val esmClass = qItem.nRsv4Protocol[11]
            val wZone = if (qItem.ucRsv4Dlv.isNotEmpty()) qItem.ucRsv4Dlv[1].toInt().toChar().toString() else "0"
            val traceId = QItemServiceUtil.byteArrayToKString(qItem.szTraceId)
            val origMvnoInfo = QItemServiceUtil.byteArrayToKString(qItem.szOrigMvnoInformation)
            val destMvnoInfo = QItemServiceUtil.byteArrayToKString(qItem.szDestMvnoInformation)
            val dcsType = when (qItem.ucDataEncoding) {
                DCS_TYPE_GSM7.code.toByte() -> DCS_TYPE_DEC_GSM7
                DCS_TYPE_ASCII7.code.toByte() -> DCS_TYPE_DEC_ASCII7
                DCS_TYPE_8BIT.code.toByte() -> DCS_TYPE_DEC_8BIT
                DCS_TYPE_UCS2.code.toByte() -> DCS_TYPE_DEC_UCS2
                DCS_TYPE_KSC5601 -> DCS_TYPE_DEC_KSC5601
                else -> DCS_TYPE_DEC_UNKNOWN
            }
            val msgLen = qItem.ucMsgLen.toInt()
            val orgMsgLen = qItem.uOrgMsgLen
            val cb = QItemServiceUtil.byteArrayToKString(qItem.szCB)
            val serverType = qItem.ucServerType.toInt().toChar().toString()
            witcomLog.c_write(
                loggerName, Level.INFO,
                String.format(
                    "InsertMO_NOTISEND_%s() ServerType(%s) szSrcCID(%s) szSrcCallNo(%s) szDestCID(%s) szDestCallNo(%s) szMsgId(%s) EsmClass(%d) DCSType(%d)",
                    domainLabel,
                    serverType,
                    srcCId,
                    srcCallNo,
                    destCId,
                    destCallNo,
                    msgId,
                    esmClass,
                    dcsType
                ), workerThreadId
            )
            val moNotISend = MONotISendEntity().apply {
                this.msgId = msgId
                this.srcCId = srcCId
                this.destCId = destCId
                this.serverType = serverType
                this.node = node
                this.moSubTime = cal.time
                this.srcCallNo = srcCallNo
                this.destCallNo = destCallNo
                val effectiveVldPrd = if (qItem.nVldPrd > 0) qItem.nVldPrd else MAX_VAILD_PERIOD
                this.expireTime = java.util.Date(System.currentTimeMillis() + (effectiveVldPrd * 1000L))
                this.segment = segment
                this.tid = tid
                this.cb = cb
                this.esmClass = esmClass
                this.wZone = wZone
                this.traceId = traceId
                this.origMvnoInfo = origMvnoInfo
                this.destMvnoInfo = destMvnoInfo
                this.msgLen = msgLen
                this.dcsType = dcsType
                this.orgMsgLen = orgMsgLen
                this.moRecvTime = moRecvTime
            }
            moNotISendRepository.save(moNotISend)
            witcomLog.c_write(
                loggerName, Level.INFO,
                String.format(
                    "InsertMO_NOTISEND_%s OK : MsgId(%s) TraceId(%s) SrcCallNo(%s) DestCID(%s)",
                    domainLabel,
                    msgId,
                    traceId,
                    srcCallNo,
                    destCId
                ), workerThreadId
            )
            0
        } catch (e: Exception) {
            val errorDestCId = try {
                QItemServiceUtil.byteArrayToKString(qItem.szCId)
            } catch (_: Exception) {
                "UNKNOWN"
            }
            witcomLog.c_write(
                loggerName, Level.INFO,
                String.format(
                    "InsertMO_NOTISEND_%s() Insert Error: destCID(%s) error(%s)",
                    domainLabel,
                    errorDestCId,
                    e.message
                ), workerThreadId
            )
            -1
        }
    }

    private fun insertMO_NOTISEND_NotiRegisteredImpl(
        qItem: QITEM,
        msgHdr: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        segmentInfo: SegmentInfo?,
        workerThreadId: Long,
        domainLabel: String
    ): Int {
        val loggerName = "${entity.cid}-${entity.ipAddr}-${entity.portNo}"
        return try {
            val srcCId = QItemServiceUtil.byteArrayToKString(qItem.szSrcCId)
            val srcCallNo = QItemServiceUtil.byteArrayToKString(qItem.szSrcMinNo)
            val destCId = QItemServiceUtil.byteArrayToKString(qItem.szCId)
            val destCallNo = QItemServiceUtil.byteArrayToKString(qItem.szMinNo)
            val msgId = QItemServiceUtil.byteArrayToKString(qItem.ucMsgId)
            if (msgId.isBlank()) {
                witcomLog.c_write(
                    loggerName, Level.INFO,
                    String.format(
                        "[insertMO_NOTISEND_%s] ❌ msgId 비어있음. destCID(%s), srcCID(%s), srcCallNo(%s)",
                        domainLabel,
                        destCId,
                        srcCId,
                        srcCallNo
                    ), workerThreadId
                )
                return -1
            }
            val node = System.getenv("SMSS_NODE") ?: System.getenv("HOSTNAME") ?: "UNKNOWN"
            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyMMddHHmmss")
            val usec = String.format("%02d", cal.get(Calendar.MILLISECOND) / 10)
            val moRecvTimeRaw = QItemServiceUtil.byteArrayToKString(qItem.szMoRecvTime)
            val moRecvTime = if (moRecvTimeRaw.isBlank()) sdf.format(cal.time) + usec else moRecvTimeRaw
            var tid = qItem.usMsgCodeReserved[0].toInt().toString()
            if (tid.length < 5) tid = tid.padEnd(5, 'F')
            if (tid == "0FFFF") tid = "4098F"
            val segment: Int = if (segmentInfo != null && segmentInfo.isValid) {
                qItem.ucRsv[1].toInt() and 0xFF
            } else {
                0xFF
            }
            val esmClass = qItem.nRsv4Protocol[11]
            val wZone = if (qItem.ucRsv4Dlv.isNotEmpty()) qItem.ucRsv4Dlv[1].toInt().toChar().toString() else "0"
            val traceId = QItemServiceUtil.byteArrayToKString(qItem.szTraceId)
            val origMvnoInfo = QItemServiceUtil.byteArrayToKString(qItem.szOrigMvnoInformation)
            val destMvnoInfo = QItemServiceUtil.byteArrayToKString(qItem.szDestMvnoInformation)
            val dcsType = when (qItem.ucDataEncoding) {
                DCS_TYPE_GSM7.code.toByte() -> DCS_TYPE_DEC_GSM7
                DCS_TYPE_ASCII7.code.toByte() -> DCS_TYPE_DEC_ASCII7
                DCS_TYPE_8BIT.code.toByte() -> DCS_TYPE_DEC_8BIT
                DCS_TYPE_UCS2.code.toByte() -> DCS_TYPE_DEC_UCS2
                DCS_TYPE_KSC5601 -> DCS_TYPE_DEC_KSC5601
                else -> DCS_TYPE_DEC_UNKNOWN
            }
            val msgLen = qItem.ucMsgLen.toInt()
            val orgMsgLen = qItem.uOrgMsgLen
            val cb = QItemServiceUtil.byteArrayToKString(qItem.szCB)
            val serverType = qItem.ucServerType.toInt().toChar().toString()
            witcomLog.c_write(
                loggerName, Level.INFO,
                String.format(
                    "InsertMO_NOTISEND_%s() ServerType(%s) szSrcCID(%s) szSrcCallNo(%s) szDestCID(%s) szDestCallNo(%s) szMsgId(%s) EsmClass(%d) DCSType(%d)",
                    domainLabel,
                    serverType,
                    srcCId,
                    srcCallNo,
                    destCId,
                    destCallNo,
                    msgId,
                    esmClass,
                    dcsType
                ), workerThreadId
            )
            val moNotISend = MONotISendEntity().apply {
                this.msgId = msgId
                this.srcCId = srcCId
                this.destCId = destCId
                this.serverType = serverType
                this.node = node
                this.moSubTime = cal.time
                this.srcCallNo = srcCallNo
                this.destCallNo = destCallNo
                val effectiveVldPrd = if (qItem.nVldPrd > 0) qItem.nVldPrd else MAX_VAILD_PERIOD
                this.expireTime = java.util.Date(System.currentTimeMillis() + (effectiveVldPrd * 1000L))
                this.segment = segment
                this.tid = tid
                this.cb = cb
                this.esmClass = esmClass
                this.wZone = wZone
                this.traceId = traceId
                this.origMvnoInfo = origMvnoInfo
                this.destMvnoInfo = destMvnoInfo
                this.msgLen = msgLen
                this.dcsType = dcsType
                this.orgMsgLen = orgMsgLen
                this.moRecvTime = moRecvTime
            }
            moNotISendRepository.save(moNotISend)
            witcomLog.c_write(
                loggerName, Level.INFO,
                String.format(
                    "InsertMO_NOTISEND_%s OK : MsgId(%s) TraceId(%s) SrcCallNo(%s) DestCID(%s)",
                    domainLabel,
                    msgId,
                    traceId,
                    srcCallNo,
                    destCId
                ), workerThreadId
            )
            0
        } catch (e: Exception) {
            val errorDestCId = try {
                QItemServiceUtil.byteArrayToKString(qItem.szCId)
            } catch (_: Exception) {
                "UNKNOWN"
            }
            witcomLog.c_write(
                loggerName, Level.INFO,
                String.format(
                    "InsertMO_NOTISEND_%s() Insert Error: destCID(%s) error(%s)",
                    domainLabel,
                    errorDestCId,
                    e.message
                ), workerThreadId
            )
            -1
        }
    }

    /**
     * Relay MO CallInfo Insert (C 코드 LINE 1636-1642 대응)
     */
    @Transactional
    open override fun insertRelayMOCallInfo(
        qItem: QITEM,
        msgHdr: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        serialNo: Long
    ): Int {
        val loggerName = "${entity.cid}-${entity.ipAddr}-${entity.portNo}"
        return try {
            witcomLog.c_write(
                loggerName,
                Level.INFO,
                String.format(
                    "InsertRelayMOCallInfo OK : MsgId(%d) SrcCallNo(%s) DestCID(%s) Relay[0]=%d, [1]=%d",
                    serialNo,
                    QItemServiceUtil.byteArrayToKString(qItem.szSrcMinNo),
                    QItemServiceUtil.byteArrayToKString(qItem.szCId),
                    qItem.nRsv4Protocol[0],
                    qItem.nRsv4Protocol[1]
                ),
                Thread.currentThread().id
            )
            witcomLog.c_write(
                loggerName,
                Level.INFO,
                "TODO: RelayMOCallInfo 엔티티 및 Repository 구현 필요",
                Thread.currentThread().id
            )
            0
        } catch (e: Exception) {
            witcomLog.c_write(
                loggerName,
                Level.INFO,
                String.format("InsertRelayMOCallInfo() Insert Error: %s", e.message),
                Thread.currentThread().id
            )
            -1
        }
    }
}
