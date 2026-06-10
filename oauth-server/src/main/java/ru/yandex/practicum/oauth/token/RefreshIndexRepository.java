package ru.yandex.practicum.oauth.token;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RefreshIndexRepository extends JpaRepository<RefreshToken, UUID> {

    @EntityGraph(attributePaths = {"client"})
    Optional<RefreshToken> findRefreshTokenById(UUID id);

}
