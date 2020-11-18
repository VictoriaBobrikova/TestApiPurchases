package test.java;

import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class TestGetQuery {

    RequestSpecification requestSpec = given()
            .baseUri("https://my-json-server.typicode.com/knastya/simpleBillingService");

//basic requests
    @Test(groups = "basic status-code")
    public void testGetAllUsersSC() {
        requestSpec.when()
                .get("/users")
                .then()
                .statusCode(200);
    }


    @Test(groups = "basic status-code")
    public void testGetAllProductsSC() {
        requestSpec.when()
                .get("/products")
                .then()
                .statusCode(200);
    }


    @Test(groups = "basic status-code")
    public void testGetAllPurchasesSC() {
        requestSpec.when()
                .get("/purchases")
                .then()
                .statusCode(200);
    }

//response verification
    @Test(dependsOnGroups = "basic status-code")
    public void testGetUsersResp() {
        requestSpec.when()
                .get("/users")
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("users-schema.json"));
    }


    @Test(dependsOnGroups = "basic status-code")
    public void testGetProductsResp() {
        requestSpec.when()
                .get("/products")
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("products-schema.json"));
    }


    @Test(dependsOnGroups = "basic status-code")
    public void testGetPurchasesResp() {
        requestSpec.when()
                .get("/purchases")
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("purchases-schema.json"));
    }

//get user
    @Test
    public void testGetUserByIdPosSC() {
        requestSpec.when()
                .get("/users/1")
                .then()
                .statusCode(200);
    }


    @Test
    public void testGetUserByIdNegSC() {
        requestSpec.when()
                .get("/users/s'")
                .then()
                .statusCode(404);
    }


    @Test
    public void testGetUserByIdResp() {
        requestSpec.when()
                .get("/users/1")
                .then()
                .assertThat()
                .body("firstName", equalTo("John"))
                .body("lastName", equalTo("Snow"))
                .body("email", equalTo("john.snowAlive@gmail.com"));
    }

//get product
    @Test
    public void testGetProductsByIdPosSC() {
        requestSpec.when()
                .get("/products?productId=3")
                .then()
                .statusCode(200);
    }


    @Test
    public void testGetProductsByIdResp() {
        requestSpec.when()
                .get("/products?productId=3")
                .then()
                .assertThat()
                .body("productId[0]", equalTo("3"))
                .body("productName[0]", equalTo("raspberry jam"))
                .body("price[0]", equalTo("34.1"));
    }

//requirement
    @Test
    public void testPurchaseHasUser() {
        List<Integer> userIds = requestSpec.when()
                .get("/users")
                .then()
                .extract()
                .jsonPath()
                .getList("id");

        List<Integer> userIdsPurchase = requestSpec.when()
                .get("/purchases")
                .then()
                .extract()
                .jsonPath()
                .getList("userId");

        Assert.assertTrue(userIds.containsAll(userIdsPurchase));
    }
}
