package com.infra.mo.skt_giphttp_mo.db.altibase.repository;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.SpcodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpcodeRepository extends JpaRepository<SpcodeEntity, String> {

    SpcodeEntity findAllByCid(String cid);

    int countAllByCid(String cid);

    /**
     * CID와 SPCODE로 하나의 entity를 검색합니다.
     * @param cid CID
     * @param spcode SPCODE
     * @return Optional<SpcodeEntity>
     */
    @Query("SELECT s FROM SpcodeEntity s WHERE s.cid = :cid AND s.spcode = :spcode")
    Optional<SpcodeEntity> findByCidAndSpcode(@Param("cid") String cid, @Param("spcode") String spcode);

}