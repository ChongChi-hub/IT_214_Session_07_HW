package com.finbank.api_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();
        
        logger.info(">>> Nhận Request: {} {}", method, path);

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long responseTime = System.currentTimeMillis() - startTime;
            
            logger.info("<<< Hoàn thành Request: {} {} trong {} ms", method, path, responseTime);
            
            exchange.getResponse().getHeaders().add("X-Response-Time", responseTime + "ms");
        }));
    }

    @Override
    public int getOrder() {
        return -1; // Highest precedence
    }
}
