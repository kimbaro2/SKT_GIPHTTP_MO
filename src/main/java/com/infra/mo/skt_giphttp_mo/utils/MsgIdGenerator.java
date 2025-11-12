package com.infra.mo.skt_giphttp_mo.utils;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

public class MsgIdGenerator {

    private static final AtomicInteger serialNo = new AtomicInteger(0);
    private static final int SERIAL_MAX = 100;

    /**
     * 메시지 ID를 생성합니다.
     *
     * @param procNo 프로세스 번호 (0~999)
     * @return 메시지 ID (int 형식)
     */
    public static String getMsgId(int procNo) {
        if (procNo < 0 || procNo > 999) {
            throw new IllegalArgumentException("procNo must be between 0 and 999");
        }

        int serial = serialNo.getAndUpdate(current -> (current + 1) % SERIAL_MAX);

        LocalTime now = LocalTime.now();
        int secondsOfDay = now.toSecondOfDay();  // 하루 기준 초

        // 예: procNo=1, secondsOfDay=43250, serial=3 → "0014325003"
        String composed = String.format("%03d%05d%02d", procNo, secondsOfDay, serial);
        long decimalValue = Long.parseLong(composed);
        String hexValue = Long.toHexString(decimalValue).toUpperCase();

        return hexValue; // 주의: 값이 2147483647(int 범위) 초과 시 예외 발생 가능
    }
}
