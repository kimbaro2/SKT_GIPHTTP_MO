package com.infra.mo.skt_giphttp_mo.db.altibase.repository;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.MOCallInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 * MOCALLINFO 테이블 Repository
 * 
 * C 코드 참고: GIDBLib.c
 * - SelectGIPMOCallInfo: LINE 2603-2607
 * - UpdateGIPMOCallInfo: LINE 3452-3459
 */
@Repository
public interface MOCallInfoRepository extends JpaRepository<MOCallInfoEntity, String> {
    
    /**
     * SelectGIPMOCallInfo 함수
     * 
     * C 코드: GIDBLib.c LINE 2603-2607
     * SELECT MOSUBTIME, MSGLEN, ROAMINGID, CB, ROAMPMN, W_ZONE, TRACE_ID, 
     *        ORIG_MVNO_INFO, DEST_MVNO_INFO, RCS, DCS_TYPE, ORG_MSGLEN, MORECVTIME
     * FROM MOCALLINFO
     * WHERE SRCCALLNO = ? AND DESTCID = ? AND MSGID = ?
     * 
     * 주의: 동일한 조합이 여러 개일 수 있으므로 첫 번째 결과만 반환 (가장 최근 것)
     * Altibase는 서브쿼리를 사용하여 ORDER BY 후 ROWNUM 적용
     * ROWNUM은 서브쿼리 내부에서 ORDER BY 전에 평가되므로, 외부 서브쿼리로 감싸서 처리
     */
    @Query(value = "SELECT * FROM (SELECT * FROM SMS.MOCALLINFO WHERE SRCCALLNO = :srcCallNo AND DESTCID = :destCId AND MSGID = :msgId ORDER BY MOSUBTIME DESC) WHERE ROWNUM <= 1",
            nativeQuery = true)
    MOCallInfoEntity findBySrcCallNoAndDestCIdAndMsgId(
            @Param("srcCallNo") String srcCallNo,
            @Param("destCId") String destCId,
            @Param("msgId") String msgId
    );
    
    /**
     * UpdateGIPMOCallInfo 함수
     * 
     * C 코드: GIDBLib.c LINE 3452-3459
     * INSERT 시 EXPIRETIME은 원본 행 값을 복사 (nVldPrd 기반 만료 시간 유지)
     */
    @Modifying
    @Query(value = "INSERT INTO SMS.MOCALLINFO " +
            "(SRCCID, SRCCALLNO, DESTCID, DESTCALLNO, MSGID, MOSUBTIME, MSGLEN, ROAMINGID, ROAMPMN, CB, W_ZONE, TRACE_ID, ORIG_MVNO_INFO, DEST_MVNO_INFO, RCS, DCS_TYPE, ORG_MSGLEN, MORECVTIME, EXPIRETIME) " +
            "SELECT SRCCID, SRCCALLNO, DESTCID, DESTCALLNO, :ackMsgId AS MSGID, MOSUBTIME, MSGLEN, ROAMINGID, ROAMPMN, CB, W_ZONE, TRACE_ID, " +
            "ORIG_MVNO_INFO, DEST_MVNO_INFO, RCS, DCS_TYPE, ORG_MSGLEN, MORECVTIME, " +
            "COALESCE(EXPIRETIME, SYSDATE + 1) AS EXPIRETIME " +
            "FROM SMS.MOCALLINFO " +
            "WHERE SRCCALLNO = :srcCallNo AND DESTCID = :destCId AND MSGID = :msgId",
            nativeQuery = true)
    int insertFromExisting(
            @Param("ackMsgId") String ackMsgId,
            @Param("srcCallNo") String srcCallNo,
            @Param("destCId") String destCId,
            @Param("msgId") String msgId
    );
    
    /**
     * SelectGIPMOCallInfo 함수 - msgId만으로 조회
     *
     * SELECT * FROM MOCALLINFO
     * WHERE MSGID = :msgId
     * ORDER BY MOSUBTIME DESC
     *
     * 주의: 동일한 msgId가 여러 개일 수 있으므로 첫 번째 결과만 반환 (가장 최근 것)
     */
    @Query(value = "SELECT * FROM (SELECT * FROM SMS.MOCALLINFO WHERE MSGID = :msgId ORDER BY MOSUBTIME DESC) WHERE ROWNUM <= 1",
            nativeQuery = true)
    MOCallInfoEntity findByMsgId(
            @Param("msgId") String msgId
    );

    /**
     * mo-report API용: MSGID, TRACE_ID 2가지 값으로 조회
     *
     * SELECT * FROM MOCALLINFO
     * WHERE MSGID = :msgId AND TRACE_ID = :traceId
     * ORDER BY MOSUBTIME DESC
     *
     * 동일 조합이 여러 개일 수 있으므로 첫 번째 결과만 반환 (가장 최근 것)
     */
    @Query(value = "SELECT * FROM (SELECT * FROM SMS.MOCALLINFO WHERE MSGID = :msgId AND TRACE_ID = :traceId ORDER BY MOSUBTIME DESC) WHERE ROWNUM <= 1",
            nativeQuery = true)
    MOCallInfoEntity findByMsgIdAndTraceId(
            @Param("msgId") String msgId,
            @Param("traceId") String traceId
    );

    /**
     * UpdateGIPMOCallInfo 함수 - 원본 레코드 삭제
     * 
     * C 코드: GIDBLib.c LINE 3884-3899
     * DELETE FROM MOCALLINFO
     * WHERE SRCCALLNO = :srcCallNo AND DESTCID = :destCId AND MSGID = :msgId
     */
    @Modifying
    @Query(value = "DELETE FROM SMS.MOCALLINFO " +
            "WHERE SRCCALLNO = :srcCallNo AND DESTCID = :destCId AND MSGID = :msgId",
            nativeQuery = true)
    int deleteBySrcCallNoAndDestCIdAndMsgId(
            @Param("srcCallNo") String srcCallNo,
            @Param("destCId") String destCId,
            @Param("msgId") String msgId
    );
    
    /**
     * DeleteGIPMOCallInfo 함수 - msgId만으로 삭제
     * 
     * DELETE FROM MOCALLINFO
     * WHERE MSGID = :msgId
     */
    @Modifying
    @Query(value = "DELETE FROM SMS.MOCALLINFO " +
            "WHERE MSGID = :msgId",
            nativeQuery = true)
    int deleteByMsgId(
            @Param("msgId") String msgId
    );
}

