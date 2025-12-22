package com.profitsoft.lotrartifactsrest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "artifact")
@Getter
@Setter
@NoArgsConstructor
public class Artifact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private Creator creator;

    @Size(max = 255)
    private String origin;

    @Size(max = 500)
    private String tags;

    @Min(0)
    private Integer yearCreated;

    @Min(0)
    @Max(10_000)
    private Integer powerLevel;
}