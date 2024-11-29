package com.seek.authentication_service.repository;

import com.seek.authentication_service.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String username);

    Optional<User> findByUsernameAndUuidNot(String username, UUID uuid);
    Optional<User> findByEmailAndUuidNot(String username, UUID uuid);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByPhoneNumberAndUuidNot(String phoneNumber, UUID uuid);

    Optional<User> findByUsername(String username);
    Optional<User> findByEmailOrUsername(String email, String username);
}
