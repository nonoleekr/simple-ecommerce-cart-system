package io.github.nonoleekr.cartiva.system;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
@Tag(name = "System", description = "Public API metadata")
public class SystemController {
    private final String applicationName;
    private final String applicationVersion;

    public SystemController(
        @Value("${spring.application.name}") String applicationName,
        @Value("${info.app.version:development}") String applicationVersion
    ) {
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
    }

    @GetMapping
    @Operation(summary = "Get API metadata")
    public ResponseEntity<SystemResponse> getSystemInformation() {
        return ResponseEntity.ok(
            new SystemResponse(applicationName, applicationVersion, Instant.now())
        );
    }

    public record SystemResponse(String name, String version, Instant currentTime) {
    }
}
