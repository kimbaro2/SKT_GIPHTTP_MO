package com.infra.mo.skt_giphttp_mo.service

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult

/**
 * MO 한도 체크 공통 로직 (checkLimitMO, checkLimitGIVE).
 * MOThreadPool에서 분리된 공통 호출 함수를 서비스 내부에서 사용할 수 있도록 의존성만 주입.
 */
interface MoLimitCheckService {

    /**
     * MO 한도 체크 (C 코드 CheckLimitMO 대응).
     * @return true: 한도 초과(차단 대상), false: 한도 미초과
     */
    fun checkLimitMO(msgHdr: SMReqTransResult, entity: GipHttpMoAccessEntity): Boolean

    /**
     * GIVE 한도 체크 (C 코드 CheckLimitGiveBill 대응).
     * @return true: 한도 초과(차단 대상), false: 한도 미초과
     */
    fun checkLimitGIVE(msgHdr: SMReqTransResult, entity: GipHttpMoAccessEntity): Boolean
}
