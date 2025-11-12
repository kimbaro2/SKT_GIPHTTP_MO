package com.infra.mo.skt_giphttp_mo.db.altibase.repository;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.TELEPrefixEntity;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.TELEPrefixId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TELEPrefixRepository extends CrudRepository<TELEPrefixEntity, TELEPrefixId> {
    @Query("""
                SELECT t
                FROM TELEPrefixEntity t
                WHERE t.telePrefix = :telePrefix
                  AND t.startPrefix <= :szMiddlePrefix
                  AND t.endPrefix >= :szMiddlePrefix
            """)
    Optional<TELEPrefixEntity> findTelecomByPrefix(
            @Param("telePrefix") String telePrefix,
            @Param("szMiddlePrefix") String szMiddlePrefix
    );

    @Query("SELECT t.telePrefix FROM TELEPrefixEntity t WHERE t.telePrefix = :szTele_Pre")
    Optional<String> findTelePrefixByTelePrefix(@Param("szTele_Pre") String szTele_Pre);


    @Query(value = """
                SELECT TELE_PREFIX
                FROM TELE_PREFIX
                WHERE TELE_PREFIX LIKE CONCAT('%', :szDestCID)
                LIMIT 1
            """, nativeQuery = true)
    Optional<String> findTelePrefixLikeTelePre(@Param("szDestCID") String szDestCID);


}
