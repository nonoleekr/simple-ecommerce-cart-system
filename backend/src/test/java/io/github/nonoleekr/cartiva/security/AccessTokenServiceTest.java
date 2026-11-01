package io.github.nonoleekr.cartiva.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.nonoleekr.cartiva.identity.Role;
import io.github.nonoleekr.cartiva.identity.RoleName;
import io.github.nonoleekr.cartiva.identity.User;
import java.time.Duration;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class AccessTokenServiceTest {
    @Test
    void issuesAnIssuerValidatedTokenWithIdentityClaims() {
        SecurityConfiguration configuration = new SecurityConfiguration();
        SecretKey key = configuration.jwtSecretKey("a-test-secret-that-is-at-least-32-bytes-long");
        JwtEncoder encoder = configuration.jwtEncoder(key);
        JwtDecoder decoder = configuration.jwtDecoder(key, "https://cartiva.test");
        AccessTokenService tokenService = new AccessTokenService(
            encoder,
            "https://cartiva.test",
            Duration.ofMinutes(15)
        );
        Role role = mock(Role.class);
        when(role.getName()).thenReturn(RoleName.CUSTOMER);
        User user = new User(
            "shopper",
            "shopper@example.com",
            "encoded-password",
            "Shopper",
            role
        );

        IssuedToken issuedToken = tokenService.issue(user);
        Jwt decodedToken = decoder.decode(issuedToken.value());

        assertThat(decodedToken.getIssuer().toString()).isEqualTo("https://cartiva.test");
        assertThat(decodedToken.getSubject()).isEqualTo(user.getId().toString());
        assertThat(decodedToken.getClaimAsString("username")).isEqualTo("shopper");
        assertThat(decodedToken.getClaimAsStringList("roles")).containsExactly("CUSTOMER");
        assertThat(decodedToken.getExpiresAt()).isEqualTo(issuedToken.expiresAt());

        JwtAuthenticationToken authentication = (JwtAuthenticationToken) configuration
            .jwtAuthenticationConverter()
            .convert(decodedToken);
        assertThat(authentication.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .contains("ROLE_CUSTOMER", "FACTOR_BEARER");
    }
}
