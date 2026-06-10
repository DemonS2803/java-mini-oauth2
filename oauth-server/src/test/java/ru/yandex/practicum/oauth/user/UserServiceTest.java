package ru.yandex.practicum.oauth.user;

import jakarta.transaction.Transactional;

import ru.yandex.practicum.common.exception.NotFoundException;
import ru.yandex.practicum.oauth.AuthApp;
import ru.yandex.practicum.oauth.common.PasswordUtil;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = AuthApp.class)
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordUtil passwordUtil;
    @Autowired
    private UserRepository userRepository;

    public static final String VALID_USERNAME = "Nick";
    public static final String VALID_PASSWORD = "password";
    public static final String VALID_USER_INFO = "info";


    @Test
    void checkUserPassword_shouldSuccess() {
        User user = getValidUser();
        user = userRepository.save(user);

        assertTrue(userService.checkUserPassword(VALID_USERNAME, VALID_PASSWORD));
    }

    @Test
    void checkUserPassword_shouldThrowNotFound() {
        User user = getValidUser();
        user = userRepository.save(user);

        assertThrows(NotFoundException.class, () -> {
            userService.checkUserPassword("testsuer", VALID_PASSWORD);
        });
    }


    @Test
    void checkUserPassword_shouldThrowInvalidPassword() {
        User user = getValidUser();
        user = userRepository.save(user);

        assertFalse(userService.checkUserPassword(VALID_USERNAME, "foo"));
    }

    private User getValidUser() {
        User user = new User();
        user.setUsername(VALID_USERNAME);
        user.setPasswordHash(passwordUtil.hashPassword(VALID_PASSWORD));
        user.setInfo(VALID_USER_INFO);
        return user;
    }

}
