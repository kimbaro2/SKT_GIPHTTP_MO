package com.infra.mo.skt_giphttp_mo.service.event

import com.infra.mo.skt_giphttp_mo.service.handler.MoServiceTypeResolver
import com.infra.mo.skt_giphttp_mo.service.mo.MoServiceHandlerRegistry
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

/**
 * MO 응답 처리 완료 이벤트 구독.
 * Registry/핸들러 쪽에서 응답 처리 결과에 따른 후속 로직(통계, 로깅 등)을 수행.
 * 의존성 방향: SmsResService → 이벤트 발행 / 이 리스너 → Registry (순환 없음).
 */
@Component
class MoResponseProcessedEventListener(
    private val moServiceTypeResolver: MoServiceTypeResolver,
    private val moServiceHandlerRegistry: MoServiceHandlerRegistry
) {

    @EventListener
    fun onMoResponseProcessed(event: MoResponseProcessedEvent) {
        // 서비스 타입별 핸들러가 필요하면 Registry에서 조회하여 후속 처리 가능
        val serviceType = moServiceTypeResolver.resolve(event.esmClass, event.destCid)
        val handler = moServiceHandlerRegistry.getHandler(serviceType)
        // 필요 시 handler 기반 통계·로깅 등 확장
        if (event.loggerName != null && event.success) {
            // 예: 핸들러별 성공 카운트, 메트릭 등
        }
    }
}
