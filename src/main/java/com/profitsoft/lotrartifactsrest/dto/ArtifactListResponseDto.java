package com.profitsoft.lotrartifactsrest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * DTO for artifact list response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ArtifactListResponseDto", description = "Artifact list response data transfer object")
public class ArtifactListResponseDto {

    @Schema(description = "Artifacts for requested page")
    private List<ArtifactDetailsDto> list;

    @Schema(description = "Total pages", example = "5")
    private int totalPages;
}