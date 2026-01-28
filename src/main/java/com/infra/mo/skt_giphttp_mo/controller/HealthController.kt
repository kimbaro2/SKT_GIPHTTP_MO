package com.infra.mo.skt_giphttp_mo.controller

import com.infra.mo.skt_giphttp_mo.config.http.DualPortConfig
import com.infra.mo.skt_giphttp_mo.config.threadPool.MOThreadPool
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 헬스체크 엔드포인트
 * - HTTP/HTTPS 서버 상태 확인
 * - 스레드풀 상태 확인
 */
@RestController
@RequestMapping("/health")
class HealthController(
    private val dualPortConfig: DualPortConfig,
    private val moThreadPool: MOThreadPool
) {
    /**
     * 전체 시스템 상태 확인
     */
    @GetMapping
    fun healthCheck(): ResponseEntity<Map<String, Any>> {
        val serverStatus = dualPortConfig.getServerStatus()
        val threadPoolStatus = moThreadPool.getThreadPoolStatus()

        val overallHealthy = serverStatus.httpServerRunning &&
                            (serverStatus.httpsServerRunning || !serverStatus.sslEnabled) &&
                            threadPoolStatus.healthy

        val response = mapOf(
            "status" to if (overallHealthy) "healthy" else "unhealthy",
            "servers" to mapOf(
                "http" to mapOf(
                    "port" to serverStatus.httpPort,
                    "running" to serverStatus.httpServerRunning,
                    "status" to if (serverStatus.httpServerRunning) "UP" else "DOWN"
                ),
                "https" to mapOf(
                    "port" to serverStatus.httpsPort,
                    "running" to serverStatus.httpsServerRunning,
                    "sslEnabled" to serverStatus.sslEnabled,
                    "status" to when {
                        !serverStatus.sslEnabled -> "DISABLED"
                        serverStatus.httpsServerRunning -> "UP"
                        else -> "DOWN"
                    }
                )
            ),
            "threadPool" to mapOf(
                "active" to threadPoolStatus.active,
                "workerCount" to threadPoolStatus.workerCount,
                "lastDequeueTime" to threadPoolStatus.lastDequeueTime,
                "timeSinceLastDequeue" to threadPoolStatus.timeSinceLastDequeue,
                "coroutineScopeActive" to threadPoolStatus.coroutineScopeActive,
                "healthy" to threadPoolStatus.healthy,
                "status" to when {
                    !threadPoolStatus.active -> "INACTIVE"
                    !threadPoolStatus.healthy -> "UNHEALTHY"
                    else -> "UP"
                }
            )
        )

        return ResponseEntity.status(if (overallHealthy) 200 else 503).body(response)
    }

    /**
     * 서버 상태만 확인
     */
    @GetMapping("/servers")
    fun serverStatus(): ResponseEntity<Map<String, Any>> {
        val serverStatus = dualPortConfig.getServerStatus()

        val response = mapOf(
            "http" to mapOf(
                "port" to serverStatus.httpPort,
                "running" to serverStatus.httpServerRunning,
                "status" to if (serverStatus.httpServerRunning) "UP" else "DOWN"
            ),
            "https" to mapOf(
                "port" to serverStatus.httpsPort,
                "running" to serverStatus.httpsServerRunning,
                "sslEnabled" to serverStatus.sslEnabled,
                "status" to when {
                    !serverStatus.sslEnabled -> "DISABLED"
                    serverStatus.httpsServerRunning -> "UP"
                    else -> "DOWN"
                }
            )
        )

        val allRunning = serverStatus.httpServerRunning &&
                        (serverStatus.httpsServerRunning || !serverStatus.sslEnabled)

        return ResponseEntity.status(if (allRunning) 200 else 503).body(response)
    }

    /**
     * 스레드풀 상태만 확인
     */
    @GetMapping("/threadpool")
    fun threadPoolStatus(): ResponseEntity<Map<String, Any>> {
        val threadPoolStatus = moThreadPool.getThreadPoolStatus()

        val response = mapOf(
            "active" to threadPoolStatus.active,
            "workerCount" to threadPoolStatus.workerCount,
            "lastDequeueTime" to threadPoolStatus.lastDequeueTime,
            "timeSinceLastDequeue" to threadPoolStatus.timeSinceLastDequeue,
            "coroutineScopeActive" to threadPoolStatus.coroutineScopeActive,
            "healthy" to threadPoolStatus.healthy,
            "status" to when {
                !threadPoolStatus.active -> "INACTIVE"
                !threadPoolStatus.healthy -> "UNHEALTHY"
                else -> "UP"
            }
        )

        return ResponseEntity.status(if (threadPoolStatus.healthy) 200 else 503).body(response)
    }
}
