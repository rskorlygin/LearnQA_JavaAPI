package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Feature("Get data")
public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Description("This test not successfully get user data without auth")
    @Test
    public void testGetUserDataNotAuth() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        Assertions.assertJsonHasKey(response, "username");
        Assertions.assertJsonNotHasKey(response, "email");
        Assertions.assertJsonNotHasKey(response, "firstName");
        Assertions.assertJsonNotHasKey(response, "lastName");
    }

    @Description("This test successfully get user data")
    @Test
    public void testGetUserDetailsAuthAsSameUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(response, "x-csrf-token");
        String cookie = this.getCookie(response, "auth_sid");

        Response response2 = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        String[] expectedFields = {"username", "email", "firstName", "lastName"};
        Assertions.assertJsonHasFields(response2, expectedFields);
    }

    @Description("This test not successfully get user data with auth another user")
    @Test
    public void testGetUserDetailsAuthAsSameAuthAnotherUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(response, "x-csrf-token");
        String cookie = this.getCookie(response, "auth_sid");

        Response response2 = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/1")
                .andReturn();

        Assertions.assertJsonHasKey(response2, "username");
        Assertions.assertJsonNotHasKey(response2, "email");
        Assertions.assertJsonNotHasKey(response2, "firstName");
        Assertions.assertJsonNotHasKey(response2, "lastName");
    }
}