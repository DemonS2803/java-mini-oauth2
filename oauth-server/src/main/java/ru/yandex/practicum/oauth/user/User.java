package ru.yandex.practicum.oauth.user;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.common.dao.converter.SpaceSeparatedListConverter;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private String username;
    private String passwordHash;
    private String info;
    @Convert(converter = SpaceSeparatedListConverter.class)
    private List<String> roles;

}
