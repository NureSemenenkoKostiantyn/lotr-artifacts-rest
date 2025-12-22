package com.profitsoft.lotrartifactsrest.service;

import com.profitsoft.lotrartifactsrest.dto.CreatorDetailsDto;
import com.profitsoft.lotrartifactsrest.dto.CreatorSaveDto;
import com.profitsoft.lotrartifactsrest.model.Creator;
import com.profitsoft.lotrartifactsrest.repository.CreatorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreatorService {
    private final CreatorRepository creatorRepository;

    public CreatorService(CreatorRepository creatorRepository) {
        this.creatorRepository = creatorRepository;
    }

    public List<CreatorDetailsDto> getAllCreators() {
        List<Creator> data = creatorRepository.findAll();
        return data.stream()
                .map(CreatorService::convertToDetailsDto)
                .toList();
    }

    public CreatorDetailsDto saveCreator(CreatorSaveDto dto) {
        Creator creator = convertToEntity(dto);
        Creator savedCreator = creatorRepository.save(creator);
        return convertToDetailsDto(savedCreator);
    }

    public CreatorDetailsDto getCreatorById(Long creatorId) {
        Creator creator = creatorRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("Creator with id '%s' not found".formatted(creatorId)));
        return convertToDetailsDto(creator);
    }

    public CreatorDetailsDto updateCreator(Long creatorId, CreatorSaveDto dto) {
        Creator existingCreator = creatorRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("Creator with id '%s' not found".formatted(creatorId)));
        existingCreator.setName(dto.getName());
        existingCreator.setRace(dto.getRace());
        existingCreator.setRealm(dto.getRealm());
        Creator updatedCreator = creatorRepository.save(existingCreator);
        return convertToDetailsDto(updatedCreator);
    }

    public void deleteCreator(Long creatorId) {
        creatorRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("Creator with id '%s' not found".formatted(creatorId)));
        creatorRepository.deleteById(creatorId);

    }

    private static CreatorDetailsDto convertToDetailsDto(Creator data) {
        return CreatorDetailsDto.builder()
                .id(data.getId())
                .name(data.getName())
                .race(data.getRace())
                .realm(data.getRealm())
                .build();
    }

    private static Creator convertToEntity(CreatorSaveDto dto) {
        Creator creator = new Creator();
        creator.setName(dto.getName());
        creator.setRace(dto.getRace());
        creator.setRealm(dto.getRealm());
        return creator;
    }

}
