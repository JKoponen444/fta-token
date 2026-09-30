package com.fintechautomation.ftatoken.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(GlobalExceptionHandlerTests.TestController.class)
@Import(GlobalExceptionHandlerTests.TestController.class)
class GlobalExceptionHandlerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unexpectedExceptionReturnsDefaultFailure() throws Exception {
        mockMvc.perform(get("/test/boom"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("""
                        {
                            "code": 600,
                            "errorCode": null,
                            "errorMessage": "The request failed",
                            "error": null,
                            "data": null
                        }
                        """, JsonCompareMode.STRICT));
    }

    @Test
    void validationErrorsAreReturnedPerField() throws Exception {
        mockMvc.perform(post("/test/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(600))
                .andExpect(jsonPath("$.errorMessage").value("Validation failed"))
                .andExpect(jsonPath("$.error.name").value("must not be blank"));
    }

    @Test
    void malformedJsonReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/test/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(600))
                .andExpect(jsonPath("$.errorMessage").value("Failed to read request"));
    }

    @Test
    void unknownPathReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(600));
    }

    @RestController
    static class TestController {

        @GetMapping("/test/boom")
        ApiResult boom() {
            throw new IllegalStateException("internal detail that must not leak");
        }

        @PostMapping("/test/validate")
        ApiResult validate(@Valid @RequestBody NameRequest request) {
            return ApiResult.success(request);
        }
    }

    record NameRequest(@NotBlank String name) {
    }
}
