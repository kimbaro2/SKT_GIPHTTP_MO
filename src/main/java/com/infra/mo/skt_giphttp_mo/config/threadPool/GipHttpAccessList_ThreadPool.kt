package com.infra.mo.skt_giphttp_mo.config.threadPool

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.CfgEtcEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpAccessEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.CfgEtcRepository
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.GipHttpAccessRepository
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
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.stream.Collectors


/*TODO 캐시메모리의 READ/WRITE를 위한 스레드풀을 구성합니다.*/
/*나중에 여기서 CLibrary 접근로직 구성하자*/
@Component("GipHttpAccessList_ThreadPool")
@Transactional
open class GipHttpAccessList_ThreadPool(
    @Qualifier("GipHttpAccessList") private val gipHttpAccessList: CopyOnWriteArrayList<GipHttpAccessEntity>,
    private val context: ApplicationContext,
    private val gipHttpAccessRepository: GipHttpAccessRepository
) {
    private val log: Logger = LoggerFactory.getLogger(GipHttpAccessList_ThreadPool::class.java)

    /**
     * TODO Enqueue All 시뮬레이션 및 비지니스 로직을 수행합니다.
     * */
    @Transactional(readOnly = true)
    open fun executeEventHandlerTask(poolSize: Int) {
        val dispatcher = Executors.newFixedThreadPool(poolSize).asCoroutineDispatcher(); //코루틴 초기화
        val coroutineCacheCheckPool = CoroutineScope(dispatcher)
        val isActive = true

        repeat(poolSize) { index ->
            coroutineCacheCheckPool.launch {
                log.info("🔹[Worker-$index] 쓰레드 시작 - 이름: ${Thread.currentThread().name}")

                while (isActive) {
                    try {
                        // 일반 쿼리 사용 (List 반환)
                        val gipHttpAccessToEntityList = gipHttpAccessRepository.findAllEntity()
//                        val gipHttpAccessToEntityList = gipHttpAccessRepository.findAllGroupByCid()
                        if (gipHttpAccessToEntityList.isPresent) {
                            gipHttpAccessList.clear()
                            gipHttpAccessList.addAll(gipHttpAccessToEntityList.get())
                        }

                        log.info("UPDATE ---> {}", gipHttpAccessToEntityList.toString())
                    } catch (e: Exception) {
                        log.error("🔹[Worker-$index] 쓰레드 에러 발생 - 이름: ${Thread.currentThread().name}", e)
                    }
                    /*데이터 컨버팅*/
                    delay(300000L)
                }
            }
        }
    }
}