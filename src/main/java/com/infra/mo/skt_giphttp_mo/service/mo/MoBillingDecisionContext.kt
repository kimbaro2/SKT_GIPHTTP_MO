package com.infra.mo.skt_giphttp_mo.service.mo

/**
 * MO 영역에서 즉시 과금(bprintf) 수행 여부를 도메인(핸들러)이 결정할 때 사용하는 컨텍스트.
 * DB 컬럼(MOTRBILL, BILLTYPE) 기반.
 *
 * 공통 규칙 (두 경우 모두 billType != "1" 상정):
 * - MO-ACK: 과금 여부 최우선 체크 = MOTRBILL = N
 * - MO-TR:  과금 여부 최우선 체크 = MOTRBILL = Y
 *
 * @param motrBill MOTRBILL='Y' 여부 (DB: GIP_HTTP_MO_ACCESS.MO_TR_BILL)
 * @param billType 검증된 BILLTYPE 문자열 (예: "1" = 비과금, DB 컬럼으로 사용자 제어)
 * @param isMoAckContext true = MO-ACK 단계에서 호출, false = MO-TR 단계에서 호출
 */
data class MoBillingDecisionContext(
    val motrBill: Boolean,
    val billType: String,
    val isMoAckContext: Boolean
)
