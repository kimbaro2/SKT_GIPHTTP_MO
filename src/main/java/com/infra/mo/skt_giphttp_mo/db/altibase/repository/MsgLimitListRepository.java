package com.infra.mo.skt_giphttp_mo.db.altibase.repository;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.MsgLimitListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface MsgLimitListRepository extends JpaRepository<MsgLimitListEntity, String> {
    /**
     * MDN으로 MSG_LIMIT_LIST 테이블에서 count를 조회합니다.
     * C 코드 참고: GIDBLib.c LINE 4782-4879 (CheckLimitMdn 함수)
     * 
     * @param mdn MDN (예: "01012345678")
     * @return count (0이면 한도 초과 아님, 0이 아니면 한도 초과)
     */
    long countByMdn(String mdn);
}