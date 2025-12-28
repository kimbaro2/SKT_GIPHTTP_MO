package com.infra.mo.skt_giphttp_mo.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoField

object TraceUtil {
    @Volatile
    private var l_nSerial = 0

    /**
     * 메시지 구분을 위한 TRACE ID 생성
     * @param nServerId 장비 ID (ex: HSMSS: 10~49, VSMSS: 50~99)
     * @param nMsgType 메시지 타입 (ex: MT:1, MO:2, 내부생성:0)
     * @return 생성된 TRACE ID 문자열 (20자리)
     * 
     * 형식: 서버ID(2) + 메시지타입(1) + 년(2) + 월(2) + 일(2) + 시(2) + 분(2) + 초(2) + 밀리초(3) + 시리얼(2) = 20자리
     */
    fun createTraceId(nServerId: Int, nMsgType: Int): String {
        val now = LocalDateTime.now(ZoneId.systemDefault())

        synchronized(this) {
            if (l_nSerial > 99) {
                l_nSerial = 0
            }
            l_nSerial %= 100
        }

        val year = now.year - 2000
        val month = now.monthValue
        val day = now.dayOfMonth
        val hour = now.hour
        val min = now.minute
        val sec = now.second
        val milli = now.get(ChronoField.MILLI_OF_SECOND)

        val serial = synchronized(this) {
            val current = l_nSerial
            l_nSerial++
            current
        }

        return String.format(
            "%02d%01d%02d%02d%02d%02d%02d%02d%03d%02d",
            nServerId, nMsgType,
            year, month, day, hour, min, sec,
            milli, serial
        )
    }

    fun ltrim(str: String?): String {
        if (str == null || str.isEmpty()) return str ?: ""

        var i = 0
        while (i < str.length && isWhitespace(str[i])) {
            i++
        }

        return str.substring(i)
    }

    fun rtrim(str: String?): String {
        if (str == null || str.isEmpty()) return str ?: ""

        var i = str.length - 1
        while (i >= 0 && isWhitespace(str[i])) {
            i--
        }

        return str.substring(0, i + 1)
    }

    fun trim(str: String?): String {
        return ltrim(rtrim(str))
    }

    private fun isWhitespace(x: Char): Boolean {
        return x == ' ' || x == '\t' || x == '\r' || x == '\n'
    }
}

