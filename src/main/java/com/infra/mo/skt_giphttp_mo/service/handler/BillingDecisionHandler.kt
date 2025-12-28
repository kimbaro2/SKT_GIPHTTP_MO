package com.infra.mo.skt_giphttp_mo.service.handler

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import org.springframework.stereotype.Component

/**
 * 과금 결정 통합 핸들러
 * BillType, EsmClass, MoTRBill을 조합하여 최종 처리 결정
 */
@Component
class BillingDecisionHandler(
    private val billTypeHandler: BillTypeHandler,
    private val esmClassHandler: EsmClassHandler,
    private val moTRBillHandler: MoTRBillHandler
) {
    
    /**
     * MO 전송 시 INSERT 테이블 결정
     * C 코드: GIPEVENT_c.c LINE 1605-1643
     * 
     * 우선순위:
     * 1. BillType 체크 (billType != "1")
      * 2. EsmClass 체크 (NOTI_PLUS: 20/21)
     * 
     * @param gipHttpMoAccess GIPHTTP_MO_ACCESS 엔티티
     * @param qItem QITEM
     * @return true: InsertMO_NOTISEND, false: InsertGIPMOCallInfo
     */
    fun decideInsertTableForMO(
        gipHttpMoAccess: GipHttpMoAccessEntity?,
        qItem: QITEM
    ): Boolean {
        val billType = billTypeHandler.getBillType(gipHttpMoAccess)
        val esmClass = esmClassHandler.getEsmClass(qItem)
        val isNotiPlus = esmClassHandler.isNotiPlus(esmClass)
        
        // C 코드 LINE 1605: gBILLTYPE != '1' && NOTI_PLUS
        return billTypeHandler.shouldInsertMO_NOTISEND(billType, isNotiPlus)
    }
    
    /**
     * TR 수신 시 처리 방식 결정
     * C 코드: GIPEVENT_c.c LINE 2087-2244
     * 
     * 우선순위:
     * 1. MoTRBill 체크 (moTrBill != "Y")
      * 2. EsmClass 체크 (NOTI_PLUS: 20/21)
     * 
     * @param gipHttpMoAccess GIPHTTP_MO_ACCESS 엔티티
     * @param esmClass EsmClass 값
     * @return TR 처리 결과
     */
    fun decideTRProcessing(
        gipHttpMoAccess: GipHttpMoAccessEntity?,
        esmClass: Int
    ): TRProcessingDecision {
        val moTrBill = moTRBillHandler.getMoTRBill(gipHttpMoAccess)
        val billType = billTypeHandler.getBillType(gipHttpMoAccess)
        
        // 우선순위 1: MoTRBill 체크
        if (moTRBillHandler.shouldProcessImmediateBilling(moTrBill)) {
            // C 코드 LINE 2087: !gMOTRBILL
            // BillType == "1"인 경우 즉시 과금 처리
            return TRProcessingDecision.ImmediateBilling(billType)
        }
        
        // 우선순위 2: EsmClass 체크
        val isNotiPlus = esmClassHandler.isNotiPlus(esmClass)
        return if (isNotiPlus) {
            // C 코드 LINE 2236-2238: UpdateMO_NOTISEND
            TRProcessingDecision.UpdateMO_NOTISEND
        } else {
            // C 코드 LINE 2240-2242: UpdateGIPMOCallInfo
            TRProcessingDecision.UpdateGIPMOCallInfo
        }
    }
    
    /**
     * TR 처리 결정 결과
     */
    sealed class TRProcessingDecision {
        /**
         * 즉시 과금 처리 (SelectGIPMOCallInfo + bprintf)
         * C 코드: LINE 2087-2233
         */
        data class ImmediateBilling(val billType: String) : TRProcessingDecision()
        
        /**
         * UpdateMO_NOTISEND 호출
         * C 코드: LINE 2238
         */
        object UpdateMO_NOTISEND : TRProcessingDecision()
        
        /**
         * UpdateGIPMOCallInfo 호출
         * C 코드: LINE 2242
         */
        object UpdateGIPMOCallInfo : TRProcessingDecision()
    }
}



