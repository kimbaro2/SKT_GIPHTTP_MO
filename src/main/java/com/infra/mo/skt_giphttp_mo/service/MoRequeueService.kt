package com.infra.mo.skt_giphttp_mo.service

import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil.QueueResult
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib

/**
 * 큐 재저장 처리 (requeueMessage).
 * MOThreadPool에서 분리된 공통 호출 함수를 서비스 내부에서 사용할 수 있도록 의존성만 주입.
 */
interface MoRequeueService {

    fun requeueMessage(
        gstQResultObj: QueueResult,
        queueNo: Int,
        smsQLib: SmsQLib,
        loggerName: String,
        workerThreadId: Long,
        gstQItemTrans: SMReqTransResult
    )
}
