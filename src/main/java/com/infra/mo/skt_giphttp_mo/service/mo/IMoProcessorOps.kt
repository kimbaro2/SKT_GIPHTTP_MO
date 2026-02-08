package com.infra.mo.skt_giphttp_mo.service.mo

import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM

/**
 * MO 처리 연산(한도/차단/DB/전송 등)을 핸들러에서 호출하기 위한 인터페이스.
 * MOThreadPool이 구현하며, SmsResServiceImpl 등에서 processSMReqSimple를 호출할 때 사용한다.
 * 각 도메인 핸들러는 handle() 내부에서 흐름 전부 구현하며, processSMReqSimple는 레거시/공통 경로용.
 */
interface IMoProcessorOps {

    /**
     * SM_REQ_SIMPLE(MO 전송) 전체 로직 수행 (레거시 INSERT 분기: NOTI/Relay/GIPMO).
     * SmsResServiceImpl에서 호출. 도메인별 핸들러는 handle() 내부에서 자체 흐름으로 처리.
     */
    fun processSMReqSimple(qItem: QITEM, context: MoServiceContext)
}
