package io.shodo.matrix.infrastructure.persistence.entity;

import static io.micronaut.data.annotation.Relation.Kind.MANY_TO_MANY;
import static io.micronaut.data.annotation.Relation.Kind.MANY_TO_ONE;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.sql.JoinColumn;
import io.micronaut.data.annotation.sql.JoinTable;
import io.shodo.matrix.domain.model.PersonnageRole;

import jakarta.annotation.Nullable;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@MappedEntity(value = "personnage")
public record PersonnageEntity(
    @Id
    @AutoPopulated
    UUID id,
    String nom,
    @Nullable String alias,
    PersonnageRole role,
    Integer scoreDeCombat,
    @Relation(MANY_TO_ONE)
    @Nullable
    VaisseauEntity vaisseau,
    @Relation(MANY_TO_MANY)
    @JoinTable(
        name = "personnage_competence",
        joinColumns = @JoinColumn(name = "personnage_id"),
        inverseJoinColumns = @JoinColumn(name = "competence_id")
    )
    Set<CompetenceEntity> competences,
    @DateCreated
    Instant dateDeCreation,
    @DateUpdated
    Instant dateDeMaj
) {

}
