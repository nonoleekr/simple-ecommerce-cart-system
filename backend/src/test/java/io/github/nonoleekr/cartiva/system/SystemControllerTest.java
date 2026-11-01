package io.github.nonoleekr.cartiva.system;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SystemController.class)
@TestPropertySource(properties = {
    "spring.application.name=cartiva-backend-test",
    "info.app.version=test"
})
class SystemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsApiMetadata() throws Exception {
        mockMvc.perform(get("/api/v1/system"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("cartiva-backend-test"))
            .andExpect(jsonPath("$.version").value("test"))
            .andExpect(jsonPath("$.currentTime", not(blankOrNullString())));
    }
}
