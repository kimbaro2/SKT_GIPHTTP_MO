package com.infra.mo.skt_giphttp_mo.config.threadPool

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.SpcodeEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.SpcodeRepository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/*TODO 캐시메모리의 READ/WRITE를 위한 스레드풀을 구성합니다.*/
/*CFG_SPCODE 테이블 데이터를 메모리 캐시로 관리*/
@Component("SpcodeMap_ThreadPool")
@Transactional
open class SpcodeMap_ThreadPool(
        @Qualifier("SpcodeMap") private val spcodeMap: ConcurrentHashMap<String, SpcodeEntity>,
        private val context: ApplicationContext,
        private val spcodeRepository: SpcodeRepository
) {
    private val log: Logger = LoggerFactory.getLogger(SpcodeMap_ThreadPool::class.java)

    /**
     * TODO Enqueue All 시뮬레이션 및 비지니스 로직을 수행합니다. CFG_SPCODE 테이블의 모든 데이터를 메모리 캐시로 로드합니다. 키 형식: CID (예:
     * "1571999882")
     */
    @Transactional(readOnly = true)
    open fun executeEventHandlerTask(poolSize: Int) {
        val dispatcher = Executors.newFixedThreadPool(poolSize).asCoroutineDispatcher()
        val coroutineCacheCheckPool = CoroutineScope(dispatcher)
        val isActive = true

        repeat(poolSize) { index ->
            coroutineCacheCheckPool.launch {
                log.info("🔹[SpcodeMap Worker-$index] 쓰레드 시작 - 이름: ${Thread.currentThread().name}")

                while (isActive) {
                    try {
                        // CFG_SPCODE 테이블의 모든 데이터 조회
                        val entities = spcodeRepository.findAll()

                        // CID 단일 키로 맵 구성 (CID는 고유값)
                        val newSpcodeMap = ConcurrentHashMap<String, SpcodeEntity>()
                        entities.forEach { entity ->
                            val key = entity.cid
                            newSpcodeMap[key] = entity
                        }

                        // 기존 캐시 클리어 후 새 데이터로 교체
                        spcodeMap.clear()
                        spcodeMap.putAll(newSpcodeMap)

                        log.info("CFG_SPCODE 캐시 갱신 완료(CID-Only): ${newSpcodeMap.size}개")
                    } catch (e: Exception) {
                        log.error(
                                "🔹[SpcodeMap Worker-$index] 쓰레드 에러 발생 - 이름: ${Thread.currentThread().name}",
                                e
                        )
                    }
                    /*데이터 컨버팅*/
                    delay(300000L) // 5분마다 갱신 (300초)
                }
            }
        }
    }
}
