package ru.yandex.practikum.generator;

import org.apache.commons.lang3.RandomStringUtils;
import ru.yandex.practikum.dto.LoginRequest;
import ru.yandex.practikum.dto.RegisterUserRequest;

public class LoginRequestGenerator {

    public static LoginRequest prepareFrom(RegisterUserRequest registerUserRequest) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(registerUserRequest.getEmail());
        loginRequest.setPassword(registerUserRequest.getPassword());
        return loginRequest;
    }

    public static LoginRequest changeEmailFrom(LoginRequest loginRequest) {
        loginRequest.setEmail(getNewRandomEmail());
        loginRequest.setPassword(loginRequest.getPassword());
        return loginRequest;
    }

    public static LoginRequest changePasswordFrom(LoginRequest loginRequest) {
        loginRequest.setEmail(loginRequest.getEmail());
        loginRequest.setPassword(RandomStringUtils.randomAlphabetic(5));
        return loginRequest;
    }

    public static String getNewRandomEmail() {
        return RandomStringUtils.randomAlphabetic(5).toLowerCase() + "@"
                + RandomStringUtils.randomAlphabetic(5).toLowerCase() + ".com";
    }
}
