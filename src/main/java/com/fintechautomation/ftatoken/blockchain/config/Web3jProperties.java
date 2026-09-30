package com.fintechautomation.ftatoken.blockchain.config;

import java.time.Duration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

/**
 * Connection settings for the Ethereum JSON-RPC node.
 *
 * @param clientAddress  HTTP(S) URL of the JSON-RPC endpoint
 * @param networkTimeout connect/read/write timeout for RPC calls
 */
@Validated
@ConfigurationProperties("web3j")
public record Web3jProperties(
        @NotBlank @DefaultValue("https://sepolia.base.org") String clientAddress,
        @NotNull @DefaultValue("30s") Duration networkTimeout) {
}
