package com.fintechautomation.ftatoken.blockchain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthChainId;

@ExtendWith(MockitoExtension.class)
class BlockchainServiceTests {

    @Mock
    private Web3j web3j;

    @Mock
    private Request<?, EthChainId> chainIdRequest;

    @Mock
    private Request<?, EthBlockNumber> blockNumberRequest;

    @InjectMocks
    private BlockchainService service;

    @Test
    void returnsNetworkInfoWhenNodeResponds() throws IOException {
        doReturn(chainIdRequest).when(web3j).ethChainId();
        doReturn(blockNumberRequest).when(web3j).ethBlockNumber();
        when(chainIdRequest.send()).thenReturn(result(new EthChainId(), "0x14a34"));
        when(blockNumberRequest.send()).thenReturn(result(new EthBlockNumber(), "0x10"));

        assertThat(service.getNetworkInfo())
                .isEqualTo(new NetworkInfo(BigInteger.valueOf(84532), BigInteger.valueOf(16)));
    }

    @Test
    void throwsWhenNodeReturnsRpcError() throws IOException {
        EthChainId error = new EthChainId();
        error.setError(new Response.Error(-32601, "method not found"));
        doReturn(chainIdRequest).when(web3j).ethChainId();
        when(chainIdRequest.send()).thenReturn(error);

        assertThatThrownBy(service::getNetworkInfo)
                .isInstanceOf(BlockchainException.class)
                .hasMessage("Ethereum node returned error -32601: method not found");
    }

    @Test
    void throwsWhenNodeUnreachable() throws IOException {
        doReturn(chainIdRequest).when(web3j).ethChainId();
        when(chainIdRequest.send()).thenThrow(new IOException("Connection refused"));

        assertThatThrownBy(service::getNetworkInfo)
                .isInstanceOf(BlockchainException.class)
                .hasCauseInstanceOf(IOException.class);
    }

    private static <T extends Response<String>> T result(T response, String value) {
        response.setResult(value);
        return response;
    }
}
