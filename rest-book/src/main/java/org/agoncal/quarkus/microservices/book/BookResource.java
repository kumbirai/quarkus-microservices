package org.agoncal.quarkus.microservices.book;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.time.Instant;

@Path("/api/books")
@Tag(name = "Book REST Endpoint")
public class BookResource
{
    @Inject
    @RestClient
    NumberProxy proxy;

    @Inject
    Logger logger;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Operation(summary = "Creates a new book")
    @Retry(maxRetries = 1,
            delay = 2000)
    @Fallback(fallbackMethod = "fallbackOnCreatingABook")
    public Response createABook(@FormParam("title") String title, @FormParam("author") String author, @FormParam("year") int yearOfPublication, @FormParam("genre") String genre)
    {
        Book book = Book.builder()
                .isbn13(proxy.generateIsbnNumbers()
                                .getIsbn13())
                .title(title)
                .author(author)
                .yearOfPublication(yearOfPublication)
                .genre(genre)
                .creationDate(Instant.now())
                .build();
        logger.info("Book created: " + book);
        return Response.status(Response.Status.CREATED.getStatusCode())
                .entity(book)
                .build();
    }

    public Response fallbackOnCreatingABook(@FormParam("title") String title, @FormParam("author") String author, @FormParam("year") int yearOfPublication, @FormParam("genre") String genre) throws
                                                                                                                                                                                              IOException
    {
        Book book = Book.builder()
                .isbn13("Will be set later")
                .title(title)
                .author(author)
                .yearOfPublication(yearOfPublication)
                .genre(genre)
                .creationDate(Instant.now())
                .build();

        saveBookOnDisk(book);

        logger.warn("Book saved on disk: " + book);

        return Response.status(Response.Status.PARTIAL_CONTENT.getStatusCode())
                .entity(book)
                .build();
    }

    private void saveBookOnDisk(Book book) throws IOException
    {
        try (Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true)))
        {
            String booksDir = "books";
            FileSystem fileSystem = FileSystems.getDefault();
            Files.createDirectories(fileSystem.getPath(booksDir));
            java.nio.file.Path path = fileSystem.getPath(booksDir, "book-" + Instant.now()
                    .toEpochMilli() + ".json");
            Files.write(path, jsonb.toJson(book)
                    .getBytes());
        }
        catch (IOException ex)
        {
            logger.error("Exception thrown", ex);
            throw ex;
        }
        catch (Exception ex)
        {
            logger.error("Exception thrown", ex);
        }
    }
}
