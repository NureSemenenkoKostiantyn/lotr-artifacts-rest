package com.profitsoft.lotrartifactsrest.service;

import com.profitsoft.lotrartifactsrest.repository.ArtifactRepository;
import org.springframework.stereotype.Service;

@Service
public class ArtifactService {
    private final ArtifactRepository artifactRepository;

    public ArtifactService(ArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }


}
