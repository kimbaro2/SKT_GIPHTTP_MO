package com.infra.mo.skt_giphttp_mo.utils;

import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import ch.qos.logback.classic.Level;

@Component
public class MsgIdCenterGenerator {

    // IP-PORT별 개별 카운터 관리
    private static final Map<String, AtomicInteger> ipPortCounters = new ConcurrentHashMap<>();

    // msgIdCacheMap 주입
    @Autowired
    @Qualifier("MsgIdCacheMap")
    private ConcurrentHashMap<String, String> msgIdCacheMap;

    @Autowired
    private WitcomLog witcomLog;


    /**
     * IP-PORT별로 개별 카운터를 사용하여 MsgId 생성 (msgIdCacheMap 활용)
     */
    public String generateMsgIdCenterWithTerminator(String cacheKey, int procNo) {
        // msgIdCacheMap에서 기존 MsgId 확인
        String cachedMsgId = msgIdCacheMap.get(cacheKey);

        if (cachedMsgId != null) {
            // 캐시된 MsgId가 있으면 카운터 증가
            AtomicInteger gMsgId = ipPortCounters.computeIfAbsent(cacheKey, k -> new AtomicInteger(0));
            gMsgId.incrementAndGet();

            // C: 범위 체크
            int maxMsgId = ((procNo + 1) * 1000000) - 1;
            if (gMsgId.get() > maxMsgId) {
                gMsgId.set(procNo * 1000000 + 1);
            }

            String newMsgId = String.format("%08X", gMsgId.get());
            msgIdCacheMap.put(cacheKey, newMsgId);
            witcomLog.p_write(Level.INFO,
                    String.format("🔹 MsgID 갱신 (캐시 사용): %s -> %s (CacheKey: %s, ProcNo: %d)", cachedMsgId, newMsgId, cacheKey, procNo));
            return newMsgId;
        } else {
            // 최초 생성: C 로직 실행
            AtomicInteger gMsgId = ipPortCounters.computeIfAbsent(cacheKey, k -> new AtomicInteger(0));

            // C: nProcNo >= 1000 처리
            if (procNo >= 1000) {
                String temp = String.valueOf(procNo);
                procNo = Integer.parseInt(temp.substring(1));
            }

            // C: current_sec 계산
            Calendar cal = Calendar.getInstance();
            int currentSec = cal.get(Calendar.SECOND) +
                    (cal.get(Calendar.MINUTE) * 60) +
                    (cal.get(Calendar.HOUR_OF_DAY) * 3600);

            // C: 초기값 생성
            String currentSecStr;
            if (currentSec >= 10000) {
                String tmp = String.valueOf(currentSec);
                currentSecStr = tmp.substring(1);
            } else {
                currentSecStr = String.format("%04d", currentSec);
            }

            String msgIdStr = String.format("%03d%s%02d", procNo, currentSecStr, 1);
            int initialMsgId = Integer.parseInt(msgIdStr);
            gMsgId.set(initialMsgId);

            // C: %08X 포맷
            String msgId = String.format("%08X", gMsgId.get());
            msgIdCacheMap.put(cacheKey, msgId);
            witcomLog.p_write(Level.INFO,
                    String.format("🔹 MsgID 생성 (최초): %s (CacheKey: %s, ProcNo: %d)", msgId, cacheKey, procNo));
            return msgId;
        }
    }

}


