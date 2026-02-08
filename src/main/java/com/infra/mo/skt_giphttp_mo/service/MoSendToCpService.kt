package com.infra.mo.skt_giphttp_mo.service

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.dto.SegmentInfo
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib

/**
 * CP 서버로 MO/MO-TR 전송 (sendMoMessageToCp, doSendMoMessage, doSendMoTr).
 * MOThreadPool에서 분리된 공통 호출 함수를 서비스 내부에서 사용할 수 있도록 의존성만 주입.
 */
interface MoSendToCpService {

    suspend fun sendMoMessageToCp(
        moResult: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        qItem: QITEM?,
        smsQLib: SmsQLib,
        gServerID: Int,
        loggerName: String,
        workerThreadId: Long,
        segmentInfo: SegmentInfo? = null
    ): Boolean

    /**
     * CP 서버로 MO-TR 결과 전송 (재시도 루프 포함).
     */
    suspend fun sendMoTrToCp(
        qItem: QITEM,
        moResult: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        loggerName: String,
        workerThreadId: Long
    ): Boolean

    suspend fun doSendMoTr(
        qItem: QITEM,
        moResult: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        connectionTimeoutSeconds: Int,
        attempt: Int,
        totalAttempts: Int,
        loggerName: String,
        workerThreadId: Long,
        gServerID: Int
    ): Boolean
}
