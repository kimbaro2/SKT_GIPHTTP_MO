package com.infra.mo.skt_giphttp_mo.service

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil.QueueResult
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib

/**
 * MO-TR 결과 전송 오케스트레이션 서비스.
 * CP로 MO-TR 전송 + 성공 시 InsqStat, 실패 시 재큐 등을 한 번에 수행.
 * 각 서비스 클래스에서 MO-TR 전송 처리를 일관되게 사용하기 위해 분리.
 */
interface MoTrSendService {

    /**
     * MO-TR 결과를 CP로 전송하고, 성공 시 통계 기록, 실패 시 재큐 처리.
     * @return 전송 성공 여부
     */
    suspend fun processMoTrResult(
        qItem: QITEM,
        moResult: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        smsQLib: SmsQLib,
        gServerID: Int,
        queueResult: QueueResult,
        queueNo: Int,
        loggerName: String,
        workerThreadId: Long
    ): Boolean
}
