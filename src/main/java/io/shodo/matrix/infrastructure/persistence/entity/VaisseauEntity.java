package io.shodo.matrix.infrastructure.persistence.entity;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.shodo.matrix.domain.model.TypeVaisseau;
import java.time.Instant;
import java.util.UUID;

@MappedEntity("vaisseau")
public record VaisseauEntity(
    @Id
    @AutoPopulated
    UUID id,
    String nom,
    TypeVaisseau typeVaisseau,
    Integer scoreDeCombat,
    @DateCreated
    Instant dateDeCreation,
    @DateUpdated
    Instant dateDeMaj
) {

}
