package com.infra.mo.skt_giphttp_mo.service

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult

/**
 * GIP 이벤트 로그 포맷 공통 로직 (formatGipEventLog, getMessageType, formatDataEncoding).
 * MOThreadPool에서 분리된 공통 호출 함수를 서비스 내부에서 사용할 수 있도록 의존성만 주입.
 */
interface MoGipEventLogService {

    fun getMessageType(msgCode: Short, msgSubCode: Short): String
    fun formatDataEncoding(dataEncoding: Byte): String
    fun formatGipEventLog(
        entity: GipHttpMoAccessEntity,
        gServerID: Int,
        msgHdr: SMReqTransResult,
        qItem: QITEM?
    ): String
}
