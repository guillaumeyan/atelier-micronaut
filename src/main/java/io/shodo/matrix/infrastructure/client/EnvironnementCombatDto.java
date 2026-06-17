package io.shodo.matrix.infrastructure.client;

import io.micronaut.serde.annotation.Serdeable;
import io.shodo.matrix.domain.model.PersonnageRole;
import java.util.List;

@Serdeable
public record EnvironnementCombatDto(
    String nom,
    String description,
    List<PersonnageRole> rolesBoosted,
    List<PersonnageRole> rolesAffaiblis
) {

}

