package com.infra.mo.skt_giphttp_mo.service

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity

/**
 * BILLTYPE / MOTRBILL 체크 로직 서비스.
 * 각 서비스 클래스에서 과금 타입·MO-TR 허용 여부 등을 일관되게 사용하기 위해 분리.
 */
interface MoBillTypeService {

    /**
     * Entity의 BILL_TYPE을 검증 후 Char로 반환 (gBILLTYPE).
     * @param defaultChar 기본값 (예: '0', '2')
     */
    fun getBillTypeChar(entity: GipHttpMoAccessEntity?, defaultChar: Char = '0'): Char

    /**
     * BILL_TYPE 문자열 검증 및 정규화 (String 반환).
     */
    fun validateAndNormalize(billType: String?, defaultValue: String = "2"): String

    /** 비과금 여부 (BILL_TYPE == '1') */
    fun isFreeBill(gBillType: Char): Boolean

    /** 선물 과금 여부 (BILL_TYPE == '4', GIVE 한도 체크 등에 사용) */
    fun isGiveBill(gBillType: Char): Boolean

    /** MO-TR 허용 여부 (MO_TR_BILL == 1) */
    fun isMoTrBillEnabled(entity: GipHttpMoAccessEntity?): Boolean

    /** MO_TR_BILL 값만으로 허용 여부 판단 (entity 없이 값만 있을 때) */
    fun isMoTrBillEnabled(moTrBill: Int?): Boolean
}
