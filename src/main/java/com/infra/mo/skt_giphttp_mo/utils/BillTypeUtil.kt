package com.infra.mo.skt_giphttp_mo.utils

/**
 * BillType 유틸리티 클래스
 * 
 * BillType별 처리 로직을 제공합니다.
 * C 코드 참고:
 * - GIPEVENT_c.c LINE 1605: MO 처리 분기
 * - GIPEVENT_c.c LINE 1995: TR 수신 즉시 과금 처리
 * - GIDBLib.c: 한도 체크 로직
 * - SmsQLib.c LINE 2056: NP_PREFIX 처리
 */
object BillTypeUtil {

    /**
     * BillType 값 조회 (기본값: "2" - SRC)
     * 
     * @param billType BillType 문자열 값 (null 가능)
     * @return BillType 값 (기본값: "2")
     */
    fun getBillType(billType: String?): String {
        return billType ?: BillType.getDefaultString()
    }

    /**
     * 비과금 여부 확인 (BillType == "1")
     * C 코드: gBILLTYPE == '1'
     * 
     * @param billType BillType 값
     * @return true: 비과금, false: 과금
     */
    fun isNotBilling(billType: String?): Boolean {
        val type = BillType.fromString(billType ?: "")
        return type == BillType.NOT
    }

    /**
     * 발신자 과금 여부 확인 (BillType == "2")
     * C 코드: gBILLTYPE == BILLTYPE_SRC
     * 
     * @param billType BillType 값
     * @return true: 발신자 과금
     */
    fun isSrcBilling(billType: String?): Boolean {
        val type = BillType.fromString(billType ?: "")
        return type == BillType.SRC
    }

    /**
     * 착신자 과금 여부 확인 (BillType == "3")
     * 
     * @param billType BillType 값
     * @return true: 착신자 과금
     */
    fun isDescBilling(billType: String?): Boolean {
        val type = BillType.fromString(billType ?: "")
        return type == BillType.DESC
    }

    /**
     * 선물 과금 여부 확인 (BillType == "4")
     * 
     * @param billType BillType 값
     * @return true: 선물 과금
     */
    fun isGiveBilling(billType: String?): Boolean {
        val type = BillType.fromString(billType ?: "")
        return type == BillType.GIVE
    }

    /**
     * 무료문자 여부 확인 (BillType == "0")
     * 
     * @param billType BillType 값
     * @return true: 무료문자
     */
    fun isFreeMessage(billType: String?): Boolean {
        val type = BillType.fromString(billType ?: "")
        return type == BillType.NONE
    }

    /**
     * MO 전송 시 INSERT 테이블 결정
     * C 코드: GIPEVENT_c.c LINE 1605-1643
     * 
     * 로직(C 오리지널): gBILLTYPE != '1' && NOTI_PLUS(20/21) → InsertMO_NOTISEND
     *      그 외 → InsertGIPMOCallInfo
     * 
     * @param billType BillType 값
     * @param isNotiPlus NOTI_PLUS 여부 (20/21)
     * @return true: InsertMO_NOTISEND, false: InsertGIPMOCallInfo
     */
    fun shouldInsertMO_NOTISEND(billType: String?, isNotiPlus: Boolean): Boolean {
        // C 코드 LINE 1605: gBILLTYPE != '1' && NOTI_PLUS
        return !isNotBilling(billType) && isNotiPlus
    }

    /**
     * TR 수신 시 즉시 과금 처리 여부 확인
     * C 코드: GIPEVENT_c.c LINE 1995-2066
     * 
     * 로직: gBILLTYPE == '1' → 즉시 과금 처리 (SelectGIPMOCallInfo + bprintf)
     * 
     * @param billType BillType 값
     * @return true: 즉시 과금 처리
     */
    fun shouldProcessImmediateBilling(billType: String?): Boolean {
        // C 코드 LINE 1995: gBILLTYPE == '1' 케이스
        return isNotBilling(billType)
    }

    /**
     * 한도 체크 결과 DTO
     */
    data class LimitCheckResult(
        val limitMO: Boolean,
        val limitMT: Boolean,
        val limitGIVE: Boolean
    )

    /**
     * 한도 체크 로직
     * C 코드: GIDBLib.c LINE 4727-4749
     * 
     * - chLimitFlag가 "Y"인 경우에만 과금 타입에 따라 한도 플래그가 설정됩니다.
     * - BILLTYPE_SRC (2)  → iLimitMO = true
     * - BILLTYPE_DESC (3) → iLimitMT = true
     * - BILLTYPE_GIVE (4) → iLimitGIVE = true
     * - 그 외 타입 → 모두 false
     * 
     * @param chLimitFlag 차단 여부 플래그 ("Y" or "N")
     * @param billType 과금 타입 코드 (문자열)
     * @return LimitCheckResult (MO, MT, GIVE 각각에 대한 한도 여부 포함)
     */
    fun getLimitCheck(chLimitFlag: String?, billType: String?): LimitCheckResult {
        var limitMO = false
        var limitMT = false
        var limitGIVE = false

        if (chLimitFlag == "Y" || chLimitFlag == "y") {
            val type = BillType.fromString(billType ?: "")
            when (type) {
                BillType.SRC -> {
                    limitMO = true
                    limitMT = false
                    limitGIVE = false
                }
                BillType.DESC -> {
                    limitMO = false
                    limitMT = true
                    limitGIVE = false
                }
                BillType.GIVE -> {
                    limitMO = false
                    limitMT = false
                    limitGIVE = true
                }
                BillType.NONE,
                BillType.NOT,
                BillType.CNT,
                null -> {
                    limitMO = false
                    limitMT = false
                    limitGIVE = false
                }
            }
        } else {
            limitMO = false
            limitMT = false
            limitGIVE = false
        }

        return LimitCheckResult(limitMO, limitMT, limitGIVE)
    }

    /**
     * NP_PREFIX 처리 여부 확인
     * C 코드: SmsQLib.c LINE 2056
     * 
     * 로직: cBillType == '2' → 특별한 통신사 코드 처리
     * 
     * @param billType BillType 값
     * @return true: NP_PREFIX 처리 필요
     */
    fun requiresNPPrefixProcessing(billType: String?): Boolean {
        return isSrcBilling(billType)
    }

    /**
     * BillType 유효성 검증
     * 
     * @param billType BillType 값
     * @return true: 유효한 값 (0~5)
     */
    fun isValid(billType: String?): Boolean {
        if (billType == null || billType.isEmpty()) {
            return false
        }
        val type = BillType.fromString(billType)
        return type != null
    }

    /**
     * BillType 설명 조회
     * 
     * @param billType BillType 값
     * @return 설명 문자열
     */
    fun getDescription(billType: String?): String {
        val type = BillType.fromString(billType ?: "")
        return type?.description ?: "알 수 없는 타입"
    }

    /**
     * BillType 의미 조회
     * 
     * @param billType BillType 값
     * @return 의미 문자열
     */
    fun getMeaning(billType: String?): String {
        val type = BillType.fromString(billType ?: "")
        return type?.meaning ?: "알 수 없는 의미"
    }
}



