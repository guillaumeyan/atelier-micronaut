package io.shodo.matrix.domain.api;

import io.shodo.matrix.domain.model.PageRequest;
import io.shodo.matrix.domain.model.PageResult;
import io.shodo.matrix.domain.model.Personnage;
import io.shodo.matrix.domain.model.PersonnageFilter;

public interface RecuperationPersonnagesApi {

  PageResult<Personnage> rechercher(PersonnageFilter filter, PageRequest pageRequest);
}
