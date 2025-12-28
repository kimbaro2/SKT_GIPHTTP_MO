package com.infra.mo.skt_giphttp_mo.utils

/**
 * BillType (과금 타입) 정의
 * 
 * C 코드 참고: GITcp.h, SmsDef.kt
 * 데이터베이스 주석: GipDefaultEntity.java LINE 81
 * 
 * @property value BillType 값 (0~5)
 * @property description 설명
 * @property meaning 의미
 */
enum class BillType(val value: Int, val description: String, val meaning: String) {
    /**
     * BILLTYPE_NONE = 0
     * 기본값/무료문자
     */
    NONE(0, "기본값/무료문자", "기본값 또는 무료문자 처리"),

    /**
     * BILLTYPE_NOT = 1
     * 비과금
     */
    NOT(1, "비과금", "비과금 처리"),

    /**
     * BILLTYPE_SRC = 2
     * 발신자 과금
     */
    SRC(2, "발신자 과금", "발신자 과금 처리"),

    /**
     * BILLTYPE_DESC = 3
     * 착신자 과금
     */
    DESC(3, "착신자 과금", "착신자 과금 처리"),

    /**
     * BILLTYPE_GIVE = 4
     * 선물 과금
     */
    GIVE(4, "선물 과금", "선물 과금 처리"),

    /**
     * BILLTYPE_CNT = 5
     * 비과금 정산용
     */
    CNT(5, "비과금 정산용", "비과금 정산용 처리");

    /**
     * 문자열 값으로 변환
     */
    fun toStringValue(): String = value.toString()

    /**
     * 문자 값으로 변환 (C 코드 호환)
     */
    fun toCharValue(): Char = value.toString()[0]

    companion object {
        /**
         * 정수 값으로 BillType 찾기
         */
        fun fromInt(value: Int): BillType? {
            return values().find { it.value == value }
        }

        /**
         * 문자열 값으로 BillType 찾기
         */
        fun fromString(value: String): BillType? {
            return value.toIntOrNull()?.let { fromInt(it) }
        }

        /**
         * 문자 값으로 BillType 찾기 (C 코드 호환)
         */
        fun fromChar(value: Char): BillType? {
            return value.toString().toIntOrNull()?.let { fromInt(it) }
        }

        /**
         * 기본값 반환 (BILLTYPE_SRC = 2)
         */
        fun getDefault(): BillType = SRC

        /**
         * 기본값 문자열 반환 ("2")
         */
        fun getDefaultString(): String = SRC.toStringValue()
    }
}




