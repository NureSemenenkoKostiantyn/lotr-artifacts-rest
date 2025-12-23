package com.profitsoft.lotrartifactsrest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for artifact import operation")
public class ArtifactImportResponseDto {
    @Schema(description = "Number of artifacts successfully imported", example = "10")
    private long imported;

    @Schema(description = "Number of artifact records that failed to import", example = "2")
    private long failed;
}