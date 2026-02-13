package com.infra.mo.skt_giphttp_mo.service.handler

import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.BIZ_NUMBER_CDMA_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.BIZ_NUMBER_GSM_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.CDMA_ROAMING
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.DOUBLE_FORWARD_TR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.FORWARD_CDMA_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.FORWARD_GSM_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.FORWARD_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.FORWARD_MT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.FORWARD_PORTED_MT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.FORWARD_TR
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.GSM_WCDMA_ROAMING
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.KTF_2G_NOTI_PLUS_PORTED_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.KTF_2G_PORTED_CDMA_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.KTF_2G_PORTED_GSM_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.KTF_3G_NOTI_PLUS_PORTED_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.KTF_3G_PORTED_CDMA_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.KTF_3G_PORTED_GSM_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LGT_2G_NOTI_PLUS_PORTED_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LGT_2G_PORTED_CDMA_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LGT_2G_PORTED_GSM_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LGT_3G_NOTI_PLUS_PORTED_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LGT_3G_PORTED_CDMA_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LGT_3G_PORTED_GSM_ROAMING_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NOTI_NORMAL_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NOTI_PLUS_NORMAL_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NOTI_PLUS_PORTED_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NOTI_PLUS_PORTED_MT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.KTF_2G_NOTI_PORTED_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.KTF_3G_NOTI_PORTED_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LGT_2G_NOTI_PORTED_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LGT_3G_NOTI_PORTED_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NOTI_PORTED_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NUMBER_PLUS_CDMA_ROAMING
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.NUMBER_PLUS_GSM_ROAMING
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.PORTED_CDMA_ROAMING
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.PORTED_GSM_WCDMA_ROAMING
import org.springframework.stereotype.Component

/**
 * ESMClass + 번호규칙(불변성규칙)으로 서비스 타입 결정.
 *
 * 5.1 번호규칙(특번)은 모든 도메인에 기본 적용: destCID 체크(1584/638/2580/#)를
 * ESMClass와 관계없이 먼저 수행하고, 매칭 시 해당 서비스 타입 반환.
 * 매칭되지 않으면 ESMClass 기준으로 도메인(안심문자/등기문자/로밍/착신전환/일반) 결정.
 *
 * 발생가능 ESMClass 규칙: .cursor/rules/ESMClass_발생가능_규칙.mdc
 */
@Component
class MoServiceTypeResolver(
    private val esmClassHandler: EsmClassHandler
) {

    /** CDMA 관련 (로밍 포함): 36,37,38,16,12,17,14,69,72 */
    private val cdmaRoamingEsmClasses = setOf(
        CDMA_ROAMING,                          // 36
        PORTED_CDMA_ROAMING,                    // 37
        FORWARD_CDMA_ROAMING_MO,                // 38
        KTF_2G_PORTED_CDMA_ROAMING_MO,          // 16
        KTF_3G_PORTED_CDMA_ROAMING_MO,          // 12
        LGT_2G_PORTED_CDMA_ROAMING_MO,          // 17
        LGT_3G_PORTED_CDMA_ROAMING_MO,          // 14
        NUMBER_PLUS_CDMA_ROAMING,               // 69
        BIZ_NUMBER_CDMA_ROAMING_MO              // 72
    )

    /** GSM 관련 (로밍 포함): 40,41,42,18,13,19,15,70,73 */
    private val gsmRoamingEsmClasses = setOf(
        GSM_WCDMA_ROAMING,                      // 40
        PORTED_GSM_WCDMA_ROAMING,               // 41
        FORWARD_GSM_ROAMING_MO,                 // 42
        KTF_2G_PORTED_GSM_ROAMING_MO,           // 18
        KTF_3G_PORTED_GSM_ROAMING_MO,           // 13
        LGT_2G_PORTED_GSM_ROAMING_MO,           // 19
        LGT_3G_PORTED_GSM_ROAMING_MO,           // 15
        NUMBER_PLUS_GSM_ROAMING,                // 70
        BIZ_NUMBER_GSM_ROAMING_MO               // 73
    )

    /** 안심문자 관련: 20,21 (도메인 기준 고정) */
    private val notiPlusEsmClasses = setOf(
        NOTI_PLUS_NORMAL_MO,                   // 20
        NOTI_PLUS_PORTED_MO                    // 21
    )

    /** 등기문자(NOTI) 관련: 90,91 (도메인 기준 고정) */
    private val notiRegisteredEsmClasses = setOf(
        NOTI_NORMAL_MO,                       // 90
        NOTI_PORTED_MO                        // 91
    )

    /** 착신전환 관련 (CDMA/GSM 로밍 포함 착신전환은 로밍 항목에 포함): 48,49,50,52,53 */
    private val forwardEsmClasses = setOf(
        FORWARD_MO,                           // 48
        FORWARD_MT,                           // 49
        FORWARD_TR,                           // 50
        FORWARD_PORTED_MT,                    // 52
        DOUBLE_FORWARD_TR                     // 53
    )

    /**
     * ESMClass와 번호규칙(CID 등)으로 서비스 타입 결정.
     * 5.1 번호규칙(특번): 모든 도메인에 기본 적용 — destCID 체크를 먼저 수행.
     *
     * @param esmClass QITEM.nRsv4Protocol[11]
     * @param destCID 착신 CID (번호규칙: 1584/638/2580/# — 모든 도메인 공통 적용)
     * @return 서비스 타입
     */
    fun resolve(esmClass: Int, destCID: String?): MoServiceType {
        // 0) ESMClass 우선 규칙: ESMClass 20(NOTI_PLUS_NORMAL_MO)은 착신번호 규칙과 무관하게 안심문자 도메인으로 고정
        if (esmClass == NOTI_PLUS_NORMAL_MO) {
            return MoServiceType.NOTI_PLUS
        }

        // 1) 5.1 번호규칙(특번) — 모든 도메인에 기본 적용: destCID로 1584/638/2580/# 구분
        if (!destCID.isNullOrEmpty()) {
            when {
                destCID.startsWith("1584") -> return MoServiceType.CHARACTER_1584
                destCID.startsWith("638") -> return MoServiceType.SMS_MANAGER_638
                destCID.startsWith("2580") -> return MoServiceType.SMS_MESSENGER_2580
                destCID.contains("#") -> return MoServiceType.SPECIAL_SHARP  // 등기: 번호 뒤에 # 붙은 경우
            }
        }

        // 2) ESMClass 검증 → 해당 클래스 진입: 안심문자(20~26), 등기문자(90~95)
        when {
            esmClass in notiPlusEsmClasses -> return MoServiceType.NOTI_PLUS
            esmClass in notiRegisteredEsmClasses -> return MoServiceType.NOTI_REGISTERED
        }

        // 3) ESMClass 검증 → 해당 클래스 진입: 로밍
        when {
            esmClass in cdmaRoamingEsmClasses -> return MoServiceType.CDMA_ROAMING
            esmClass in gsmRoamingEsmClasses -> return MoServiceType.GSM_ROAMING
        }

        // 4) ESMClass 검증 → 해당 클래스 진입: 착신전환 (48,49,50,52,53)
        when {
            esmClass in forwardEsmClasses -> return MoServiceType.FORWARD
        }

        // 5) 그 외: 일반 MO (특번 미매칭, ESMClass 기반 도메인도 아님)
        return MoServiceType.NORMAL_MO
    }
}
