package com.infra.mo.skt_giphttp_mo.db.altibase.repository;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.SpamList1Entity;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.SpamListId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SpamListRepository extends JpaRepository<SpamList1Entity, SpamListId> {

    @Transactional(readOnly = true)
    @Query(nativeQuery = true
            , value = """
            SELECT
              CASE
                WHEN EXISTS (SELECT 1 FROM SPAM_LIST1 WHERE MIN = :mdn)
                  OR EXISTS (SELECT 1 FROM SPAM_LIST2 WHERE MIN = :mdn)
                THEN 'Y'
                ELSE 'N'
              END AS exists_flag
            """)
    public String findBySpamMDN(String mdn);

}