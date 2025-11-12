package com.infra.mo.skt_giphttp_mo.db.altibase.repository;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.CfwdDetectCidHistoryEntity;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.CfwdDetectCidHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CfwdDetectCidHistoryRepository extends JpaRepository<CfwdDetectCidHistoryEntity, CfwdDetectCidHistoryId> {

    @Query(nativeQuery = true
            , value = "SELECT *" +
                      "  FROM CFG_CFWD_DETECT_CID_HISTORY" +
                      " WHERE CID = :#{#idClass.cid}" +
                      "   AND TIME  >= :#{#idClass.time}" +
                      "   AND TIME   < :#{#idClass.time} + (1/24/60/60)"
    )
    CfwdDetectCidHistoryEntity selectByHistoryQuery(@Param("idClass") CfwdDetectCidHistoryId cfwdDetectCidHistoryId);
}