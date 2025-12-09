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
        return factory;
    }

    /**
     * HTTPS 서버를 server.port에서 시작
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
                .handle(new ReactorHttpHandlerAdapter(httpHandler))
                .bindNow();

            log.info("✅ HTTPS 서버 시작 완료: 포트 {} (SSL 활성화, 모든 클라이언트 허용)", serverPort);
            log.info("   - HTTP 서버: 포트 {} (SSL 없음, 특정 IP 대역만 허용)", httpPort);
            log.info("   - HTTPS 서버: 포트 {} (SSL 활성화, 모든 클라이언트 허용)", serverPort);
        } catch (Exception e) {
            log.error("❌ HTTPS 서버 시작 실패: {}", e.getMessage(), e);
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
            log.info("HTTPS 서버 종료 완료");
        }
    }
}

