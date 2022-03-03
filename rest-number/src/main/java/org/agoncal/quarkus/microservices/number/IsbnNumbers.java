package org.agoncal.quarkus.microservices.number;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@Schema(description = "Several ISBN formats")
public class IsbnNumbers
{
    @Schema(required = true)
    @JsonbProperty("isbn_10")
    private String isbn10;
    @Schema(required = true)
    @JsonbProperty("isbn_13")
    private String isbn13;
    @JsonbTransient
    private Instant generationDate;
}
