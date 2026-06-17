package io.shodo.matrix.infrastructure.persistence.repository;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Join.Type;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;
import io.shodo.matrix.infrastructure.persistence.entity.PersonnageEntity;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface JdbcPersonnageRepository extends PageableRepository<PersonnageEntity, UUID> {

  Page<PersonnageEntity> findByNomIlike(String nom, Pageable pageable);

  Page<PersonnageEntity> find(Pageable pageable);

  @Join(value = "vaisseau", type = Type.LEFT_FETCH)
  @Join(value = "competences", type = Type.LEFT_FETCH)
  Set<PersonnageEntity> findByNomInList(List<String> names);

}
