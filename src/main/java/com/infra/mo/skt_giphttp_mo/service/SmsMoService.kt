package com.infra.mo.skt_giphttp_mo.service

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.service.handler.BillingDecisionHandler

/**
 * MO 메시지 처리 서비스 인터페이스
 * 
 * BillType, EsmClass, MoTRBill을 기반으로 MO 처리 결정
 */
interface SmsMoService {
    
    /**
     * MO 전송 시 INSERT 테이블 결정
     * 
     * @param gipHttpMoAccess GIPHTTP_MO_ACCESS 엔티티
     * @param qItem QITEM
     * @return true: InsertMO_NOTISEND, false: InsertGIPMOCallInfo
     */
    fun decideInsertTableForMO(
        gipHttpMoAccess: GipHttpMoAccessEntity?,
        qItem: QITEM
    ): Boolean
    
    /**
     * TR 수신 시 처리 방식 결정
     * 
     * @param gipHttpMoAccess GIPHTTP_MO_ACCESS 엔티티
     * @param esmClass EsmClass 값
     * @return TR 처리 결정 결과
     */
    fun decideTRProcessing(
        gipHttpMoAccess: GipHttpMoAccessEntity?,
        esmClass: Int
    ): BillingDecisionHandler.TRProcessingDecision
    
    /**
     * BillType 값 조회
     * 
     * @param gipHttpMoAccess GIPHTTP_MO_ACCESS 엔티티
     * @return BillType 값
     */
    fun getBillType(gipHttpMoAccess: GipHttpMoAccessEntity?): String
    
    /**
     * MoTRBill 값 조회
     * 
     * @param gipHttpMoAccess GIPHTTP_MO_ACCESS 엔티티
     * @return MoTRBill 값
     */
    fun getMoTRBill(gipHttpMoAccess: GipHttpMoAccessEntity?): String
    
    /**
     * EsmClass 값 조회
     * 
     * @param qItem QITEM
     * @return EsmClass 값
     */
    fun getEsmClass(qItem: QITEM): Int
    
    /**
     * NOTI_PLUS 여부 확인
     * 
     * @param esmClass EsmClass 값
     * @return true: NOTI_PLUS, false: 일반 SMS
     */
    fun isNotiPlus(esmClass: Int): Boolean
}




