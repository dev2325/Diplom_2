import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import ru.yandex.practikum.client.UserActions;
import ru.yandex.practikum.dto.LoginRequest;
import ru.yandex.practikum.dto.RegisterUserRequest;
import ru.yandex.practikum.generator.LoginRequestGenerator;
import ru.yandex.practikum.generator.RegisterUserRequestGenerator;

import static org.hamcrest.CoreMatchers.equalTo;

public class LoginTest {

    // создали объект pojo и записали туда объект с рандомными данными
    RegisterUserRequest randomRegisterUserRequest = RegisterUserRequestGenerator.getRandomRegisterUserRequest();

    UserActions userActions = new UserActions();
    private String bearerToken;

    @Test
    @DisplayName("Login user positive test")
    public void loginUserPositiveTest() {
        userActions.createUser(randomRegisterUserRequest); // регистрируем юзера
        // подготовим объект избавляясь от "name", оставим только данные для авторизации
        LoginRequest loginRequest = LoginRequestGenerator.prepareFrom(randomRegisterUserRequest);

        // пробуем авторизоваться, проверим код ответа и сохраним токен
        Response responseLogin = userActions.login(loginRequest);
        responseLogin.then().assertThat().statusCode(200);
        bearerToken = userActions.loginAndGetBearerToken(loginRequest);
    }

    @Test
    @DisplayName("Try to login with incorrect email then 401 error")
    public void tryLoginUserIncorrectEmailThenUnauthorizedError() {
        userActions.createUser(randomRegisterUserRequest); // регистрируем юзера
        // подготовим объект избавляясь от "name", оставим только данные для авторизации
        LoginRequest loginRequest = LoginRequestGenerator.prepareFrom(randomRegisterUserRequest);

        // проверяем что c этими данными заходит и сохраним токен юзера
        Response responseLogin = userActions.login(loginRequest);
        responseLogin.then().assertThat().statusCode(200);
        bearerToken = userActions.loginAndGetBearerToken(loginRequest);

        loginRequest = LoginRequestGenerator.changeEmailFrom(loginRequest); // меняем текущий email юзера
        Response responseChanged = userActions.login(loginRequest); // попытка авторизации c измененным email
        responseChanged.then().assertThat().statusCode(401);
        responseChanged.then().assertThat().body("success", equalTo(false));
    }

    @Test
    @DisplayName("Try to login with incorrect password then 401 error")
    public void tryLoginUserIncorrectPasswordThenUnauthorizedError() {
        userActions.createUser(randomRegisterUserRequest); // регистрируем юзера
        // подготовим объект избавляясь от "name", оставим только данные для авторизации
        LoginRequest loginRequest = LoginRequestGenerator.prepareFrom(randomRegisterUserRequest);

        // проверяем что c этими данными заходит и сохраним токен юзера
        Response responseLogin = userActions.login(loginRequest);
        responseLogin.then().assertThat().statusCode(200);
        bearerToken = userActions.loginAndGetBearerToken(loginRequest);

        loginRequest = LoginRequestGenerator.changePasswordFrom(loginRequest); // меняем текущий password юзера
        Response responseChanged = userActions.login(loginRequest); // попытка авторизации c измененным password
        responseChanged.then().assertThat().statusCode(401);
        responseChanged.then().assertThat().body("success", equalTo(false));
    }

    @After
    public void cleanData() {
        if (bearerToken != null) {
            Response responseDelete = userActions.deleteUser(bearerToken);
            responseDelete.then().assertThat().statusCode(202);
            responseDelete.then().assertThat().body("success", equalTo(true));
        }
    }
}
