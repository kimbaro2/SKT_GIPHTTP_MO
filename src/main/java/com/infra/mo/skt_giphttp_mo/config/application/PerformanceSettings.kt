package com.infra.mo.skt_giphttp_mo.config.application

import lombok.ToString
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

/*TODO properties에 명시한 prefix 를 매핑합니다.*/

@Configuration
@ConfigurationProperties(prefix = "witcom.performance")
@ToString
open class PerformanceSettings {
    var per = 0
    var par80 = 80
    var loop = false
    var cLibraryFilePath = ""
    var cLibPath = ""
    var cLibFileName = ""
    var validMobilePrefixes: Set<String>? = null
    var moduleName = ""
    var pFilePath = ""
    var cFilePath = ""
    var cacheLoop = false
}