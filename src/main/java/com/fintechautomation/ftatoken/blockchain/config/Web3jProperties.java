package com.fintechautomation.ftatoken.blockchain.config;

import java.time.Duration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

/**
 * Connection settings for the Ethereum JSON-RPC node, bound from the {@code web3j.*} properties
 * in application.yml.
 *
 * <p>{@link Validated @Validated} makes the application fail at startup if a value is missing or
 * blank, rather than failing later on the first blockchain call.
 *
 * <p>The {@link DefaultValue @DefaultValue}s apply only when a property is absent entirely;
 * application.yml normally supplies both values, reading them from environment variables.
 *
 * @param clientAddress  HTTP(S) URL of the JSON-RPC endpoint ({@code web3j.client-address},
 *                       env {@code WEB3J_CLIENT_ADDRESS}). Defaults to Base's public Sepolia
 *                       testnet endpoint (chain ID 84532), which is rate-limited; use a dedicated
 *                       provider URL (Alchemy, Infura, QuickNode) for anything beyond local
 *                       development. Provider URLs usually embed an API key, so supply them via
 *                       the environment variable and never commit them.
 * @param networkTimeout connect/read/write timeout applied to every RPC call
 *                       ({@code web3j.network-timeout}, env {@code WEB3J_NETWORK_TIMEOUT}).
 *                       Accepts Spring duration formats such as {@code 30s} or {@code 500ms}.
 */
@Validated
@ConfigurationProperties("web3j")
public record Web3jProperties(
        @NotBlank @DefaultValue("https://sepolia.base.org") String clientAddress,
        @NotNull @DefaultValue("30s") Duration networkTimeout) {
}
