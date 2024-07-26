package com.pikuco.userservice.repository;

import com.pikuco.userservice.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
    @Query("SELECT t FROM Token t INNER JOIN  User u ON t.user.id = u.id " +
            "WHERE u.id = :userId and (t.expired = false OR t.revoked = false)")
    List<Token> findAllValidTokensByUser(Long userId);
    @Query("SELECT t FROM Token t INNER JOIN User u ON t.user.id = u.id WHERE u.id = :userId")
    boolean deleteAllByUser(Long userId);

    Optional<Token> findByToken(String token);
}
