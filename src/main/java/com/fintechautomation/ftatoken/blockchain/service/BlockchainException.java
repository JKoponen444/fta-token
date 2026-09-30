package com.fintechautomation.ftatoken.blockchain.service;

/**
 * Thrown when the Ethereum node cannot be reached or returns a JSON-RPC error.
 */
public class BlockchainException extends RuntimeException {

    public BlockchainException(String message) {
        super(message);
    }

    public BlockchainException(String message, Throwable cause) {
        super(message, cause);
    }
}
