package com.infra.mo.skt_giphttp_mo.db.altibase.repository;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.MONotISendEntity;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.MONotISendId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * MO_NOTISEND 테이블 Repository
 * 
 * C 코드 참고: GIDBLib.c
 * - InsertMO_NOTISEND: LINE 4054-4070
 * - UpdateMO_NOTISEND: LINE 4418-4425
 * - DbReadMO_NOTISEND: LINE 2274-2280
 */
@Repository
public interface MONotISendRepository extends JpaRepository<MONotISendEntity, MONotISendId> {
    
    /**
     * DbReadMO_NOTISEND 함수
     * 
     * C 코드: GIDBLib.c LINE 2274-2280
     * SELECT ... FROM MO_NOTISEND
     * WHERE MSGID = ? AND DESTCID = ? AND SRCCID = ? AND SERVERTYPE = 'H'
     */
    @Query("SELECT m FROM MONotISendEntity m WHERE m.msgId = :msgId AND m.destCId = :destCId AND m.srcCId = :srcCId AND m.serverType = 'H'")
    Optional<MONotISendEntity> findByMsgIdAndDestCIdAndSrcCIdAndServerType(
            @Param("msgId") String msgId,
            @Param("destCId") String destCId,
            @Param("srcCId") String srcCId
    );
    
    /**
     * findByMsgIdAndServerType 함수 - msgId만으로 조회 (msgID는 고유 값)
     * 
     * SELECT * FROM MO_NOTISEND
     * WHERE MSGID = ? AND SERVERTYPE = ?
     * ORDER BY MOSUBTIME DESC
     */
    @Query(value = "SELECT * FROM (SELECT * FROM SMS.MO_NOTISEND " +
            "WHERE MSGID = :msgId AND SERVERTYPE = :serverType " +
            "ORDER BY MOSUBTIME DESC) WHERE ROWNUM <= 1",
            nativeQuery = true)
    Optional<MONotISendEntity> findByMsgIdAndServerType(
            @Param("msgId") String msgId,
            @Param("serverType") String serverType
    );
    
    /**
     * UpdateMO_NOTISEND 함수
     * 
     * C 코드: GIDBLib.c LINE 4418-4425
     * INSERT INTO MO_NOTISEND (NODE, SERVERTYPE, MSGID, MOSUBTIME, SRCCID, SRCCALLNO, DESTCID, DESTCALLNO, SEGMENT, TID, CB, ESMCLASS, W_ZONE, TRACE_ID, ORIG_MVNO_INFO, DEST_MVNO_INFO, MSGLEN, DCS_TYPE, ORG_MSGLEN, MORECVTIME)
     * SELECT NODE, SERVERTYPE, :ackMsgId AS MSGID, MOSUBTIME, SRCCID, SRCCALLNO, DESTCID, DESTCALLNO, SEGMENT, TID, CB, ESMCLASS, W_ZONE, TRACE_ID, ORIG_MVNO_INFO, DEST_MVNO_INFO, MSGLEN, DCS_TYPE, ORG_MSGLEN, MORECVTIME
     * FROM MO_NOTISEND
     * WHERE SRCCALLNO = :srcCallNo AND DESTCID = :destCId AND MSGID = :msgId AND SERVERTYPE = 'V'
     */
    @Modifying
    @Query(value = "INSERT INTO SMS.MO_NOTISEND " +
            "(NODE, SERVERTYPE, MSGID, MOSUBTIME, SRCCID, SRCCALLNO, DESTCID, DESTCALLNO, SEGMENT, TID, CB, ESMCLASS, W_ZONE, TRACE_ID, ORIG_MVNO_INFO, DEST_MVNO_INFO, MSGLEN, DCS_TYPE, ORG_MSGLEN, MORECVTIME) " +
            "SELECT NODE, SERVERTYPE, :ackMsgId AS MSGID, MOSUBTIME, SRCCID, SRCCALLNO, DESTCID, DESTCALLNO, SEGMENT, TID, CB, ESMCLASS, W_ZONE, TRACE_ID, ORIG_MVNO_INFO, DEST_MVNO_INFO, MSGLEN, DCS_TYPE, ORG_MSGLEN, MORECVTIME " +
            "FROM SMS.MO_NOTISEND " +
            "WHERE SRCCALLNO = :srcCallNo AND DESTCID = :destCId AND MSGID = :msgId AND SERVERTYPE = 'V'",
            nativeQuery = true)
    int insertFromExisting(
            @Param("ackMsgId") String ackMsgId,
            @Param("srcCallNo") String srcCallNo,
            @Param("destCId") String destCId,
            @Param("msgId") String msgId
    );
    
    /**
     * MO_NOTISEND 단일 조회 (SERVERTYPE='V' 전용)
     *
     * HTTP 수신 인자 3가지만 사용: traceId, srcCid, destCid
     *
     * @param traceId TRACE_ID (필수)
     * @param srcCid  SRCCID (필수)
     * @param destCid DESTCID (필수)
     */
    @Query(value = "SELECT * FROM (SELECT * FROM SMS.MO_NOTISEND " +
            "WHERE SERVERTYPE = 'V' " +
            "AND (NOTI_FLAG IS NULL OR NOTI_FLAG <> 9) " +
            "AND TRACE_ID = :traceId AND SRCCID = :srcCid AND DESTCID = :destCid " +
            "ORDER BY MOSUBTIME DESC) A WHERE ROWNUM <= 1",
            nativeQuery = true)
    Optional<MONotISendEntity> findOneForV(
            @Param("traceId") String traceId,
            @Param("srcCid") String srcCid,
            @Param("destCid") String destCid
    );

    /**
     * mo-report API용: MSGID + SRCCID + DESTCID + SRCCALLNO + DESTCALLNO 조합으로 조회 (SERVERTYPE='V')
     *
     * MSGID + srcCid + destCid + srcCallNo + destCallNo 조합으로
     * MO_NOTISEND 레코드를 1건 조회한다.
     */
    @Query(value = "SELECT * FROM (SELECT * FROM SMS.MO_NOTISEND " +
            "WHERE SERVERTYPE = 'V' " +
            "AND MSGID = :msgId " +
            "AND SRCCID = :srcCid " +
            "AND DESTCID = :destCid " +
            "AND SRCCALLNO = :srcCallNo " +
            "AND DESTCALLNO = :destCallNo " +
            "ORDER BY MOSUBTIME DESC) A WHERE ROWNUM <= 1",
            nativeQuery = true)
    Optional<MONotISendEntity> findOneForVByMsgAndCidAndCallNo(
            @Param("msgId") String msgId,
            @Param("srcCid") String srcCid,
            @Param("destCid") String destCid,
            @Param("srcCallNo") String srcCallNo,
            @Param("destCallNo") String destCallNo
    );

    /**
     * SelectTRMO_NOTISEND 함수 - 원본 레코드 삭제 (VBILL_MO용)
     * 
     * C 코드: VBILL\GIDBLib.c LINE 3147-3207
     * DELETE FROM MO_NOTISEND
     * WHERE SRCCALLNO = ? AND DESTCID = ? AND MSGID = ? AND SERVERTYPE = 'V'
     */
    @Modifying
    @Query(value = "DELETE FROM SMS.MO_NOTISEND " +
            "WHERE SRCCALLNO = :srcCallNo AND DESTCID = :destCId AND MSGID = :msgId AND SERVERTYPE = 'V'",
            nativeQuery = true)
    int deleteBySrcCallNoAndDestCIdAndMsgIdAndServerType(
            @Param("srcCallNo") String srcCallNo,
            @Param("destCId") String destCId,
            @Param("msgId") String msgId
    );

    /**
     * MO_NOTISEND 삭제 — MSGID, SRCCID, DESTCID, SRCCALLNO, DESTCALLNO 5키 + SERVERTYPE='V' (traceId 미사용)
     */
    @Modifying
    @Query(value = "DELETE FROM SMS.MO_NOTISEND " +
            "WHERE MSGID = :msgId AND SRCCID = :srcCId AND DESTCID = :destCId " +
            "AND SRCCALLNO = :srcCallNo AND DESTCALLNO = :destCallNo AND SERVERTYPE = 'V'",
            nativeQuery = true)
    int deleteByMsgIdAndSrcCIdAndDestCIdAndSrcCallNoAndDestCallNoAndServerType(
            @Param("msgId") String msgId,
            @Param("srcCId") String srcCId,
            @Param("destCId") String destCId,
            @Param("srcCallNo") String srcCallNo,
            @Param("destCallNo") String destCallNo
    );

    /**
     * MO_NOTISEND 삭제 — SRCCALLNO + DESTCID + MSGID + TRACE_ID + SERVERTYPE='V' (deprecated: 5키 삭제 사용 권장)
     */
    @Modifying
    @Query(value = "DELETE FROM SMS.MO_NOTISEND " +
            "WHERE SRCCALLNO = :srcCallNo AND DESTCID = :destCId AND MSGID = :msgId AND TRACE_ID = :traceId AND SERVERTYPE = 'V'",
            nativeQuery = true)
    int deleteBySrcCallNoAndDestCIdAndMsgIdAndTraceIdAndServerType(
            @Param("srcCallNo") String srcCallNo,
            @Param("destCId") String destCId,
            @Param("msgId") String msgId,
            @Param("traceId") String traceId
    );

    /**
     * MO_NOTISEND MSGID 업데이트 (HTTP 환경용)
     *
     * C 코드의 UpdateMO_NOTISEND는 INSERT 후 DELETE 패턴을 사용하지만,
     * HTTP 환경에서는 동일 레코드를 UPDATE 하는 것이 더 안전하다.
     *
     * - 대상: SERVERTYPE = 'V' 인 원본 레코드
     * - 조건: SRCCID + DESTCID (+ SERVERTYPE='V')
     * - 동작: MSGID를 :newMsgId 로 변경 (SERVERTYPE는 변경하지 않음)
     */
    @Modifying
    @Query(value = "UPDATE SMS.MO_NOTISEND " +
            "SET MSGID = :newMsgId " +
            "WHERE SRCCID = :srcCid " +
            "AND DESTCID = :destCId " +
            "AND SERVERTYPE = 'V'",
            nativeQuery = true)
    int updateMsgIdForHttpEnv(
            @Param("newMsgId") String newMsgId,
            @Param("srcCid") String srcCid,
            @Param("destCId") String destCId
    );
}




