package com.infra.mo.skt_giphttp_mo.config.application;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;

public class ThreadCustomGradeFilter extends TurboFilter {

    // 현재 Logback에서 Reactor Context를 직접 접근할 수 없으므로,
    // WitcomLog 호출 시 MDC 대신 Reactor Context에서 ThreadLocal로 값을 임시 저장하는 방식 사용 가능
    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        // ThreadLocal에 logGradeFlag 저장
        Integer grade = WitcomLog.THREAD_LOG_GRADE.get();
        String activeProfile = System.getProperty("spring.profiles.active"); // 또는 환경변수, applicationContext에서 가져올 수도 있음
        if ("test".equalsIgnoreCase(activeProfile) || "local".equalsIgnoreCase(activeProfile)) {
            return FilterReply.ACCEPT;
        }

        if (grade == null) return FilterReply.NEUTRAL;

        switch (grade) {
            case 0: // 모든 로그 허용
                return FilterReply.ACCEPT;

            case 1: // INFO만
                if (level == Level.INFO) return FilterReply.ACCEPT;
                return FilterReply.DENY;

            case 2: // INFO, DEBUG
                if (level == Level.INFO || level == Level.DEBUG) return FilterReply.ACCEPT;
                return FilterReply.DENY;

            case 3: // INFO, DEBUG, TRACE
                if (level == Level.INFO || level == Level.DEBUG || level == Level.TRACE) return FilterReply.ACCEPT;
                return FilterReply.DENY;

            default:
                return FilterReply.DENY; // 기본은 차단하는 게 안전
        }
    }
}
