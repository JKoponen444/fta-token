package com.fintechautomation.ftatoken.blockchain.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.health.contributor.AbstractHealthIndicator;
import org.springframework.boot.health.contributor.Health;
import org.springframework.stereotype.Component;

import com.fintechautomation.ftatoken.blockchain.service.BlockchainService;
import com.fintechautomation.ftatoken.blockchain.service.NetworkInfo;

/**
 * Reports the Ethereum node as UP when it answers chain ID and block number queries.
 * The node URL is deliberately not exposed, as it often embeds a provider API key.
 */
@Component
public class EthereumHealthIndicator extends AbstractHealthIndicator {

    @Autowired
    private BlockchainService blockchainService;

    public EthereumHealthIndicator() {
        super("Ethereum node health check failed");
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        NetworkInfo info = blockchainService.getNetworkInfo();
        builder.up()
                .withDetail("chainId", info.chainId())
                .withDetail("blockNumber", info.blockNumber());
    }
}
