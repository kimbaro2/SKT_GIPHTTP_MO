package com.infra.mo.skt_giphttp_mo.service.impl

import ch.qos.logback.classic.Level
import com.infra.mo.skt_giphttp_mo.config.application.WitcomLog
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.GipHttpMoAccessRepository
import com.infra.mo.skt_giphttp_mo.service.MoRegisteredServerIpService
import org.springframework.stereotype.Service
import java.net.Inet4Address
import java.net.NetworkInterface

/**
 * 등록된 서버 IP 조회 서비스 구현.
 * MOThreadPool 공통 호출 함수(findRegisteredServerIp)를 서비스 내부에서 사용할 수 있도록 의존성만 주입.
 */
@Service
class MoRegisteredServerIpServiceImpl(
    private val gipHttpMoAccessRepository: GipHttpMoAccessRepository,
    private val witcomLog: WitcomLog
) : MoRegisteredServerIpService {

    @Volatile
    private var cachedRegisteredServerIp: String? = null

    override fun findRegisteredServerIp(): String? {
        if (cachedRegisteredServerIp != null) return cachedRegisteredServerIp
        try {
            val networkInterfaceIps = mutableListOf<String>()
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val addresses = networkInterfaces.nextElement().inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address is Inet4Address) {
                        networkInterfaceIps.add(address.hostAddress)
                    }
                }
            }
            if (networkInterfaceIps.isEmpty()) {
                witcomLog.p_write(Level.INFO, "[findRegisteredServerIp] 네트워크 인터페이스 IP를 찾을 수 없습니다.")
                return null
            }
            val registeredIps = gipHttpMoAccessRepository.findAllEntity()
                .orElse(emptyList())
                .map { it.ipAddr }
                .distinct()
                .filterNotNull()
            if (registeredIps.isEmpty()) {
                witcomLog.p_write(Level.INFO, "[findRegisteredServerIp] HTTP_MOSEND_ACCESS 테이블에 등록된 IP가 없습니다.")
                return null
            }
            val matchedIp = networkInterfaceIps.firstOrNull { registeredIps.contains(it) }
            if (matchedIp != null) {
                cachedRegisteredServerIp = matchedIp
                witcomLog.p_write(Level.INFO, "[findRegisteredServerIp] 등록된 서버 IP 찾음: $matchedIp")
                return matchedIp
            }
            witcomLog.p_write(Level.INFO, "[findRegisteredServerIp] 네트워크 IP 중 DB 등록 IP를 찾을 수 없습니다.")
            return null
        } catch (e: Exception) {
            witcomLog.p_write(Level.INFO, "[findRegisteredServerIp] 오류: ${e.message}")
            return null
        }
    }
}
