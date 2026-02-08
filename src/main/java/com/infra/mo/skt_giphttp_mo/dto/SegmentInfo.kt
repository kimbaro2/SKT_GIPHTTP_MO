package com.infra.mo.skt_giphttp_mo.dto

/**
 * Segment 정보를 담는 데이터 클래스 (C 코드의 SEGMENT 구조체 대응)
 * C 코드 LINE 1601-1613 참조
 */
data class SegmentInfo(
    val msgRefId: Int,      // ucRsv[0]
    val totalSeg: Int,      // (ucRsv[1] & 0xF0) >> 4
    val segSeq: Int,        // ucRsv[1] & 0x0F
    val isValid: Boolean    // MMS/PUSH 타입 여부
)
