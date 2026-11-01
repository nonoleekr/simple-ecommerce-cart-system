package io.github.nonoleekr.cartiva.identity;

import io.github.nonoleekr.cartiva.security.AccessTokenService;
import io.github.nonoleekr.cartiva.security.IssuedToken;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenService accessTokenService;

    public AuthenticationService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AccessTokenService accessTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.accessTokenService = accessTokenService;
    }

    @Transactional(readOnly = true)
    public AuthenticationResponse login(LoginRequest request) {
        String identifier = request.identifier().trim().toLowerCase(Locale.ROOT);
        User user = userRepository
            .findByUsernameIgnoreCaseOrEmailIgnoreCase(identifier, identifier)
            .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AccountUnavailableException();
        }

        IssuedToken token = accessTokenService.issue(user);
        return new AuthenticationResponse(
            token.value(),
            "Bearer",
            token.expiresAt(),
            UserResponse.from(user)
        );
    }
}
