package com.infra.mo.skt_giphttp_mo.db.altibase.repository;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.CfwdDetectCidEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CfwdDetectCidRepository extends JpaRepository<CfwdDetectCidEntity, String> {



    CfwdDetectCidEntity findAllByCid (String cid);

    boolean existsByCid(String cid);

    int countAllByCid (String cid);

}