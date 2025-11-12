package com.infra.mo.skt_giphttp_mo.db.altibase.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.CallInfoEntity;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.CallInfoId;

@Repository
public interface CallInfoRepository extends JpaRepository<CallInfoEntity, CallInfoId> {
    /**
     * MSGIDSERVER와 DESTCALLNO로 CallInfo 조회 (DBReadW2PCALLINFO용)
     */
    @Query("SELECT c FROM CallInfoEntity c WHERE c.msgidserver = :msgIdServer AND c.destcallno = :destCallNo")
    Optional<CallInfoEntity> findByMsgIdServerAndDestCallNo(
            @Param("msgIdServer") String msgIdServer,
            @Param("destCallNo") String destCallNo);

    /**
     * MSGIDSERVER, SRCCID, DESTCALLNO로 CallInfo 조회 (DbReadCallInfo용)
     */
    @Query("SELECT c FROM CallInfoEntity c WHERE c.msgidserver = :msgIdServer AND c.srccid = :srcCId AND c.destcallno = :destCallNo")
    Optional<CallInfoEntity> findByMsgIdServerAndSrcCIdAndDestCallNo(
            @Param("msgIdServer") String msgIdServer,
            @Param("srcCId") String srcCId,
            @Param("destCallNo") String destCallNo);
}
