package ru.yandex.practicum.oauth.user;

public interface UserService {

    boolean checkUserPassword(String username, String password);

    User getUserByUsername(String username);

}
