import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class GetHotelsTest {
    public static RequestSpecification requestSpecification;
    public static ResponseSpecification responseSpecification;

    @BeforeClass
    public static void setUp() {
        requestSpecification = given().contentType("application/json")
                .baseUri("https://www.tajawal.ae/api")
                .basePath("/hotel/ahs/geo-suggest");
        responseSpecification = expect().statusCode(200)
                .header("Content-Type", "application/json");
    }

    @Test
    public void getHotelsStatusCodeTestWithTryCatch() {
        Response response = null;
        try {
            response = given()
                    .spec(requestSpecification)
                    .queryParam("query", "paris")
                    .when()
                    .get("");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertEquals(200, response.getStatusCode());
    }

    @Test
    public void getHotelsHeaderAndStatusCodeTest() {
          given()
                    .spec(requestSpecification)
                    .queryParam("query", "paris")
                    .when()
                    .get("")
                    .then()
                    .spec(responseSpecification);
    }
    @Test
    public void getHotelsInvalidCountryTest() {
        given()
                .spec(requestSpecification)
                .queryParam("query", "#$#@%#%")
                .when()
                .get("")
                .then()
                .spec(responseSpecification)
                .statusCode(200);
    }

    @Test
    public void getHotelsInvalidPathTest() {
        given()
                .spec(requestSpecification)
                .basePath("/hotel//geo-suggest")
                .queryParam("query", "india")
                .when()
                .get("")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("title", equalTo("[Gateway:``] Not found"))
                .body("detail", equalTo("Resource not found"))
                .body("type", equalTo("https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html"));
    }

    @Test
    public void getHotelsInvalidRouteTest() {
        given()
                .spec(requestSpecification)
                .basePath("/hote/ahs/geo-suggest")
                .queryParam("query", "india")
                .when()
                .get("")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("title", equalTo("[Gateway:``] Invalid Route"))
                .body("detail", containsString("Unable to resolve the request"))
                .body("type", equalTo("https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html"));
    }

    @Test
    public void getHotelsInvalidQueryParamTest() {
        given()
                .spec(requestSpecification)
                .queryParam("que", "india")
                .when()
                .get("")
                .then()
                .statusCode(400)
                .body("detail", containsString("invalid parameter name"));
    }

    @Test
    public void getHotelsEmptyQueryParamValueTest() {
        given()
                .spec(requestSpecification)
                .queryParam("query", "")
                .when()
                .get("")
                .then()
                .statusCode(400)
                .body("detail", containsString("invalid parameter value"));
    }

    @Test
    public void getHotelJsonSchemaTest() {
        given()
                .spec(requestSpecification)
                .queryParam("query", "paris")
                .when()
                .get("")
                .then()
                .spec(responseSpecification)
                .assertThat().body(matchesJsonSchemaInClasspath("HotelsSchema.json"));
    }
}

