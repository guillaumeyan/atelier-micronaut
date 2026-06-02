package io.shodo.matrix.domain.spi;

import io.shodo.matrix.domain.model.NomPersonnage;
import io.shodo.matrix.domain.model.PageRequest;
import io.shodo.matrix.domain.model.PageResult;
import io.shodo.matrix.domain.model.Personnage;
import io.shodo.matrix.domain.model.PersonnageFilter;
import java.util.List;

public interface PersonnageRepository {

    List<Personnage> findAllByName(List<NomPersonnage> names);

    PageResult<Personnage> rechercher(PersonnageFilter filter, PageRequest pageRequest);

}
