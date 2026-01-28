package com.infra.mo.skt_giphttp_mo.service.handler

import com.infra.mo.skt_giphttp_mo.config.application.MoServiceProperties
import org.springframework.stereotype.Component

@Component
class EsmClassDomainResolver(
    private val moServiceProperties: MoServiceProperties
) {
    enum class ServiceDomain {
        NOTI_PLUS,
        ROAMING,
        NORMAL,
        OTHER
    }

    fun resolve(esmClass: Int): ServiceDomain {
        return when {
            esmClass in moServiceProperties.domain.notiPlus -> ServiceDomain.NOTI_PLUS
            esmClass in moServiceProperties.domain.roaming -> ServiceDomain.ROAMING
            esmClass in moServiceProperties.domain.normal -> ServiceDomain.NORMAL
            else -> ServiceDomain.OTHER
        }
    }
}
