package com.infra.mo.skt_giphttp_mo.service

import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib

/**
 * 블록 알림 메시지 처리 (processBlockNotification).
 * MOThreadPool에서 분리된 공통 호출 함수를 서비스 내부에서 사용할 수 있도록 의존성만 주입.
 */
interface MoBlockNotificationService {

    fun processBlockNotification(
        stQItem: QITEM,
        gstQItem: QITEM,
        smsQLib: SmsQLib,
        gServerID: Int,
        loggerName: String,
        workerThreadId: Long
    )
}
