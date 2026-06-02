package io.shodo.matrix.infrastructure.controller.mapper;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.shodo.matrix.domain.model.PageRequest;
import io.shodo.matrix.domain.model.PageResult;
import io.shodo.matrix.domain.model.Personnage;
import io.shodo.matrix.infrastructure.controller.dto.PersonnageDto;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class PersonnageMapper {

  public List<PersonnageDto> toPersonnageDto(List<Personnage> personnages) {
    return personnages.stream()
        .map(this::toPersonnageDto)
        .toList();
  }

  public PersonnageDto toPersonnageDto(Personnage personnage) {
    return new PersonnageDto(
        personnage.nom(),
        personnage.alias(),
        personnage.role(),
        personnage.scoreDeCombat()
    );
  }

  public Page<PersonnageDto> toPage(PageResult<Personnage> result) {
    List<PersonnageDto> content = toPersonnageDto(result.content());
    return Page.of(
        content,
        Pageable.from(result.page(), result.size()),
        result.totalElements()
    );
  }

  public PageRequest toPageRequest(Pageable pageable) {
    return new PageRequest(
        pageable.getNumber(),
        pageable.getSize(),
        toSortString(pageable)
    );
  }

  private String toSortString(Pageable pageable) {
    return pageable.getSort()
        .getOrderBy()
        .stream()
        .findFirst()
        .map(order -> order.getProperty() + "," + order.getDirection()
            .name())
        .orElse(null);
  }
}
