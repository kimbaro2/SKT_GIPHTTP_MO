package com.infra.mo.skt_giphttp_mo.config.threadPool

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpAccessEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.GipHttpAccessRepository
import com.infra.mo.skt_giphttp_mo.db.altibase.repository.GipHttpMoAccessRepository
import kotlinx.coroutines.*
import javax.annotation.PreDestroy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors


/*TODO 캐시메모리의 READ/WRITE를 위한 스레드풀을 구성합니다.*/
/*나중에 여기서 CLibrary 접근로직 구성하자*/
@Component("GipHttpMoAccessList_ThreadPool")
@Transactional
open class GipHttpMoAccessList_ThreadPool(
    @Qualifier("GipHttpMoAccessList") private val gipHttpMoAccessList: CopyOnWriteArrayList<GipHttpMoAccessEntity>,
    @Qualifier("GipHttpAccessMap") private val gipHttpAccessMap: java.util.concurrent.ConcurrentHashMap<String, GipHttpAccessEntity>,
    private val context: ApplicationContext,
    private val gipHttpMoAccessRepository: GipHttpMoAccessRepository
) {
    private val log: Logger = LoggerFactory.getLogger(GipHttpMoAccessList_ThreadPool::class.java)
    
    // 코루틴 스코프 저장 (애플리케이션 종료 시 취소하기 위함)
    private var coroutineScope: CoroutineScope? = null

    /**
     * TODO Enqueue All 시뮬레이션 및 비지니스 로직을 수행합니다.
     * */
    @Transactional(readOnly = true)
    open fun executeEventHandlerTask(poolSize: Int) {
        val dispatcher = Executors.newFixedThreadPool(poolSize).asCoroutineDispatcher(); //코루틴 초기화
        coroutineScope = CoroutineScope(dispatcher)
        val coroutineCacheCheckPool = coroutineScope!!

        repeat(poolSize) { index ->
            coroutineCacheCheckPool.launch {
                log.info("🔹[Worker-$index] 쓰레드 시작 - 이름: ${Thread.currentThread().name}")

                while (coroutineCacheCheckPool.isActive) {
                    try {
                        // 스코프가 여전히 활성화되어 있는지 확인
                        if (!coroutineCacheCheckPool.isActive) {
                            break
                        }
                        
                        // Spring 컨텍스트가 활성화되어 있는지 확인
                        val isContextActive = if (context is ConfigurableApplicationContext) {
                            context.isActive
                        } else {
                            true // ConfigurableApplicationContext가 아니면 일단 true로 가정
                        }
                        
                        if (!isContextActive) {
                            // 컨텍스트가 닫혔으면 DB 접근 스킵하고 delay만 실행
                            log.warn("🔹[Worker-$index] Spring 컨텍스트가 비활성화됨 - DB 접근 스킵, 계속 실행: ${Thread.currentThread().name}")
                        } else {
                            // 컨텍스트가 활성화되어 있으면 DB 접근
                            // 일반 쿼리 사용 (List 반환)
                            val gipHttpMoAccessToEntityList = gipHttpMoAccessRepository.findAllEntity()
//                            val gipHttpAccessToEntityList = gipHttpAccessRepository.findAllGroupByCid()
                            if (gipHttpMoAccessToEntityList.isPresent) {
                                gipHttpMoAccessList.clear()
                                gipHttpMoAccessList.addAll(gipHttpMoAccessToEntityList.get())
                                
                                // GipHttpAccessMap도 업데이트 (로깅용)
                                gipHttpAccessMap.clear()
                                gipHttpMoAccessToEntityList.get().forEach { moEntity ->
                                    val loggerName = "${moEntity.cid}-${moEntity.ipAddr}-${moEntity.portNo}"
                                    // GipHttpMoAccessEntity를 GipHttpAccessEntity로 변환 (생성자 사용)
                                    val accessEntity = GipHttpAccessEntity(moEntity)
                                    gipHttpAccessMap[loggerName] = accessEntity
                                }
                            }

                            log.info("UPDATE ---> {}", gipHttpMoAccessToEntityList.toString())
                        }
                    } catch (e: CancellationException) {
                        // 코루틴 취소 시 정상 종료
                        log.info("🔹[Worker-$index] 코루틴 취소됨 - 이름: ${Thread.currentThread().name}")
                        break
                    } catch (e: org.springframework.beans.factory.BeanCreationNotAllowedException) {
                        // Spring 컨텍스트가 종료 중일 때 발생하는 예외 - DB 접근 스킵하고 계속 실행
                        log.warn("🔹[Worker-$index] Spring 컨텍스트 종료 중 - DB 접근 스킵, 계속 실행: ${Thread.currentThread().name}")
                    } catch (e: org.springframework.beans.factory.UnsatisfiedDependencyException) {
                        // Spring 컨텍스트가 닫혔을 때 발생하는 예외 - DB 접근 스킵하고 계속 실행
                        val cause = e.cause
                        if (cause is IllegalStateException && cause.message?.contains("has been closed") == true) {
                            log.warn("🔹[Worker-$index] Spring 컨텍스트가 이미 닫혔음 - DB 접근 스킵, 계속 실행: ${Thread.currentThread().name}")
                        } else {
                            log.warn("🔹[Worker-$index] UnsatisfiedDependencyException 발생 - DB 접근 스킵, 계속 실행: ${Thread.currentThread().name}")
                        }
                    } catch (e: IllegalStateException) {
                        // ApplicationContext가 이미 닫혔을 때 발생하는 예외 - DB 접근 스킵하고 계속 실행
                        if (e.message?.contains("has been closed") == true) {
                            log.warn("🔹[Worker-$index] Spring 컨텍스트가 이미 닫혔음 - DB 접근 스킵, 계속 실행: ${Thread.currentThread().name}")
                        } else {
                            log.error("🔹[Worker-$index] 쓰레드 에러 발생 - 이름: ${Thread.currentThread().name}", e)
                        }
                    } catch (e: Exception) {
                        // 기타 예외 중 컨텍스트가 닫혔을 가능성이 있는 경우 확인
                        val cause = e.cause
                        if (cause is IllegalStateException && cause.message?.contains("has been closed") == true) {
                            log.warn("🔹[Worker-$index] Spring 컨텍스트가 이미 닫혔음 (중첩 예외) - DB 접근 스킵, 계속 실행: ${Thread.currentThread().name}")
                        } else {
                            log.error("🔹[Worker-$index] 쓰레드 에러 발생 - 이름: ${Thread.currentThread().name}", e)
                        }
                    }
                    
                    // 스코프가 여전히 활성화되어 있는지 확인 후 delay
                    if (coroutineCacheCheckPool.isActive) {
                        try {
                            delay(30000L)
                        } catch (e: CancellationException) {
                            // delay 중 취소된 경우 정상 종료
                            log.info("🔹[Worker-$index] delay 중 코루틴 취소됨 - 이름: ${Thread.currentThread().name}")
                            break
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Spring 컨텍스트가 닫힐 때 코루틴을 안전하게 종료
     */
    @EventListener
    fun onContextClosed(event: ContextClosedEvent) {
        if (event.applicationContext === context) {
            log.info("GipHttpMoAccessList_ThreadPool: ContextClosedEvent 수신 - 코루틴 안전 종료 시작...")
            safeShutdown()
        }
    }
    
    /**
     * 애플리케이션 종료 시 코루틴 스코프를 안전하게 취소하여 리소스 정리
     */
    @PreDestroy
    fun cleanup() {
        log.info("GipHttpMoAccessList_ThreadPool cleanup: 코루틴 스코프 안전 종료 중...")
        safeShutdown()
    }
    
    /**
     * 코루틴을 안전하게 종료 (취소 후 일정 시간 대기)
     */
    private fun safeShutdown() {
        val scope = coroutineScope
        if (scope != null) {
            try {
                // 코루틴 취소 요청
                scope.cancel()
                log.info("GipHttpMoAccessList_ThreadPool: 코루틴 취소 요청 완료")
                
                // 코루틴이 안전하게 종료될 때까지 최대 5초 대기
                runBlocking {
                    var waitCount = 0
                    while (scope.isActive && waitCount < 50) {
                        delay(100L) // 100ms씩 대기
                        waitCount++
                    }
                    if (scope.isActive) {
                        log.warn("GipHttpMoAccessList_ThreadPool: 코루틴이 5초 내에 종료되지 않음")
                    } else {
                        log.info("GipHttpMoAccessList_ThreadPool: 코루틴 안전 종료 완료")
                    }
                }
            } catch (e: Exception) {
                log.error("GipHttpMoAccessList_ThreadPool: 코루틴 종료 중 예외 발생", e)
            } finally {
                coroutineScope = null
            }
        }
    }
}