package com.infra.mo.skt_giphttp_mo.utils;

import java.time.ZoneId;

/**
 * 타임존 관련 상수 클래스
 * 프로젝트 전역에서 사용하는 타임존 상수를 정의합니다.
 */
public final class TimeZoneConstants {
    private TimeZoneConstants() {
        // 유틸리티 클래스이므로 인스턴스화 방지
    }

    /**
     * 서울 타임존 문자열 상수
     */
    public static final String SEOUL_TIMEZONE_STRING = "Asia/Seoul";

    /**
     * 서울 타임존 ZoneId 상수 (재사용을 위해 미리 생성)
     */
    public static final ZoneId SEOUL_ZONE_ID = ZoneId.of(SEOUL_TIMEZONE_STRING);
}
