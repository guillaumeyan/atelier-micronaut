package io.shodo.matrix.infrastructure.persistence.entity;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.Id;

import io.micronaut.data.annotation.MappedEntity;
import java.time.Instant;
import java.util.UUID;

@MappedEntity("competence")
public record CompetenceEntity(
    @Id
    @AutoPopulated
    UUID id,
    String nom,
    String description,
    @DateCreated
    Instant dateDeCreation,
    @DateUpdated
    Instant dateDeMaj
) {

}
