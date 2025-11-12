package com.infra.mo.skt_giphttp_mo.db.altibase.repository;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.MmcsCidListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MmcsCidListRepository extends JpaRepository<MmcsCidListEntity, String> {

//    MmcsCidListEntity findAllBycid(String cid);

    int countAllBycid(String cid);

}
