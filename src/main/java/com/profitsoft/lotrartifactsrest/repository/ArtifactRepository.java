package com.profitsoft.lotrartifactsrest.repository;

import com.profitsoft.lotrartifactsrest.model.Artifact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtifactRepository extends JpaRepository<Artifact, Long> {
}
