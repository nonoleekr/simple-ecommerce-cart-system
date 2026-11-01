package io.github.nonoleekr.cartiva.identity;

import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);
}
