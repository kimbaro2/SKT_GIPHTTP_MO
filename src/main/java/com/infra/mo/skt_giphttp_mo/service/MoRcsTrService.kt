package com.infra.mo.skt_giphttp_mo.service

import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib

/**
 * RCS TR 메시지 처리 (processRcsTrMessage).
 * MOThreadPool에서 분리된 공통 호출 함수를 서비스 내부에서 사용할 수 있도록 의존성만 주입.
 */
interface MoRcsTrService {

    fun processRcsTrMessage(
        gstQItem: QITEM,
        smsQLib: SmsQLib,
        loggerName: String,
        workerThreadId: Long
    )
}
