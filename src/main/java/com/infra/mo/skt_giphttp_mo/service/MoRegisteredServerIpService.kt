package com.infra.mo.skt_giphttp_mo.service

/**
 * 등록된 서버 IP 조회 (findRegisteredServerIp).
 * 네트워크 인터페이스 IP 중 DB에 등록된 IP를 반환. CDMA 케이스 LOG_NO 조회용.
 * MOThreadPool에서 분리된 공통 호출 함수를 서비스 내부에서 사용할 수 있도록 의존성만 주입.
 */
interface MoRegisteredServerIpService {

    /**
     * 네트워크 인터페이스 IP 중 DB(HTTP_MOSEND_ACCESS)에 등록된 IP 한 개 반환.
     * @return 등록된 IP 또는 null
     */
    fun findRegisteredServerIp(): String?
}
