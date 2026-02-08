package com.infra.mo.skt_giphttp_mo.service.mo

import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM

/**
 * 서비스별 MO 처리 핸들러 인터페이스.
 * ESMClass 분기부에서 서비스 타입에 따라 해당 핸들러만 호출한다.
 */
interface MoServiceHandler {

    /**
     * 해당 서비스에 맞는 MO 로직 수행.
     * @param qItem dequeue된 QITEM
     * @param context 처리에 필요한 컨텍스트
     */
    fun handle(qItem: QITEM, context: MoServiceContext)
}
