package com.fintechautomation.ftatoken.blockchain.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

import com.fintechautomation.ftatoken.blockchain.service.BlockchainException;
import com.fintechautomation.ftatoken.blockchain.service.BlockchainService;
import com.fintechautomation.ftatoken.blockchain.service.NetworkInfo;

@ExtendWith(MockitoExtension.class)
class EthereumHealthIndicatorTests {

    @Mock
    private BlockchainService blockchainService;

    @InjectMocks
    private EthereumHealthIndicator indicator;

    @Test
    void upWithChainDetailsWhenNodeResponds() {
        when(blockchainService.getNetworkInfo())
                .thenReturn(new NetworkInfo(BigInteger.valueOf(84532), BigInteger.valueOf(16)));

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails())
                .containsEntry("chainId", BigInteger.valueOf(84532))
                .containsEntry("blockNumber", BigInteger.valueOf(16));
    }

    @Test
    void downWhenNodeFails() {
        when(blockchainService.getNetworkInfo()).thenThrow(new BlockchainException("Connection refused"));

        assertThat(indicator.health().getStatus()).isEqualTo(Status.DOWN);
    }
}
