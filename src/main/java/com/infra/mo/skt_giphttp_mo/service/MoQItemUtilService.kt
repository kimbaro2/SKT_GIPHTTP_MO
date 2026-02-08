package com.infra.mo.skt_giphttp_mo.service

import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM

/**
 * QITEM 유틸 공통 로직 (copyQItem, getNInforNo).
 * MOThreadPool에서 분리된 공통 호출 함수를 서비스 내부에서 사용할 수 있도록 의존성만 주입.
 */
interface MoQItemUtilService {

    /** JNA QITEM 딥카피 (원본 변형 방지). */
    fun copyQItem(src: QITEM): QITEM

    /** qItem.usSource를 nInforNo로 반환. */
    fun getNInforNo(qItem: QITEM): Int
}
