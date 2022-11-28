package uz.pdp.appoauth2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appoauth2.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByPhoneNumber(String username);
    Optional<User> findByGoogleUsername(String googleUsername);
    boolean existsByPhoneNumber(String username);
}