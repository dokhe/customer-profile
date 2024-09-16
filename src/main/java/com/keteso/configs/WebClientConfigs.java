package com.keteso.configs;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.time.Duration;

@Component
public class WebClientConfigs {

    @Value("${apps.timeout}")
    private int timeout;

    @Bean(name = "sslWebclient")
    public WebClient createWebClient() throws SSLException {
        SslContext sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofMillis(timeout)).secure(
                t -> t.sslContext(sslContext)).resolver(DefaultAddressResolverGroup.INSTANCE);
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }
}
