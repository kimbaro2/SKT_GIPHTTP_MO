package com.infra.mo.skt_giphttp_mo.service

import com.infra.mo.skt_giphttp_mo.dto.SegmentInfo
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult

/**
 * MO 처리 시 DB Insert 전용 레이어.
 * MOThreadPool에서 MOCALLINFO, MO_NOTISEND, RelayMOCallInfo 저장 시 이 서비스를 통해 호출한다.
 */
interface MoDbInsertService {

    /**
     * GIPMOCallInfo(MOCALLINFO) 저장.
     * @return 성공 시 0, 실패 시 -1
     */
    fun insertGIPMOCallInfo(
        qItem: QITEM,
        msgHdr: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        workerThreadId: Long
    ): Int

    /**
     * MO_NOTISEND 저장 (NOTI 타입).
     * @param segmentInfo Segment 정보 (MMS/PUSH 타입에만 유효), null 가능
     * @return 성공 시 0, 실패 시 -1
     */
    fun insertMO_NOTISEND(
        qItem: QITEM,
        msgHdr: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        segmentInfo: SegmentInfo? = null,
        workerThreadId: Long
    ): Int

    /**
     * Relay MO CallInfo 저장.
     * @return 성공 시 0, 실패 시 -1
     */
    fun insertRelayMOCallInfo(
        qItem: QITEM,
        msgHdr: SMReqTransResult,
        entity: GipHttpMoAccessEntity,
        serialNo: Long
    ): Int
}
