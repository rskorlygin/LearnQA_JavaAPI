package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response response = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Users with email '" + email + "' already exists");
    }

    @Test
    public void testCreateUserSuccessfully() {
        String email = DataGenerator.getRandomEmail();

        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response response = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(response, 200);
        Assertions.assertJsonHasKey(response, "id");
    }

    @Test
    public void testCreateUserIncorrectEmail() {
        Map<String, String> userData = new HashMap<>();
        userData.put("email", "vinkotovexample.com");   // отсутствие знака @
        userData.put("password", "123");
        userData.put("username", "learnqa");
        userData.put("firstName", "learnqa");
        userData.put("lastName", "learnqa");

        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertJsonNotHasKey(response, "id");
    }

    @ParameterizedTest
    @ValueSource(ints = {0,1,2,3,4})
    public void testCreateUserNoField(int usersData) {
        Map<String, String> userDataNotPass = new HashMap<>();
        userDataNotPass.put("email", "learnqa" + new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date()) + "@example.com");
        userDataNotPass.put("username", "learnqa");
        userDataNotPass.put("firstName", "learnqa");
        userDataNotPass.put("lastName", "learnqa");

        Map<String, String> userDataNotEmail = new HashMap<>();
        userDataNotEmail.put("password", "123");
        userDataNotEmail.put("username", "learnqa");
        userDataNotEmail.put("firstName", "learnqa");
        userDataNotEmail.put("lastName", "learnqa");

        Map<String, String> userDataNotUsername = new HashMap<>();
        userDataNotUsername.put("email", "learnqa" + new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date()) + "@example.com");
        userDataNotUsername.put("password", "123");
        userDataNotUsername.put("firstName", "learnqa");
        userDataNotUsername.put("lastName", "learnqa");

        Map<String, String> userDataNotFirstName = new HashMap<>();
        userDataNotFirstName.put("email", "learnqa" + new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date()) + "@example.com");
        userDataNotFirstName.put("password", "123");
        userDataNotFirstName.put("username", "learnqa");
        userDataNotFirstName.put("lastName", "learnqa");

        Map<String, String> userDataNotLastName = new HashMap<>();
        userDataNotLastName.put("email", "learnqa" + new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date()) + "@example.com");
        userDataNotLastName.put("password", "123");
        userDataNotLastName.put("username", "learnqa");
        userDataNotLastName.put("firstName", "learnqa");

        switch (usersData) {
            case 0:
                Response response0 = apiCoreRequests
                        .makePostRequest("https://playground.learnqa.ru/api/user/", userDataNotPass);

                Assertions.assertResponseCodeEquals(response0, 400);
                Assertions.assertJsonNotHasKey(response0, "id");
                break;

            case 1:
                Response response1 = apiCoreRequests
                        .makePostRequest("https://playground.learnqa.ru/api/user/", userDataNotEmail);

                Assertions.assertResponseCodeEquals(response1, 400);
                Assertions.assertJsonNotHasKey(response1, "id");
                break;

            case 2:
                Response response2 = apiCoreRequests
                        .makePostRequest("https://playground.learnqa.ru/api/user/", userDataNotUsername);

                Assertions.assertResponseCodeEquals(response2, 400);
                Assertions.assertJsonNotHasKey(response2, "id");
                break;

            case 3:
                Response response3 = apiCoreRequests
                        .makePostRequest("https://playground.learnqa.ru/api/user/", userDataNotFirstName);

                Assertions.assertResponseCodeEquals(response3, 400);
                Assertions.assertJsonNotHasKey(response3, "id");
                break;

            case 4:
                Response response4 = apiCoreRequests
                        .makePostRequest("https://playground.learnqa.ru/api/user/", userDataNotLastName);

                Assertions.assertResponseCodeEquals(response4, 400);
                Assertions.assertJsonNotHasKey(response4, "id");
                break;
        }
    }

    @Test
    public void testCreateUserShortUsername() {
        Map<String, String> userData = new HashMap<>();
        userData.put("email", "learnqa" + new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date()) + "@example.com");
        userData.put("password", "123");
        userData.put("username", "l");  // короткое имя в один символ
        userData.put("firstName", "learnqa");
        userData.put("lastName", "learnqa");

        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertJsonNotHasKey(response, "id");
    }

    @Test
    public void testCreateUserLongUsername() {
        Map<String, String> userData = new HashMap<>();
        userData.put("email", "learnqa" + new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date()) + "@example.com");
        userData.put("password", "123");
        userData.put("username", "Необходимо помнить о различиях в системных вызовах, библиотеках и API, которые используются для взаимодействия с операционной системой. Например, для Windows приложения могут использовать WinAPI, для Linux - GTK или Qt, на macOS - Cocoa и Objective-C.");  // длинное имя более 250 символов
        userData.put("firstName", "learnqa");
        userData.put("lastName", "learnqa");

        System.out.println("Длинна имени пользователя: " + userData.get("username").length());
        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertJsonNotHasKey(response, "id");
    }
}