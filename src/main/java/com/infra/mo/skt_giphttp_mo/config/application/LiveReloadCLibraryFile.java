package com.infra.mo.skt_giphttp_mo.config.application;

import com.infra.mo.skt_giphttp_mo.dto.jna.LibC;
import com.infra.mo.skt_giphttp_mo.dto.jna.SmsDef;
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib;
import com.sun.jna.Library;
import com.sun.jna.Native;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class LiveReloadCLibraryFile {

    private final ApplicationContext context;

    // 스레드별로 미리 초기화된 SmsQLib 인스턴스를 저장하는 맵
    private final Map<Integer, SmsQLib> preInitializedLibraries = new ConcurrentHashMap<>();

    public String rebuildCLibraryCopies(String originalLibPath, int threadCount) throws IOException {
        // cLibraryFilePath 읽기 (절대 경로 기준)
        String cLibraryFilePath = context.getEnvironment().getProperty("witcom.performance.cLibraryFilePath");
        if (cLibraryFilePath == null || cLibraryFilePath.isEmpty()) {
            throw new IllegalArgumentException("witcom.performance.cLibraryFilePath is not set");
        }
        
        // cLibraryFilePath를 절대 경로로 변환하여 원본 파일 확인
        File originalFile = new File(cLibraryFilePath);
        if (!originalFile.isAbsolute()) {
            originalFile = originalFile.getAbsoluteFile();
        }
        
        if (!originalFile.exists()) {
            throw new IllegalArgumentException("Original .so file does not exist: " + originalFile.getAbsolutePath());
        }
        
        // 기존 초기화된 라이브러리 맵 클리어
        preInitializedLibraries.clear();
        
        // 원본 파일 정보 확인
        log.info("Original library file: {} (exists: {}, readable: {}, size: {} bytes)", 
            originalFile.getAbsolutePath(),
            originalFile.exists(),
            originalFile.canRead(),
            originalFile.length());

        // 🔹 원본 파일을 Native.load()로 직접 로드
        try {
            Map<String, Object> options = new HashMap<>();
            options.put(Library.OPTION_OPEN_FLAGS, LibC.RTLD_LAZY | LibC.RTLD_LOCAL);
            options.put(Library.OPTION_STRUCTURE_ALIGNMENT, 1);
            options.put(Library.OPTION_STRING_ENCODING, "CP949");

            // 원본 파일 경로 직접 사용
            String originalAbsolutePath = originalFile.getAbsolutePath();
            log.info("Loading native library from original: {}", originalAbsolutePath);
            
            SmsQLib smsQLib = (SmsQLib) Native.load(originalAbsolutePath, SmsQLib.class, options);
            log.info("Native.load() successful for: {}", originalAbsolutePath);

            // 🔹 미리 초기화 수행 (한 번만)
            String pLogName = String.format("GIPHTTPMO_P_%s",
                    context.getEnvironment().getProperty("server.port")
            );
            smsQLib.LvDprintfInit(
                    pLogName,
                    SmsDef.DPRINTF_LOG_PERIOD_DAILY,
                    3
            );

            int initResult = smsQLib.InitQInfo();
            log.info("🔹 InitQInfo() 결과: {}", initResult);

            // 초기화된 라이브러리 인스턴스를 맵에 저장 (인덱스 0)
            preInitializedLibraries.put(0, smsQLib);

            log.info("✅ SO 파일 초기화 완료: {}", originalAbsolutePath);

        } catch (Exception e) {
            log.error("❌ SO 파일 초기화 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize SO file", e);
        }

        log.info("SO 파일 초기화 완료 (단일 공유 인스턴스)");
        return "rebuildCLibraryCopies() file batch success with pre-initialization";
    }

    public int getLibraryInstanceForSize() {
        return preInitializedLibraries.size();
    }

    /**
     * 특정 스레드 인덱스에 해당하는 미리 초기화된 SmsQLib 인스턴스를 반환
     */
    public SmsQLib getPreInitializedLibrary(int threadIndex) {
        SmsQLib library = preInitializedLibraries.get(threadIndex);
        if (library == null) {
            throw new IllegalStateException("Pre-initialized library not found for thread index: " + threadIndex);
        }
        return library;
    }

}
