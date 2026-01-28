package com.infra.mo.skt_giphttp_mo.service

import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseTR
import com.infra.mo.skt_giphttp_mo.dto.smsController.MoReportRequest
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.MOCallInfoEntity
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib

/**
 * SMS 결과 처리 서비스 인터페이스
 * 
 * C 코드 참고: GIPEVENT_c.c LINE 1928 (ProcessSMRes)
 * - SM_REQ_SIMPLE: MO 메시지 ACK 처리
 * - SM_REQ_TRANS_RESULT: TR 결과 처리
 */
interface SmsResService {
    /**
     * C 코드의 ProcessSMRes() 함수와 동일한 로직 수행
     * 
     * @param request ResponseTR (CP 서버로부터 받은 결과)
     * @param clientIp 클라이언트 IP 주소 (선택사항, GIPHTTP_MO_ACCESS 조회용)
     * @param serverPort 서버 리스닝 포트 (선택사항, GIPHTTP_MO_ACCESS 조회용)
     * @param queueNo 큐 번호 (선택사항, GIPHTTP_MO_ACCESS 조회용)
     * @param loggerName 로거 이름 (선택사항, 없으면 자동 생성)
     */
    suspend fun processSMRes(request: ResponseTR, clientIp: String? = null, serverPort: Int? = null, queueNo: Int? = null, loggerName: String? = null)
    
    /**
     * MO 과금 처리 공통 함수
     * 
     * C 코드 참고: GIDBLib.c LINE 2087-2244
     * - DBGet_GIENQ_CID 호출
     * - SelectGIPMOCallInfo 호출
     * - bprintf 과금 데이터 출력
     * - InsqStat 호출 (과금 완료)
     * - RCS 처리 (필요 시)
     * - MOCALLINFO 삭제 (과금 성공 후)
     * 
     * @param qItem QITEM 객체
     * @param request ResponseTR 객체 (msgId 필수)
     * @param gipHttpMoAccess GipHttpMoAccessEntity (MOTRBILL 확인용)
     * @param smsQLib SmsQLib (InsqStat 호출용)
     * @param loggerName 로거 이름
     */
    suspend fun processMOBilling(
        qItem: QITEM,
        request: ResponseTR,
        gipHttpMoAccess: GipHttpMoAccessEntity?,
        smsQLib: SmsQLib,
        loggerName: String
    )
    
    /**
     * mo-report API로 수신한 MO-TR 결과 처리
     * 
     * @param request MoReportRequest (LOG_NO, CID, MSG_ID, MSG_STATUS, TRACE_ID)
     * @param moCallInfo MOCALLINFO 레코드
     * @param gipHttpMoAccess HTTP_MOSEND_ACCESS 레코드
     * @param clientIp 클라이언트 IP
     * @param serverPort 서버 포트
     */
    suspend fun processMoReport(
        request: MoReportRequest,
        moCallInfo: MOCallInfoEntity,
        gipHttpMoAccess: GipHttpMoAccessEntity,
        clientIp: String,
        serverPort: Int
    )
    
    /**
     * InsertGIPMOCallInfo 함수
     * 
     * C 코드 참고: GIDBLib.c LINE 1851-1950
     * MOCALLINFO 테이블에 레코드 저장
     * 
     * @param qItem QITEM 객체
     * @param msgHdr SMReqTransResult 객체
     * @param entity GipHttpMoAccessEntity 객체
     * @param workerThreadId 워커 스레드 ID
     * @return 성공 시 0, 실패 시 -1
     */
    fun insertGIPMOCallInfo(
        qItem: QITEM,
        msgHdr: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        workerThreadId: Long
    ): Int
}
