package com.fintechautomation.ftatoken.web;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fintechautomation.ftatoken.common.ApiResult;

@RestController
@RequestMapping("/api")
public class PingController {

    @Value("${spring.application.name}")
    private String applicationName;

    @GetMapping("/ping")
    public ApiResult ping() {
        return ApiResult.success(new PingResponse(applicationName, "ok", Instant.now()));
    }

    public record PingResponse(String service, String status, Instant timestamp) {
    }
}
