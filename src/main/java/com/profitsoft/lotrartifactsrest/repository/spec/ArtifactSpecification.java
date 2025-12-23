package com.profitsoft.lotrartifactsrest.repository.spec;

import com.profitsoft.lotrartifactsrest.model.Artifact;
import org.springframework.data.jpa.domain.Specification;

public class ArtifactSpecification {

    public static Specification<Artifact> hasCreator(Long creatorId) {
        return (root, query, cb) ->
                creatorId == null ? null :
                        cb.equal(root.get("creator").get("id"), creatorId);
    }

    public static Specification<Artifact> hasOrigin(String origin) {
        return (root, query, cb) ->
                origin == null ? null :
                        cb.equal(cb.lower(root.get("origin")), origin.toLowerCase());
    }

    public static Specification<Artifact> yearBetween(Integer from, Integer to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return null;
            if (from != null && to != null)
                return cb.between(root.get("yearCreated"), from, to);
            if (from != null)
                return cb.greaterThanOrEqualTo(root.get("yearCreated"), from);
            return cb.lessThanOrEqualTo(root.get("yearCreated"), to);
        };
    }

    public static Specification<Artifact> powerBetween(Integer from, Integer to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return null;
            if (from != null && to != null)
                return cb.between(root.get("powerLevel"), from, to);
            if (from != null)
                return cb.greaterThanOrEqualTo(root.get("powerLevel"), from);
            return cb.lessThanOrEqualTo(root.get("powerLevel"), to);
        };
    }
}