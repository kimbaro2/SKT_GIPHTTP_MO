package com.infra.mo.skt_giphttp_mo.service.handler

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import org.springframework.stereotype.Component

/**
 * MoTRBill 처리 핸들러
 * C 코드 참고: GIPEVENT_c.c LINE 2087-2244
 */
@Component
class MoTRBillHandler {
    
    /**
     * MoTRBill 값 조회
     * @param gipHttpMoAccess GIPHTTP_MO_ACCESS 엔티티
     * @return MoTRBill 값 (기본값: "N")
     */
    fun getMoTRBill(gipHttpMoAccess: GipHttpMoAccessEntity?): String {
        return gipHttpMoAccess?.moTrBill ?: "N"  // 기본값: 'N'
    }
    
    /**
     * MO-TR 과금 사용 여부 확인
     * C 코드: gMOTRBILL == TRUE
     */
    fun isMoTRBillEnabled(moTrBill: String): Boolean {
        return moTrBill == "Y"
    }
    
    /**
     * TR 수신 시 즉시 과금 처리 여부 확인
     * C 코드: GIPEVENT_c.c LINE 2087
     * 
     * @param moTrBill MoTRBill 값
     * @return true: 즉시 과금 처리 (!gMOTRBILL), false: Update만 수행
     */
    fun shouldProcessImmediateBilling(moTrBill: String): Boolean {
        // C 코드 LINE 2087: !gMOTRBILL
        return !isMoTRBillEnabled(moTrBill)
    }
    
    /**
     * TR 수신 시 Update 처리 여부 확인
     * C 코드: GIPEVENT_c.c LINE 2234-2244
     * 
     * @param moTrBill MoTRBill 값
     * @return true: Update 수행 (gMOTRBILL == TRUE)
     */
    fun shouldUpdateOnTR(moTrBill: String): Boolean {
        // C 코드 LINE 2234: else (gMOTRBILL == TRUE)
        return isMoTRBillEnabled(moTrBill)
    }
}




