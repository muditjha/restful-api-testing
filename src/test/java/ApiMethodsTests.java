import Constants.TestApiConstants;
import io.restassured.http.ContentType;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApiMethodsTests {

    int statusCode;

    @Test
    void testGetDeckOfCards_GivenNewDeck_ThenReturnResponseOk() {
        //Check the response is 200(OK) when new deck is created
        Response response = RestAssured.given()
                .headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
                .when()
                .get(TestApiConstants.BASE_URI + "new/")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .extract()
                .response();
    }

    @Test
    void testGetDeckOfCards_GivenDrawTwoCards_ThenMatchRemainingCards() {

        //Create new deck and retrieve deck id
        String deckid = RestAssured.given()
                .headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
                .when()
                .get(TestApiConstants.BASE_URI + "new/")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("deck_id");

        //Use deck id to draw one card from the deck
        int remaining = RestAssured.given()
                .headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
                .queryParam("count","2")
                .when()
                .get(TestApiConstants.BASE_URI + deckid + "/draw/")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getInt("remaining");

        Assert.assertEquals(50, remaining);
    }

    @Test
    void testGetDeckOfCards_GivenJokersEnabledTrue_ThenReturnRemainingCardsWithJokers() {

        //Create new deck with jokers enabled
        int remaining = RestAssured.given()
                .headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
                .queryParam("jokers_enabled",true)
                .when()
                .get(TestApiConstants.BASE_URI + "new/")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getInt("remaining");

        Assert.assertEquals(54, remaining);
    }

    @Test
    void testPostDeckOfCards_GivenJokersEnabledTrue_ThenReturnResponseForbidden() {
        RestAssured.baseURI = TestApiConstants.BASE_URI.concat("new/");

        RequestSpecification httpRequest = RestAssured.given();

        JSONObject requestParams = new JSONObject();
        requestParams.put("jokers_enabled", true);

        httpRequest.header("Content-Type", "application/json");

        httpRequest.body(requestParams.toJSONString());

        Response response = httpRequest.request(Method.POST);

        statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 403);

    }
}
