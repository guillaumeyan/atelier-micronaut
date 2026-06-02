package io.shodo.matrix.domain.model;

import io.micronaut.serde.annotation.Serdeable;
import java.util.List;

@Serdeable
public record EnvironnementCombat(
    String nom,
    List<PersonnageRole> rolesBoosted,
    List<PersonnageRole> rolesAffaiblis
) {

}

