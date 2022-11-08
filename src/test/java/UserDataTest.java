import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import ru.yandex.practikum.client.UserActions;
import ru.yandex.practikum.dto.LoginRequest;
import ru.yandex.practikum.dto.RegisterUserRequest;
import ru.yandex.practikum.generator.LoginRequestGenerator;
import ru.yandex.practikum.generator.RegisterUserRequestGenerator;

import static org.hamcrest.CoreMatchers.equalTo;
import static ru.yandex.practikum.config.Config.UNAUTHORISED_MESSAGE;

public class UserDataTest {

    // создадим объект pojo и записали туда объект с рандомными данными
    RegisterUserRequest randomRegisterUserRequest = RegisterUserRequestGenerator.getRandomRegisterUserRequest();

    // подготовим объект избавляясь от "name", оставим только данные для авторизации
    LoginRequest loginRequest = LoginRequestGenerator.prepareFrom(randomRegisterUserRequest);

    UserActions userActions = new UserActions();
    private String bearerToken;

    @Test
    @DisplayName("Change user email positive test")
    public void changeUserEmailPositiveTest() {
        prepareNewUser(); // подготовим нового пользователя
        String newEmail = LoginRequestGenerator.getNewRandomEmail(); // подготовим новый email
        loginRequest.setEmail(newEmail); // установим новый пароль объекту

        // обновим данные юзера на сервере передав в запросе измененный объект и токен
        Response responsePatch = userActions.patchUserData(loginRequest, bearerToken);
        responsePatch.then().assertThat().statusCode(200);
        responsePatch.then().assertThat().body("success", equalTo(true));

        JsonPath jsonPathEvaluator = responsePatch.jsonPath();
        String actualEmail = jsonPathEvaluator.get("user.email"); // возьмем email из json-ответа

        // убедимся что email в ответе соответствует тому, который установили
        Assert.assertEquals("Фактический email не соответствует ожидаемому", newEmail, actualEmail);
    }

    @Test
    @DisplayName("Change user name positive test")
    public void changeUserNamePositiveTest() {
        prepareNewUser();
        String newName = RandomStringUtils.randomAlphabetic(5); // подготовим новое имя
        randomRegisterUserRequest.setName(newName); // установим объекту новое имя

        // обновим данные юзера на сервере передав в запросе измененный объект и токен
        Response responsePatch = userActions.patchUserData(randomRegisterUserRequest, bearerToken);
        responsePatch.then().assertThat().statusCode(200);
        responsePatch.then().assertThat().body("success", equalTo(true));

        JsonPath jsonPathEvaluator = responsePatch.jsonPath();
        String actualName = jsonPathEvaluator.get("user.name"); // возьмем имя из json-ответа

        // убедимся что имя в ответе соответствует тому, которое установили
        Assert.assertEquals("Фактическое имя не соответствует ожидаемому", newName, actualName);
    }

    @Test
    @DisplayName("Change user password positive test")
    public void changeUserPasswordPositiveTest() {
        prepareNewUser();
        String newPassword = RandomStringUtils.randomAlphabetic(5); // подготовим новый пароль
        loginRequest.setPassword(newPassword); // установим объекту новый пароль

        // обновим данные юзера на сервере передав в запросе измененный объект и токен
        Response responsePatch = userActions.patchUserData(loginRequest, bearerToken);
        responsePatch.then().assertThat().statusCode(200);
        responsePatch.then().assertThat().body("success", equalTo(true));

        // проверим что с новым паролем можно авторизоваться
        Response responseLogin = userActions.login(loginRequest);
        responseLogin.then().assertThat().statusCode(200);
        responseLogin.then().assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Try to change user password without authorization then 401 error")
    public void tryChangeUserPasswordWithoutAuthorizationThenUnauthorizedError() {
        prepareNewUser();
        String newPassword = RandomStringUtils.randomAlphabetic(5); // подготовим новый пароль
        loginRequest.setPassword(newPassword); // установим объекту новый пароль

        // пробуем обновить данные юзера на сервере передав в запросе измененный объект и пустой токен
        Response responsePatch = userActions.patchUserData(loginRequest, "");
        responsePatch.then().assertThat().statusCode(401);
        responsePatch.then().assertThat().body("success", equalTo(false));
        responsePatch.then().assertThat().body("message", equalTo(UNAUTHORISED_MESSAGE));
    }

    @Test
    @DisplayName("Try to change user email without authorization then 401 error")
    public void tryChangeUserEmailWithoutAuthorizationThenUnauthorizedError() {
        prepareNewUser();
        String newEmail = LoginRequestGenerator.getNewRandomEmail(); // подготовим новый email
        loginRequest.setEmail(newEmail); // установим объекту новый пароль

        // пробуем обновить данные юзера на сервере передав в запросе измененный объект и пустой токен
        Response responsePatch = userActions.patchUserData(loginRequest, "");
        responsePatch.then().assertThat().statusCode(401);
        responsePatch.then().assertThat().body("success", equalTo(false));
        responsePatch.then().assertThat().body("message", equalTo(UNAUTHORISED_MESSAGE));
    }

    @Test
    @DisplayName("Try to change user name without authorization then 401 error")
    public void tryChangeUserNameWithoutAuthorizationThenUnauthorizedError() {
        prepareNewUser();
        String newName = RandomStringUtils.randomAlphabetic(5); // подготовим новое имя
        randomRegisterUserRequest.setName(newName); // установим объекту новое имя

        // пробуем обновить данные юзера передав в запросе измененный объект и пустой токен
        Response responsePatch = userActions.patchUserData(randomRegisterUserRequest, "");
        responsePatch.then().assertThat().statusCode(401);
        responsePatch.then().assertThat().body("success", equalTo(false));
        responsePatch.then().assertThat().body("message", equalTo(UNAUTHORISED_MESSAGE));
    }

    @Step("Prepare new user")
    public void prepareNewUser() {
        Response responseNewUser = userActions.createUser(randomRegisterUserRequest); // регистрируем юзера
        responseNewUser.then().assertThat().statusCode(200);
        responseNewUser.then().assertThat().body("success", equalTo(true));
        bearerToken = userActions.loginAndGetBearerToken(loginRequest); // авторизуемся и сохраним токен юзера
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
