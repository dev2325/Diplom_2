package ru.yandex.practikum.client;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static ru.yandex.practikum.config.Config.*;

public class OrderActions {

    public Response placeOrder(Object request, String bearerToken) {
        return given()
                .header("Authorization", bearerToken)
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .and()
                .body(request)
                .when()
                .post(ORDERS);
    }

    public Response getUserOrders(String bearerToken) {
        return given()
                .header("Authorization", bearerToken)
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .get(ORDERS);
    }

}
