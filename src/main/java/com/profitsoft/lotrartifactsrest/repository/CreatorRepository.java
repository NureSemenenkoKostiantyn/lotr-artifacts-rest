package com.profitsoft.lotrartifactsrest.repository;

import com.profitsoft.lotrartifactsrest.model.Creator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreatorRepository extends JpaRepository<Creator, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdIsNot(String name, Long id);
}