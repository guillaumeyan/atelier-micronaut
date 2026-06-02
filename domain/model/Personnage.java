package io.shodo.matrix.domain.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record Personnage(
    UUID id,
    @NotNull String nom,
    String alias,
    @NotNull PersonnageRole role,
    @Min(0) Integer scoreDeCombat,
    Vaisseau vaisseau,
    @NotNull Set<Competence> competences
) {

  public Integer calculerScoreCombat(List<Personnage> teamMates) {
    return teamMates.stream()
        .filter(teamMate -> teamMate.nom.equalsIgnoreCase("Morpheus"))
        .findAny()
        .map(morpheus -> scoreDeCombat + 10)
        .orElse(scoreDeCombat);
  }
}