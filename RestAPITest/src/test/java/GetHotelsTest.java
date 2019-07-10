import core.TestBase;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class GetHotelsTest extends TestBase {
    public static RequestSpecification requestSpecification;
    public static ResponseSpecification responseSpecification;

    @BeforeClass
    public static void setUp() {
        requestSpecification = given().contentType("application/json")
                .baseUri(BASE_URL)
                .basePath("/hotel/ahs/geo-suggest");
        responseSpecification = expect().statusCode(200)
                .header("Content-Type", "application/json");
    }

    @Test(groups = {"hotels"})
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

    @Test(groups = {"hotels"})
    public void getHotelsHeaderAndStatusCodeTest() {
          given()
                    .spec(requestSpecification)
                    .queryParam("query", "paris")
                    .when()
                    .get("")
                    .then()
                    .spec(responseSpecification);
    }
    @Test(groups = {"hotels"})
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

    @Test(groups = {"hotels"})
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

    @Test(groups = {"hotels"})
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

    @Test(groups = {"hotels"})
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

    @Test(groups = {"hotels"})
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

    @Test(groups = {"hotels"})
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

