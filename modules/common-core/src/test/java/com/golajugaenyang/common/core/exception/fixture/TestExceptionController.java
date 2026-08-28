package com.golajugaenyang.common.core.exception.fixture;


import com.golajugaenyang.common.core.exception.AppException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestExceptionController {

    @GetMapping("/test/app-exception")
    public void throwAppException() {
        throw new AppException(SampleErrorCode.SAMPLE_NOT_FOUND);
    }

    @GetMapping("/test/unexpected-exception")
    public void throwUnexpected() {
        throw new IllegalStateException("db connection lost");
    }

    @PostMapping("/test/validate")
    public void validate(@Valid @RequestBody SampleRequest request) {
    }

    @GetMapping("/test/missing-param")
    public void missingParam(@RequestParam String requiredParam) {
    }
}
