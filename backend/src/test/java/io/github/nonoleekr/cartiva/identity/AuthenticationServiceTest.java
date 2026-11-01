package io.github.nonoleekr.cartiva.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.nonoleekr.cartiva.security.AccessTokenService;
import io.github.nonoleekr.cartiva.security.IssuedToken;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AccessTokenService accessTokenService;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(
            userRepository,
            passwordEncoder,
            accessTokenService
        );
    }

    @Test
    void logsInByNormalizedIdentifierAndReturnsBearerToken() {
        Role customerRole = mock(Role.class);
        when(customerRole.getName()).thenReturn(RoleName.CUSTOMER);
        User user = new User(
            "shopper",
            "shopper@example.com",
            "encoded-password",
            "Shopper",
            customerRole
        );
        Instant expiresAt = Instant.parse("2030-01-01T00:00:00Z");
        when(userRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(
            "shopper@example.com",
            "shopper@example.com"
        )).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("a-secure-password", "encoded-password")).thenReturn(true);
        when(accessTokenService.issue(user)).thenReturn(new IssuedToken("signed-token", expiresAt));

        AuthenticationResponse response = authenticationService.login(
            new LoginRequest("  SHOPPER@EXAMPLE.COM  ", "a-secure-password")
        );

        assertThat(response.accessToken()).isEqualTo("signed-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresAt()).isEqualTo(expiresAt);
        assertThat(response.user().username()).isEqualTo("shopper");
        verify(accessTokenService).issue(user);
    }

    @Test
    void rejectsAnIncorrectPasswordWithoutIssuingAToken() {
        Role customerRole = mock(Role.class);
        User user = new User(
            "shopper",
            "shopper@example.com",
            "encoded-password",
            "Shopper",
            customerRole
        );
        when(userRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase("shopper", "shopper"))
            .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> authenticationService.login(
            new LoginRequest("shopper", "wrong-password")
        )).isInstanceOf(InvalidCredentialsException.class);
    }
}
