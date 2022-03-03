package org.agoncal.quarkus.microservices;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BookClient
{
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
        //instance of random class
        //generate random values from 0-24
        //int int_random = new Random().nextInt(25) + 1;

        Faker faker = new Faker();
        for (int i = 1; i <= 1000; i++)
        {
            var values = new HashMap<String, String>()
            {{
                put("title", faker.book().title());
                put("author", faker.name().nameWithMiddle());
                put("year", String.valueOf(2012 + new Random().nextInt(10)));
                put("genre", faker.book().genre());
            }};

            var requestBody = new ObjectMapper().writeValueAsString(values);
            System.out.println(requestBody);

            var request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8702/api/books"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(ofFormData(values))
                    .build();

            System.out.println(request);

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // print response headers
            var headers = response.headers();
            headers.map()
                    .forEach((k, v) -> System.out.println(k + ":" + v));

            // print status code
            System.out.println(response.statusCode());

            // print response body
            System.out.println(response.body());
        }
    }
}
