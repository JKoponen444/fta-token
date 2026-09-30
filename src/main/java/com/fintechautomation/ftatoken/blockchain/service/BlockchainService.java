package com.fintechautomation.ftatoken.blockchain.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;

@Service
public class BlockchainService {

    @Autowired
    private Web3j web3j;

    public NetworkInfo getNetworkInfo() {
        return new NetworkInfo(
                send(web3j.ethChainId()).getChainId(),
                send(web3j.ethBlockNumber()).getBlockNumber());
    }

    private static <T extends Response<?>> T send(Request<?, T> request) {
        T response;
        try {
            response = request.send();
        } catch (IOException e) {
            throw new BlockchainException("Ethereum node request failed: " + request.getMethod(), e);
        }
        if (response.hasError()) {
            Response.Error error = response.getError();
            throw new BlockchainException(
                    "Ethereum node returned error " + error.getCode() + ": " + error.getMessage());
        }
        return response;
    }
}
