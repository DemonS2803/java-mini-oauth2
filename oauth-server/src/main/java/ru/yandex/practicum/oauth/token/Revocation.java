package ru.yandex.practicum.oauth.token;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "revocation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Revocation {

    @EmbeddedId
    private TypeTokenId id;
    private LocalDateTime revokedAt;

}
