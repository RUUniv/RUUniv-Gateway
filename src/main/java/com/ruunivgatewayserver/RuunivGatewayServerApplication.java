package com.ruunivgatewayserver;

import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.netty.http.client.HttpClient;

@SpringBootApplication
public class RuunivGatewayServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RuunivGatewayServerApplication.class, args);
    }

}


