package com.infra.mo.skt_giphttp_mo.service.mo

import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.smsController.ResponseTR
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.service.handler.MoServiceType
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib
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
     * @deprecated 과금 여부는 {@link #shouldDoBillingInMoAreaNow} 사용 권장 (DB 컬럼 기반 도메인 결정)
     */
    fun shouldSkipBprintf(): Boolean

    /**
     * 과금을 MO 영역에서 바로 할지 여부. DB 컬럼(MOTRBILL, BILLTYPE) 기반.
     * processMOBilling에서 사용하며, true이면 bprintf 등 MO 과금 수행, false이면 스킵.
     *
     * 공통 규칙 (두 경우 모두 billType != "1" 상정):
     * - MO-ACK: 과금 여부 최우선 체크 = MOTRBILL = N
     * - MO-TR:  과금 여부 최우선 체크 = MOTRBILL = Y
     */
    fun shouldDoBillingInMoAreaNow(context: MoBillingDecisionContext): Boolean =
        when {
            context.isMoAckContext -> !context.motrBill && context.billType != "1"  // MO-ACK: MOTRBILL=N 최우선
            else -> context.motrBill && context.billType != "1"                    // MO-TR: MOTRBILL=Y 최우선
        }

    /**
     * MO-TR 단계 과금(bprintf) 호출. ESMClass 모든 타입은 MOTRBILL Y/N 모두 과금은 기본적으로 도메인 핸들러에서 수행.
     * - 일반/638/2580/로밍/1584/Forward: MOTRBILL=Y && BillType!=1 이면 processMOBilling 호출.
     * - 등기문자·안심문자: MOTRBILL=N → MO-ACK에서 도메인 수행. MOTRBILL=Y → MO-TR에서 3분 이내/이후, 성공/실패 조건에 따라 bprintf 호출 분기 결정.
     *
     * @param qItem QITEM
     * @param request MO-TR 응답(ResponseTR)
     * @param gipHttpMoAccess GIP_HTTP_MO_ACCESS 엔티티 (MOTRBILL, BILLTYPE 확인용)
     * @param smsQLib SmsQLib
     * @param loggerName 로거명
     */
    fun recordMoTrBilling(
        qItem: QITEM,
        request: ResponseTR,
        gipHttpMoAccess: GipHttpMoAccessEntity?,
        smsQLib: SmsQLib,
        loggerName: String
    ) {
        // 기본: no-op. 각 도메인에서 MOTRBILL=Y && BillType!=1 조건 등에 따라 오버라이드하여 processMOBilling 호출.
    }
}
