package com.profitsoft.lotrartifactsrest.controller;

import com.profitsoft.lotrartifactsrest.dto.*;
import com.profitsoft.lotrartifactsrest.service.ArtifactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/artifact")
@RequiredArgsConstructor
@Tag(name = "Artifacts", description = "API for managing artifacts")
public class ArtifactController {
    private final ArtifactService artifactService;

    @Operation(summary = "Save artifact entity", description = "Saves artifact entity to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Artifact created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArtifactDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = "Creator not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ArtifactDetailsDto saveArtifact(@Valid @Parameter(description = "Artifact data", required = true)
                                           @RequestBody ArtifactSaveDto artifactSaveDto) {
        return artifactService.saveArtifact(artifactSaveDto);
    }

    @Operation(summary = "Get artifact by ID", description = "Retrieves artifact details by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artifact found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArtifactDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = "Artifact not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{artifactId}")
    public ArtifactDetailsDto getArtifactById(@Parameter(description = "ID of the artifact to retrieve", required = true)
                                              @PathVariable Long artifactId) {
        return artifactService.getArtifactById(artifactId);
    }

    @Operation(summary = "Get page of artifacts", description = "Retrieves artifacts in pageable format")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artifact found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArtifactListResponseDto.class))),
    })
    @PostMapping("/_list")
    public ArtifactListResponseDto getArtifactList(@Valid @Parameter(description = "Filters and pageable data", required = true)
                                                       @RequestBody ArtifactListRequestDto artifactListRequestDto) {
        return artifactService.getPageableArtifactsList(artifactListRequestDto);
    }

    @Operation(summary = "Update artifact by ID", description = "Updates artifact details by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artifact updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArtifactDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = "Artifact or creator not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{artifactId}")
    public ArtifactDetailsDto updateArtifact(@Parameter(description = "ID of the artifact to update", required = true)
                                             @PathVariable Long artifactId,
                                             @Valid @Parameter(description = "Updated artifact data", required = true)
                                             @RequestBody ArtifactSaveDto artifactSaveDto) {
        return artifactService.updateArtifact(artifactId, artifactSaveDto);
    }

    @Operation(summary = "Delete artifact by ID", description = "Deletes artifact by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Artifact deleted"),
            @ApiResponse(responseCode = "404", description = "Artifact not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{artifactId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteArtifact(@Parameter(description = "ID of the artifact to delete", required = true)
                               @PathVariable Long artifactId) {
        artifactService.deleteArtifact(artifactId);
    }

    @Operation(summary = "Import artifacts from JSON file", description = "Imports artifacts from uploaded JSON array")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Import completed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArtifactImportResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input file",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/upload")
    public ArtifactImportResponseDto importArtifacts(@Parameter(description = "JSON file with artifact records", required = true)
                                                     @RequestParam("file") MultipartFile file) {
        return artifactService.importArtifacts(file);
    }
}
