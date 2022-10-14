package com.kumbirai.udemy.quarkus.microservices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import static java.util.Map.entry;

public class BookClient
{
	private static final Logger LOG = LoggerFactory.getLogger(BookClient.class);

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
			builder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
			builder.append("=");
			builder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
		}
		return HttpRequest.BodyPublishers.ofString(builder.toString());
	}

	public static void main(String[] args) throws IOException, InterruptedException
	{
		Faker faker = new Faker();
		for (int i = 1; i <= 100000; i++)
		{
			var values = Map.ofEntries(entry("title", faker.book()
					.title()), entry("author", faker.name()
					.nameWithMiddle()), entry("year", String.valueOf(2012 + new Random().nextInt(10))), entry("genre", faker.book()
					.genre()));

			var requestBody = new ObjectMapper().writeValueAsString(values);
			if (LOG.isDebugEnabled())
			{
				LOG.debug(requestBody);
			}

			var request = HttpRequest.newBuilder()
					.uri(URI.create("http://localhost:8702/api/books"))
					.header("Content-Type", "application/x-www-form-urlencoded")
					.POST(ofFormData(values))
					.build();

			if (LOG.isDebugEnabled())
			{
				LOG.debug("request: {}", request);
			}

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			// print response headers
			var headers = response.headers();
			StringBuilder sb = new StringBuilder();
			headers.map()
					.forEach((k, v) -> sb.append(k)
							.append(":")
							.append(v)
							.append(System.lineSeparator()));
			if (LOG.isDebugEnabled())
			{
				LOG.debug(sb.toString());
			}
			// print status code
			if (LOG.isDebugEnabled())
			{
				LOG.debug("statusCode: {}", response.statusCode());
			}

			// print response body
			if (LOG.isDebugEnabled())
			{
				LOG.debug(response.body());
			}
		}
	}
}
