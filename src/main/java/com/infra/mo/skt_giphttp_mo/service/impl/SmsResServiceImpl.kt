package com.infra.mo.skt_giphttp_mo.service.impl

import ch.qos.logback.classic.Level
import com.infra.mo.skt_giphttp_mo.config.application.LiveReloadCLibraryFile
import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GIENQEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.MOCallInfoEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.GIENQRepository
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.GipHttpMoAccessRepository
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.MOCallInfoRepository
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.COMMON_SMS
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
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_UCS2
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DCS_TYPE_UNKNOWN
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_FAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_NODATA
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_SUCCESS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_TR_SUCCESS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.GI_RES_NO_ERR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.IF_NULL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LT_BOTH
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LT_TRACE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MESSAGE_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MESSAGE_TR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MODULEID_GIPEVENT_C
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NOTI_PLUS_NORMAL_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NOTI_PLUS_PORTED_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.QTYPE_SM_REQ
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.RCS_RESULT_SENT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.RCS_TO_SMS
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SERVICEID_GIPEVENT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_SEND
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_SIMPLE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SM_REQ_TRANS_RESULT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_DB_NO_DATA_GIPMOCALLINFO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIPEVENT_MOACK_BILL_OK
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIPEVENT_MORS_OK
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIPEVENT_MTTR_OK
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIP_INVALID_CID
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ST_GIP_MORS_FAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SUB_QTYPE_RCS_TR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.TERM_TYPE_KOR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.TID_NO_SAVE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.VSMSS_TYPE
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseTR
import com.infra.mo.skt_giphttp_mo.service.SmsResService
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.FileWriter
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * SMS 결과 처리 서비스 구현체
 * 
 * C 코드 참고: GIPEVENT_c.c LINE 1928 (ProcessSMRes)
 * - SM_REQ_SIMPLE: MO 메시지 ACK 처리
 * - SM_REQ_TRANS_RESULT: TR 결과 처리
 */
@Service
open class SmsResServiceImpl(
    private val liveReloadCLibraryFile: LiveReloadCLibraryFile,
    private val witcomLog: WitcomLog,
    @Qualifier("ApplicationCacheMap") private val cacheMap: ConcurrentHashMap<String, Any>,
    private val moCallInfoRepository: MOCallInfoRepository,
    private val gienqRepository: GIENQRepository,
    private val gipHttpMoAccessRepository: GipHttpMoAccessRepository
) : SmsResService {
    private val gServerID = System.getenv("SMSS_NO")?.trim()?.toIntOrNull() ?: 0
    
    // bprintf 로그 파일 경로: C 코드 bprintf.c LINE 78 - getenv("SMS_VCDR")/LOG/
    private val vcdrDir: String = System.getenv("SMS_VCDR") ?: "/APP/sms/vcdr"
    private val billingLogDir = "$vcdrDir/LOG"
    private var billingLogFile: FileWriter? = null
    private var lastBillingLogMinute: Int = -1
    
    /**
     * C 코드의 ProcessSMRes() 함수와 동일한 로직 수행
     * 
     * @param request ResponseTR (CP 서버로부터 받은 결과)
     */
    override suspend fun processSMRes(request: ResponseTR) {
        // C 코드 LINE 1978: MsgHdrToQItem
        val qItem = QItemServiceUtil.responseTRToQItem(request)
        qItem.ucServerType = VSMSS_TYPE.code.toByte()
        
        // C 코드 LINE 1981: switch(ptrMsgHdr->usMsgSubCode)
        when (request.data.msgSubCode.toInt()) {
            SM_REQ_SIMPLE -> {
                witcomLog.p_write(Level.INFO,String.format("processSMRes ----> [%s]", request.data.msgSubCode))
                // C 코드 LINE 1983-2245: MO ACK 처리
                processSMReqSimple(qItem, request)
            }
            SM_REQ_TRANS_RESULT -> {
                // C 코드 LINE 2247-2272: TR 결과 처리
                witcomLog.p_write(Level.INFO,String.format("processSMRes ----> [%s]", request.data.msgSubCode))
                processSMReqTransResult(qItem, request)
            }
            else -> {
                witcomLog.p_write(
                    Level.ERROR,
                    String.format("Invalid msgSubCode: %d", request.data.msgSubCode)
                )
            }
        }
    }
    
    /**
     * C 코드 LINE 1983-2245: SM_REQ_SIMPLE 케이스 처리 (MO ACK)
     */
    private suspend fun processSMReqSimple(
        qItem: QITEM, 
        request: ResponseTR
    ) {
        // C 코드 LINE 1989-1990: gMsgSendReady, gRetryCount 초기화
        // HTTP 환경에서는 불필요
        
        val smsQLib: SmsQLib = liveReloadCLibraryFile.getPreInitializedLibrary(0)
        
        // C 코드 LINE 1995-2066: gBILLTYPE == '1' 케이스 처리
        val gipHttpMoAccess = gipHttpMoAccessRepository.findByCid(request.data.destCID).orElse(null)
        val billType = gipHttpMoAccess?.billType ?: "2"  // 기본값: '2' (SRC)
        
        if (billType == "1") {
            // C 코드 LINE 2003: SelectGIPMOCallInfo 호출
            val moCallInfo = selectGIPMOCallInfo(
                request.data.srcCallNo,
                request.data.destCID,
                request.data.msgSeqNo.toString()
            )
            
            if (moCallInfo != null) {
                // C 코드 LINE 2006-2009: TraceId, MVNO 정보, RCS Tag 복사
                if (moCallInfo.traceId != null) {
                    val traceIdBytes = moCallInfo.traceId.toByteArray(Charset.forName("CP949"))
                    System.arraycopy(traceIdBytes, 0, qItem.szTraceId, 0, 
                        minOf(traceIdBytes.size, qItem.szTraceId.size - 1))
                    if (traceIdBytes.size < qItem.szTraceId.size) {
                        qItem.szTraceId[traceIdBytes.size] = 0x00
                    }
                }
                
                if (moCallInfo.origMvnoInfo != null) {
                    val origMvnoBytes = moCallInfo.origMvnoInfo.toByteArray(Charset.forName("CP949"))
                    System.arraycopy(origMvnoBytes, 0, qItem.szOrigMvnoInformation, 0,
                        minOf(origMvnoBytes.size, qItem.szOrigMvnoInformation.size - 1))
                }
                
                if (moCallInfo.destMvnoInfo != null) {
                    val destMvnoBytes = moCallInfo.destMvnoInfo.toByteArray(Charset.forName("CP949"))
                    System.arraycopy(destMvnoBytes, 0, qItem.szDestMvnoInformation, 0,
                        minOf(destMvnoBytes.size, qItem.szDestMvnoInformation.size - 1))
                }
                
                if (moCallInfo.rcs != null && moCallInfo.rcs.isNotEmpty()) {
                    val rcsBytes = moCallInfo.rcs.toByteArray(Charset.forName("CP949"))
                    System.arraycopy(rcsBytes, 0, qItem.RcsTag, 0,
                        minOf(rcsBytes.size, qItem.RcsTag.size - 1))
                    if (rcsBytes.size < qItem.RcsTag.size) {
                        qItem.RcsTag[rcsBytes.size] = 0x00
                    }
                }
                
                // C 코드 LINE 2011-2026: DCS Type 변환
                val nDCSType = moCallInfo.dcsType ?: DCS_TYPE_DEC_UNKNOWN
                when (nDCSType) {
                    DCS_TYPE_DEC_GSM7 -> qItem.ucDataEncoding = DCS_TYPE_GSM7.code.toByte()
                    DCS_TYPE_DEC_ASCII7 -> qItem.ucDataEncoding = DCS_TYPE_ASCII7.code.toByte()
                    DCS_TYPE_DEC_8BIT -> qItem.ucDataEncoding = DCS_TYPE_8BIT.code.toByte()
                    DCS_TYPE_DEC_UCS2 -> qItem.ucDataEncoding = DCS_TYPE_UCS2.code.toByte()
                    DCS_TYPE_DEC_KSC5601 -> qItem.ucDataEncoding = DCS_TYPE_KSC5601
                    else -> qItem.ucDataEncoding = DCS_TYPE_UNKNOWN.code.toByte()
                }
                
                qItem.ucMsgLen = (moCallInfo.msgLen ?: 0).toInt()
                qItem.uOrgMsgLen = moCallInfo.orgMsgLen ?: 0
                
                // C 코드 LINE 2030-2053: RCS 태그 처리 및 큐 삽입
                if (moCallInfo.rcs != null && moCallInfo.rcs.isNotEmpty()) {
                    val rcsQItem = QITEM()
                    QItemServiceUtil.copyQItem(qItem, rcsQItem)
                    
                    // C 코드 LINE 2036-2042: CID와 MinNo 교환
                    val tempCid = ByteArray(16)
                    System.arraycopy(rcsQItem.szCId, 0, tempCid, 0, 16)
                    System.arraycopy(rcsQItem.szSrcCId, 0, rcsQItem.szCId, 0, 16)
                    System.arraycopy(tempCid, 0, rcsQItem.szSrcCId, 0, 16)
                    
                    val tempMin = ByteArray(12)
                    System.arraycopy(rcsQItem.szMinNo, 0, tempMin, 0, 12)
                    System.arraycopy(rcsQItem.szSrcMinNo, 0, rcsQItem.szMinNo, 0, 12)
                    System.arraycopy(tempMin, 0, rcsQItem.szSrcMinNo, 0, 12)
                    
                    // C 코드 LINE 2044-2050: RCS TR 메시지 설정
                    rcsQItem.usMsgCode = QTYPE_SM_REQ.toShort()
                    rcsQItem.usMsgSubCode = SUB_QTYPE_RCS_TR.toShort()
                    rcsQItem.ucTermType = TERM_TYPE_KOR.code.toByte()
                    rcsQItem.ucDataEncoding = DCS_TYPE_KSC5601
                    rcsQItem.nVldPrd = 43200
                    rcsQItem.RcsResult = RCS_RESULT_SENT.toShort()
                    
                    // C 코드 LINE 2052: InsertIntoSmsQnQNo 호출
                    val rcsQueueNo = 0
                    smsQLib.InsertIntoSmsQnQNo(rcsQItem, rcsQueueNo)
                }
            } else {
                // C 코드 LINE 2057-2063: ORA_NODATA 처리
                smsQLib.InsqStat(
                    qItem,
                    MESSAGE_MO,
                    0,
                    gServerID,
                    MODULEID_GIPEVENT_C,
                    SERVICEID_GIPEVENT,
                    ERRORID_CP_MO_NODATA,
                    ST_DB_NO_DATA_GIPMOCALLINFO,
                    IF_NULL,
                    TID_NO_SAVE,
                    LT_BOTH,
                    0
                )
                
                // C 코드 LINE 2062: InsertHistory 호출
                // InsertHistory(gCallHistory, &gstQItem, NULL, MODULE_GIPEVENT, DB_NO_DATA, __LINE__, (int)ptrMsgHdr->ucData[3]);
                // TODO: History 저장 로직 구현 필요 (C 코드 LINE 2062)
            }
            
            // C 코드 LINE 2066: break
            return
        }
        
        // C 코드 LINE 2069: RcsResult 설정
        qItem.RcsResult = RCS_RESULT_SENT.toShort()
        
        // C 코드 LINE 2071: ACK 결과 확인
        val ackResult = request.data.ackResult ?: GI_RES_NO_ERR
        
        if (ackResult == GI_RES_NO_ERR) {
            // C 코드 LINE 2073-2075: InsqStat 호출 (성공)
            smsQLib.InsqStat(
                qItem,
                MESSAGE_MO,
                0,
                gServerID,
                MODULEID_GIPEVENT_C,
                SERVICEID_GIPEVENT,
                ERRORID_CP_MO_SUCCESS,
                ST_GIPEVENT_MORS_OK,
                ackResult,
                TID_NO_SAVE,
                LT_BOTH,
                0
            )
            
            witcomLog.p_write(
                Level.INFO,
                String.format(
                    "MO ACK Success: srcCID(%s), srcCallNo(%s), destCID(%s), destCallNo(%s), ackResult(%d)",
                    request.data.srcCID,
                    request.data.srcCallNo,
                    request.data.destCID,
                    request.data.destCallNo,
                    ackResult
                )
            )
        } else {
            // C 코드 LINE 2079-2081: InsqStat 호출 (실패)
            smsQLib.InsqStat(
                qItem,
                MESSAGE_MO,
                0,
                gServerID,
                MODULEID_GIPEVENT_C,
                SERVICEID_GIPEVENT,
                ERRORID_CP_MO_FAIL,
                ST_GIP_MORS_FAIL,
                ackResult,
                TID_NO_SAVE,
                LT_BOTH,
                0
            )
            
            witcomLog.p_write(
                Level.ERROR,
                String.format(
                    "MO ACK Fail: srcCID(%s), srcCallNo(%s), destCID(%s), destCallNo(%s), ackResult(%d)",
                    request.data.srcCID,
                    request.data.srcCallNo,
                    request.data.destCID,
                    request.data.destCallNo,
                    ackResult
                )
            )
            
            // C 코드 LINE 2083-2084: InsertHistory 호출
            // InsertHistory(gCallHistory, &gstQItem, NULL, MODULE_GIPEVENT, ACK_RESULT_FAIL, __LINE__, (int)ptrMsgHdr->ucData[3]);
            // TODO: History 저장 로직 구현 필요 (C 코드 LINE 2083-2084)
        }
        
        // C 코드 LINE 2087-2244: 과금 처리 로직 (!gMOTRBILL)
        val moTrBill = gipHttpMoAccess?.moTrBill ?: "N"
        if (moTrBill != "Y") {  // C 코드: !gMOTRBILL
            // C 코드 LINE 2096: DBGet_GIENQ_CID 호출
            val cidLen = dbGetGIENQCID(request.data.destCID)
            if (cidLen < 0) {
                // C 코드 LINE 2107-2112: Invalid CID 처리
                smsQLib.InsqStat(
                    qItem,
                    MESSAGE_MO,
                    0,
                    gServerID,
                    MODULEID_GIPEVENT_C,
                    SERVICEID_GIPEVENT,
                    ERRORID_CP_MO_FAIL,
                    ST_GIP_INVALID_CID,
                    IF_NULL,
                    TID_NO_SAVE,
                    LT_TRACE,
                    0
                )
                
                witcomLog.p_write(
                    Level.ERROR,
                    String.format("Invalid DestCID [%s]", request.data.destCID)
                )
                
                // C 코드 LINE 2110-2111: InsertHistory 호출
                // InsertHistory(gCallHistory, &gstQItem, NULL, MODULE_GIPEVENT, INVALID_CID, __LINE__, (int)ptrMsgHdr->ucData[3]);
                // TODO: History 저장 로직 구현 필요 (C 코드 LINE 2110-2111)
                return
            }
            
            val cdrCId = request.data.destCID.substring(0, cidLen)
            
            // C 코드 LINE 2122: SelectGIPMOCallInfo 호출
            val moCallInfo = selectGIPMOCallInfo(
                request.data.srcCallNo,
                request.data.destCID,
                request.data.msgSeqNo.toString()
            )
            
            if (moCallInfo != null) {
                // C 코드 LINE 2125-2128: TraceId, MVNO 정보, RCS Tag 복사
                if (moCallInfo.traceId != null) {
                    val traceIdBytes = moCallInfo.traceId.toByteArray(Charset.forName("CP949"))
                    System.arraycopy(traceIdBytes, 0, qItem.szTraceId, 0,
                        minOf(traceIdBytes.size, qItem.szTraceId.size - 1))
                    if (traceIdBytes.size < qItem.szTraceId.size) {
                        qItem.szTraceId[traceIdBytes.size] = 0x00
                    }
                }
                
                if (moCallInfo.origMvnoInfo != null) {
                    val origMvnoBytes = moCallInfo.origMvnoInfo.toByteArray(Charset.forName("CP949"))
                    System.arraycopy(origMvnoBytes, 0, qItem.szOrigMvnoInformation, 0,
                        minOf(origMvnoBytes.size, qItem.szOrigMvnoInformation.size - 1))
                }
                
                if (moCallInfo.destMvnoInfo != null) {
                    val destMvnoBytes = moCallInfo.destMvnoInfo.toByteArray(Charset.forName("CP949"))
                    System.arraycopy(destMvnoBytes, 0, qItem.szDestMvnoInformation, 0,
                        minOf(destMvnoBytes.size, qItem.szDestMvnoInformation.size - 1))
                }
                
                if (moCallInfo.rcs != null && moCallInfo.rcs.isNotEmpty()) {
                    val rcsBytes = moCallInfo.rcs.toByteArray(Charset.forName("CP949"))
                    System.arraycopy(rcsBytes, 0, qItem.RcsTag, 0,
                        minOf(rcsBytes.size, qItem.RcsTag.size - 1))
                    if (rcsBytes.size < qItem.RcsTag.size) {
                        qItem.RcsTag[rcsBytes.size] = 0x00
                    }
                }
                
                // C 코드 LINE 2130-2142: DCS Type 변환
                val nDCSType = moCallInfo.dcsType ?: DCS_TYPE_DEC_UNKNOWN
                when (nDCSType) {
                    DCS_TYPE_DEC_GSM7 -> qItem.ucDataEncoding = DCS_TYPE_GSM7.code.toByte()
                    DCS_TYPE_DEC_ASCII7 -> qItem.ucDataEncoding = DCS_TYPE_ASCII7.code.toByte()
                    DCS_TYPE_DEC_8BIT -> qItem.ucDataEncoding = DCS_TYPE_8BIT.code.toByte()
                    DCS_TYPE_DEC_UCS2 -> qItem.ucDataEncoding = DCS_TYPE_UCS2.code.toByte()
                    DCS_TYPE_DEC_KSC5601 -> qItem.ucDataEncoding = DCS_TYPE_KSC5601
                    else -> qItem.ucDataEncoding = DCS_TYPE_UNKNOWN.code.toByte()
                }
                
                qItem.ucMsgLen = (moCallInfo.msgLen ?: 0).toInt()
                qItem.uOrgMsgLen = moCallInfo.orgMsgLen ?: 0
                
                // C 코드 LINE 2149-2154: RCS 여부 확인
                val nRcs = if (moCallInfo.rcs != null && moCallInfo.rcs.isNotEmpty()) {
                    RCS_TO_SMS
                } else {
                    COMMON_SMS
                }
                
                // C 코드 LINE 2156: CallbackFilter 호출
                val callback = moCallInfo.cb ?: ""
                val srcCallback = callback
                val dstCallback = callbackFilter(callback)
                
                if (dstCallback != srcCallback) {
                    witcomLog.p_write(
                        Level.WARN,
                        String.format("Callback values are filtered, Before[%s] => After[%s]", srcCallback, dstCallback)
                    )
                }
                
                // C 코드 LINE 2163-2195: bprintf 과금 데이터 출력
                // bprintf 로그 파일 경로: $SMS_VCDR/LOG/
                // C 코드: bprintf.c LINE 78 - getenv("SMS_VCDR")
                val now = Calendar.getInstance()
                val moSubTime = moCallInfo.moSubTime ?: ""
                val moRecvTime = moCallInfo.moRecvTime ?: ""
                val buf = SimpleDateFormat("yyMMddHHmmss").format(Date(now.timeInMillis - 15000))  // C 코드 LINE 2115: time(NULL) - 15
                val szUsec = String.format("%.4f", (now.get(Calendar.MILLISECOND) / 1000.0)).substring(2)  // 마이크로초
                
                val destCallNoInt = request.data.destCallNo.toIntOrNull() ?: 0
                val isPortableNo = destCallNoInt >= 100000000  // C 코드 LINE 2165: ptrMsgHdr->uDestCallNo/100000000 != 0
                
                // C 코드 LINE 2163: COLORSMS 또는 AVATASMS 체크
                val isColorOrAvata = request.data.destCID.startsWith("COL") || request.data.destCID.startsWith("AVAT")
                
                // C 코드 상수 참조 (CALL_TYPE_MO = 1, MSG_DELEVER_OK = 0)
                val callTypeMo = 1
                val msgDeleverOk = 0
                val moduleNo = 0  // 모듈 번호 00으로 고정
                
                if (isColorOrAvata) {
                    val billingLine = if (isPortableNo) {
                        // C 코드 LINE 2169: 포터블 번호 케이스
                        String.format(";%d;;;%d;%s;%s%d;0%d;%s;11;%08d;%s;%s;%s%s;%d;%d;%d;;;;;;%s;%s;0;%c;;%s;%s;%d;0;;\n",
                            callTypeMo, moduleNo, "", request.data.srcCID, request.data.srcCallNo.toIntOrNull() ?: 0,
                            destCallNoInt, cdrCId, request.data.msgSeqNo,
                            moRecvTime, moSubTime, buf, szUsec, msgDeleverOk, qItem.ucMsgLen.toInt(), moCallInfo.roamingId ?: 0,
                            moCallInfo.roamPMN ?: "", dstCallback, moCallInfo.wZone?.get(0) ?: '0',
                            moCallInfo.origMvnoInfo ?: "", moCallInfo.destMvnoInfo ?: "", nRcs)
                    } else {
                        // C 코드 LINE 2179: 일반 번호 케이스
                        String.format(";%d;;;%d;%s;%s%d;%d;%s;11;%08d;%s;%s;%s%s;%d;%d;%d;;;;;;%s;%s;0;%c;;%s;%s;%d;0;;\n",
                            callTypeMo, moduleNo, "", request.data.srcCID, request.data.srcCallNo.toIntOrNull() ?: 0,
                            destCallNoInt, cdrCId, request.data.msgSeqNo,
                            moRecvTime, moSubTime, buf, szUsec, msgDeleverOk, qItem.ucMsgLen.toInt(), moCallInfo.roamingId ?: 0,
                            moCallInfo.roamPMN ?: "", dstCallback, moCallInfo.wZone?.get(0) ?: '0',
                            moCallInfo.origMvnoInfo ?: "", moCallInfo.destMvnoInfo ?: "", nRcs)
                    }
                    writeBillingLog(billingLine)
                } else {
                    // C 코드 LINE 2190: 일반 케이스
                    val billingLine = String.format(";%d;;;%d;%s;%s%d;%s;;11;%08d;%s;%s;%s%s;%d;%d;%d;;;;;;%s;%s;0;%c;;%s;%s;%d;0;;\n",
                        callTypeMo, moduleNo, "", request.data.srcCID, request.data.srcCallNo.toIntOrNull() ?: 0,
                        cdrCId, request.data.msgSeqNo,
                        moRecvTime, moSubTime, buf, szUsec, msgDeleverOk, qItem.ucMsgLen.toInt(), moCallInfo.roamingId ?: 0,
                        moCallInfo.roamPMN ?: "", dstCallback, moCallInfo.wZone?.get(0) ?: '0',
                        moCallInfo.origMvnoInfo ?: "", moCallInfo.destMvnoInfo ?: "", nRcs)
                    writeBillingLog(billingLine)
                }
                
                // C 코드 LINE 2196-2197: InsqStat 호출
                smsQLib.InsqStat(
                    qItem,
                    MESSAGE_MO,
                    0,
                    gServerID,
                    MODULEID_GIPEVENT_C,
                    SERVICEID_GIPEVENT,
                    ERRORID_CP_MO_SUCCESS,
                    ST_GIPEVENT_MOACK_BILL_OK,
                    IF_NULL,
                    TID_NO_SAVE,
                    LT_TRACE,
                    0
                )
                
                // C 코드 LINE 2199-2221: RCS 태그 처리 및 큐 삽입
                if (moCallInfo.rcs != null && moCallInfo.rcs.isNotEmpty()) {
                    val rcsQItem = QITEM()
                    QItemServiceUtil.copyQItem(qItem, rcsQItem)
                    
                    // C 코드 LINE 2205-2211: CID와 MinNo 교환
                    val tempCid = ByteArray(16)
                    System.arraycopy(rcsQItem.szCId, 0, tempCid, 0, 16)
                    System.arraycopy(rcsQItem.szSrcCId, 0, rcsQItem.szCId, 0, 16)
                    System.arraycopy(tempCid, 0, rcsQItem.szSrcCId, 0, 16)
                    
                    val tempMin = ByteArray(12)
                    System.arraycopy(rcsQItem.szMinNo, 0, tempMin, 0, 12)
                    System.arraycopy(rcsQItem.szSrcMinNo, 0, rcsQItem.szMinNo, 0, 12)
                    System.arraycopy(tempMin, 0, rcsQItem.szSrcMinNo, 0, 12)
                    
                    // C 코드 LINE 2213-2218: RCS TR 메시지 설정
                    rcsQItem.usMsgCode = QTYPE_SM_REQ.toShort()
                    rcsQItem.usMsgSubCode = SUB_QTYPE_RCS_TR.toShort()
                    rcsQItem.ucTermType = TERM_TYPE_KOR.code.toByte()
                    rcsQItem.ucDataEncoding = DCS_TYPE_KSC5601
                    rcsQItem.nVldPrd = 43200
                    
                    // C 코드 LINE 2220: InsertIntoSmsQnQNo 호출
                    val rcsQueueNo = 0
                    smsQLib.InsertIntoSmsQnQNo(rcsQItem, rcsQueueNo)
                }
            } else {
                // C 코드 LINE 2225-2231: ORA_NODATA 처리
                smsQLib.InsqStat(
                    qItem,
                    MESSAGE_MO,
                    0,
                    gServerID,
                    MODULEID_GIPEVENT_C,
                    SERVICEID_GIPEVENT,
                    ERRORID_CP_MO_NODATA,
                    ST_DB_NO_DATA_GIPMOCALLINFO,
                    IF_NULL,
                    TID_NO_SAVE,
                    LT_BOTH,
                    0
                )
                
                // C 코드 LINE 2230: InsertHistory 호출
                // InsertHistory(gCallHistory, &gstQItem, NULL, MODULE_GIPEVENT, DB_NO_DATA, __LINE__, (int)ptrMsgHdr->ucData[3]);
                // TODO: History 저장 로직 구현 필요 (C 코드 LINE 2230)
            }
        } else {
            // C 코드 LINE 2234-2244: gMOTRBILL이 TRUE일 때
            val rsv4Protocol11 = request.data.rsv4Protocol?.getOrNull(11)?.data?.code ?: 0
            val notiPlusNormalMo = rsv4Protocol11 == NOTI_PLUS_NORMAL_MO
            val notiPlusPortedMo = rsv4Protocol11 == NOTI_PLUS_PORTED_MO
            
            if (notiPlusNormalMo || notiPlusPortedMo) {
                // C 코드 LINE 2238: UpdateMO_NOTISEND 호출
                // UpdateMO_NOTISEND(ptrMsgHdr, &stLog_AddrList);
                // TODO: UpdateMO_NOTISEND 함수 구현 필요 (C 코드 LINE 2238)
            } else {
                // C 코드 LINE 2242: UpdateGIPMOCallInfo 호출
                updateGIPMOCallInfo(request)
            }
        }
    }
    
    /**
     * C 코드 LINE 2247-2272: SM_REQ_TRANS_RESULT 케이스 처리 (TR 결과)
     */
    private suspend fun processSMReqTransResult(
        qItem: QITEM, 
        request: ResponseTR
    ) {
        // C 코드 LINE 2248-2249: gMsgSendReady, gRetryCount 초기화
        // HTTP 환경에서는 불필요
        
        witcomLog.p_write(
            Level.INFO,
            String.format(
                "Receive TR Result ACK: destCID(%s), destCallNo(%s)",
                request.data.destCID,
                request.data.destCallNo
            )
        )
        
        // C 코드 LINE 2266-2269: InsqStat 호출
        // HTTP 요청은 단일 스레드이므로 index 0 사용
        val smsQLib: SmsQLib = liveReloadCLibraryFile.getPreInitializedLibrary(0)
        smsQLib.InsqStat(
            qItem,
            MESSAGE_TR,
            0,
            gServerID,
            MODULEID_GIPEVENT_C,
            SERVICEID_GIPEVENT,
            ERRORID_CP_TR_SUCCESS,
            ST_GIPEVENT_MTTR_OK,
            IF_NULL,
            TID_NO_SAVE,
            LT_TRACE,
            0
        )
        
        // C 코드 LINE 2271: update MT Billing
        // TODO: MT Billing 업데이트 로직 구현 필요 (C 코드 LINE 2271)
    }
    
    /**
     * SelectGIPMOCallInfo 함수
     * 
     * C 코드 참고: GIDBLib.c LINE 2528-2607
     * SELECT MOSUBTIME, MSGLEN, ROAMINGID, CB, ROAMPMN, W_ZONE, TRACE_ID, 
     *        ORIG_MVNO_INFO, DEST_MVNO_INFO, RCS, DCS_TYPE, ORG_MSGLEN, MORECVTIME
     * FROM MOCALLINFO
     * WHERE SRCCALLNO = ? AND DESTCID = ? AND MSGID = ?
     * 
     * @return MOCallInfoEntity 또는 null (데이터 없음)
     */
    @Transactional(readOnly = true)
    open suspend fun selectGIPMOCallInfo(
        srcCallNo: String,
        destCId: String,
        msgId: String
    ): MOCallInfoEntity? {
        return try {
            moCallInfoRepository.findBySrcCallNoAndDestCIdAndMsgId(srcCallNo, destCId, msgId).orElse(null)
        } catch (e: Exception) {
            witcomLog.p_write(
                Level.ERROR,
                String.format("SelectGIPMOCallInfo() Select Error: %s", e.message)
            )
            null
        }
    }
    
    /**
     * UpdateGIPMOCallInfo 함수
     * 
     * C 코드 참고: GIDBLib.c LINE 3398-3487
     * INSERT INTO MOCALLINFO (SRCCID, SRCCALLNO, DESTCID, DESTCALLNO, MSGID, ...)
     * SELECT SRCCID, SRCCALLNO, DESTCID, DESTCALLNO, :ackMsgId AS MSGID, ...
     * FROM MOCALLINFO
     * WHERE SRCCALLNO = ? AND DESTCID = ? AND MSGID = ?
     */
    @Transactional
    open suspend fun updateGIPMOCallInfo(request: ResponseTR) {
        try {
            // C 코드 LINE 3433: AckMsgId 추출 (ptrMsgHdr->ucData+4, 9바이트)
            // HTTP 환경에서는 msgSeqNo를 AckMsgId로 사용
            val ackMsgId = request.data.msgSeqNo.toString()
            val srcCallNo = request.data.srcCallNo
            val destCId = request.data.destCID
            val msgId = request.data.msgSeqNo.toString()
            
            val result = moCallInfoRepository.insertFromExisting(ackMsgId, srcCallNo, destCId, msgId)
            
            witcomLog.p_write(
                Level.INFO,
                String.format(
                    "UpdateGIPMOCallInfo() SourceCallNo(%s) DestCID(%s) MsgId(%s) AckMsgId(%s) Result(%d)",
                    srcCallNo, destCId, msgId, ackMsgId, result
                )
            )
        } catch (e: Exception) {
            witcomLog.p_write(
                Level.ERROR,
                String.format("UpdateGIPMOCallInfo() Error: %s", e.message)
            )
        }
    }
    
    /**
     * DBGet_GIENQ_CID 함수
     * 
     * C 코드 참고: GIDBLib.c LINE 3611-3751
     * SELECT CID, LENGTH(CID)
     * FROM GIENQ
     * WHERE CID = SUBSTR(:v_tmp_cid, 1, :Cid_len)
     * 
     * @param tmpCid CID 문자열
     * @return CID 길이 (성공), -1 (실패)
     */
    @Transactional(readOnly = true)
    open suspend fun dbGetGIENQCID(tmpCid: String): Int {
        if (tmpCid.isEmpty()) {
            witcomLog.p_write(Level.ERROR, String.format("[DBGet_GIENQ_CID] Invalid Cid(%s)", tmpCid))
            return -1
        }
        
        try {
            var cidLen = tmpCid.length
            var found = false
            
            while (cidLen > 0) {
                val searchCid = tmpCid.substring(0, cidLen)
                
                // GIENQ 테이블에서 CID 조회
                val gienqList = gienqRepository.findAll()
                val matched = gienqList.firstOrNull { 
                    it.cid != null && it.cid.startsWith(searchCid) 
                }
                
                if (matched != null) {
                    val matchedCidLen = matched.cid?.length ?: 0
                    if (matchedCidLen > 0) {
                        return matchedCidLen
                    }
                    found = true
                    break
                }
                
                cidLen--
            }
            
            if (!found) {
                witcomLog.p_write(Level.ERROR, String.format("[DBGet_GIENQ_CID] NO_DATA CID(%s)", tmpCid))
                return -1
            }
            
            return cidLen
        } catch (e: Exception) {
            witcomLog.p_write(
                Level.ERROR,
                String.format("[DBGet_GIENQ_CID] Error: %s", e.message)
            )
            return -1
        }
    }
    
    /**
     * CallbackFilter 함수
     * 
     * C 코드 참고: GIPEVENT_c.c LINE 2156
     * 콜백 값을 필터링합니다.
     * 
     * @param callback 원본 콜백
     * @return 필터링된 콜백
     */
    private fun callbackFilter(callback: String): String {
        // TODO: CallbackFilter 로직 구현 필요
        // C 코드에서 CallbackFilter 함수의 구현을 찾아서 매핑 필요
        // 현재는 원본 콜백을 그대로 반환
        return callback
    }
    
    /**
     * bprintf 과금 데이터 출력 함수
     * 
     * C 코드 참고: bprintf.c LINE 114-210
     * 로그 파일 경로: $SMS_VCDR/LOG/ (환경변수 SMS_VCDR 사용)
     * 파일명 형식: {모듈번호}_{YYYYMMDDHHMM} (5분 단위)
     * 
     * @param billingLine 과금 데이터 라인
     */
    private fun writeBillingLog(billingLine: String) {
        try {
            val now = Calendar.getInstance()
            val currentMinute = now.get(Calendar.MINUTE)
            
            // 분이 변경되면 파일 재생성 (C 코드 LINE 156-164)
            if (lastBillingLogMinute != currentMinute) {
                billingLogFile?.close()
                
                val logFile = File(billingLogDir)
                if (!logFile.exists()) {
                    logFile.mkdirs()
                }
                
                // C 코드 LINE 93: 파일명 형식 {모듈번호}_{YYYYMMDDHHMM}
                val moduleNo = "00"  // 모듈 번호 00으로 고정
                val fileDate = SimpleDateFormat("yyyyMMddHHmm").format(now.time)
                val fileName = "$billingLogDir/${moduleNo}_$fileDate"
                
                val billingFile = File(fileName)
                billingLogFile = FileWriter(billingFile, true)
                lastBillingLogMinute = currentMinute
            }
            
            // C 코드 LINE 205: vfprintf로 데이터 출력
            billingLogFile?.write(billingLine)
            billingLogFile?.flush()
        } catch (e: Exception) {
            witcomLog.p_write(
                Level.ERROR,
                String.format("bprintf write error: %s", e.message)
            )
        }
    }
    
}

