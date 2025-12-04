package com.infra.mo.skt_giphttp_mo.db.altibase.repository;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.MOCallInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
     */
    @Query("SELECT m FROM MOCallInfoEntity m WHERE m.srcCallNo = :srcCallNo AND m.destCId = :destCId AND m.msgId = :msgId")
    Optional<MOCallInfoEntity> findBySrcCallNoAndDestCIdAndMsgId(
            @Param("srcCallNo") String srcCallNo,
            @Param("destCId") String destCId,
            @Param("msgId") String msgId
    );
    
    /**
     * UpdateGIPMOCallInfo 함수
     * 
     * C 코드: GIDBLib.c LINE 3452-3459
     * INSERT INTO MOCALLINFO (SRCCID, SRCCALLNO, DESTCID, DESTCALLNO, MSGID, MOSUBTIME, MSGLEN, 
     *                        ROAMINGID, ROAMPMN, CB, W_ZONE, TRACE_ID, ORIG_MVNO_INFO, DEST_MVNO_INFO, 
     *                        RCS, DCS_TYPE, ORG_MSGLEN, MORECVTIME)
     * SELECT SRCCID, SRCCALLNO, DESTCID, DESTCALLNO, :ackMsgId AS MSGID, MOSUBTIME, MSGLEN, 
     *        ROAMINGID, ROAMPMN, CB, W_ZONE, TRACE_ID, ORIG_MVNO_INFO, DEST_MVNO_INFO, 
     *        RCS, DCS_TYPE, ORG_MSGLEN, MORECVTIME
     * FROM MOCALLINFO
     * WHERE SRCCALLNO = :srcCallNo AND DESTCID = :destCId AND MSGID = :msgId
     */
    @Modifying
    @Query(value = "INSERT INTO SMS.MOCALLINFO " +
            "(SRCCID, SRCCALLNO, DESTCID, DESTCALLNO, MSGID, MOSUBTIME, MSGLEN, ROAMINGID, ROAMPMN, CB, W_ZONE, TRACE_ID, ORIG_MVNO_INFO, DEST_MVNO_INFO, RCS, DCS_TYPE, ORG_MSGLEN, MORECVTIME) " +
            "SELECT SRCCID, SRCCALLNO, DESTCID, DESTCALLNO, :ackMsgId AS MSGID, MOSUBTIME, MSGLEN, ROAMINGID, ROAMPMN, CB, W_ZONE, TRACE_ID, " +
            "ORIG_MVNO_INFO, DEST_MVNO_INFO, RCS, DCS_TYPE, ORG_MSGLEN, MORECVTIME " +
            "FROM SMS.MOCALLINFO " +
            "WHERE SRCCALLNO = :srcCallNo AND DESTCID = :destCId AND MSGID = :msgId",
            nativeQuery = true)
    int insertFromExisting(
            @Param("ackMsgId") String ackMsgId,
            @Param("srcCallNo") String srcCallNo,
            @Param("destCId") String destCId,
            @Param("msgId") String msgId
    );
}

