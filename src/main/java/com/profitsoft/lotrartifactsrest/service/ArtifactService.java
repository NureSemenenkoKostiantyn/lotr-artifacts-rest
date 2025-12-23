package com.profitsoft.lotrartifactsrest.service;

import com.profitsoft.lotrartifactsrest.dto.*;
import com.profitsoft.lotrartifactsrest.model.Artifact;
import com.profitsoft.lotrartifactsrest.model.Creator;
import com.profitsoft.lotrartifactsrest.repository.ArtifactRepository;
import com.profitsoft.lotrartifactsrest.repository.CreatorRepository;
import com.profitsoft.lotrartifactsrest.repository.spec.ArtifactSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ArtifactService {
    private final ArtifactRepository artifactRepository;
    private final CreatorRepository creatorRepository;
    private final ArtifactUploadParser artifactUploadParser;

    public ArtifactService(ArtifactRepository artifactRepository,
                           CreatorRepository creatorRepository,
                           ArtifactUploadParser artifactUploadParser) {
        this.artifactRepository = artifactRepository;
        this.creatorRepository = creatorRepository;
        this.artifactUploadParser = artifactUploadParser;
    }

    public ArtifactDetailsDto saveArtifact(ArtifactSaveDto dto) {
        Creator creator = creatorRepository.findById(dto.getCreatorId())
                .orElseThrow(() -> new IllegalArgumentException("Creator with id '%s' not found".formatted(dto.getCreatorId())));
        Artifact artifact = convertToEntity(dto, creator);
        Artifact savedArtifact = artifactRepository.save(artifact);
        return convertToDetailsDto(savedArtifact);
    }

    public ArtifactDetailsDto getArtifactById(Long artifactId) {
        Artifact artifact = artifactRepository.findById(artifactId)
                .orElseThrow(() -> new IllegalArgumentException("Artifact with id '%s' not found".formatted(artifactId)));
        return convertToDetailsDto(artifact);
    }

    public ArtifactListResponseDto getPageableArtifactsList(ArtifactListRequestDto requestDto) {

        Specification<Artifact> spec = Specification
                .where(ArtifactSpecification.hasCreator(requestDto.getCreatorId()))
                .and(ArtifactSpecification.hasOrigin(requestDto.getOrigin()))
                .and(ArtifactSpecification.yearBetween(
                        requestDto.getYearFrom(),
                        requestDto.getYearTo()))
                .and(ArtifactSpecification.powerBetween(
                        requestDto.getPowerFrom(),
                        requestDto.getPowerTo()));

        PageRequest pageRequest = PageRequest.of(
                requestDto.getPage(),
                requestDto.getSize()
        );

        Page<Artifact> page = artifactRepository.findAll(spec, pageRequest);

        return new ArtifactListResponseDto(
                page.getContent().stream()
                        .map(ArtifactService::convertToDetailsDto)
                        .toList(),
                page.getTotalPages()
        );
    }

    public ArtifactDetailsDto updateArtifact(Long artifactId, ArtifactSaveDto dto) {
        Artifact artifact = artifactRepository.findById(artifactId)
                .orElseThrow(() -> new IllegalArgumentException("Artifact with id '%s' not found".formatted(artifactId)));
        Creator creator = creatorRepository.findById(dto.getCreatorId())
                .orElseThrow(() -> new IllegalArgumentException("Creator with id '%s' not found".formatted(dto.getCreatorId())));

        artifact.setName(dto.getName());
        artifact.setCreator(creator);
        artifact.setOrigin(dto.getOrigin());
        artifact.setTags(dto.getTags());
        artifact.setYearCreated(dto.getYearCreated());
        artifact.setPowerLevel(dto.getPowerLevel());

        Artifact updatedArtifact = artifactRepository.save(artifact);
        return convertToDetailsDto(updatedArtifact);
    }

    public void deleteArtifact(Long artifactId) {
        artifactRepository.findById(artifactId)
                .orElseThrow(() -> new IllegalArgumentException("Artifact with id '%s' not found".formatted(artifactId)));
        artifactRepository.deleteById(artifactId);
    }

    public ArtifactImportResponseDto importArtifacts(MultipartFile file) {
        ArtifactUploadParser.ImportResult importResult = artifactUploadParser.parse(file, dto -> {
            try {
                saveArtifact(dto);
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex);
            }
        });

        return ArtifactImportResponseDto.builder()
                .imported(importResult.imported())
                .failed(importResult.failed())
                .build();
    }

    private static Artifact convertToEntity(ArtifactSaveDto dto, Creator creator) {
        Artifact artifact = new Artifact();
        artifact.setName(dto.getName());
        artifact.setCreator(creator);
        artifact.setOrigin(dto.getOrigin());
        artifact.setTags(dto.getTags());
        artifact.setYearCreated(dto.getYearCreated());
        artifact.setPowerLevel(dto.getPowerLevel());
        return artifact;
    }

    private static ArtifactDetailsDto convertToDetailsDto(Artifact artifact) {
        return ArtifactDetailsDto.builder()
                .id(artifact.getId())
                .name(artifact.getName())
                .origin(artifact.getOrigin())
                .tags(artifact.getTags())
                .yearCreated(artifact.getYearCreated())
                .powerLevel(artifact.getPowerLevel())
                .creator(CreatorDetailsDto.builder()
                        .id(artifact.getCreator().getId())
                        .name(artifact.getCreator().getName())
                        .race(artifact.getCreator().getRace())
                        .realm(artifact.getCreator().getRealm())
                        .build())
                .build();
    }

}
