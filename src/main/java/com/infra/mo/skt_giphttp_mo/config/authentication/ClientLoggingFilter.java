package com.infra.mo.skt_giphttp_mo.config.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;


@Component
@Slf4j
public class ClientLoggingFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        getClientAccessInfo(
                getClientIp(exchange),
                exchange.getRequest().getMethod().name(),
                exchange.getRequest().getURI().getPath(),
                exchange.getRequest().getHeaders().getFirst("User-Agent"));

        return chain.filter(exchange);
    }

    /*TODO : 클라이언트 인입 정보를 로깅합니다.*/
    private void getClientAccessInfo(String clientIp,
                                     String method,
                                     String url,
                                     String userAgent) {
        log.info("getClientAccessInfo() CLIENT_IP[{}]   METHOD[{}]    API[{}]   CLIENT_DEVICE[{}]", clientIp, method, url, userAgent);

    }

    private String getClientIp(ServerWebExchange exchange) {
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            // 여러 IP 중 첫번째가 실제 클라이언트 IP
            return xForwardedFor.split(",")[0].trim();
        }
        // 없으면 remote address 사용
        return exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown";
    }
}
