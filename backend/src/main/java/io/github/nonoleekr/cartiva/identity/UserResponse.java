package io.github.nonoleekr.cartiva.identity;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserResponse(
    UUID id,
    String username,
    String email,
    String displayName,
    UserStatus status,
    Set<RoleName> roles
) {
    static UserResponse from(User user) {
        Set<RoleName> roleNames = user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toUnmodifiableSet());

        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getDisplayName(),
            user.getStatus(),
            roleNames
        );
    }
}
