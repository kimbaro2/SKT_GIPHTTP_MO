package com.infra.mo.skt_giphttp_mo.service

import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseTR
import com.infra.mo.skt_giphttp_mo.dto.smsController.MoReportRequest
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.MOCallInfoEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.MONotISendEntity
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib

/**
 * mo-report에서 MOCALLINFO 구성 후, 나중에 processTR_*에서 삭제할 MO_NOTISEND 엔티티를 함께 전달하기 위한 결과 타입.
 * @param moCallInfo 비즈니스 로직에서 사용할 MOCALLINFO 형태 엔티티
 * @param moNotiToDelete 등기/안심 TR 처리 시 delete(entity)로 삭제할 MO_NOTISEND 엔티티 (객체 재사용). 동일 MSGID로 생략한 경우 null
 * @param skipMoNotiDelete true면 MO_NOTISEND 삭제 수행 안 함 (동일 MSGID로 DB 미변경 시 기존 행 보존)
 */
data class MoReportCallInfoResult(
    val moCallInfo: MOCallInfoEntity,
    val moNotiToDelete: MONotISendEntity? = null,
    val skipMoNotiDelete: Boolean = false
)

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
     * @param isMoAckContext true = MO-ACK 단계 호출, false = MO-TR 단계 호출 (MO-TR에서는 MOTRBILL=Y일 때만 bprintf)
     */
    suspend fun processMOBilling(
        qItem: QITEM,
        request: ResponseTR,
        gipHttpMoAccess: GipHttpMoAccessEntity?,
        smsQLib: SmsQLib,
        loggerName: String,
        isMoAckContext: Boolean
    )
    
    /**
     * mo-report API: 안심/등기 전용 처리 (MOCALLINFO 없음 + MO_NOTISEND 있음).
     * OCS 미관할. processESMClassBranch 호출, InsqStat 27 / MOCALLINFO 삭제 없음.
     * @param moCallInfo buildMoCallInfoFromMoNotiForReport로 만든 엔티티 (QItem 생성용)
     */
    suspend fun processMoReportForNoti(
        request: MoReportRequest,
        moCallInfo: MOCallInfoEntity,
        gipHttpMoAccess: GipHttpMoAccessEntity,
        clientIp: String,
        serverPort: Int,
        moNotiToDelete: MONotISendEntity,
        skipMoNotiDelete: Boolean = false
    )

    /**
     * mo-report API: 일반 MO 전용 처리 (MOCALLINFO 있음).
     * OCS 삭제(해당 시), InsqStat 27, MOCALLINFO 삭제 수행.
     */
    suspend fun processMoReportForGeneralMo(
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

    /**
     * mo-report 전용: MO_NOTISEND를 갱신하지 않고, 조회된 엔티티로 MOCallInfo 형태만 구성한다.
     * DB UPDATE(DELETE+INSERT) 없이 "조회 + 삭제만" 하려면 이 메서드를 사용한다.
     *
     * @param cpMsgId CP가 보낸 msgId (응답/로직에 사용할 최종 MSGID)
     * @param moNoti 조회된 MO_NOTISEND 엔티티 (이후 processMoReport에서 delete(entity)로 삭제 대상)
     * @return MoReportCallInfoResult (moCallInfo + moNotiToDelete=moNoti, skipMoNotiDelete=false)
     */
    fun buildMoCallInfoFromMoNotiForReport(cpMsgId: String, moNoti: MONotISendEntity): MoReportCallInfoResult

    /**
     * mo-report 흐름에서 MO_NOTISEND MSGID를 반영하고,
     * 이후 비즈니스 로직/응답에 사용할 MOCallInfoEntity를 구성한다.
     *
     * CP 요청의 MSGID·srcCid·destCid가 DB 레코드와 모두 같으면 DELETE+INSERT 생략.
     *
     * @param cpMsgId CP가 보낸 msgId
     * @param requestSrcCid CP가 보낸 srcCid (동일 여부 비교용)
     * @param requestDestCid CP가 보낸 destCid (동일 여부 비교용)
     * @param moNoti 조회된 MO_NOTISEND 엔티티
     * @param loggerName 로거 이름
     * @return MoReportCallInfoResult (moCallInfo + 나중에 삭제할 moNotiToDelete, 객체 재사용용)
     */
    fun updateMoNotiMsgIdAndBuildMoCallInfo(
        cpMsgId: String,
        requestSrcCid: String,
        requestDestCid: String,
        requestSrcCallNo: String,
        requestDestCallNo: String,
        moNoti: MONotISendEntity,
        loggerName: String
    ): MoReportCallInfoResult

    /**
     * C 코드 DBGet_GIENQ_CID: CID로 GIENQ 조회 후 유효 CID 길이 반환.
     * MO-ACK 도메인별 bprintf 시 cdrCId 산출에 사용.
     * @param tmpCid CID 문자열 (예: destCID)
     * @param loggerName 로거 이름 (선택)
     * @return 유효 길이 (성공), -1 (실패)
     */
    suspend fun dbGetGIENQCID(tmpCid: String, loggerName: String? = null): Int

    /**
     * recordMoAckBilling에서 processMOBilling 호출 시 사용할 MO-ACK용 ResponseTR 생성.
     * qItem에서 msgId, srcCID, destCID, srcCallNo, destCallNo를 채워 processMOBilling 5키 조회에 사용.
     */
    fun buildMoAckRequestFromQItem(qItem: QITEM): ResponseTR
}
