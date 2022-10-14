package com.kumbirai.udemy.quarkus.microservices.number;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

@QuarkusTest
class NumberResourceTest
{
	@Test
	void shouldGenerateIsbnNumbers()
	{
		given().when()
				.get("/api/numbers")
				.then()
				.statusCode(Response.Status.OK.getStatusCode())
				.body("isbn_13", startsWith("13-"))
				.body("isbn_10", startsWith("10-"))
				.body(not(hasKey("generationDate")));
	}
}
