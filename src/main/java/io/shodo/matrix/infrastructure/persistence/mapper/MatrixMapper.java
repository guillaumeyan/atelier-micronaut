package io.shodo.matrix.infrastructure.persistence.mapper;

import io.micronaut.context.annotation.Mapper;
import io.micronaut.context.annotation.Mapper.Mapping;
import io.micronaut.core.annotation.Introspected;
import io.shodo.matrix.domain.model.Competence;
import io.shodo.matrix.domain.model.Personnage;
import io.shodo.matrix.domain.model.Vaisseau;
import io.shodo.matrix.infrastructure.persistence.entity.CompetenceEntity;
import io.shodo.matrix.infrastructure.persistence.entity.PersonnageEntity;
import io.shodo.matrix.infrastructure.persistence.entity.VaisseauEntity;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Set;

@Singleton
@Introspected(classes = {Personnage.class, Vaisseau.class, Competence.class})
public interface MatrixMapper {

  @Mapping(from = "#{ this.toDomain(entity.vaisseau) }", to = "vaisseau", condition = "#{ entity.vaisseau != null }")
  @Mapping(from = "#{ this.toDomainCompetence(entity.competences) }", to = "competences", condition = "#{ entity.competences != null }")
  Personnage toDomain(PersonnageEntity entity);

  @Mapper
  Vaisseau toDomain(VaisseauEntity entity);

  @Mapper
  Competence toDomain(CompetenceEntity entity);

  default List<Competence> toDomainCompetence(Set<CompetenceEntity> entities) {
    return entities.stream()
        .map(this::toDomain)
        .toList();
  }

  default List<Personnage> toDomain(Set<PersonnageEntity> entities) {
    return entities.stream()
        .map(this::toDomain)
        .toList();
  }

  default List<Personnage> toDomain(List<PersonnageEntity> entities) {
    return entities.stream()
        .map(this::toDomain)
        .toList();
  }
}
