package ru.yandex.practicum.oauth.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.common.exception.InvalidCredentialsException;
import ru.yandex.practicum.common.exception.NotFoundException;
import ru.yandex.practicum.oauth.common.PasswordUtil;

import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordUtil passwordUtil;

    @Override
    public boolean checkUserPassword(String username, String password) {
        Optional<User> user = userRepository.findUserByUsername(username);
        if (user.isEmpty()) {
            throw new NotFoundException("User " + username + " not found");
        }

        log.info("DB user passwd hash {}", user.get().getPasswordHash());
        if (!passwordUtil.checkPassword(password, user.get().getPasswordHash())) {
            throw new InvalidCredentialsException("User " + username + " entered invalid credentials");
        }
        // bool ret type is just a marker that always is OK
        return true;
    }
}
