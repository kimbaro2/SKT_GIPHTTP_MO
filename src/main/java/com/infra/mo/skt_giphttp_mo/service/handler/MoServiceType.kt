package com.infra.mo.skt_giphttp_mo.service.handler

/**
 * MO 서비스 유형 (서비스 리스트)
 *
 * 전제: ESMClass 별도 검증 후 해당 클래스 진입 이후에만 번호형식(CID) 체크.
 * ESMClass 분기 + 번호규칙(불변성규칙) 후 결정.
 */
enum class MoServiceType {
    /** 1. 일반 MO */
    NORMAL_MO,

    /** 2. 1584 캐릭터문자 */
    CHARACTER_1584,

    /** 3. 638 문자매니저 */
    SMS_MANAGER_638,

    /** 4. 2580 문자매신저 */
    SMS_MESSENGER_2580,

    /** 5. CDMA 로밍 */
    CDMA_ROAMING,

    /** 6. GSM 로밍 */
    GSM_ROAMING,

    /** 7. 착신전환 (48,49,50,52,53 — CDMA/GSM 로밍 포함 착신전환은 로밍 항목에 포함) */
    FORWARD,

    /** 8. 등기 확인용 — 번호 뒤에 # 붙은 경우만 구분 (특번은 NORMAL_MO, 별도 검증 없음) */
    SPECIAL_SHARP,

    /** 9. 안심문자 */
    NOTI_PLUS,

    /** 10. 등기문자 */
    NOTI_REGISTERED,
}
