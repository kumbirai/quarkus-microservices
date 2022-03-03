package org.agoncal.quarkus.microservices.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.json.bind.annotation.JsonbProperty;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class IsbnThirteen
{
    @JsonbProperty("isbn_13")
    private String isbn13;
}
