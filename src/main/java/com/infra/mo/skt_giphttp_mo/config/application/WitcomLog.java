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
        GipHttpAccessEntity e = gipHttpAccessMap.get(loggerName);
        if (e == null) return;

        int logGradeFlag = e.getLogFlag();
        THREAD_LOG_GRADE.set(logGradeFlag);

        NumberFormat formatter = new DecimalFormat("0000");
        String logNo = formatter.format(Long.valueOf(e.getLogNo()));

        // 전달받은 WorkerThread 번호 사용
        String threadNo = String.valueOf(workerThreadId);

        // 현재 시간 기반 타임스탬프 생성 (MMdd_HH)
        String timestamp = new SimpleDateFormat("MMdd_HH").format(new Date());

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        // dynamicLoggerName에 시간 정보 포함하여 시간 변경 시 새 Logger 생성
        String dynamicLoggerName = loggerName + "-" + logNo + "-" + threadNo + "-" + timestamp;
        Logger logger = context.getLogger(dynamicLoggerName);

        logger.setLevel(logLevel);
        logger.setAdditive(false);

        // FileAppender가 없거나 파일이 삭제된 경우 재생성
        if (!logger.iteratorForAppenders().hasNext()) {
            FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
            fileAppender.setContext(context);

            // 파일명 패턴: GIPHTTPMO_C_{logNo}_{threadNo}_{MMdd}_{HH}
            String logFile = cFilePath + "GIPHTTPMO_C_" + logNo + "_" + threadNo + "_" + timestamp;

            fileAppender.setFile(logFile);
            fileAppender.setAppend(true);

            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(context);
            encoder.setPattern("[%d{HH:mm:ss:SSSS}] [%-5level] %msg%n");
            encoder.start();

            fileAppender.setEncoder(encoder);
            fileAppender.start();

            logger.addAppender(fileAppender);
        } else {
            // 기존 FileAppender가 있지만 파일이 삭제되었을 수 있으므로 확인
            java.util.Iterator<ch.qos.logback.core.Appender<ILoggingEvent>> appenderIterator = 
                logger.iteratorForAppenders();
            if (appenderIterator.hasNext()) {
                ch.qos.logback.core.Appender<ILoggingEvent> appender = appenderIterator.next();
                if (appender instanceof FileAppender) {
                    FileAppender<ILoggingEvent> fileAppender = (FileAppender<ILoggingEvent>) appender;
                    String currentFile = fileAppender.getFile();
                    java.io.File file = new java.io.File(currentFile);
                    // 파일이 존재하지 않거나 디렉토리가 없는 경우 FileAppender 재생성
                    if (currentFile != null && (!file.exists() || (file.getParentFile() != null && !file.getParentFile().exists()))) {
                        logger.detachAppender(fileAppender);
                        fileAppender.stop();
                        
                        // 새로운 FileAppender 생성
                        FileAppender<ILoggingEvent> newFileAppender = new FileAppender<>();
                        newFileAppender.setContext(context);
                        String logFile = cFilePath + "GIPHTTPMO_C_" + logNo + "_" + threadNo + "_" + timestamp;
                        newFileAppender.setFile(logFile);
                        newFileAppender.setAppend(true);
                        
                        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
                        encoder.setContext(context);
                        encoder.setPattern("[%d{HH:mm:ss:SSSS}] [%-5level] %msg%n");
                        encoder.start();
                        
                        newFileAppender.setEncoder(encoder);
                        newFileAppender.start();
                        logger.addAppender(newFileAppender);
                    }
                }
            }
        }

        // 로그 작성 시 예외 처리
        try {
            switch (logLevel.levelStr) {
                case "TRACE" -> logger.trace(message);
                case "DEBUG" -> logger.debug(message);
                case "INFO" -> logger.info(message);
                case "WARN" -> logger.warn(message);
                case "ERROR" -> logger.error(message);
                default -> logger.info(message);
            }
        } catch (Exception ex) {
            // 파일 쓰기 실패 시 FileAppender 재생성 시도
            System.err.println("Error writing log, attempting to recreate FileAppender: " + ex.getMessage());
            logger.detachAndStopAllAppenders();
            
            FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
            fileAppender.setContext(context);
            String logFile = cFilePath + "GIPHTTPMO_C_" + logNo + "_" + threadNo + "_" + timestamp;
            fileAppender.setFile(logFile);
            fileAppender.setAppend(true);
            
            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(context);
            encoder.setPattern("[%d{HH:mm:ss:SSSS}] [%-5level] %msg%n");
            encoder.start();
            
            fileAppender.setEncoder(encoder);
            fileAppender.start();
            logger.addAppender(fileAppender);
            
            // 재시도
            try {
                switch (logLevel.levelStr) {
                    case "TRACE" -> logger.trace(message);
                    case "DEBUG" -> logger.debug(message);
                    case "INFO" -> logger.info(message);
                    case "WARN" -> logger.warn(message);
                    case "ERROR" -> logger.error(message);
                    default -> logger.info(message);
                }
            } catch (Exception retryEx) {
                System.err.println("Failed to write log after retry: " + retryEx.getMessage());
            }
        }
        THREAD_LOG_GRADE.remove();
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

                System.out.println("▶ New log file created: " + logFilePath);
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
            System.err.println("Error in p_write: " + e.getMessage());
            e.printStackTrace();
        } finally {
            THREAD_LOG_GRADE.remove();
        }
    }
}
