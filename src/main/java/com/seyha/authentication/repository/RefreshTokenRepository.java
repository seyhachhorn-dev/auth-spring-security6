package com.seyha.authentication.repository;

import com.seyha.authentication.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Developed by ChhornSeyha
 * Date: 16/02/2026
 */

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    @Query("""
select t from RefreshToken t inner join User u on t.user.id = u.id
where u.id = :userId and (t.revoked = false)
""")
    List<RefreshToken> findAllValidTokenByUser(UUID userId);
}
