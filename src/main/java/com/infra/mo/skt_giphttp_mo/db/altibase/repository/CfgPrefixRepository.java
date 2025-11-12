package com.infra.mo.skt_giphttp_mo.db.altibase.repository;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.CFGPrefixEntity;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.CfgPrefixId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CfgPrefixRepository extends JpaRepository<CFGPrefixEntity, CfgPrefixId> {
    Optional<CFGPrefixEntity> findTopByTelePre(String telePre);

    // 특정 값이 ST_PRE ~ END_PRE 사이에 포함되는 엔티티 중 ST_PRE에 가장 가까운 값 1개 조회
    // 기존 JPQL 방식 (호환성 유지)
    @Query("SELECT c FROM CFGPrefixEntity c WHERE :value BETWEEN c.stPre AND c.endPre ORDER BY ABS(CAST(c.stPre AS int) - CAST(:value AS int)) ASC")
    CFGPrefixEntity findFirstByValueBetween(@Param("value") String value);

    // 알티베이스 최적화 Native Query (성능 향상)
    @Query(nativeQuery = true, value = """
        SELECT * FROM (
            SELECT * FROM CFG_PREFIX 
            WHERE :value BETWEEN ST_PRE AND END_PRE 
            ORDER BY ABS(CAST(ST_PRE AS INTEGER) - CAST(:value AS INTEGER)) ASC
        ) WHERE ROWNUM = 1
        """)
    CFGPrefixEntity findFirstByValueBetweenNative(@Param("value") String value);

    @Query(nativeQuery = true, value = """
        SELECT * FROM (
            SELECT * FROM CFG_PREFIX 
            WHERE TELE_PRE =:prefix AND :value BETWEEN ST_PRE AND END_PRE
            ORDER BY ABS(CAST(ST_PRE AS INTEGER) - CAST(:value AS INTEGER)) ASC
        ) WHERE ROWNUM = 1
        """)
    CFGPrefixEntity findFirstByValueBetweenNativeAndPrefix(@Param("value") String value, @Param("prefix") String prefix);


    //    @Query("""
//            select c from CFGPrefixEntity c
//            where c.telePre = :telePre
//            """)
//    public CFGPrefixEntity findByTelePre(String telePre);
}
