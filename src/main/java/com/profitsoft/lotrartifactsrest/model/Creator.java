package com.profitsoft.lotrartifactsrest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "creator")
@Getter
@Setter
@NoArgsConstructor
public class Creator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String name;

    @Size(max = 255)
    private String race;

    @Size(max = 255)
    private String realm;
}