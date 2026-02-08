package com.infra.mo.skt_giphttp_mo.service.impl

import ch.qos.logback.classic.Level
import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.GipHttpMoAccessRepository
import com.infra.mo.skt_giphttp_mo.service.Mo1584EntityResolveService
import com.infra.mo.skt_giphttp_mo.service.MoRegisteredServerIpService
import org.springframework.stereotype.Service

/**
 * 로밍(CDMA/GSM) + 1584 목적지일 때 DB 등록 IP로 entity(LOG_NO, MOTRBILL) 재조회.
 * CdmaRoamingHandler, GsmRoamingHandler 내부에서만 호출.
 */
@Service
class Mo1584EntityResolveServiceImpl(
    private val moRegisteredServerIpService: MoRegisteredServerIpService,
    private val gipHttpMoAccessRepository: GipHttpMoAccessRepository
) : Mo1584EntityResolveService {

    override fun resolveEntityFor1584(
        destCID: String,
        currentEntity: GipHttpMoAccessEntity,
        queueNo: Int,
        loggerName: String,
        workerThreadId: Long,
        witcomLog: WitcomLog
    ): GipHttpMoAccessEntity {
        if (!destCID.startsWith("1584")) return currentEntity

        val registeredServerIp = moRegisteredServerIpService.findRegisteredServerIp() ?: run {
            witcomLog.c_write(
                loggerName,
                Level.INFO,
                String.format(
                    "[1584 entity 재조회] 등록된 서버 IP를 찾을 수 없음: DestCID(%s), QueueNo(%d), 기존 LogNo(%s) 사용",
                    destCID,
                    queueNo,
                    currentEntity.logNo
                ),
                workerThreadId
            )
            return currentEntity
        }

        val matchedEntity = try {
            gipHttpMoAccessRepository.findByCidAndIpAddrAndPortNo(
                destCID,
                registeredServerIp,
                currentEntity.portNo
            ).orElse(null)
        } catch (e: Exception) {
            try {
                gipHttpMoAccessRepository.findByCidAndIpAddr(destCID, registeredServerIp).orElse(null)
            } catch (e2: Exception) {
                gipHttpMoAccessRepository.findByCidAndIpAddr(destCID, registeredServerIp).orElse(null)
            }
        }

        return when (matchedEntity) {
            null -> {
                witcomLog.c_write(
                    loggerName,
                    Level.INFO,
                    String.format(
                        "[1584 entity 재조회] DB 등록 IP로 레코드를 찾지 못함: DestCID(%s), QueueNo(%d), RegisteredIP(%s), 기존 LogNo(%s) 사용",
                        destCID,
                        queueNo,
                        registeredServerIp,
                        currentEntity.logNo
                    ),
                    workerThreadId
                )
                currentEntity
            }
            else -> {
                witcomLog.c_write(
                    loggerName,
                    Level.INFO,
                    String.format(
                        "[1584 entity 재조회] DB 등록 IP로 정확한 LOG_NO 찾음: DestCID(%s), QueueNo(%d), RegisteredIP(%s), LogNo(%s), MOTRBILL(%d)",
                        destCID,
                        queueNo,
                        registeredServerIp,
                        matchedEntity.logNo,
                        matchedEntity.moTrBill ?: 0
                    ),
                    workerThreadId
                )
                matchedEntity
            }
        }
    }
}
