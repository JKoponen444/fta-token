package com.fintechautomation.ftatoken.blockchain.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import okhttp3.OkHttpClient;

/**
 * Creates the application-wide {@link Web3j} client used to talk to the Ethereum node.
 *
 * <p>We wire web3j ourselves rather than using {@code web3j-spring-boot-starter}: the starter's
 * last release (1.6.0, 2018) targets Spring Boot 1.5 and web3j 3.x and does not work on Boot 4.
 *
 * <p>Connection settings come from {@link Web3jProperties} ({@code web3j.*} in application.yml).
 */
// proxyBeanMethods = false: no @Bean method calls another, so the CGLIB proxy isn't needed.
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(Web3jProperties.class)
public class Web3jConfig {

    /**
     * Single shared {@link Web3j} instance. It is thread-safe, so inject it wherever node access
     * is needed (normally via {@code BlockchainService}) instead of building new clients.
     *
     * <p>{@code destroyMethod = "shutdown"} stops web3j's internal scheduler threads and closes
     * the underlying service when the application context closes.
     */
    @Bean(destroyMethod = "shutdown")
    public Web3j web3j(Web3jProperties properties) {
        // HttpService's built-in client uses OkHttp's default timeouts; supplying our own
        // OkHttpClient makes them configurable. One value covers connect, read and write,
        // because a slow node can surface in any of the three.
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(properties.networkTimeout())
                .readTimeout(properties.networkTimeout())
                .writeTimeout(properties.networkTimeout())
                .build();
        return Web3j.build(new HttpService(properties.clientAddress(), httpClient));
    }
}
