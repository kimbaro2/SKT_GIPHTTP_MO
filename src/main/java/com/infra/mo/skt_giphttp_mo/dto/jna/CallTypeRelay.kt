package com.infra.mo.skt_giphttp_mo.dto.jna

/**
 * Relay Traffic 호 유형 (C 코드 VBILLMO_c.c LINE 423, 530 등과 동일)
 * - C: ptrQitem->ucAgingCnt == '5' | '6' | '7' 로 RelayFlag·bprintf 분기
 * - Java: MOCALLINFO/MO_NOTISEND SRC_TYPE 또는 qItem.ucAgingCnt와 매핑
 */
enum class CallTypeRelay(val code: Char, val codeAsString: String) {
    /** VSMSS Relay MT — RelayFlag='Y'이어도 RelayTraffic bprintf 미호출, 일반 bprintf */
    VSMSS_RELAY_MT('5', "5"),
    /** HSMSS Relay MT — RelayFlag=='Y' && srcType != '5' 일 때 RelayTraffic bprintf */
    HSMSS_RELAY_MT('6', "6"),
    /** HSMSS Relay MO — 동일하게 RelayTraffic bprintf */
    HSMSS_RELAY_MO('7', "7");

    companion object {
        /** C LINE 423: ucAgingCnt == '5' || '6' || '7' → Relay 구간 여부 */
        fun isRelaySrcType(srcType: String?): Boolean =
            srcType != null && enumValues<CallTypeRelay>().any { it.codeAsString == srcType }

        /** C LINE 530: ucAgingCnt != CALL_TYPE_VSMSS_RELAY_MT → RelayTraffic bprintf 호출 여부 (5가 아니면 true) */
        fun shouldCallRelayTrafficBprintf(srcType: String?): Boolean =
            srcType != null && srcType != VSMSS_RELAY_MT.codeAsString
    }
}
