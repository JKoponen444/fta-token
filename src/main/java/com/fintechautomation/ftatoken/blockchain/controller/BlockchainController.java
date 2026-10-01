package com.fintechautomation.ftatoken.blockchain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fintechautomation.ftatoken.blockchain.service.BlockchainService;
import com.fintechautomation.ftatoken.common.ApiResult;

@RestController
@RequestMapping("/api/blockchain")
public class BlockchainController {

    @Autowired
    private BlockchainService blockchainService;

    @GetMapping("/status")
    public ApiResult status() {
        return ApiResult.success(blockchainService.getNetworkInfo());
    }
}
