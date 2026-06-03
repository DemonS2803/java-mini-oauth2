package ru.yandex.practicum.oauth.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.common.exception.InvalidCredentialsException;
import ru.yandex.practicum.common.exception.NotFoundException;
import ru.yandex.practicum.oauth.common.PasswordUtil;

import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PasswordUtil passwordUtil;

    @Override
    public boolean checkClientSecret(String clientId, String secret) {
        Optional<Client> client = clientRepository.findClientByClientId(clientId);
        if (client.isEmpty()) {
            throw new NotFoundException("Client " + clientId + " not found");
        }

        String hash = passwordUtil.hashPassword(secret);
        if (!passwordUtil.checkPassword(secret, client.get().getSecretHash())) {
            throw new InvalidCredentialsException("Client " + clientId + " invalid credentials");
        }
        return true;
    }

}
