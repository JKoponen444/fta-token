package com.fintechautomation.ftatoken.blockchain.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.health.contributor.AbstractHealthIndicator;
import org.springframework.boot.health.contributor.Health;
import org.springframework.stereotype.Component;

import com.fintechautomation.ftatoken.blockchain.service.BlockchainService;
import com.fintechautomation.ftatoken.blockchain.service.NetworkInfo;

/**
 * Actuator health check for the Ethereum node, reported as the {@code ethereum} component of
 * {@code /actuator/health} (Spring derives the name from the class name minus "HealthIndicator").
 *
 * <p>The node is UP when it answers both a chain ID and a block number query; any failure
 * (unreachable node, timeout, JSON-RPC error) reports DOWN.
 *
 * <p>Things to know:
 * <ul>
 *   <li>It affects only the overall {@code /actuator/health} status. The liveness and readiness
 *       probe groups don't include it, so a node outage won't make Kubernetes restart the app.</li>
 *   <li>Component details (chain ID, block number) are hidden unless
 *       {@code management.endpoint.health.show-details} is enabled.</li>
 *   <li>The node URL is deliberately not reported, as provider URLs often embed an API key.</li>
 *   <li>Every health request makes two live RPC calls, which counts against provider rate
 *       limits if the endpoint is polled frequently.</li>
 * </ul>
 */
@Component
public class EthereumHealthIndicator extends AbstractHealthIndicator {

    @Autowired
    private BlockchainService blockchainService;

    /**
     * Sets the message logged (with the exception) when the check fails. When details are shown,
     * the failure also appears as an {@code error} detail holding the exception's class and
     * message; {@code BlockchainException} messages name the RPC method, never the node URL.
     */
    public EthereumHealthIndicator() {
        super("Ethereum node health check failed");
    }

    /**
     * Queries the node through {@link BlockchainService}. No try/catch is needed here:
     * {@link AbstractHealthIndicator} turns any exception thrown by this method, such as a
     * {@code BlockchainException}, into a DOWN status.
     */
    @Override
    protected void doHealthCheck(Health.Builder builder) {
        NetworkInfo info = blockchainService.getNetworkInfo();
        builder.up()
                .withDetail("chainId", info.chainId())
                .withDetail("blockNumber", info.blockNumber());
    }
}
