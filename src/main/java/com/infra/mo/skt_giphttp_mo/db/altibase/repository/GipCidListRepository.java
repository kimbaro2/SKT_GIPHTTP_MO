package com.infra.mo.skt_giphttp_mo.db.altibase.repository;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipCidListEntity;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.GipCidListId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GipCidListRepository extends JpaRepository<GipCidListEntity, GipCidListId> {
    public boolean existsByModuleIdAndParentCid(String moduleId, String parentCid);
}

