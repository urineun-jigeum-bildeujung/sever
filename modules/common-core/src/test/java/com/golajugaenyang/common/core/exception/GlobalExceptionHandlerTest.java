package com.golajugaenyang.common.core.exception;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.golajugaenyang.common.core.exception.fixture.TestExceptionController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = TestExceptionController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("AppException은 errorCode를 포함한 ProblemDetail로 응답한다.")
    void appExceptionReturnsProblemDetailWithErrorCode() throws Exception {
        mockMvc.perform(get("/test/app-exception"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.errorCode").value("SAMPLE_404"))
            .andExpect(jsonPath("$.timestamp").doesNotExist())
            .andExpect(jsonPath("$.traceId").doesNotExist());
    }

    @Test
    @DisplayName("예상하지 못한 예외도 ProblemDetail 형식과 errorCode를 포함해 응답한다.")
    void unexpectedExceptionReturnsProblemDetailWithErrorCode() throws Exception {
        mockMvc.perform(get("/test/unexpected-exception"))
            .andExpect(status().isInternalServerError())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.errorCode").value("COMMON_500"))
            .andExpect(jsonPath("$.detail", not(containsString("db connection lost"))));
    }

    @Test
    @DisplayName("Validation 실패시 필드별 에러와 errorCode가 포함된다.")
    void validationFailureReturnsFieldErrorsAndErrorCode() throws Exception {
        mockMvc.perform(post("/test/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("COMMON_400"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("name"));
    }

    @Test
    @DisplayName("필수 파라미터 누락시에도 errorCode가 포함된다.")
    void missingRequiredParameterReturnsErrorCode() throws Exception {
        mockMvc.perform(get("/test/missing-param"))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.errorCode").value("COMMON_400"))
            .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    @DisplayName("잘못된 JSON 요청시에도 errorCode가 포함된다.")
    void malformedJsonReturnsErrorCode() throws Exception {
        mockMvc.perform(post("/test/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid-json "))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.errorCode").value("COMMON_400"));
    }

    @Test
    @DisplayName("지원하지 않는 HTTP 메서드 요청시에도 errorCode가 포함된다.")
    void unsupportedHttpMethodReturnsErrorCode() throws Exception {
        mockMvc.perform(post("/test/app-exception"))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.errorCode").value("COMMON_405"));
    }
}