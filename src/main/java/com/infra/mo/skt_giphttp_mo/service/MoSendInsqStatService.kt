package com.infra.mo.skt_giphttp_mo.service

import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.service.mo.MoServiceContext

/**
 * MO CP 전송 성공/실패 시 InsqStat 호출 공통 서비스.
 * 모든 도메인에서 동일한 실패 통계 기록을 위해 사용한다.
 */
interface MoSendInsqStatService {

    /**
     * MO → CP 전송 실패 시 InsqStat 호출 (공통).
     * requeue 후 호출한다. MoSendToCpServiceImpl 내부 InsqStat과 중복되지 않도록
     * CP 전송 레이어에서는 실패 시 InsqStat을 호출하지 않고, 여기서만 호출한다.
     *
     * @param qItem QITEM
     * @param context MoServiceContext (smsQLib, gServerID 사용)
     */
    fun recordMoFailedInsqStat(qItem: QITEM, context: MoServiceContext)
}
