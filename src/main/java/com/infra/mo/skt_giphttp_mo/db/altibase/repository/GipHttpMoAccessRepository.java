package com.infra.mo.skt_giphttp_mo.db.altibase.repository;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.GipAccessHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface GipHttpMoAccessRepository extends JpaRepository<GipHttpMoAccessEntity, GipAccessHistoryId> {

    @org.springframework.data.jpa.repository.Query(value = """
        SELECT *
        FROM SMS.GIPHTTP_MO_ACCESS;
            """, nativeQuery = true)
    public Optional<List<GipHttpMoAccessEntity>> findAllEntity();

    // MSG_TYPE으로 필터링하여 조회
    @org.springframework.data.jpa.repository.Query(value = """
        SELECT *
        FROM SMS.GIPHTTP_MO_ACCESS
        WHERE MSG_TYPE = :msgType
            """, nativeQuery = true)
    public Optional<List<GipHttpMoAccessEntity>> findAllByMsgType(@Param("msgType") String msgType);

    @org.springframework.data.jpa.repository.Query(value = """
        SELECT CID
        FROM SMS.GIPHTTP_MO_ACCESS
        GROUP BY CID;
            """, nativeQuery = true)
    public Optional<List<String>> findAllGroupByCid();

    public boolean existsByCidAndIpAddr(String cid, String ipAddr);

    @Transactional(readOnly = true)
    public Optional<GipHttpMoAccessEntity> findByCidAndIpAddrAndPortNo(String cid, String ipAddr, int portNo);

    // 유니크 제약조건 (CID, IP_ADDR, PORT_NO, MSG_TYPE)으로 조회
    @Transactional(readOnly = true)
    public Optional<GipHttpMoAccessEntity> findByCidAndIpAddrAndPortNoAndMsgType(String cid, String ipAddr, int portNo, String msgType);

    // CID로 조회
    @Transactional(readOnly = true)
    public Optional<GipHttpMoAccessEntity> findByCid(String cid);

    // CID와 IP로 조회
    @Transactional(readOnly = true)
    public Optional<GipHttpMoAccessEntity> findByCidAndIpAddr(String cid, String ipAddr);

    // CID와 IP로 MSG_TYPE 포함하여 조회
    @Transactional(readOnly = true)
    public Optional<GipHttpMoAccessEntity> findByCidAndIpAddrAndMsgType(String cid, String ipAddr, String msgType);

    // CID로 MSG_TYPE 포함하여 조회
    @Transactional(readOnly = true)
    public Optional<GipHttpMoAccessEntity> findByCidAndMsgType(String cid, String msgType);

    // AES 키와 IV 업데이트를 위한 커스텀 쿼리
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE GipHttpMoAccessEntity e SET e.aesKeyBase64 = :aesKeyBase64, e.ivBase64 = :ivBase64 WHERE e.cid = :cid")
    @Transactional
    public int updateAesKeysByCid(String cid, String aesKeyBase64, String ivBase64);

    // CID와 IP로 AES 키와 IV 업데이트
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE GipHttpMoAccessEntity e SET e.aesKeyBase64 = :aesKeyBase64, e.ivBase64 = :ivBase64 WHERE e.cid = :cid AND e.ipAddr = :ipAddr")
    @Transactional
    public int updateAesKeysByCidAndIpAddr(String cid, String ipAddr, String aesKeyBase64, String ivBase64);
}
