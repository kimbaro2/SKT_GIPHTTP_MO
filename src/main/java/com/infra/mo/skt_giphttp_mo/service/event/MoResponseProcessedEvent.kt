package com.infra.mo.skt_giphttp_mo.service.event

import org.springframework.context.ApplicationEvent

/**
 * MO 응답 처리 완료 이벤트.
 * SmsResServiceImpl이 직접 Registry를 호출하지 않고, 처리 결과를 발행하면
 * 리스너(Registry/핸들러 쪽)가 구독하여 의존성 방향을 한쪽으로 유지.
 */
class MoResponseProcessedEvent(
    source: Any,
    val esmClass: Int,
    val destCid: String?,
    val msgSubCode: Int,
    val success: Boolean,
    val loggerName: String? = null
) : ApplicationEvent(source)
