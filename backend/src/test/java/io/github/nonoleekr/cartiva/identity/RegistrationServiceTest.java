package io.github.nonoleekr.cartiva.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private RegistrationService registrationService;

    @BeforeEach
    void setUp() {
        registrationService = new RegistrationService(userRepository, roleRepository, passwordEncoder);
    }

    @Test
    void registersNormalizedCustomerWithEncodedPassword() {
        Role customerRole = mock(Role.class);
        when(customerRole.getName()).thenReturn(RoleName.CUSTOMER);
        when(roleRepository.findByName(RoleName.CUSTOMER)).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("a-secure-password")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = registrationService.registerCustomer(
            new RegistrationRequest(
                "  Shopper.One  ",
                "  SHOPPER@EXAMPLE.COM  ",
                "a-secure-password",
                "  Shopper One  "
            )
        );

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getUsername()).isEqualTo("shopper.one");
        assertThat(savedUser.getEmail()).isEqualTo("shopper@example.com");
        assertThat(savedUser.getPasswordHash()).isEqualTo("encoded-password");
        assertThat(response.displayName()).isEqualTo("Shopper One");
        assertThat(response.roles()).containsExactly(RoleName.CUSTOMER);
    }

    @Test
    void rejectsAnExistingUsernameBeforeEncodingPassword() {
        when(userRepository.existsByUsernameIgnoreCase("shopper")).thenReturn(true);

        assertThatThrownBy(() -> registrationService.registerCustomer(
            new RegistrationRequest("shopper", "new@example.com", "a-secure-password", "Shopper")
        ))
            .isInstanceOf(DuplicateIdentityException.class)
            .hasMessage("Username is already registered");
    }
}
