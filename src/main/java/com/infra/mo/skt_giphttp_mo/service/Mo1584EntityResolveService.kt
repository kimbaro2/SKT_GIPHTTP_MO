package com.infra.mo.skt_giphttp_mo.service

import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity

/**
 * 로밍(CDMA/GSM) ESMClass에 해당하는 핸들러 내부에서만 사용.
 * DestCID가 1584로 시작할 때 DB 등록 IP로 실제 entity(LOG_NO, MOTRBILL) 재조회.
 * ESMClass 선행 후 해당 핸들러에서 호출한다.
 */
interface Mo1584EntityResolveService {

    /**
     * 1584 목적지일 때 DB 등록 IP로 entity 재조회.
     * destCID가 1584로 시작하지 않으면 currentEntity 그대로 반환.
     *
     * @param destCID 착신 CID (1584로 시작 여부만 사용)
     * @param currentEntity 파티션에서 할당된 entity
     * @param queueNo 큐 번호 (로깅용)
     * @param loggerName 로거명
     * @param workerThreadId 워커 스레드 ID
     * @param witcomLog 로그
     * @return 재조회된 entity 또는 currentEntity
     */
    fun resolveEntityFor1584(
        destCID: String,
        currentEntity: GipHttpMoAccessEntity,
        queueNo: Int,
        loggerName: String,
        workerThreadId: Long,
        witcomLog: WitcomLog
    ): GipHttpMoAccessEntity
}
