package com.infra.mo.skt_giphttp_mo.service.handler

import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.BIZ_NUMBER_CDMA_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.BIZ_NUMBER_GSM_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.CDMA_ROAMING
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.FORWARD_CDMA_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.FORWARD_GSM_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.GSM_WCDMA_ROAMING
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NORMAL_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NOTI_PLUS_NORMAL_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NOTI_PLUS_PORTED_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NUMBER_PLUS_CDMA_ROAMING
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NUMBER_PLUS_GSM_ROAMING
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.PORTED_CDMA_ROAMING
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.PORTED_GSM_WCDMA_ROAMING
import org.springframework.stereotype.Component

/**
 * EsmClass 처리 핸들러
 * C 코드 참고: GIPEVENT_c.c LINE 1607, 2236
 */
@Component
class EsmClassHandler {
    
    /**
     * QITEM에서 EsmClass 값 추출
     * C 코드: nRsv4Protocol[11]
     */
    fun getEsmClass(qItem: QITEM): Int {
        return qItem.nRsv4Protocol[11].toInt()
    }
    
    /**
     * NOTI_PLUS 여부 확인 (C 오리지널 기준: 2종)
     * C 코드: GIPEVENT_c.c LINE 1607, 2236
     * 
     * @param esmClass EsmClass 값
     * @return true: NOTI_PLUS(20/21), false: 그 외(예: esm_class=1 포함)
     */
    fun isNotiPlus(esmClass: Int): Boolean {
        return esmClass == NOTI_PLUS_NORMAL_MO ||
            esmClass == NOTI_PLUS_PORTED_MO
    }
    
    /**
     * QITEM에서 NOTI_PLUS 여부 확인
     */
    fun isNotiPlus(qItem: QITEM): Boolean {
        return isNotiPlus(getEsmClass(qItem))
    }
    
    /**
     * MO 전송 시 INSERT 테이블 결정
     * C 코드: GIPEVENT_c.c LINE 1607-1610
     * 
     * @param esmClass EsmClass 값
     * @return true: InsertMO_NOTISEND, false: InsertGIPMOCallInfo
     */
    fun shouldInsertMO_NOTISEND(esmClass: Int): Boolean {
        return isNotiPlus(esmClass)
    }
    
    /**
     * TR 수신 시 UPDATE 테이블 결정
     * C 코드: GIPEVENT_c.c LINE 2236-2242
     * 
     * @param esmClass EsmClass 값
     * @return true: UpdateMO_NOTISEND, false: UpdateGIPMOCallInfo
     */
    fun shouldUpdateMO_NOTISEND(esmClass: Int): Boolean {
        return isNotiPlus(esmClass)
    }
    
    /**
     * CID 2580/6381 + ESMCLASS 1 (NORMAL_MO) 조합 확인
     * C 코드: 서드파티 일반 MO 처리 시 bprintf 및 특정 InsqStat 스킵
     * 
     * @param esmClass EsmClass 값
     * @param cid CID 값 (null 또는 빈 문자열 가능)
     * @return true: CID 2580/6381 + ESMCLASS 1 조합, false: 그 외
     */
    fun isNormalMoWithSpecialCid(esmClass: Int, cid: String?): Boolean {
        if (cid.isNullOrEmpty()) return false
        return esmClass == NORMAL_MO && 
               (cid.startsWith("2580") || cid.startsWith("6381"))
    }
    
    /**
     * CID 1584 + ESMCLASS 36 (CDMA_ROAMING) 조합 확인
     * C 코드: CDMA 로밍 처리 시 직접 VSTAT 27 기록
     * 
     * @param esmClass EsmClass 값
     * @param cid CID 값 (null 또는 빈 문자열 가능)
     * @return true: CID 1584 + ESMCLASS 36 조합, false: 그 외
     */
    fun isCdmaRoamingWithCid1584(esmClass: Int, cid: String?): Boolean {
        if (cid.isNullOrEmpty()) return false
        return esmClass == CDMA_ROAMING && cid.startsWith("1584")
    }
    
    /**
     * 로밍 ESMCLASS 여부 확인 (전체 로밍 타입)
     * C 코드: 로밍 ID 설정 및 로밍 처리 분기용
     * 
     * @param esmClass EsmClass 값
     * @return true: 로밍 ESMCLASS, false: 그 외
     */
    fun isRoamingEsmClass(esmClass: Int): Boolean {
        return esmClass in setOf(
            CDMA_ROAMING,                   // 36
            PORTED_CDMA_ROAMING,            // 37
            GSM_WCDMA_ROAMING,              // 40
            PORTED_GSM_WCDMA_ROAMING,       // 41
            FORWARD_CDMA_ROAMING_MO,        // 38
            FORWARD_GSM_ROAMING_MO,         // 42
            NUMBER_PLUS_CDMA_ROAMING,       // 69
            NUMBER_PLUS_GSM_ROAMING,        // 70
            BIZ_NUMBER_CDMA_ROAMING_MO,     // 72
            BIZ_NUMBER_GSM_ROAMING_MO       // 73
        )
    }
}



