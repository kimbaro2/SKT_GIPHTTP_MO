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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class LiveReloadCLibraryFile {

    private final ApplicationContext context;

    // 스레드별로 미리 초기화된 SmsQLib 인스턴스를 저장하는 맵
    private final Map<Integer, SmsQLib> preInitializedLibraries = new ConcurrentHashMap<>();

    public String rebuildCLibraryCopies(String originalLibPath, int threadCount) throws IOException {
        File originalFile = new File(originalLibPath);

        if (!originalFile.exists()) {
            throw new IllegalArgumentException("Original .so file does not exist: " + originalLibPath);
        }

        String parentDir = originalFile.getParent();
        String fileName = originalFile.getName();
        int dotIndex = fileName.lastIndexOf('.');

        String baseName = (dotIndex != -1) ? fileName.substring(0, dotIndex) : fileName;
        String extension = (dotIndex != -1) ? fileName.substring(dotIndex) : "";

        // /SO 디렉토리 경로
        Path soDirPath = Paths.get(parentDir, "SO_SEND");

        // 기존 디렉토리 삭제 후 재생성
        deleteDirectoryRecursively(soDirPath);
        Files.createDirectories(soDirPath);

        // 기존 초기화된 라이브러리 맵 클리어
        preInitializedLibraries.clear();

        // 새 복사본 생성 및 미리 초기화
        for (int i = 0; i < threadCount; i++) {
            String newFileName = baseName + i + extension;
            Path newFilePath = soDirPath.resolve(newFileName);
            Files.copy(originalFile.toPath(), newFilePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Created: {}", newFilePath);

            // 🔹 SO 파일 생성 후 즉시 Native.load 및 초기화
            try {
                Map<String, Object> options = new HashMap<>();
                options.put(Library.OPTION_OPEN_FLAGS, LibC.RTLD_LAZY | LibC.RTLD_LOCAL);
                options.put(Library.OPTION_STRUCTURE_ALIGNMENT, 1);
                options.put(Library.OPTION_STRING_ENCODING, "CP949");

                SmsQLib smsQLib = (SmsQLib) Native.load(newFilePath.toString(), SmsQLib.class, options);

                // 🔹 미리 초기화 수행 (P 로그 명칭 패턴 사용)
                String pLogName = String.format("GIPHTTPMO_P_%s",
                        context.getEnvironment().getProperty("server.port")
                );
                smsQLib.LvDprintfInit(
                        pLogName,
                        SmsDef.DPRINTF_LOG_PERIOD_DAILY,
                        3
                );

                int initResult = smsQLib.InitQInfo();
                log.info("🔹[Thread-{}] InitQInfo() 결과: {}", i, initResult);

                // 초기화된 라이브러리 인스턴스를 맵에 저장
                preInitializedLibraries.put(i, smsQLib);

                log.info("✅ [Thread-{}] SO 파일 생성 및 초기화 완료: {}", i, newFilePath);

            } catch (Exception e) {
                log.error("❌ [Thread-{}] SO 파일 초기화 실패: {}", i, e.getMessage(), e);
                throw new RuntimeException("Failed to initialize SO file for thread " + i, e);
            }
        }

        log.info("총 {}개의 SO 파일 생성 및 초기화 완료", threadCount);
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

    private void deleteDirectoryRecursively(Path dirPath) throws IOException {
        if (!Files.exists(dirPath)) return;

        Files.walk(dirPath)
                .sorted(Comparator.reverseOrder()) // 파일 → 하위폴더 → 디렉토리 순으로 삭제
                .map(Path::toFile)
                .forEach(file -> {
                    if (!file.delete()) {
                        System.err.println("Failed to delete: " + file.getAbsolutePath());
                    }
                });
    }
}
