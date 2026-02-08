package com.infra.mo.skt_giphttp_mo.service.mo

import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM

/**
 * 도메인별 MO 전송 흐름.
 * 공통 흐름이 아니라 각 도메인(NORMAL_MO, NOTI_PLUS, NOTI_REGISTERED 등)마다 별도 흐름 클래스가 구현한다.
 * 통계_과금_ESMClass별_설계.md: "MO 전송, InsqStat, bprintf, bill 등 모든 기능은 ESMClass 호출 클래스 내부에서만 동작."
 *
 * @param handler 해당 도메인 핸들러 (checkQItemVariables, recordMoSuccessInsqStat, recordMoAckBilling 등 위임)
 */
fun interface MoDomainFlow {

    /**
     * 해당 도메인 전용 MO 전송 흐름 수행.
     * (QITEM 검증 → 한도 체크 → CP 전송 → InsqStat → 도메인별 INSERT → MO-ACK → 과금 통계)
     */
    suspend fun execute(qItem: QITEM, context: MoServiceContext, handler: MoServiceTypeAwareHandler)
}
