package com.fintechautomation.ftatoken.blockchain.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import okhttp3.OkHttpClient;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(Web3jProperties.class)
public class Web3jConfig {

    @Bean(destroyMethod = "shutdown")
    public Web3j web3j(Web3jProperties properties) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(properties.networkTimeout())
                .readTimeout(properties.networkTimeout())
                .writeTimeout(properties.networkTimeout())
                .build();
        return Web3j.build(new HttpService(properties.clientAddress(), httpClient));
    }
}
