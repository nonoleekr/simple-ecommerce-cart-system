package io.github.nonoleekr.cartiva.identity;

import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(
        UserRepository userRepository,
        RoleRepository roleRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse registerCustomer(RegistrationRequest request) {
        return register(request, RoleName.CUSTOMER);
    }

    @Transactional
    public UserResponse registerSeller(RegistrationRequest request) {
        return register(request, RoleName.SELLER);
    }

    private UserResponse register(RegistrationRequest request, RoleName roleName) {
        String username = request.username().trim().toLowerCase(Locale.ROOT);
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String displayName = request.displayName().trim();

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateIdentityException("Username is already registered");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateIdentityException("Email is already registered");
        }

        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new IdentityConfigurationException(roleName + " role is not configured"));
        User user = new User(
            username,
            email,
            passwordEncoder.encode(request.password()),
            displayName,
            role
        );

        return UserResponse.from(userRepository.save(user));
    }
}
