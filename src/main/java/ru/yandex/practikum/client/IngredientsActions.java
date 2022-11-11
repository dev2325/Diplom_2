package ru.yandex.practikum.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static ru.yandex.practikum.config.Config.BASE_URL;
import static ru.yandex.practikum.config.Config.INGREDIENTS;

public class IngredientsActions {

    @Step("Get all ingredients")
    public Response getAllIngredients() {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .get(INGREDIENTS);
    }
}
