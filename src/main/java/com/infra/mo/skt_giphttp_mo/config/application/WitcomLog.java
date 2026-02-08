package com.infra.mo.skt_giphttp_mo.config.application;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpAccessEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WitcomLog {

    @Qualifier("GipHttpAccessMap")
    private final ConcurrentHashMap<String, GipHttpAccessEntity> gipHttpAccessMap;

    @Value("${server.port}")
    private String serverPort;

    @Value("${witcom.performance.cFilePath}")
    private String cFilePath;

    @Value("${witcom.performance.pFilePath}")
    private String pFilePath;


    // ThreadLocal을 통해 TurboFilter에서 logGradeFlag 확인 가능
    public static final ThreadLocal<Integer> THREAD_LOG_GRADE = new ThreadLocal<>();

    // LoggerName은 'C-1571799999-175.125.130.76-8500' 형태
    public void c_write(String loggerName, Level logLevel, String message) {
        // 기본적으로 현재 Thread ID 사용
        c_write(loggerName, logLevel, message, Thread.currentThread().getId());
    }

    // WorkerThread 번호를 매개변수로 받는 메서드
    public void c_write(String loggerName, Level logLevel, String message, long workerThreadId) {
        GipHttpAccessEntity entity = findGipHttpAccessEntity(loggerName);
        if (entity == null) {
            handleEntityNotFound(loggerName);
            return;
        }

        String logNo = formatLogNo(entity.getLogNo());
        THREAD_LOG_GRADE.set(entity.getLogFlag());
        
        String timestamp = new SimpleDateFormat("MMdd_HH").format(new Date());
        String threadNo = String.valueOf(workerThreadId);
        String dynamicLoggerName = loggerName + "-" + logNo + "-" + threadNo + "-" + timestamp;
        
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = context.getLogger(dynamicLoggerName);
        logger.setLevel(logLevel);
        logger.setAdditive(false);

        ensureFileAppenderExists(logger, context, logNo, threadNo, timestamp);
        writeLogMessage(logger, logLevel, message, context, logNo, threadNo, timestamp);
        
        THREAD_LOG_GRADE.remove();
    }

    private GipHttpAccessEntity findGipHttpAccessEntity(String loggerName) {
        GipHttpAccessEntity entity = gipHttpAccessMap.get(loggerName);
        if (entity != null) {
            return entity;
        }
        
        if (loggerName == null || !loggerName.contains("-")) {
            return null;
        }
        
        String cid = loggerName.substring(0, loggerName.indexOf("-"));
        for (GipHttpAccessEntity e : gipHttpAccessMap.values()) {
            if (e != null && cid.equals(e.getCid())) {
                return e;
            }
        }
        return null;
    }

    private void handleEntityNotFound(String loggerName) {
        try {
            p_write(Level.INFO, String.format(
                "c_write 실패: loggerName(%s)을 gipHttpAccessMap에서 찾을 수 없습니다. GIPHTTP_MO_ACCESS 테이블에 등록된 IP, 포트 조합을 확인하세요.",
                loggerName
            ));
        } catch (Exception ex) {
            // 프로덕션 환경에서는 조용히 실패 (로깅 실패는 무시)
        }
    }

    private String formatLogNo(String logNo) {
        NumberFormat formatter = new DecimalFormat("0000");
        String logNoStr = logNo != null ? logNo : "0";
        long logNoLong = logNoStr.isEmpty() ? 0L : Long.parseLong(logNoStr);
        return formatter.format(logNoLong);
    }

    private void ensureFileAppenderExists(Logger logger, LoggerContext context, String logNo, String threadNo, String timestamp) {
        if (!logger.iteratorForAppenders().hasNext()) {
            createAndAddFileAppender(logger, context, logNo, threadNo, timestamp);
            return;
        }
        
        FileAppender<ILoggingEvent> existingAppender = getExistingFileAppender(logger);
        if (existingAppender != null && isFileAppenderInvalid(existingAppender)) {
            recreateFileAppender(logger, context, logNo, threadNo, timestamp, existingAppender);
        }
    }

    private FileAppender<ILoggingEvent> createAndAddFileAppender(Logger logger, LoggerContext context, 
                                                                 String logNo, String threadNo, String timestamp) {
        FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
        fileAppender.setContext(context);
        fileAppender.setFile(cFilePath + "GIPHTTPMO_C_" + logNo + "_" + threadNo + "_" + timestamp);
        fileAppender.setAppend(true);

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("[%d{HH:mm:ss:SSSS}] [%-5level] %msg%n");
        encoder.start();

        fileAppender.setEncoder(encoder);
        fileAppender.start();
        logger.addAppender(fileAppender);
        return fileAppender;
    }

    private FileAppender<ILoggingEvent> getExistingFileAppender(Logger logger) {
        java.util.Iterator<ch.qos.logback.core.Appender<ILoggingEvent>> appenderIterator = logger.iteratorForAppenders();
        if (!appenderIterator.hasNext()) {
            return null;
        }
        
        ch.qos.logback.core.Appender<ILoggingEvent> appender = appenderIterator.next();
        if (appender instanceof FileAppender) {
            return (FileAppender<ILoggingEvent>) appender;
        }
        return null;
    }

    private boolean isFileAppenderInvalid(FileAppender<ILoggingEvent> fileAppender) {
        String currentFile = fileAppender.getFile();
        if (currentFile == null) {
            return true;
        }
        
        java.io.File file = new java.io.File(currentFile);
        return !file.exists() || (file.getParentFile() != null && !file.getParentFile().exists());
    }

    private void recreateFileAppender(Logger logger, LoggerContext context, String logNo, 
                                      String threadNo, String timestamp, FileAppender<ILoggingEvent> oldAppender) {
        logger.detachAppender(oldAppender);
        oldAppender.stop();
        createAndAddFileAppender(logger, context, logNo, threadNo, timestamp);
    }

    private void writeLogMessage(Logger logger, Level logLevel, String message, 
                                LoggerContext context, String logNo, String threadNo, String timestamp) {
        try {
            writeLogByLevel(logger, logLevel, message);
        } catch (Exception ex) {
            handleLogWriteFailure(logger, context, logNo, threadNo, timestamp, logLevel, message);
        }
    }

    private void writeLogByLevel(Logger logger, Level logLevel, String message) {
        switch (logLevel.levelStr) {
            case "TRACE" -> logger.trace(message);
            case "DEBUG" -> logger.debug(message);
            case "INFO" -> logger.info(message);
            case "WARN" -> logger.warn(message);
            case "ERROR" -> logger.error(message);
            default -> logger.info(message);
        }
    }

    private void handleLogWriteFailure(Logger logger, LoggerContext context, String logNo, 
                                      String threadNo, String timestamp, Level logLevel, String message) {
        logger.detachAndStopAllAppenders();
        createAndAddFileAppender(logger, context, logNo, threadNo, timestamp);
        
        try {
            writeLogByLevel(logger, logLevel, message);
        } catch (Exception retryEx) {
            // 프로덕션 환경에서는 조용히 실패 (로깅 실패는 무시)
        }
    }

    // LoggerName은 'P-1571799999-175.125.130.76-8500' 형태
    public void p_write(Level logLevel, String message) {
        try {
            THREAD_LOG_GRADE.set(0);

            // 현재 로그 기준 시각 (MMdd_HH)
            String currentTimeKey = java.time.LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("MMdd_HH"));

            // Logger 이름
            String logFileName = String.format("GIPHTTPMO_P_%s_%s", serverPort, currentTimeKey);
            String logFilePath = pFilePath + logFileName;

            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            Logger logger = context.getLogger(logFileName);

            // 이전 시간대와 비교해서 새로운 파일이 필요한지 확인
            FileAppender<ILoggingEvent> fileAppender = null;
            if (!logger.iteratorForAppenders().hasNext()) {
                // 새 Logger 또는 새로운 시간대 → 새로운 Appender 생성
                fileAppender = new FileAppender<>();
                fileAppender.setContext(context);
                fileAppender.setFile(logFilePath);
                fileAppender.setAppend(true);

                PatternLayoutEncoder encoder = new PatternLayoutEncoder();
                encoder.setContext(context);
                encoder.setPattern("[%d{HH:mm:ss.SSS}] [%-5level] %msg%n");
                encoder.start();

                fileAppender.setEncoder(encoder);
                fileAppender.start();

                logger.detachAndStopAllAppenders(); // 기존 연결 제거
                logger.addAppender(fileAppender);

                logger.setLevel(logLevel);
                logger.setAdditive(false);
            }

            // 로그 작성
            switch (logLevel.levelStr) {
                case "TRACE" -> logger.trace(message);
                case "DEBUG" -> logger.debug(message);
                case "INFO" -> logger.info(message);
                case "WARN" -> logger.warn(message);
                case "ERROR" -> logger.error(message);
                default -> logger.info(message);
            }
        } catch (Exception e) {
            // 프로덕션 환경에서는 조용히 실패 (로깅 실패는 무시)
            // 디버그 출력 제거: log.error, e.printStackTrace()
        } finally {
            THREAD_LOG_GRADE.remove();
        }
    }
}
