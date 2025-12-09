package com.infra.mo.skt_giphttp_mo.config.application;

import com.infra.mo.skt_giphttp_mo.config.threadPool.CfgEtcMap_ThreadPool;
import com.infra.mo.skt_giphttp_mo.config.threadPool.GipHttpMoAccessList_ThreadPool;
import com.infra.mo.skt_giphttp_mo.config.threadPool.SENDThreadPool;
import com.infra.mo.skt_giphttp_mo.config.threadPool.SpcodeMap_ThreadPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*TODO 스프링 빌드 시 함께 실행될 기능을 정의합니다.*/

@Component
@RequiredArgsConstructor
@Slf4j
public class InitManager {

    private final ApplicationContext applicationContext;
    private final PerformanceSettings performanceSettings;
    private final LiveReloadCLibraryFile liveReloadCLibraryFile;


    /**
     * TODO : Cache Memory에 존재하는 데이터를 Dequeue 하여 비지니스로직을 수행합니다.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("setup performance settings -> [{}]", performanceSettings.toString());
        log.info("setup performance settings -> [{}]", performanceSettings.getCLibraryFilePath());
        log.info("setup performance settings -> [{}]", performanceSettings.getPer());

        SENDThreadPool sendThreadPool = applicationContext.getBean("SENDThreadPool", SENDThreadPool.class);
        CfgEtcMap_ThreadPool cfgEtcMap_threadPool = applicationContext.getBean("CfgEtcMap_ThreadPool", CfgEtcMap_ThreadPool.class);
        GipHttpMoAccessList_ThreadPool gipHttpMoAccessList_threadPool = applicationContext.getBean("GipHttpMoAccessList_ThreadPool", GipHttpMoAccessList_ThreadPool.class);
        SpcodeMap_ThreadPool spcodeMap_threadPool = applicationContext.getBean("SpcodeMap_ThreadPool", SpcodeMap_ThreadPool.class);

        if (performanceSettings.getLoop()) {
            Integer cpuCoreCount = Runtime.getRuntime().availableProcessors();
            Integer threadCount = (int) Math.ceil(cpuCoreCount * (performanceSettings.getPer() / 100.0));/*CPU 개수 중 getPer 만큼의 비율을 스레드풀로 할당 : 비중 20%*/
            Integer threadCount80 = (int) Math.ceil(cpuCoreCount * (performanceSettings.getPar80() / 100.0));/*CPU 개수 중 getPer 만큼의 비율을 스레드풀로 할당 : 비중 80%*/

            cpuCoreCount = 1;
            threadCount = 1;
            threadCount80 = 1;

            Integer libraryThreadCount = 1; // 단일 공유 인스턴스 방식으로 1개만 생성

            log.info("CPU Core Count : {}, Thread Pool Size : {}", cpuCoreCount, threadCount);
//        val threadCount = (cpuCoreCount / cpuCoreCount).toInt()
            try {
                cfgEtcMap_threadPool.executeEventHandlerTask(1); //CFGETC 맵 재구성 프로세스
                gipHttpMoAccessList_threadPool.executeEventHandlerTask(1);
                spcodeMap_threadPool.executeEventHandlerTask(1); //CFG_SPCODE 맵 재구성 프로세스
                log.info(liveReloadCLibraryFile.rebuildCLibraryCopies(performanceSettings.getCLibraryFilePath(), libraryThreadCount)); //단일 공유 인스턴스로 so 파일 구성
                sendThreadPool.executeEventHandlerTask(threadCount80.intValue()); //SMSS 큐 전달 프로세스
            } catch (IOException e) {
                log.info("⚠️ error! File Not Found Exception");
            }
            log.info("✅ enabled executeEventHandlerTask");
//
        } else {
            log.info("⚠️ warning! disabled executeEventHandlerTask");
        }
    }
}
