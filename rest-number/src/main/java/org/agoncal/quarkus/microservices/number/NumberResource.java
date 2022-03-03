package org.agoncal.quarkus.microservices.number;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Random;

@Path("/api/numbers")
@Tag(name = "Number REST Endpoint")
public class NumberResource
{
    @Inject
    Logger logger;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Generates book numbers")
    public IsbnNumbers generateIsbnNumbers()
    {
        IsbnNumbers isbnNumbers = IsbnNumbers.builder()
                .isbn13("13-" + new DecimalFormat("000000000").format(new Random().nextInt(1_000_000_000)))
                .isbn10("10-" + new DecimalFormat("000000").format(new Random().nextInt(1_000_000)))
                .generationDate(Instant.now())
                .build();
        logger.info("Numbers generated " + isbnNumbers);
        return isbnNumbers;
    }
}
