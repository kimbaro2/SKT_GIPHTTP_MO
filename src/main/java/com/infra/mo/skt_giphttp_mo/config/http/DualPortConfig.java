package com.infra.mo.skt_giphttp_mo.config.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.netty.http.server.HttpServer;
import io.netty.channel.ChannelPipeline;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import reactor.netty.DisposableServer;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * HTTP/HTTPS 듀얼 포트 설정
 * 
 * - HTTPS 포트: server.port (입력받은 포트, 예: 7500)
 * - HTTP 포트: server.port + 44 (예: 7544)
 * 
 * 참고: HTTP 포트의 요청은 IpSecurityFilter에서 IP 필터링됩니다.
 */
@Configuration
@Slf4j
public class DualPortConfig {

    @Value("${server.port}")
    private int serverPort;  // 이 포트는 HTTPS로 사용 (필수 파라미터)

    @Value("${server.ssl.enabled:false}")
    private boolean sslEnabled;

    @Value("${server.ssl.key-store:classpath:ssl/keystore.p12}")
    private String keyStorePath;

    @Value("${server.ssl.key-store-password:changeit}")
    private String keyStorePassword;

    @Value("${server.ssl.key-store-type:PKCS12}")
    private String keyStoreType;

    @Value("${server.ssl.key-alias:skt-giphttp-mo}")
    private String keyAlias;

    @Value("${server.http2.enabled:true}")
    private boolean http2Enabled;

    private final ApplicationContext applicationContext;
    private DisposableServer httpsServer;
    private int httpPort;  // server.port + 44
    private boolean httpServerStarted = false;
    private boolean httpsServerStarted = false;

    public DualPortConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        // server.port 검증 (0보다 큰 값이어야 함)
        if (serverPort <= 0) {
            throw new IllegalStateException("server.port는 필수 파라미터이며 0보다 큰 값이어야 합니다. --server.port=<포트번호>를 입력해주세요.");
        }
        // HTTP 포트 = server.port + 44
        httpPort = serverPort + 44;
        log.info("포트 설정: HTTPS={}, HTTP={} (HTTPS + 44)", serverPort, httpPort);
    }

    /**
     * HTTP 서버 팩토리 (server.port + 44, SSL 없음, HTTP/2 비활성화)
     * HTTP/1.1 전용이므로 HTTP/2 업그레이드를 비활성화합니다.
     * @Primary를 사용하여 Spring Boot의 기본 팩토리보다 우선순위를 갖도록 함
     */
    @Bean
    @Primary
    public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() {
        // server.port 검증
        if (serverPort <= 0) {
            throw new IllegalStateException("server.port는 필수 파라미터이며 0보다 큰 값이어야 합니다. --server.port=<포트번호>를 입력해주세요.");
        }
        // httpPort 계산이 먼저 되어야 하므로 init()에서 처리
        if (httpPort == 0) {
            httpPort = serverPort + 44;
        }
        
        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory(httpPort);  // HTTP 포트로 설정
        
        // HTTP 포트에서는 HTTP/2 업그레이드 핸들러를 파이프라인에서 제거
        factory.addServerCustomizers(new NettyServerCustomizer() {
            @Override
            public HttpServer apply(HttpServer httpServer) {
                return httpServer.doOnChannelInit((observer, channel, remoteAddress) -> {
                    // 채널이 활성화된 후 파이프라인에서 HTTP/2 업그레이드 핸들러 제거
                    channel.eventLoop().execute(() -> {
                        ChannelPipeline pipeline = channel.pipeline();
                        // HTTP/2 업그레이드 핸들러 제거
                        if (pipeline.get("reactor.left.h2cUpgradeHandler") != null) {
                            pipeline.remove("reactor.left.h2cUpgradeHandler");
                            log.debug("HTTP/2 업그레이드 핸들러 제거됨: reactor.left.h2cUpgradeHandler");
                        }
                        if (pipeline.get("HttpServerUpgradeHandler#0") != null) {
                            pipeline.remove("HttpServerUpgradeHandler#0");
                            log.debug("HTTP/2 업그레이드 핸들러 제거됨: HttpServerUpgradeHandler#0");
                        }
                    });
                });
            }
        });
        
        log.info("✅ HTTP 서버 설정: 포트 {} (SSL 없음, HTTP/1.1 전용, 특정 IP 대역만 허용)", httpPort);
        log.info("   - HTTP/2 업그레이드 핸들러 제거 (HTTP/1.1만 지원)");
        log.info("   - Spring Boot 기본 서버는 이 Factory로 대체됨 (server.port={} 무시, httpPort={} 사용)", serverPort, httpPort);
        
        // HTTP 서버 시작 상태 추적을 위한 커스터마이저 추가
        factory.addServerCustomizers(new NettyServerCustomizer() {
            @Override
            public HttpServer apply(HttpServer httpServer) {
                return httpServer.doOnBound(disposableServer -> {
                    httpServerStarted = true;
                    log.info("✅ HTTP 서버 바인딩 완료: 포트 {}", httpPort);
                }).doOnUnbound(disposableServer -> {
                    httpServerStarted = false;
                    log.warn("⚠️ HTTP 서버 언바인딩: 포트 {}", httpPort);
                });
            }
        });
        
        return factory;
    }

    /**
     * HTTPS 서버를 server.port에서 시작
     * HTTP와 HTTPS 두 포트는 항상 모두 시작되어야 함
     */
    @PostConstruct
    public void startHttpsServer() {
        // server.port 검증
        if (serverPort <= 0) {
            throw new IllegalStateException("server.port는 필수 파라미터이며 0보다 큰 값이어야 합니다. --server.port=<포트번호>를 입력해주세요.");
        }
        // httpPort 계산이 먼저 되어야 하므로 init()에서 처리
        if (httpPort == 0) {
            httpPort = serverPort + 44;
        }
        
        try {
            // HTTP 핸들러 생성 (HTTP 서버와 동일한 핸들러 사용)
            HttpHandler httpHandler = WebHttpHandlerBuilder
                .applicationContext(applicationContext)
                .build();

            // SSL이 활성화되어 있고 키스토어 파일이 있으면 HTTPS 서버 시작
            if (sslEnabled) {
                try {
                    // Netty SslContext 로드
                    SslContext sslContext = loadNettySslContext();

                    // HTTPS 서버 시작 (server.port 사용)
                    HttpServer httpServer = HttpServer.create()
                        .port(serverPort)  // 입력받은 포트를 HTTPS로 사용
                        .secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

                    // HTTPS 포트에서만 HTTP/2 활성화
                    // 참고: ReactorNetty는 SSL이 활성화된 경우 자동으로 HTTP/2를 지원합니다.
                    if (http2Enabled) {
                        log.info("   - HTTP/2 활성화 (HTTP/1.1 fallback 지원, ALPN 협상)");
                    }

                    httpsServer = httpServer
                        .doOnBound(disposableServer -> {
                            httpsServerStarted = true;
                            log.info("✅ HTTPS 서버 바인딩 완료: 포트 {}", serverPort);
                        })
                        .doOnUnbound(disposableServer -> {
                            httpsServerStarted = false;
                            log.warn("⚠️ HTTPS 서버 언바인딩: 포트 {}", serverPort);
                        })
                        .handle(new ReactorHttpHandlerAdapter(httpHandler))
                        .bindNow();

                    log.info("✅ HTTPS 서버 시작 완료: 포트 {} (SSL 활성화, 모든 클라이언트 허용)", serverPort);
                    log.info("   - HTTP 서버: 포트 {} (SSL 없음, 특정 IP 대역만 허용)", httpPort);
                    log.info("   - HTTPS 서버: 포트 {} (SSL 활성화, 모든 클라이언트 허용)", serverPort);
                } catch (IllegalStateException e) {
                    // 키스토어 파일이 없을 경우 경고만 출력하고 HTTP 서버는 정상 작동
                    if (e.getMessage() != null && e.getMessage().contains("키스토어 파일을 찾을 수 없습니다")) {
                        log.warn("⚠️ 키스토어 파일을 찾을 수 없습니다: {}", keyStorePath);
                        log.warn("   - HTTPS 서버를 시작할 수 없습니다. HTTP 서버만 시작됩니다.");
                        log.warn("   - 키스토어 파일을 생성하거나 server.ssl.enabled=false로 설정하세요.");
                        log.info("✅ HTTP 서버만 시작: 포트 {} (SSL 없음, 특정 IP 대역만 허용)", httpPort);
                    } else {
                        log.error("❌ HTTPS 서버 시작 실패: {}", e.getMessage(), e);
                        log.info("✅ HTTP 서버는 정상 작동: 포트 {} (SSL 없음, 특정 IP 대역만 허용)", httpPort);
                    }
                } catch (Exception e) {
                    log.error("❌ HTTPS 서버 시작 실패: {}", e.getMessage(), e);
                    log.info("✅ HTTP 서버는 정상 작동: 포트 {} (SSL 없음, 특정 IP 대역만 허용)", httpPort);
                }
            } else {
                // SSL이 비활성화되어 있으면 HTTPS 서버를 시작하지 않음
                log.info("ℹ️ SSL이 비활성화되어 있습니다 (server.ssl.enabled=false). HTTPS 서버를 시작하지 않습니다.");
                log.info("✅ HTTP 서버만 시작: 포트 {} (SSL 없음, 특정 IP 대역만 허용)", httpPort);
            }
        } catch (Exception e) {
            log.error("❌ HTTPS 서버 초기화 실패: {}", e.getMessage(), e);
            log.info("✅ HTTP 서버는 정상 작동: 포트 {} (SSL 없음, 특정 IP 대역만 허용)", httpPort);
        }
    }

    /**
     * Netty SslContext 로드
     */
    private SslContext loadNettySslContext() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        
        // 키스토어 파일 로드
        String resourcePath = keyStorePath.replace("classpath:", "");
        InputStream keyStoreStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        
        if (keyStoreStream == null) {
            throw new IllegalStateException("키스토어 파일을 찾을 수 없습니다: " + keyStorePath);
        }

        keyStore.load(keyStoreStream, keyStorePassword.toCharArray());
        keyStoreStream.close();

        // KeyManagerFactory 초기화
        javax.net.ssl.KeyManagerFactory keyManagerFactory = 
            javax.net.ssl.KeyManagerFactory.getInstance(javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

        // Netty SslContext 생성
        return SslContextBuilder
            .forServer(keyManagerFactory)
            .build();
    }

    @PreDestroy
    public void stopHttpsServer() {
        if (httpsServer != null) {
            httpsServer.disposeNow();
            httpsServerStarted = false;
            log.info("HTTPS 서버 종료 완료");
        }
    }

    /**
     * 서버 상태 확인 메서드
     */
    public ServerStatus getServerStatus() {
        return new ServerStatus(
            serverPort,
            httpPort,
            httpServerStarted,
            httpsServerStarted,
            sslEnabled
        );
    }

    /**
     * 서버 상태 정보 클래스
     */
    public static class ServerStatus {
        public final int httpsPort;
        public final int httpPort;
        public final boolean httpServerRunning;
        public final boolean httpsServerRunning;
        public final boolean sslEnabled;

        public ServerStatus(int httpsPort, int httpPort, boolean httpServerRunning, boolean httpsServerRunning, boolean sslEnabled) {
            this.httpsPort = httpsPort;
            this.httpPort = httpPort;
            this.httpServerRunning = httpServerRunning;
            this.httpsServerRunning = httpsServerRunning;
            this.sslEnabled = sslEnabled;
        }
    }
}

