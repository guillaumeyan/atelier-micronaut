package io.shodo.matrix.domain.service;

import io.shodo.matrix.domain.api.RecuperationPersonnagesApi;
import io.shodo.matrix.domain.model.PageRequest;
import io.shodo.matrix.domain.model.PageResult;
import io.shodo.matrix.domain.model.Personnage;
import io.shodo.matrix.domain.model.PersonnageFilter;
import io.shodo.matrix.domain.spi.PersonnageRepository;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class RecuperationPersonnages implements RecuperationPersonnagesApi {

  private final PersonnageRepository personnageRepository;

  @Override
  public PageResult<Personnage> rechercher(PersonnageFilter filter, PageRequest pageRequest) {
    PersonnageFilter effectiveFilter = filter == null ? PersonnageFilter.aucun() : filter;
    return personnageRepository.rechercher(effectiveFilter, pageRequest);
  }
}
