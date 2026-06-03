package ru.yandex.practicum.oauth.common;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {

    // Метод для хеширования (кодирования) пароля
    public String hashPassword(String plainPassword) {
        String salt = BCrypt.gensalt(12); // 12 – фактор сложности
        return BCrypt.hashpw(plainPassword, salt);
    }

    // Метод для проверки пароля
    public boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

//    // Пример использования
//    public static void main(String[] args) {
//        String password = "mySecret123";
//
//        // Хешируем
//        String hashed = hashPassword(password);
//        System.out.println("Хэш: " + hashed);
//
//        // Проверяем правильный пароль
//        boolean ok = checkPassword("mySecret123", hashed);
//        System.out.println("Правильный пароль? " + ok);
//
//        // Проверяем неправильный пароль
//        boolean bad = checkPassword("wrongPass", hashed);
//        System.out.println("Неправильный пароль? " + bad);
//    }
}

