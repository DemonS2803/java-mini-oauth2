package ru.yandex.practicum.oauth.client;

import jakarta.transaction.Transactional;

import ru.yandex.practicum.common.exception.NotFoundException;
import ru.yandex.practicum.oauth.AuthApp;
import ru.yandex.practicum.oauth.common.PasswordUtil;

import lombok.extern.slf4j.Slf4j;
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
public class ClientServiceTest {

    @Autowired
    private ClientService clientService;
    @Autowired
    private PasswordUtil passwordUtil;
    @Autowired
    private ClientRepository clientRepository;

    public static final String VALID_CLIENT_ID = "client";
    public static final String VALID_SECRET = "super_secret";
    public static final String VALID_USER_INFO = "info";


    @Test
    void checkClientSecret_shouldSuccess() {
        Client client = getValidClient();
        client = clientRepository.save(client);

        assertTrue(clientService.checkClientSecret(VALID_CLIENT_ID, VALID_SECRET));
    }

    @Test
    void checkClientSecret_shouldThrowNotFound() {
        Client client = getValidClient();
        client = clientRepository.save(client);

        assertThrows(NotFoundException.class, () -> {
            clientService.checkClientSecret("testclient", VALID_SECRET);
        });
    }


    @Test
    void checkClientSecret_shouldThrowInvalidPassword() {
        Client user = getValidClient();
        user = clientRepository.save(user);
        assertFalse(clientService.checkClientSecret(VALID_CLIENT_ID, "foo"));
    }

    private Client getValidClient() {
        Client user = new Client();
        user.setClientId(VALID_CLIENT_ID);
        user.setSecretHash(passwordUtil.hashPassword(VALID_SECRET));
        user.setInfo(VALID_USER_INFO);
        return user;
    }

}
