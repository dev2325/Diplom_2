import io.qameta.allure.Step;
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

public class CreateUserTest {

    // создали объект pojo и записали туда объект с рандомными данными
    RegisterUserRequest randomRegisterUserRequest = RegisterUserRequestGenerator.getRandomRegisterUserRequest();

    UserActions userActions = new UserActions();
    private String bearerToken;

    @Test
    @DisplayName("Create a new user positive test")
    public void createUserPositiveTest() {
        Response responseNewUser = userActions.createUser(randomRegisterUserRequest); // регистрируем юзера и сохраняем ответ
        responseNewUser.then().assertThat().statusCode(200);
        responseNewUser.then().assertThat().body("success", equalTo(true));
        loginAndSetBearerToken(); // авторизуемся и убедимся что юзер действительно создался
    }

    @Test
    @DisplayName("Try to create a duplicate user then 403 error")
    public void tryCreateDuplicateUserThenForbidden() {
        userActions.createUser(randomRegisterUserRequest); // регистрируем юзера
        loginAndSetBearerToken(); // авторизуемся и убедимся что юзер действительно создался

        // пробуем регистрировать еще одного юзера с теми же учетными данными
        Response responseDuplicateUser = userActions.createUser(randomRegisterUserRequest);
        responseDuplicateUser.then().assertThat().statusCode(403);
        responseDuplicateUser.then().assertThat().body("success", equalTo(false));
    }

    @Test
    @DisplayName("Try to create a new user without an email then 403 error")
    public void tryCreateUserWithoutEmailForbidden() {
        randomRegisterUserRequest.setEmail(""); // подготовим объект юзера с пустым email
        Response responseUserWithoutEmail = userActions.createUser(randomRegisterUserRequest); // пробуем регистрировать юзера
        responseUserWithoutEmail.then().assertThat().statusCode(403);
        responseUserWithoutEmail.then().assertThat().body("success", equalTo(false));
    }

    @Test
    @DisplayName("Try to create a new user without a password then 403 error")
    public void tryCreateUserWithoutPasswordForbidden() {
        randomRegisterUserRequest.setPassword(""); // подготовим объект юзера с пустым password
        Response responseUserWithoutPassword = userActions.createUser(randomRegisterUserRequest); // пробуем регистрировать юзера
        responseUserWithoutPassword.then().assertThat().statusCode(403);
        responseUserWithoutPassword.then().assertThat().body("success", equalTo(false));
    }

    @Test
    @DisplayName("Try to create a new user without a name then 403 error")
    public void tryCreateUserWithoutNameForbidden() {
        randomRegisterUserRequest.setName(""); // подготовим объект юзера с пустым name
        Response responseUserWithoutName = userActions.createUser(randomRegisterUserRequest); // пробуем регистрировать юзера
        responseUserWithoutName.then().assertThat().statusCode(403);
        responseUserWithoutName.then().assertThat().body("success", equalTo(false));
    }

    @Step("Log in and save bearer token")
    public void loginAndSetBearerToken() {
        // подготовим объект избавляясь от "name", оставим только данные для авторизации
        LoginRequest loginRequest = LoginRequestGenerator.prepareFrom(randomRegisterUserRequest);

        // авторизуемся, проверим что юзер действительно создался
        Response responseLogin = userActions.login(loginRequest);
        responseLogin.then().assertThat().statusCode(200);
        responseLogin.then().assertThat().body("success", equalTo(true));
        bearerToken = userActions.loginAndGetBearerToken(loginRequest); // и сохраним его токен
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
