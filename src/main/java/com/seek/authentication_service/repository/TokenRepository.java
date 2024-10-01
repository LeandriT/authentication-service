package com.seek.authentication_service.repository;


import com.seek.authentication_service.model.Token;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {

    @Query("""
            select t from Token t inner join User u on t.user.uuid = u.uuid
            where t.user.uuid = :userId and t.loggedOut = false
            """)
    List<Token> findAllTokensByUser(UUID userId);

    Optional<Token> findByToken(String token);
}
