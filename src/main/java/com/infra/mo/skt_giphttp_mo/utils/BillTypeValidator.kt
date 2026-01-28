package com.infra.mo.skt_giphttp_mo.utils

import org.slf4j.LoggerFactory

/**
 * BILL_TYPE 검증 및 변환 유틸리티
 */
object BillTypeValidator {
    private val log = LoggerFactory.getLogger(BillTypeValidator::class.java)
    
    // 유효한 BILL_TYPE 값 목록
    private val VALID_BILL_TYPES = setOf('0', '1', '2', '3', '4', '5')
    
    // 기본값: SRC (발신자 과금)
    const val DEFAULT_BILL_TYPE = "2"
    
    /**
     * BILL_TYPE 값 검증 및 정규화
     * @param billType 원본 BILL_TYPE 값 (String)
     * @param defaultValue 기본값 (기본: "2")
     * @return 검증된 BILL_TYPE 값
     */
    fun validateAndNormalize(billType: String?, defaultValue: String = DEFAULT_BILL_TYPE): String {
        // null 체크
        if (billType == null) {
            log.warn("BILL_TYPE이 null입니다. 기본값({}) 사용", defaultValue)
            return defaultValue
        }
        
        // 공백 제거 및 첫 문자 추출
        val trimmed = billType.trim()
        if (trimmed.isEmpty()) {
            log.warn("BILL_TYPE이 빈 문자열입니다. 기본값({}) 사용", defaultValue)
            return defaultValue
        }
        
        val firstChar = trimmed.first()
        
        // 유효한 값인지 확인
        if (!VALID_BILL_TYPES.contains(firstChar)) {
            log.error("유효하지 않은 BILL_TYPE 값: '{}'. 기본값({}) 사용", billType, defaultValue)
            return defaultValue
        }
        
        return firstChar.toString()
    }
    
    /**
     * BILL_TYPE 값이 유효한지 확인
     * @param billType BILL_TYPE 값
     * @return 유효하면 true
     */
    fun isValid(billType: String?): Boolean {
        if (billType == null) return false
        val trimmed = billType.trim()
        if (trimmed.isEmpty()) return false
        return VALID_BILL_TYPES.contains(trimmed.first())
    }
    
    /**
     * BILL_TYPE을 Char로 변환 (C 코드 호환)
     * @param billType BILL_TYPE 값
     * @param defaultChar 기본값 (기본: '2')
     * @return Char 타입의 BILL_TYPE
     */
    fun toChar(billType: String?, defaultChar: Char = '2'): Char {
        val normalized = validateAndNormalize(billType, defaultChar.toString())
        return normalized.first()
    }
    
    /**
     * 비과금 여부 확인 (BILL_TYPE == '1')
     */
    fun isNotBilling(billType: String?): Boolean {
        val normalized = validateAndNormalize(billType)
        return normalized == "1"
    }
    
    /**
     * 발신자 과금 여부 확인 (BILL_TYPE == '2')
     */
    fun isSrcBilling(billType: String?): Boolean {
        val normalized = validateAndNormalize(billType)
        return normalized == "2"
    }
    
    /**
     * 착신자 과금 여부 확인 (BILL_TYPE == '3')
     */
    fun isDescBilling(billType: String?): Boolean {
        val normalized = validateAndNormalize(billType)
        return normalized == "3"
    }
    
    /**
     * 선물 과금 여부 확인 (BILL_TYPE == '4')
     */
    fun isGiveBilling(billType: String?): Boolean {
        val normalized = validateAndNormalize(billType)
        return normalized == "4"
    }
}
