package com.profitsoft.lotrartifactsrest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO for artifact details.
 */
@Getter
@Builder
@Jacksonized
@Schema(description = "Artifact details data transfer object")
public class ArtifactDetailsDto {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "One Ring")
    private String name;

    @Schema(example = "Mordor")
    private String origin;

    @Schema(example = "ring,dark,unique")
    private String tags;

    @Schema(example = "1600")
    private Integer yearCreated;

    @Schema(example = "9000")
    private Integer powerLevel;

    @Schema(description = "Creator details", implementation = CreatorDetailsDto.class)
    private CreatorDetailsDto creator;
}