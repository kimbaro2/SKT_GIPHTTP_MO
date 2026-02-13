package com.infra.mo.skt_giphttp_mo.service.impl

import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.ERRORID_CP_MO_FAIL
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LT_BOTH
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MESSAGE_MO
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.MODULEID_GIPEVENT_C
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.SERVICEID_GIPEVENT
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.TID_NO_SAVE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.LT_TRACE
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef.TID_SAVE
import com.infra.mo.skt_giphttp_mo.dto.jna.TraceDef.ST_GIP_SOCK_SEND_FAIL
import com.infra.mo.skt_giphttp_mo.service.MoSendInsqStatService
import com.infra.mo.skt_giphttp_mo.service.mo.MoServiceContext
import org.springframework.stereotype.Service

/**
 * MO CP 전송 실패 시 InsqStat 호출 공통 구현.
 * 모든 도메인에서 requeue 후 동일한 실패 통계를 기록한다.
 */
@Service
class MoSendInsqStatServiceImpl : MoSendInsqStatService {

    override fun recordMoFailedInsqStat(qItem: QITEM, context: MoServiceContext) {
        val smsQLib = context.smsQLib ?: return
        val gServerID = context.gServerID ?: return
        val nInforNo = qItem.usSource
        smsQLib.InsqStat(
            qItem,
            MESSAGE_MO,
            0,
            gServerID,
            MODULEID_GIPEVENT_C,
            SERVICEID_GIPEVENT,
            ERRORID_CP_MO_FAIL,
            ST_GIP_SOCK_SEND_FAIL,
            nInforNo,
            TID_SAVE,
            LT_BOTH,
            0
        )
    }
}
