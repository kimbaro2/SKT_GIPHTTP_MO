package com.infra.mo.skt_giphttp_mo.service.mo

import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.service.handler.MoServiceType
import org.springframework.stereotype.Component

/**
 * 서비스 타입별 MO 핸들러 등록/조회.
 * ESMClass 분기부에서 resolve(esmClass, destCID)로 MoServiceType을 구한 뒤
 * getHandler(serviceType).handle(qItem, context)로 서비스별 로직만 호출.
 */
@Component
class MoServiceHandlerRegistry(
    handlers: List<MoServiceHandler>
) {
    private val handlerMap: Map<MoServiceType, MoServiceHandler> = handlers
        .filterIsInstance<MoServiceTypeAwareHandler>()
        .associateBy { it.serviceType() }

    fun getHandler(serviceType: MoServiceType): MoServiceHandler {
        return handlerMap[serviceType]
            ?: handlerMap[MoServiceType.NORMAL_MO]
            ?: throw IllegalStateException("No handler for serviceType=$serviceType and no default NORMAL_MO handler")
    }
}

/**
 * 서비스 타입을 알고 있는 핸들러 (Registry가 매핑에 사용).
 * MO 전송·InsqStat·bprintf·bill 등 모든 기능을 ESMClass 호출 클래스(핸들러) 내부에서만 동작하도록 구현.
 * 중복 코드 허용, 유지보수 시 해당 핸들러만 수정.
 */
interface MoServiceTypeAwareHandler : MoServiceHandler {
    fun serviceType(): MoServiceType

    /**
     * QITEM 변수체크 (msgId, traceId 등). ESMClass 확정 이후 호출.
     * @return true: 검증 통과, false: 검증 실패(핸들러 내부에서 로그·InsqStat 후 반환)
     */
    fun checkQItemVariables(qItem: QITEM, context: MoServiceContext): Boolean

    /**
     * MO 전송 성공 통계(ST_GIPEVENT_MO_OK) 호출. 핸들러 내부에서 호출 또는 스킵(로그) 처리.
     */
    fun recordMoSuccessInsqStat(qItem: QITEM, context: MoServiceContext)

    /**
     * MO-ACK 과금 통계(ST_GIPEVENT_MOACK_BILL_OK) 호출. 핸들러 내부에서 호출 또는 스킵(로그) 처리.
     */
    fun recordMoAckBilling(qItem: QITEM, context: MoServiceContext, billType: Char)

    /**
     * bprintf 호출 스킵 여부. processMOBilling 등에서 사용.
     */
    fun shouldSkipBprintf(): Boolean
}
