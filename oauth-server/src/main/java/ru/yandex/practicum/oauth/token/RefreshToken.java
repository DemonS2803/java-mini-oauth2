package ru.yandex.practicum.oauth.token;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.oauth.client.Client;
import ru.yandex.practicum.oauth.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_index")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

    @Id
    @Column(name = "refresh_id")
    private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Client client;
    @Column(name = "exp")
    private LocalDateTime expiredAt;

}
