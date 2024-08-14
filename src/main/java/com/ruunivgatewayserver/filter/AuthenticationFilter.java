package com.ruunivgatewayserver.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * todo : Account Server 구현하고 , pass부분에서 passport 정보를 불러와서 헤더에 추가하는 로직 추가 및 인가 추가
 */


@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Value("${secret.key}")
    private String secretKey;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {

        return ((exchange, chain)->{
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();


            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            String token = Objects.requireNonNull(exchange.getRequest().getHeaders().get("Authorization")).get(0).substring(7);

            if(!isJwtValid(token)){
                return onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
            }

            log.info("Pass");

            return chain.filter(exchange);
        });
    }

    private boolean isJwtValid(String token){
        try{
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e){
            log.info("[AUTH] : TOKEN 유효성 예외");
            return false;
        }
    }

    private String extractUserId(String token){
        try{
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("userId").toString();
        } catch (Exception e){
            log.info("[AUTH] : TOKEN 추출 예외");
            return null;
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error("[AUTH] : {}",err);
        return response.setComplete();
    }

    @Getter
    @Setter
    public static class Config{
        private String requiredRole;
    }
}
