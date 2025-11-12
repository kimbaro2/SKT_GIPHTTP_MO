package com.infra.mo.skt_giphttp_mo.db.altibase.repository;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.CfgQinforEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CfgQinforRepository extends JpaRepository<CfgQinforEntity, Long> {

    @Query("select c from CfgQinforEntity c where c.qtype = :qtype and c.qNo = :qNo")
    CfgQinforEntity findByQtypeAndQNo(@Param("qtype") Long qtype, @Param("qNo") Long qNo);

    @Query("select c from CfgQinforEntity c where c.qtype = :qtype")
    List<CfgQinforEntity> findAllByQtype(@Param("qtype") Long qtype);

}
