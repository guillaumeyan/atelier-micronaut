package io.shodo.matrix.infrastructure.controller.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.shodo.matrix.domain.model.PersonnageRole;

@Serdeable
public record PersonnageDto(
    String nom,
    String alias,
    PersonnageRole role,
    Integer scoreDeCombat) {

}
