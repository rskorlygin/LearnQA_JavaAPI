import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

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

        for (int i =0; i<100; i++) {
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
            if (statusCode==200) {
                redirectSum++;
                break;
            }
            else {
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
        }
        else {
            System.out.println("Invalidate status token!");
        }

        Thread.sleep((answerTime* 1000L));

        JsonPath response3 = RestAssured
                .given()
                .queryParams(token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        if ((response3.get("status").equals("Job is ready")) && (response3.get("result"))!=null) {
            response3.prettyPrint();
        }
        else {
            System.out.println("Invalidate status token!");
        }
    }
}