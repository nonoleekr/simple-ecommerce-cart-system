package io.github.nonoleekr.cartiva.identity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Account registration and authentication")
public class RegistrationController {
    private final RegistrationService registrationService;
    private final AuthenticationService authenticationService;

    public RegistrationController(
        RegistrationService registrationService,
        AuthenticationService authenticationService
    ) {
        this.registrationService = registrationService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a customer account")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegistrationRequest request) {
        UserResponse user = registrationService.registerCustomer(request);
        return ResponseEntity.created(URI.create("/api/v1/users/" + user.id())).body(user);
    }

    @PostMapping("/login")
    @Operation(summary = "Log in with a username or email")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }
}
