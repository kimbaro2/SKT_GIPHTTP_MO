package com.infra.mo.skt_giphttp_mo.config.application;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
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

    @Value("${server.port}")
    private String serverPort;

    @Value("${witcom.performance.pFilePath}")
    private String pFilePath;


    // ThreadLocal을 통해 TurboFilter에서 logGradeFlag 확인 가능
    public static final ThreadLocal<Integer> THREAD_LOG_GRADE = new ThreadLocal<>();

    // LoggerName은 'P-1571799999-175.125.130.76-8500' 형태
    public void p_write(Level logLevel, String message) {
        try {
            THREAD_LOG_GRADE.set(0);

            // 현재 로그 기준 시각 (MMdd_HH)
            String currentTimeKey = java.time.LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("MMdd_HH"));

            // Logger 이름
            String logFileName = String.format("GIPHTTP_P_%s_%s", serverPort, currentTimeKey);
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
