package com.infra.mo.skt_giphttp_mo.service.handler

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.utils.BillTypeValidator
import org.springframework.stereotype.Component

/**
 * BillType 처리 핸들러
 * C 코드 참고: GIPEVENT_c.c LINE 1605, 1995-2066
 */
@Component
class BillTypeHandler {
    
    /**
     * BillType 값 조회 (검증 포함)
     * @param gipHttpMoAccess GIPHTTP_MO_ACCESS 엔티티
     * @return 검증된 BillType 값 (기본값: "2" - SRC)
     */
    fun getBillType(gipHttpMoAccess: GipHttpMoAccessEntity?): String {
        return BillTypeValidator.validateAndNormalize(gipHttpMoAccess?.billType)
    }
    
    /**
     * 비과금 여부 확인 (BillType == "1")
     * C 코드: gBILLTYPE == '1'
     */
    fun isNotBilling(billType: String?): Boolean {
        return BillTypeValidator.isNotBilling(billType)
    }
    
    /**
     * 발신자 과금 여부 확인 (BillType == "2")
     * C 코드: gBILLTYPE == BILLTYPE_SRC
     */
    fun isSrcBilling(billType: String?): Boolean {
        return BillTypeValidator.isSrcBilling(billType)
    }
    
    /**
     * MO 전송 시 INSERT 테이블 결정
     * C 코드: GIPEVENT_c.c LINE 1605-1643
     * 
     * @param billType BillType 값
     * @param isNotiPlus NOTI_PLUS 여부
     * @return true: InsertMO_NOTISEND, false: InsertGIPMOCallInfo
     */
    fun shouldInsertMO_NOTISEND(billType: String?, isNotiPlus: Boolean): Boolean {
        // C 코드 LINE 1605: gBILLTYPE != '1' && NOTI_PLUS
        return !BillTypeValidator.isNotBilling(billType) && isNotiPlus
    }
    
    /**
     * TR 수신 시 즉시 과금 처리 여부 확인
     * C 코드: GIPEVENT_c.c LINE 1995-2066
     * 
     * @param billType BillType 값
     * @return true: 즉시 과금 처리 (SelectGIPMOCallInfo + bprintf)
     */
    fun shouldProcessImmediateBilling(billType: String?): Boolean {
        // C 코드 LINE 1995: gBILLTYPE == '1' 케이스
        return BillTypeValidator.isNotBilling(billType)
    }
}




