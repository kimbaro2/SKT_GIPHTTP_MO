package com.infra.mo.skt_giphttp_mo.service.impl

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.service.SmsMoService
import com.infra.mo.skt_giphttp_mo.service.handler.BillingDecisionHandler
import com.infra.mo.skt_giphttp_mo.service.handler.BillTypeHandler
import com.infra.mo.skt_giphttp_mo.service.handler.EsmClassHandler
import com.infra.mo.skt_giphttp_mo.service.handler.MoTRBillHandler
import org.springframework.stereotype.Service

/**
 * MO 메시지 처리 서비스 구현체
 * 
 * BillType, EsmClass, MoTRBill을 기반으로 MO 처리 결정
 * 각 핸들러를 조합하여 비즈니스 로직 수행
 */
@Service
class SmsMoServiceImpl(
    private val billingDecisionHandler: BillingDecisionHandler,
    private val billTypeHandler: BillTypeHandler,
    private val esmClassHandler: EsmClassHandler,
    private val moTRBillHandler: MoTRBillHandler
) : SmsMoService {
    
    /**
     * MO 전송 시 INSERT 테이블 결정
     * C 코드: GIPEVENT_c.c LINE 1605-1643
     * 
     * 우선순위:
     * 1. BillType 체크 (billType != "1")
     * 2. EsmClass 체크 (NOTI_PLUS)
     */
    override fun decideInsertTableForMO(
        gipHttpMoAccess: GipHttpMoAccessEntity?,
        qItem: QITEM
    ): Boolean {
        return billingDecisionHandler.decideInsertTableForMO(gipHttpMoAccess, qItem)
    }
    
    /**
     * TR 수신 시 처리 방식 결정
     * C 코드: GIPEVENT_c.c LINE 2087-2244
     * 
     * 우선순위:
     * 1. MoTRBill 체크 (moTrBill != "Y")
     * 2. EsmClass 체크 (NOTI_PLUS)
     */
    override fun decideTRProcessing(
        gipHttpMoAccess: GipHttpMoAccessEntity?,
        esmClass: Int
    ): BillingDecisionHandler.TRProcessingDecision {
        return billingDecisionHandler.decideTRProcessing(gipHttpMoAccess, esmClass)
    }
    
    /**
     * BillType 값 조회
     */
    override fun getBillType(gipHttpMoAccess: GipHttpMoAccessEntity?): String {
        return billTypeHandler.getBillType(gipHttpMoAccess)
    }
    
    /**
     * MoTRBill 값 조회
     */
    override fun getMoTRBill(gipHttpMoAccess: GipHttpMoAccessEntity?): Int {
        return moTRBillHandler.getMoTRBill(gipHttpMoAccess)
    }
    
    /**
     * EsmClass 값 조회
     */
    override fun getEsmClass(qItem: QITEM): Int {
        return esmClassHandler.getEsmClass(qItem)
    }
    
    /**
     * NOTI_PLUS 여부 확인
     */
    override fun isNotiPlus(esmClass: Int): Boolean {
        return esmClassHandler.isNotiPlus(esmClass)
    }
}




