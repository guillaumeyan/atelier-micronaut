package io.shodo.matrix.infrastructure.controller.dto;

import io.shodo.matrix.domain.model.NomPersonnage;
import java.util.List;

public record PersonnageNotFoundError(List<NomPersonnage> teamNames, List<NomPersonnage> teamFounds) {

}
