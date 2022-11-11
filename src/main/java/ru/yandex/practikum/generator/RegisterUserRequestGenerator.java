package ru.yandex.practikum.generator;

import org.apache.commons.lang3.RandomStringUtils;
import ru.yandex.practikum.dto.RegisterUserRequest;

public class RegisterUserRequestGenerator {

    public static RegisterUserRequest getRandomRegisterUserRequest() {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();

        registerUserRequest.setEmail(RandomStringUtils.randomAlphabetic(5).toLowerCase() + "@"
                + RandomStringUtils.randomAlphabetic(5).toLowerCase() + ".com");
        registerUserRequest.setPassword(RandomStringUtils.randomAlphabetic(5));
        registerUserRequest.setName(RandomStringUtils.randomAlphabetic(5));
        return registerUserRequest;
    }
}
