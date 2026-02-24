package com.infra.mo.skt_giphttp_mo.db.altibase.repository;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.CfgRelayCidListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * CFG_RELAY_CID_LIST 테이블 Repository
 * 
 * C 코드의 GetRelayCidInfoByQueueNo와 동일한 로직으로 queue_no를 기준으로 조회
 */
public interface CfgRelayCidListRepository extends JpaRepository<CfgRelayCidListEntity, String> {
    
    /**
     * CID로 Relay CID List 정보 조회
     * 
     * @param cid CID 값
     * @return CfgRelayCidListEntity 또는 Optional.empty()
     */
    @Transactional(readOnly = true)
    Optional<CfgRelayCidListEntity> findByCid(String cid);
    
    /**
     * RELAY_QUEUE로 Relay CID List 정보 조회
     * C 코드의 GetRelayCidInfoByQueueNo와 동일: relay_queue == queue_no인 항목을 찾음
     * 
     * @param relayQueue RELAY_QUEUE 값 (queue_no)
     * @return CfgRelayCidListEntity 또는 Optional.empty()
     */
    @Transactional(readOnly = true)
    Optional<CfgRelayCidListEntity> findByRelayQueue(Integer relayQueue);
}
