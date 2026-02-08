package com.infra.mo.skt_giphttp_mo.service.impl

import ch.qos.logback.classic.Level
import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.MsgLimitListRepository
import com.infra.mo.skt_giphttp_mo.dto.jna.SMReqTransResult
import com.infra.mo.skt_giphttp_mo.service.MoLimitCheckService
import com.infra.mo.skt_giphttp_mo.utils.LimitCheckFlags
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

/**
 * MO 한도 체크 서비스 구현.
 * MOThreadPool 공통 호출 함수(checkLimitMO, checkLimitGIVE)를 서비스 내부에서 사용할 수 있도록 의존성만 주입.
 */
@Service
class MoLimitCheckServiceImpl(
    @Qualifier("LimitCheckFlagsMap") private val limitCheckFlagsMap: ConcurrentHashMap<String, LimitCheckFlags>,
    private val msgLimitListRepository: MsgLimitListRepository,
    private val witcomLog: WitcomLog
) : MoLimitCheckService {

    override fun checkLimitMO(msgHdr: SMReqTransResult, entity: GipHttpMoAccessEntity): Boolean {
        val limitFlags = limitCheckFlagsMap[entity.logNo] ?: LimitCheckFlags.from(entity)
        if (!limitFlags.limitMO) return false
        val szMdn = "${msgHdr.srcCid}${msgHdr.srcMinNo}"
        val loggerName = "${entity.cid}-${entity.ipAddr}-${entity.portNo}"
        return try {
            msgLimitListRepository.countByMdn(szMdn) > 0L
        } catch (e: Exception) {
            witcomLog.c_write(loggerName, Level.INFO, String.format("CheckLimitMO() DB Error: MDN(%s)", szMdn), Thread.currentThread().id)
            false
        }
    }

    override fun checkLimitGIVE(msgHdr: SMReqTransResult, entity: GipHttpMoAccessEntity): Boolean {
        val limitFlags = limitCheckFlagsMap[entity.logNo] ?: LimitCheckFlags.from(entity)
        if (!limitFlags.limitGIVE) return false
        val srcCallNoInt = msgHdr.srcMinNo.toIntOrNull() ?: 0
        val szMdn = "0$srcCallNoInt"
        val loggerName = "${entity.cid}-${entity.ipAddr}-${entity.portNo}"
        return try {
            msgLimitListRepository.countByMdn(szMdn) > 0L
        } catch (e: Exception) {
            witcomLog.c_write(loggerName, Level.INFO, String.format("CheckLimitGIVE() DB Error: MDN(%s)", szMdn), Thread.currentThread().id)
            false
        }
    }
}
