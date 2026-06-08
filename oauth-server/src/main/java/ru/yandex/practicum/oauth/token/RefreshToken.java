package ru.yandex.practicum.oauth.token;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;

import ru.yandex.practicum.common.dao.converter.SpaceSeparatedListConverter;
import ru.yandex.practicum.oauth.client.Client;
import ru.yandex.practicum.oauth.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @JoinColumn(name = "username")
    private User user;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    @Column(name = "exp")
    private LocalDateTime expiredAt;
    private boolean rotated;
    @Convert(converter = SpaceSeparatedListConverter.class)
    private List<String> scopes;

    @Override
    public String toString() {
        return "RefreshToken{" +
                "id=" + id +
                ", user=" + user +
                ", client=" + client +
                ", expiredAt=" + expiredAt +
                ", rotated=" + rotated +
                ", scopes=" + scopes +
                '}';
    }
}
