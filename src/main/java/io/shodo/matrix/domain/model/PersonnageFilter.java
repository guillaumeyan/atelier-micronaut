package io.shodo.matrix.domain.model;

public record PersonnageFilter(String nom) {

  public static PersonnageFilter aucun() {
    return new PersonnageFilter(null);
  }

  public boolean hasNom() {
    return nom != null && !nom.isBlank();
  }
}
