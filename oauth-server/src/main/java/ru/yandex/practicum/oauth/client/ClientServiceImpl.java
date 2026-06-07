package ru.yandex.practicum.oauth.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        Client client = getClientByIdOrThrow(clientId);

//        if (!passwordUtil.checkPassword(secret, client.getSecretHash())) {
//            throw new InvalidCredentialsException("Client " + clientId + " invalid credentials");
//        }
        return passwordUtil.checkPassword(secret, client.getSecretHash());
    }

    @Override
    public Client getClientById(String clientId) {
        return getClientByIdOrThrow(clientId);
    }

    private Client getClientByIdOrThrow(String clientId) {
        return clientRepository.findClientByClientId(clientId)
                .orElseThrow(() -> new NotFoundException("Client " + clientId + " not found"));
    }

}
