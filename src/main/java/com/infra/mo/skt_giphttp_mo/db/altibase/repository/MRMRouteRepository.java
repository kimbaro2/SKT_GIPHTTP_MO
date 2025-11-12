package com.infra.mo.skt_giphttp_mo.db.altibase.repository;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.MRMRouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MRMRouteRepository extends JpaRepository<MRMRouteEntity, String> {

    @Query("SELECT r FROM MRMRouteEntity r " +
            "WHERE r.destCid = :destCid " +
            "AND :tempMinNo BETWEEN r.startMin AND r.endMin")
    Optional<MRMRouteEntity> findMatchingRoute(@Param("destCid") String destCid,
                                               @Param("tempMinNo") long tempMinNo);
}
