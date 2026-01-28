package com.infra.mo.skt_giphttp_mo.config.application

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "witcom.mo")
open class MoServiceProperties {
    var domain: Domain = Domain()

    open class Domain {
        var notiPlus: Set<Int> = emptySet()
        var roaming: Set<Int> = emptySet()
        var normal: Set<Int> = emptySet()
    }
}
