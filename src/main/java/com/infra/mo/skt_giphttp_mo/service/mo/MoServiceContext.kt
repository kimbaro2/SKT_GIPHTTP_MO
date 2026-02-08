package com.infra.mo.skt_giphttp_mo.service.mo

import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.CfgEtcEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib

/**
 * MO 서비스 처리 시 공통으로 필요한 컨텍스트.
 * ESMClass 분기부에서 생성 후 서비스별 핸들러에 전달.
 * 로직 이관 시 witcomLog, smsQLib, gServerID, cfgEtcMap, gstQResultObj, moProcessorOps를 채워
 * 핸들러에서 processSMReqSimple 등 연산을 호출할 수 있다.
 */
data class MoServiceContext(
    val destCID: String,
    val esmClass: Int,
    val loggerName: String,
    val workerThreadId: Long,
    val entity: GipHttpMoAccessEntity?,
    val queueNo: Int,
    val gstQItemTrans: Any? = null,
    val witcomLog: WitcomLog? = null,
    val smsQLib: SmsQLib? = null,
    val gServerID: Int? = null,
    val cfgEtcMap: Map<String, CfgEtcEntity>? = null,
    val gstQResultObj: Any? = null,
    val moProcessorOps: IMoProcessorOps? = null,
)
