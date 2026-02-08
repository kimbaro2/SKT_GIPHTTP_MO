package com.infra.mo.skt_giphttp_mo.service.impl

import ch.qos.logback.classic.Level
import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult
import com.infra.mo.skt_giphttp_mo.service.MoRequeueService
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.QItemServiceUtil.QueueResult
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib
import org.springframework.stereotype.Service

/**
 * 큐 재저장 처리 서비스 구현.
 * MOThreadPool 공통 호출 함수(requeueMessage)를 서비스 내부에서 사용할 수 있도록 의존성만 주입.
 */
@Service
class MoRequeueServiceImpl(
    private val witcomLog: WitcomLog
) : MoRequeueService {

    override fun requeueMessage(
        gstQResultObj: QueueResult,
        queueNo: Int,
        smsQLib: SmsQLib,
        loggerName: String,
        workerThreadId: Long,
        gstQItemTrans: SMReqTransResult
    ) {
        val originalQItemForRequeue = gstQResultObj.qItem
        if (originalQItemForRequeue != null) {
            val targetQueueNo = queueNo
            originalQItemForRequeue.write()

            witcomLog.c_write(
                loggerName,
                Level.INFO,
                "[MO] 최종 큐 삽입 (재저장): targetQueueNo=$targetQueueNo, srcCID=${gstQItemTrans.srcCid}, destCID=${gstQItemTrans.destCid}",
                workerThreadId
            )

            val requeueResult = smsQLib.InsertIntoSmsQWithQNo(originalQItemForRequeue, targetQueueNo)

            if (requeueResult >= 0) {
                witcomLog.c_write(
                    loggerName,
                    Level.INFO,
                    String.format("MO 메시지 Queue 재저장 성공: result=%d, targetQueueNo=%d, srcCID(%s), destCID(%s)", requeueResult, targetQueueNo, gstQItemTrans.srcCid, gstQItemTrans.destCid),
                    workerThreadId
                )
            } else {
                witcomLog.c_write(
                    loggerName,
                    Level.INFO,
                    String.format("MO 메시지 Queue 재저장 실패: result=%d, targetQueueNo=%d, srcCID(%s), destCID(%s)", requeueResult, targetQueueNo, gstQItemTrans.srcCid, gstQItemTrans.destCid),
                    workerThreadId
                )
            }
        }
    }
}
