package com.kumbirai.udemy.quarkus.microservices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Map.entry;

public class BookClient
{
	private static final Logger LOGGER = Logger.getLogger(BookClient.class.getName());

	private static final HttpClient httpClient = HttpClient.newBuilder()
			.version(HttpClient.Version.HTTP_1_1)
			.connectTimeout(Duration.ofSeconds(10))
			.build();

	public static HttpRequest.BodyPublisher ofFormData(Map<String, String> data)
	{
		var builder = new StringBuilder();
		for (Map.Entry<String, String> entry : data.entrySet())
		{
			if (builder.length() > 0)
			{
				builder.append("&");
			}
			builder.append(URLEncoder.encode(entry.getKey(),
					StandardCharsets.UTF_8));
			builder.append("=");
			builder.append(URLEncoder.encode(entry.getValue(),
					StandardCharsets.UTF_8));
		}
		return HttpRequest.BodyPublishers.ofString(builder.toString());
	}

	public static void main(String[] args) throws IOException, InterruptedException
	{
		Faker faker = new Faker();
		for (int i = 1; i <= 100000; i++)
		{
			var values = Map.ofEntries(entry("title",
							faker.book()
									.title()),
					entry("author",
							faker.name()
									.nameWithMiddle()),
					entry("year",
							String.valueOf(2012 + new Random().nextInt(10))),
					entry("genre",
							faker.book()
									.genre()));

			var requestBody = new ObjectMapper().writeValueAsString(values);
			LOGGER.log(Level.INFO,
					requestBody);

			var request = HttpRequest.newBuilder()
					.uri(URI.create("http://localhost:8702/api/books"))
					.header("Content-Type",
							"application/x-www-form-urlencoded")
					.POST(ofFormData(values))
					.build();

			LOGGER.log(Level.INFO,
					"request: {0}",
					request);

			HttpResponse<String> response = httpClient.send(request,
					HttpResponse.BodyHandlers.ofString());

			// print response headers
			var headers = response.headers();
			StringBuilder sb = new StringBuilder();
			headers.map()
					.forEach((k, v) -> sb.append(k)
							.append(":")
							.append(v)
							.append(System.lineSeparator()));
			LOGGER.log(Level.INFO,
					sb::toString);
			// print status code
			LOGGER.log(Level.INFO,
					"statusCode: {0}",
					response.statusCode());

			// print response body
			LOGGER.log(Level.INFO,
					response::body);
		}
	}
}
