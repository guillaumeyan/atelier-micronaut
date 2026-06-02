package io.shodo.matrix.domain.exception;

import io.shodo.matrix.domain.model.NomPersonnage;
import java.util.List;
import lombok.Getter;

@Getter
public class PersonnageNotFoundException extends RuntimeException {

  private final List<NomPersonnage> personnages;

  public PersonnageNotFoundException(List<NomPersonnage> personnages) {
    this.personnages = personnages;
  }
}
