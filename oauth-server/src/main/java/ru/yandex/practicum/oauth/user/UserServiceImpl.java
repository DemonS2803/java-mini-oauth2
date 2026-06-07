package ru.yandex.practicum.oauth.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        User user = getUserByUsernameOrThrow(username);

//        if (!passwordUtil.checkPassword(password, user.get().getPasswordHash())) {
//            throw new InvalidCredentialsException("User " + username + " entered invalid credentials");
//        }
        // bool ret type is just a marker that always is OK
        return passwordUtil.checkPassword(password, user.getPasswordHash());
    }

    @Override
    public User getUserByUsername(String username) {
        return getUserByUsernameOrThrow(username);
    }

    private User getUserByUsernameOrThrow(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new NotFoundException("User " + username + " not found"));
    }
}
