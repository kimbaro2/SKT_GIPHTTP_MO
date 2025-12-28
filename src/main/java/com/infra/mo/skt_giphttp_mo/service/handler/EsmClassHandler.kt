package com.infra.mo.skt_giphttp_mo.service.handler

import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NOTI_PLUS_NORMAL_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NOTI_PLUS_PORTED_MO
import org.springframework.stereotype.Component

/**
 * EsmClass 처리 핸들러
 * C 코드 참고: GIPEVENT_c.c LINE 1607, 2236
 */
@Component
class EsmClassHandler {
    
    /**
     * QITEM에서 EsmClass 값 추출
     * C 코드: nRsv4Protocol[11]
     */
    fun getEsmClass(qItem: QITEM): Int {
        return qItem.nRsv4Protocol[11].toInt()
    }
    
    /**
     * NOTI_PLUS 여부 확인 (C 오리지널 기준: 2종)
     * C 코드: GIPEVENT_c.c LINE 1607, 2236
     * 
     * @param esmClass EsmClass 값
     * @return true: NOTI_PLUS(20/21), false: 그 외(예: esm_class=1 포함)
     */
    fun isNotiPlus(esmClass: Int): Boolean {
        return esmClass == NOTI_PLUS_NORMAL_MO ||
            esmClass == NOTI_PLUS_PORTED_MO
    }
    
    /**
     * QITEM에서 NOTI_PLUS 여부 확인
     */
    fun isNotiPlus(qItem: QITEM): Boolean {
        return isNotiPlus(getEsmClass(qItem))
    }
    
    /**
     * MO 전송 시 INSERT 테이블 결정
     * C 코드: GIPEVENT_c.c LINE 1607-1610
     * 
     * @param esmClass EsmClass 값
     * @return true: InsertMO_NOTISEND, false: InsertGIPMOCallInfo
     */
    fun shouldInsertMO_NOTISEND(esmClass: Int): Boolean {
        return isNotiPlus(esmClass)
    }
    
    /**
     * TR 수신 시 UPDATE 테이블 결정
     * C 코드: GIPEVENT_c.c LINE 2236-2242
     * 
     * @param esmClass EsmClass 값
     * @return true: UpdateMO_NOTISEND, false: UpdateGIPMOCallInfo
     */
    fun shouldUpdateMO_NOTISEND(esmClass: Int): Boolean {
        return isNotiPlus(esmClass)
    }
}



