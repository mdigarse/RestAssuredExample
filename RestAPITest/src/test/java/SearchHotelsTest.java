import core.TestBase;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SearchHotelsTest extends TestBase {
    public static RequestSpecification requestSpecification;
    public static ResponseSpecification responseSpecification;
    Details details;

    @BeforeClass
    public static void setUp() {
        requestSpecification = given().contentType("application/json")
                .baseUri(BASE_URL)
                .basePath("/hotel/ahs/search/request");
        responseSpecification = expect().statusCode(200)
                .header("Content-Type", "application/json");
    }

    @Test(groups = {"hotels"})
    public void searchHotelsWithEmptyPayloadTest() {
        given()
                .spec(requestSpecification)
                .when()
                .post("")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("title", equalTo("[Gateway:``] Bad Request"))
                .body("type", equalTo("https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html"));
    }

    @Test(groups = {"hotels"})
    public void searchHotelsWithInvalidPathTest() {
        given()
                .spec(requestSpecification)
                .basePath("/hotel//search/request")
                .when()
                .post("")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("title", equalTo("[Gateway:``] Not found"))
                .body("detail", equalTo("Resource not found"))
                .body("type", equalTo("https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html"));
    }

    @Test(groups = {"hotels"})
    public void searchHotelsWithValidPayloadTest() {
        details = new Details();
        details.dates.setCheckin("11-09-2019");
        details.dates.setCheckout("13-09-2019");
        details.setDestination("paris");
        Guest guest = new Guest();
        guest.setAge(23);
        guest.setType("abc");
        List<Guest> guestList = new ArrayList<Guest>();
        guestList.add(guest);
        Room room = new Room();
        room.setGuest(guestList);
        List<Room> roomList = new ArrayList<Room>();
        roomList.add(room);
        details.setRoom(roomList);
        details.setPlaceId("3243");
        given()
                .spec(requestSpecification)
                .body(details)
                .when()
                .post("")
                .then()
                .statusCode(200)
                .log().all();
    }
}
