package ru.yandex.practicum.oauth.user;

import java.util.List;

import jakarta.persistence.*;

import ru.yandex.practicum.common.dao.converter.SpaceSeparatedListConverter;

import lombok.*;

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
