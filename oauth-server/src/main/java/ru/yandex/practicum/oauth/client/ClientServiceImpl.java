package ru.yandex.practicum.oauth.client;

import ru.yandex.practicum.common.exception.NotFoundException;
import ru.yandex.practicum.oauth.common.PasswordUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PasswordUtil passwordUtil;

    @Override
    public boolean checkClientSecret(String clientId, String secret) {
        Client client = getClientByIdOrThrow(clientId);
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
