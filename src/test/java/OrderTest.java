import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import ru.yandex.practikum.client.OrderActions;
import ru.yandex.practikum.client.UserActions;
import ru.yandex.practikum.dto.LoginRequest;
import ru.yandex.practikum.dto.OrderRequest;
import ru.yandex.practikum.dto.RegisterUserRequest;
import ru.yandex.practikum.generator.LoginRequestGenerator;
import ru.yandex.practikum.generator.OrderRequestGenerator;
import ru.yandex.practikum.generator.RegisterUserRequestGenerator;

import static org.hamcrest.CoreMatchers.equalTo;
import static ru.yandex.practikum.config.Config.UNAUTHORISED_MESSAGE;

public class OrderTest {

    OrderActions orderActions = new OrderActions();
    String bearerToken;

    @Test
    @DisplayName("Place a new order positive test")
    public void placeOrderPositiveTest() {
        prepareNewUser(); // подготовим нового пользователя
        // создаем объект и записываем туда объект с рандомными данными заказа
        OrderRequest randomOrderRequest = OrderRequestGenerator.getRandomOrderRequest();

        // размещаем заказ и проверяем ответ
        Response responsePlaceOrder = orderActions.placeOrder(randomOrderRequest, bearerToken);
        responsePlaceOrder.then().assertThat().statusCode(200);
        responsePlaceOrder.then().assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Try to place a new order without ingredients then 400 error")
    public void tryPlaceOrderWithoutIngredientsThenBadRequest() {
        prepareNewUser();
        OrderRequest orderWithoutIngredientsRequest = new OrderRequest(); // создаем пустой объект заказа

        // пробуем размещать заказ без ингредиентов
        Response responsePlaceOrder = orderActions.placeOrder(orderWithoutIngredientsRequest, bearerToken);
        responsePlaceOrder.then().assertThat().statusCode(400);
        responsePlaceOrder.then().assertThat().body("success", equalTo(false));
    }

    @Test
    @DisplayName("Place a new order without authorization positive test")
    public void placeOrderWithoutAuthorizationPositiveTest() {
        // создали объект и записали туда объект с рандомными данными заказа
        OrderRequest randomOrderRequest = OrderRequestGenerator.getRandomOrderRequest();

        // пробуем размещать заказ без авторизации, с пустым токеном
        Response responsePlaceOrder = orderActions.placeOrder(randomOrderRequest, "");
        responsePlaceOrder.then().assertThat().statusCode(200);
        responsePlaceOrder.then().assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Try to place an order without ingredients and without authorization then 400 error")
    public void tryPlaceOrderWithoutAuthorizationAndWithoutIngredientsThenBadRequest() {
        OrderRequest orderWithoutIngredientsRequest = new OrderRequest(); // создаем пустой объект заказа

        // пробуем размещать заказ без ингредиентов и без токена
        Response responsePlaceOrder = orderActions.placeOrder(orderWithoutIngredientsRequest, "");
        responsePlaceOrder.then().assertThat().statusCode(400);
        responsePlaceOrder.then().assertThat().body("success", equalTo(false));
    }

    @Test
    @DisplayName("Try to place a new order with invalid ingredient then 500 error")
    public void tryPlaceOrderWithInvalidIngredientThenInternalServerError() {
        // создали объект и записали туда объект с невалидным ингридиентом
        OrderRequest invalidOrderRequest = OrderRequestGenerator.getOrderWithInvalidIngredientRequest();

        // пробуем размещать заказ без авторизации, с пустым токеном
        Response responsePlaceOrder = orderActions.placeOrder(invalidOrderRequest, "");
        responsePlaceOrder.then().assertThat().statusCode(500);
    }

    @Test
    @DisplayName("Get orders for user positive test")
    public void getOrdersForUserPositiveTest() {
        prepareNewUser();
        // подготовим объект с рандомными данными заказа
        OrderRequest randomOrderRequest = OrderRequestGenerator.getRandomOrderRequest();

        // разместим один заказ от имени созданного юзера
        Response responsePlaceOrder = orderActions.placeOrder(randomOrderRequest, bearerToken);
        responsePlaceOrder.then().assertThat().statusCode(200);
        responsePlaceOrder.then().assertThat().body("success", equalTo(true));

        // сохраним номер размещенного заказа
        int orderNum = responsePlaceOrder.then().extract().body().path("order.number");

        // получим список заказов для созданного юзера и проверим что там есть размещенный заказ
        Response responseGetOrders = orderActions.getUserOrders(bearerToken);
        responseGetOrders.then().assertThat().statusCode(200);
        responseGetOrders.then().assertThat().body("orders[0].number", equalTo(orderNum));
    }

    @Test
    @DisplayName("Try to get orders without authorization then 401 error")
    public void tryGetOrdersForUserWithoutAuthorizationThenUnauthorizedError() {
        // пробуем получить список заказов без авторизации
        Response responseGetOrders = orderActions.getUserOrders("");
        responseGetOrders.then().assertThat().statusCode(401);
        responseGetOrders.then().assertThat().body("success", equalTo(false));
        responseGetOrders.then().assertThat().body("message", equalTo(UNAUTHORISED_MESSAGE));
    }

    @Step("Prepare new user")
    public void prepareNewUser() {
        // создадим объект с рандомными данными юзера
        RegisterUserRequest randomRegisterUserRequest = RegisterUserRequestGenerator.getRandomRegisterUserRequest();

        // подготовим объект избавляясь от "name", оставим только данные для авторизации
        LoginRequest loginRequest = LoginRequestGenerator.prepareFrom(randomRegisterUserRequest);
        UserActions userActions = new UserActions();
        Response responseNewUser = userActions.createUser(randomRegisterUserRequest); // регистрируем юзера
        responseNewUser.then().assertThat().statusCode(200);
        responseNewUser.then().assertThat().body("success", equalTo(true));
        bearerToken = userActions.loginAndGetBearerToken(loginRequest); // авторизуемся и сохраним токен юзера
    }

    @After
    public void cleanData() {
        if (bearerToken != null) {
            UserActions userActions = new UserActions();
            Response responseDelete = userActions.deleteUser(bearerToken);
            responseDelete.then().assertThat().statusCode(202);
            responseDelete.then().assertThat().body("success", equalTo(true));
        }
    }
}

