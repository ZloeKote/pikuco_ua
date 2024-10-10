package com.pikuco.userservice.repository;

import com.pikuco.userservice.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
    @Query("SELECT t FROM Token t INNER JOIN  User u ON t.user.id = u.id " +
            "WHERE u.id = :userId and (t.expired = false OR t.revoked = false)")
    List<Token> findAllValidTokensByUser(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.user.id = :userId")
    void deleteAllByUser(Long userId);

    Optional<Token> findByToken(String token);
}
