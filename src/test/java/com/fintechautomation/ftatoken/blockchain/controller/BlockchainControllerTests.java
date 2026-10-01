package com.fintechautomation.ftatoken.blockchain.controller;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fintechautomation.ftatoken.blockchain.service.BlockchainException;
import com.fintechautomation.ftatoken.blockchain.service.BlockchainService;
import com.fintechautomation.ftatoken.blockchain.service.NetworkInfo;

@WebMvcTest(BlockchainController.class)
class BlockchainControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BlockchainService blockchainService;

    @Test
    void statusReturnsChainIdAndBlockNumber() throws Exception {
        when(blockchainService.getNetworkInfo())
                .thenReturn(new NetworkInfo(BigInteger.valueOf(84532), BigInteger.valueOf(16)));

        mockMvc.perform(get("/api/blockchain/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.errorMessage").value(nullValue()))
                .andExpect(jsonPath("$.data.chainId").value(84532))
                .andExpect(jsonPath("$.data.blockNumber").value(16));
    }

    @Test
    void statusReturnsBadGatewayWhenNodeFails() throws Exception {
        when(blockchainService.getNetworkInfo())
                .thenThrow(new BlockchainException("Ethereum node request failed: eth_chainId"));

        mockMvc.perform(get("/api/blockchain/status"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code").value(600))
                .andExpect(jsonPath("$.errorMessage").value("Ethereum node request failed: eth_chainId"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }
}
