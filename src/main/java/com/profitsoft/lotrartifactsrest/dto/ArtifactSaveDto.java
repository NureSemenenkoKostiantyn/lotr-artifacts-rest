package com.profitsoft.lotrartifactsrest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO for artifact save and update operations.
 */
@Getter
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Artifact save data transfer object")
public class ArtifactSaveDto {

    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be blank")
    @Size(max = 255, message = "Name must be at most 255 characters long")
    private String name;

    @NotNull(message = "Creator id must not be null")
    private Long creatorId;

    @Size(max = 255, message = "Origin must be at most 255 characters long")
    private String origin;

    @Size(max = 500, message = "Tags must be at most 500 characters long")
    private String tags;

    @Min(value = 0, message = "Year created must be greater or equal to 0")
    private Integer yearCreated;

    @Min(value = 0, message = "Power level must be greater or equal to 0")
    @Max(value = 10_000, message = "Power level must be less or equal to 10000")
    private Integer powerLevel;


}