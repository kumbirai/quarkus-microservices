package org.agoncal.quarkus.microservices.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;
import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@Schema(description = "A book")
public class Book
{
    @JsonbProperty("isbn_13")
    @Schema(required = true)
    private String isbn13;
    @Schema(required = true)
    private String title;
    private String author;
    @JsonbProperty("year_of_publication")
    private int yearOfPublication;
    private String genre;
    @JsonbDateFormat("yyyy/MM/dd")
    @JsonbProperty("creation_date")
    @Schema(implementation = String.class,
            format = "date")
    private Instant creationDate;
}
