package ru.yandex.practikum.client;

import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import ru.yandex.practikum.dto.LoginRequest;

import static io.restassured.RestAssured.given;
import static ru.yandex.practikum.config.Config.*;

public class UserActions {

    @Step("Create user")
    public Response createUser(Object randomUserRequest) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .and()
                .body(randomUserRequest)
                .when()
                .post(AUTH_REGISTER);
    }

    @Step("Log in")
    public Response login(LoginRequest loginRequest) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .and()
                .body(loginRequest)
                .when()
                .post(AUTH_LOGIN);
    }

    @Step("Log in and get bearer token from server response")
    public String loginAndGetBearerToken(LoginRequest loginRequest) {
        Response responseLogin = login(loginRequest); // авторизуемся и сохраним ответ
        JsonPath jsonPathEvaluator = responseLogin.jsonPath(); // извлечем accessToken из ответа и сохраним его
        return jsonPathEvaluator.get("accessToken");
    }

    @Step("Delete user")
    public Response deleteUser(String bearerToken) {
        return given()
                .header("Authorization", bearerToken)
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .delete(AUTH_USER);
    }

    @Step("Patch user data")
    public Response patchUserData(Object request, String bearerToken) {
        return given()
                .header("Authorization", bearerToken)
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .and()
                .body(request)
                .when()
                .patch(AUTH_USER);
    }
}
