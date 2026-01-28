package com.infra.mo.skt_giphttp_mo.utils

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity

/**
 * 한도체크 플래그 정보
 * C 코드: GetLimitCheck() 함수의 반환값과 동일한 구조
 * C 코드 참고: GIDBLib.c LINE 5138-5166
 * 
 * @property limitMO MO 한도체크 여부 (BILL_TYPE='2'일 때 true)
 * @property limitMT MT 한도체크 여부 (BILL_TYPE='3'일 때 true)
 * @property limitGIVE GIVE 한도체크 여부 (BILL_TYPE='4'일 때 true)
 */
data class LimitCheckFlags(
    val limitMO: Boolean,
    val limitMT: Boolean,
    val limitGIVE: Boolean
) {
    companion object {
        /**
         * GipHttpMoAccessEntity로부터 LimitCheckFlags 생성
         * 
         * @param entity GipHttpMoAccessEntity
         * @return LimitCheckFlags
         */
        fun from(entity: GipHttpMoAccessEntity): LimitCheckFlags {
            val result = BillTypeUtil.getLimitCheck(
                entity.limitCheckFlag,
                entity.billType
            )
            return LimitCheckFlags(
                limitMO = result.limitMO,
                limitMT = result.limitMT,
                limitGIVE = result.limitGIVE
            )
        }
    }
}
