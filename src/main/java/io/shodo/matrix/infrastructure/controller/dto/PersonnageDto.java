package io.shodo.matrix.infrastructure.controller.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import io.shodo.matrix.domain.model.PersonnageRole;

public record PersonnageDto(
    String nom,
    String alias,
    PersonnageRole role,
    Integer scoreDeCombat) {

}
