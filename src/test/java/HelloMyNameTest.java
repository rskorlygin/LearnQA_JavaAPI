import groovy.json.JsonOutput;
import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HelloMyNameTest {

    @Test
    public void testHelloMyName() {
        System.out.println("Hello from Roman");
    }

    @Test
    public void testGetRequest() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.prettyPrint();
    }

    @Test
    public void testEx5JsonParsing() {
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();
        response.prettyPrint();

        String answer = response.get("messages[1].message");
        System.out.println(answer);
    }

    @Test
    public void testEx6Redirects() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(true)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();
        String locationHeaders = response.getHeader("X-Host");
        System.out.println(locationHeaders);
    }

    @Test
    public void testEx7LongRedirects() {
        int redirectSum = 0;

        for (int i = 0; i < 100; i++) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(true)
                    .when()
                    .get("https://playground.learnqa.ru/api/long_redirect")
                    .andReturn();

            String locationHeaders = response.getHeader("X-Host");
            System.out.println(locationHeaders);
            int statusCode = response.getStatusCode();
            System.out.println(statusCode);
            if (statusCode == 200) {
                redirectSum++;
                break;
            } else {
                redirectSum++;
            }
        }
        System.out.println(redirectSum);
    }

    @Test
    public void testEx8Token() throws InterruptedException {
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String answerToken = response.get("token");
        int answerTime = response.get("seconds");

        Map<String, String> token = new HashMap<>();
        token.put("token", answerToken);

        JsonPath response2 = RestAssured
                .given()
                .queryParams(token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        if (response2.get("status").equals("Job is NOT ready")) {
            response2.prettyPrint();
        } else {
            System.out.println("Invalidate status token!");
        }

        Thread.sleep((answerTime * 1000L));

        JsonPath response3 = RestAssured
                .given()
                .queryParams(token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        if ((response3.get("status").equals("Job is ready")) && (response3.get("result")) != null) {
            response3.prettyPrint();
        } else {
            System.out.println("Invalidate status token!");
        }
    }

    @Test
    public void testEx9PassSelection() {
        String[] wikiPass = {"password", "123456789", "12345678", "abc123", "football", "monkey",
                "letmein", "dragon", "trustno1", "adobe123", "welcome", "qwerty123", "solo", "master", "photoshop",
                "ashley", "bailey", "shadow", "7777777", "michael", "jesus", "696969", "qazwsx", "batman", "Football",
                "123456", "qwerty", "12345", "1234567890", "1234567", "111111", "1234", "baseball", "princess",
                "121212", "login", "flower", "1q2w3e4r", "666666", "1qaz2wsx", "mustang", "access", "passw0rd",
                "loveme", "!@#$%^&*", "superman", "hottie", "ninja", "zaq1zaq1", "123qwe", "qwertyuiop", "654321",
                "555555", "lovely", "888888", "donald", "aa123456", "charlie", "123123", "iloveyou", "sunshine",
                "admin", "starwars", "hello", "password1", "freedom", "azerty", "whatever", "000000"};

        for (int i = 0; i < 69; i++) {
            Map<String, Object> body = new HashMap<>();
            body.put("login", "super_admin");
            body.put("password", wikiPass[i]);

            Response response = RestAssured
                    .given()
                    .body(body)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            String responseCookie = response.getCookie("auth_cookie");

            Map<String, Object> body2 = new HashMap<>();
            body2.put("auth_cookie", responseCookie);

            Response response2 = RestAssured
                    .given()
                    .body(body)
                    .cookies(body2)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();

            if (response2.print().equals("You are authorized")) {
                System.out.println("Password: " + wikiPass[i]);
                break;
            }
            i++;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Ab ovo usque ad mala", "Test text"})
    public void testEx10StringLength(String text) {
        assertTrue(text.length() > 15, "The text is shorter than 15 characters");
    }

    @Test
    public void testEx11AssertCookie() {
        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_cookie");

        assertEquals(response.getCookie("HomeWork"), "hw_value", "HomeWork cookie not expected result");
    }

    @Test
    public void testEx12AssertHeader() {
        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_header");

        assertEquals(
                response.getHeader("Date").substring(0, 5),    // дата меняется в зависимости от времени запуска тестов, поэтому была обрезана
                "Wed, ",
                "Date header not expected result");
        assertEquals(
                response.getHeader("Content-Type"),
                "application/json",
                "Content-Type header not expected result");
        assertEquals(
                response.getHeader("Content-Length"),
                "15",
                "Content-Length header not expected result");
        assertEquals(
                response.getHeader("Connection"),
                "keep-alive",
                "Connection header not expected result");
        assertEquals(
                response.getHeader("Keep-Alive"),
                "timeout=10",
                "Keep-Alive header not expected result");
        assertEquals(
                response.getHeader("Server"),
                "Apache",
                "Server header not expected result");
        assertEquals(
                response.getHeader("x-secret-homework-header"),
                "Some secret value",
                "X-secret-homework-header header not expected result");
        assertEquals(
                response.getHeader("Cache-Control"),
                "max-age=0",
                "Cache-Control header not expected result");
        assertEquals(
                response.getHeader("Expires").substring(0, 5),    // дата меняется в зависимости от времени запуска тестов, поэтому была обрезана
                "Wed, ",
                "Expires header not expected result");
    }

    @ParameterizedTest
    @CsvSource({
            "0, Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30",
            "1, Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1",
            "2, Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
            "3, Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0",
            "4, Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1"})
    public void testEx13UserAgent(int data, String name) {
        Map<String, String> headers = new HashMap<>();
        headers.put("user-agent", name);

        String[] dataResult = new String[] {
                "platform: Mobile, browser: No, device: Android",
                "platform: Mobile, browser: Chrome, device: iOS",
                "platform: Googlebot, browser: Unknown, device: Unknown",
                "platform: Web, browser: Chrome, device: No",
                "platform: Mobile, browser: No, device: iPhone"};

        JsonPath response = RestAssured
                .given()
                .headers(headers)
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .jsonPath();

        assertEquals(
                dataResult[data],
                ("platform: " + response.getString("platform") + ", " +
                 "browser: " + response.getString("browser") + ", " +
                 "device: " + response.getString("device")),
                "Incorrect result");
    }
}