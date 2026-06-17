package io.shodo.matrix.infrastructure.controller;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.shodo.matrix.domain.api.RecuperationPersonnagesApi;
import io.shodo.matrix.domain.api.ZoneCombatApi;
import io.shodo.matrix.domain.exception.PersonnageNotFoundException;
import io.shodo.matrix.domain.model.NomPersonnage;
import io.shodo.matrix.domain.model.PageRequest;
import io.shodo.matrix.domain.model.PageResult;
import io.shodo.matrix.domain.model.Personnage;
import io.shodo.matrix.domain.model.PersonnageFilter;
import io.shodo.matrix.domain.model.ResultatCombat;
import io.shodo.matrix.infrastructure.controller.dto.Combattant;
import io.shodo.matrix.infrastructure.controller.dto.PersonnageDto;
import io.shodo.matrix.infrastructure.controller.dto.ResultatCombatDto;
import io.shodo.matrix.infrastructure.controller.mapper.PersonnageMapper;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@Controller("/matrix")
@RequiredArgsConstructor
public class MatrixController {

  private final RecuperationPersonnagesApi recuperationPersonnagesApi;
  private final PersonnageMapper personnageMapper;
  private final ZoneCombatApi zoneCombatApi;

  @Get("/personnages")
  public Page<PersonnageDto> getAll(@Nullable @QueryValue String nom, Pageable pageable) {
    PageRequest pageRequest = personnageMapper.toPageRequest(pageable);
    PageResult<Personnage> result = recuperationPersonnagesApi.rechercher(
        new PersonnageFilter(nom),
        pageRequest
    );
    return personnageMapper.toPage(result);
  }

  @Post("/combat")
  @ExecuteOn(TaskExecutors.IO)
  public ResultatCombatDto combattre(@Valid @Body Combattant combattant) {
    List<NomPersonnage> teamANames = Objects.requireNonNullElse(combattant.combattantEquipeA(), List.<String>of())
        .stream()
        .map(NomPersonnage::new)
        .toList();
    List<NomPersonnage> teamBNames = Objects.requireNonNullElse(combattant.combattantEquipeB(), List.<String>of())
        .stream()
        .map(NomPersonnage::new)
        .toList();
    ResultatCombat resultatCombat = zoneCombatApi.combattre(teamANames, teamBNames);
    return new ResultatCombatDto(
        resultatCombat.gagnants()
            .stream()
            .map(NomPersonnage::name)
            .toList(),
        resultatCombat.totalPointGagnant(),
        resultatCombat.perdants()
            .stream()
            .map(NomPersonnage::name)
            .toList(),
        resultatCombat.totalPointPerdant()
    );
  }

  @Error
  public HttpResponse<JsonError> notFound(HttpRequest<?> request, PersonnageNotFoundException personnageNotFoundException) {
    JsonError error = new JsonError("Personnage non trouvés : " + personnageNotFoundException.getPersonnages()
        .stream()
        .map(NomPersonnage::name)
        .toList())
        .link(Link.SELF, Link.of(request.getUri()));

    return HttpResponse.<JsonError>notFound()
        .body(error);
  }
}
