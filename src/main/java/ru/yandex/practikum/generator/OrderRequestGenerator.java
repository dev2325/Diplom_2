package ru.yandex.practikum.generator;

import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import ru.yandex.practikum.client.IngredientsActions;
import ru.yandex.practikum.client.OrderActions;
import ru.yandex.practikum.dto.OrderRequest;

import java.util.ArrayList;
import java.util.List;

import static ru.yandex.practikum.config.Config.*;

public class OrderRequestGenerator {

    static IngredientsActions ingredientsActions = new IngredientsActions();

    public static OrderRequest getRandomOrderRequest() {
        OrderRequest orderRequest = new OrderRequest();
        Response ingredientsResponse = ingredientsActions.getAllIngredients();

        // подготовим список для формирования объекта заказа
        ArrayList<String> ingredientsList = new ArrayList<>();
        String bunId = null;
        String mainId = null;
        String sauceId = null;

        // по количеству id узнаем число доступных ингредиентов в ответе
        List<String> ids = ingredientsResponse.jsonPath().getList("data._id");

        // пройдемся по ингредиентам, возьмем по одному ингредиенту каждого типа и добавим их в список
        for (int i = 0; i < ids.size(); i++) {
            String type = String.format("data[%s].type", i);
            String id = String.format("data[%s]._id", i);

            if (INGREDIENT_BUN.equals(ingredientsResponse.then().extract().body().path(type)) && bunId == null) {
                bunId = ingredientsResponse.then().extract().body().path(id);
                ingredientsList.add(bunId);
            } else if (INGREDIENT_MAIN.equals(ingredientsResponse.then().extract().body().path(type)) && mainId == null) {
                mainId = ingredientsResponse.then().extract().body().path(id);
                ingredientsList.add(mainId);
            } else if (INGREDIENT_SAUCE.equals(ingredientsResponse.then().extract().body().path(type)) && sauceId == null) {
                sauceId = ingredientsResponse.then().extract().body().path(id);
                ingredientsList.add(sauceId);
            }
        }
        orderRequest.setIngredients(ingredientsList);
        return orderRequest;
    }

    public static OrderRequest getOrderWithInvalidIngredientRequest() {
        OrderRequest orderRequest = new OrderRequest();

        String invalidIngredient = RandomStringUtils.randomAlphabetic(24).toLowerCase();
        ArrayList<String> ingredientsList = new ArrayList<>();
        ingredientsList.add(invalidIngredient);
        orderRequest.setIngredients(ingredientsList);
        return orderRequest;
    }
}
