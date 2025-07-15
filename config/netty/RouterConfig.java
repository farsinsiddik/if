
package com.tag.biometric.ifService.config.netty;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.server.HttpServer;

@Configuration
public class RouterConfig {

//    @Bean
//    public RestTemplate restTemplate() {
//        // Create a connection manager with a connection pool
//        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
//        connectionManager.setMaxTotal(200); // Maximum total connections
//        connectionManager.setDefaultMaxPerRoute(200); // Maximum connections per route
//        connectionManager.setValidateAfterInactivity(TimeValue.ofSeconds(5)); // Connection validation
//
//        // Create the HttpClient with the connection manager
//        CloseableHttpClient httpClient = HttpClients.custom()
//                .setConnectionManager(connectionManager)
//                .evictExpiredConnections()
//                .evictIdleConnections(TimeValue.ofSeconds(10)) // Evict idle connections
//                .build();
//
//        // Create the RestTemplate using the HttpClient
//        HttpComponentsClientHttpRequestFactory requestFactory =
//                new HttpComponentsClientHttpRequestFactory(httpClient);
//        return new RestTemplate(requestFactory);
////        return new RestTemplate();
//    }

    @Bean
    public HttpServer httpServer() {
        return HttpServer.create().tcpConfiguration(tcpServer ->
                tcpServer.runOn(reactor.netty.resources.LoopResources.create("http-server", 100, true)));
    }

//    @Bean
//    public WebClient webClient() {
//        ConnectionProvider provider = ConnectionProvider.builder("custom")
//                .maxConnections(200) // Maximum number of connections
//                .pendingAcquireMaxCount(500) // Maximum number of pending requests
//                .build();
//
//        HttpClient httpClient = HttpClient.create(provider);
//
//        return WebClient.builder()
//                .clientConnector(new ReactorClientHttpConnector(httpClient))
//                .build();
//    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://sandbox-api.marqeta.com")
                .build();
    }

}
