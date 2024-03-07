package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;


public class UserAuthTest extends BaseTestCase {

    String cookie;
    String header;
    int userId;

    @BeforeEach
    public void loginUser() {
        Map<String, String> userData = new HashMap<>();
        userData.put("email", "vinkotov@example.com");
        userData.put("password", "1234");

        Response response = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        this.cookie = this.getCookie(response,"auth_sid");
        this.header = this.getHeader(response, "x-csrf-token");
        this.userId = this.getIntFromJson(response, "user_id");
    }

    @Test
    public void testAuthUser() {
        Response response2 = RestAssured
                .given()
                .header("x-csrf-token", this.header)
                .cookie("auth_sid", this.cookie)
                .get("https://playground.learnqa.ru/api/user/auth")
                .andReturn();

        Assertions.asserJsonByName(response2, "user_id", this.userId);
    }

    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeAuthUser(String condition) {
        RequestSpecification specification = RestAssured.given();
        specification.baseUri("https://playground.learnqa.ru/api/user/auth");

        if(condition.equals("cookie")) {
            specification.cookie("auth_sid", this.cookie);
        }
        else if(condition.equals("headers")) {
            specification.header("x-csrf-token", this.header);
        }
        else {
            throw new IllegalArgumentException("Condition value is know: " + condition);
        }

        Response response2 = specification.get().andReturn();
        Assertions.asserJsonByName(response2, "user_id", 0);
    }
}