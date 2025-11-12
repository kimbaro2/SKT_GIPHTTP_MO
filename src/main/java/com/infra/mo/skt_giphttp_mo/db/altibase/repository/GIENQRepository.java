package com.infra.mo.skt_giphttp_mo.db.altibase.repository;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GIENQEntity;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.GIENQId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GIENQRepository extends JpaRepository<GIENQEntity, GIENQId> {
}
