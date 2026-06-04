package ru.yandex.practicum.oauth.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefreshIndexRepository extends JpaRepository<RefreshToken, UUID> {
}
