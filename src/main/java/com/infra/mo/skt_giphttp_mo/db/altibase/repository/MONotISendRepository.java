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
     * SelectTRMO_NOTISEND 함수 (VBILL_MO용)
     * 
     * C 코드: VBILL\GIDBLib.c LINE 2709-3207
     * SELECT TO_CHAR(MOSUBTIME, 'YYMMDDHH24MISS'), TO_CHAR(MOSUBTIME,'MM/DD HH24:MI'), MSGLEN, CB, ESMCLASS,
     *        TRUNC((SYSDATE - MOSUBTIME)*(24*60)), W_ZONE, TRACE_ID, ORIG_MVNO_INFO, DEST_MVNO_INFO, DCS_TYPE, ORG_MSGLEN, MORECVTIME
     * FROM MO_NOTISEND
     * WHERE SRCCALLNO = ? AND DESTCID = ? AND MSGID = ? AND NOTI_FLAG <> 9 AND SERVERTYPE = 'V'
     * 
     * DELETE FROM MO_NOTISEND
     * WHERE SRCCALLNO = ? AND DESTCID = ? AND MSGID = ? AND SERVERTYPE = 'V' (조회 후 삭제)
     * 
     * 주의: 실제로는 SELECT 후 DELETE를 수행하지만, Repository에서는 SELECT만 수행
     * DELETE는 별도 함수로 구현
     */
    @Query(value = "SELECT * FROM (SELECT * FROM SMS.MO_NOTISEND " +
            "WHERE SRCCALLNO = :srcCallNo AND DESTCID = :destCId AND MSGID = :msgId " +
            "AND (NOTI_FLAG IS NULL OR NOTI_FLAG <> 9) AND SERVERTYPE = 'V' " +
            "ORDER BY MOSUBTIME DESC) WHERE ROWNUM <= 1",
            nativeQuery = true)
    Optional<MONotISendEntity> findBySrcCallNoAndDestCIdAndMsgIdAndServerType(
            @Param("srcCallNo") String srcCallNo,
            @Param("destCId") String destCId,
            @Param("msgId") String msgId
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
}




