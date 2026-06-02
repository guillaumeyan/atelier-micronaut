package io.shodo.matrix.domain.spi;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.shodo.matrix.domain.model.NomPersonnage;
import io.shodo.matrix.domain.model.Personnage;
import java.util.List;

public interface PersonnageRepository {

    List<Personnage> findAllByName(List<NomPersonnage> names);

}
