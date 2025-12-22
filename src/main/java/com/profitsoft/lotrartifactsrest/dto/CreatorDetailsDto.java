package com.profitsoft.lotrartifactsrest.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO for creator details.
 */
@Getter
@Builder
@Jacksonized
@Schema(description = "Creator details data transfer object")
public class CreatorDetailsDto {

    private Long id;

    private String name;

    private String race;

    private String realm;
}
