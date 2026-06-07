package ru.yandex.practicum.oauth.token;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.common.dao.converter.SpaceSeparatedListConverter;
import ru.yandex.practicum.oauth.client.Client;
import ru.yandex.practicum.oauth.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "refresh_index")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "refresh_id")
    private UUID id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Client client;
    @Column(name = "exp")
    private LocalDateTime expiredAt;
    private boolean rotated;
    @Convert(converter = SpaceSeparatedListConverter.class)
    private List<String> scopes;

}
