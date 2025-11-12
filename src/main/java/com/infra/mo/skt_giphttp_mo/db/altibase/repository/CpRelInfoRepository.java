package com.infra.mo.skt_giphttp_mo.db.altibase.repository;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.CpRelInfoEntity;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.CpRelInfoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CpRelInfoRepository extends JpaRepository<CpRelInfoEntity, CpRelInfoId> {
    @Transactional(readOnly = true)
    Optional<CpRelInfoEntity> findByIpAddrAndPcidAndCcid(String ipaddr, String pcid, String ccid);

    @Transactional(readOnly = true)
    Optional<CpRelInfoEntity> findByCcid(String ccid);

    @Transactional(readOnly = true)
    Optional<CpRelInfoEntity> findByPcidAndCcid(String pcid, String ccid);

    public boolean existsByPcidAndCcid(String pcid, String ccid);


}
