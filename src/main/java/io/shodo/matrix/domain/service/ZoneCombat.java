package io.shodo.matrix.domain.service;

import io.shodo.matrix.domain.api.ZoneCombatApi;
import io.shodo.matrix.domain.exception.PersonnageNotFoundException;
import io.shodo.matrix.domain.model.NomPersonnage;
import io.shodo.matrix.domain.model.Personnage;
import io.shodo.matrix.domain.model.ResultatCombat;
import io.shodo.matrix.domain.spi.EnvironnementCombatProvider;
import io.shodo.matrix.domain.spi.PersonnageRepository;
import io.shodo.matrix.domain.spi.ReportingProvider;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class ZoneCombat implements ZoneCombatApi {

  private final PersonnageRepository personnageRepository;
  private final Optional<EnvironnementCombatProvider> environnementCombatProvider;
  private final Optional<ReportingProvider> reportingProvider;

  @Override
  public ResultatCombat combattre(List<NomPersonnage> teamANames, List<NomPersonnage> teamBNames) {
    List<Personnage> teamA = personnageRepository.findAllByName(teamANames);
    List<Personnage> teamB = personnageRepository.findAllByName(teamBNames);
    if (teamANames.size() != teamA.size() || teamBNames.size() != teamB.size()) {
      gererErreurPersonnageNonTrouve(teamANames, teamBNames, teamA, teamB);
    }

    int scoreTeamA = calculerScoreEquipe(teamA) + ThreadLocalRandom.current()
        .nextInt(1, 11);
    int scoreTeamB = calculerScoreEquipe(teamB) + ThreadLocalRandom.current()
        .nextInt(1, 11);

    // En cas d'égalité résiduelle, on départage aléatoirement
    while (scoreTeamA == scoreTeamB) {
      scoreTeamA += ThreadLocalRandom.current()
          .nextInt(1, 11);
      scoreTeamB += ThreadLocalRandom.current()
          .nextInt(1, 11);
    }

    List<Personnage> gagnants;
    List<Personnage> perdants;
    List<NomPersonnage> nomsGagnants;
    List<NomPersonnage> nomsPerdants;
    int scoreGagnants;
    int scorePerdants;

    if (scoreTeamA > scoreTeamB) {
      gagnants = teamA;
      perdants = teamB;
      nomsGagnants = teamANames;
      nomsPerdants = teamBNames;
      scoreGagnants = scoreTeamA;
      scorePerdants = scoreTeamB;
    } else {
      gagnants = teamB;
      perdants = teamA;
      nomsGagnants = teamBNames;
      nomsPerdants = teamANames;
      scoreGagnants = scoreTeamB;
      scorePerdants = scoreTeamA;
    }

    reportingProvider.ifPresent(provider ->
        provider.sendReporting(
            UUID.randomUUID()
                .toString(), gagnants, perdants
        ));

    return new ResultatCombat(nomsGagnants, scoreGagnants, nomsPerdants, scorePerdants);
  }

  private int calculerScoreEquipe(List<Personnage> team) {
    return calculerScoreCombatPersonnage(team)
        + calculerScoreCombatCompetence(team)
        + calculerScoreEquipeEnvironnement(team);
  }

  private int calculerScoreCombatPersonnage(List<Personnage> team) {
    return team.stream()
        .mapToInt(character -> {
          List<Personnage> teamMates = team.stream()
              .filter(c -> !c.id()
                  .equals(character.id()))
              .toList();
          return character.calculerScoreCombat(teamMates);
        })
        .sum();
  }

  private int calculerScoreCombatCompetence(List<Personnage> team) {
    return team.stream()
        .map(Personnage::competences)
        .flatMap(Collection::stream)
        .filter(skill -> skill.nom()
            .equalsIgnoreCase("Kung Fu"))
        .count() > 2 ? 10 : 0;
  }

  private void gererErreurPersonnageNonTrouve(List<NomPersonnage> teamANames, List<NomPersonnage> teamBNames, List<Personnage> teamA,
      List<Personnage> teamB) {
    List<NomPersonnage> tousLesPersonnagesRequetesNames = new ArrayList<>(teamANames);
    tousLesPersonnagesRequetesNames.addAll(teamBNames);
    List<Personnage> tousLesPersonnages = new ArrayList<>(teamA);
    tousLesPersonnages.addAll(teamB);
    List<NomPersonnage> nomPersonnagesTrouves = tousLesPersonnages.stream()
        .map(personnage -> new NomPersonnage(personnage.nom()))
        .toList();
    List<NomPersonnage> personnagesNonTrouve = tousLesPersonnagesRequetesNames.stream()
        .filter(nomPersonnageRequete -> !nomPersonnagesTrouves.contains(nomPersonnageRequete))
        .toList();
    throw new PersonnageNotFoundException(personnagesNonTrouve);
  }

  private int calculerScoreEquipeEnvironnement(List<Personnage> team) {
    return environnementCombatProvider.map(EnvironnementCombatProvider::recupererEnvironnementCombat)
        .map(environnementCombat -> {
          int scoreEquipe = 0;
          if (environnementCombat.rolesBoosted()
              .stream()
              .anyMatch(role -> team.stream()
                  .anyMatch(character -> character.role() == role))) {
            scoreEquipe += 5;
          }
          if (environnementCombat.rolesAffaiblis()
              .stream()
              .anyMatch(role -> team.stream()
                  .anyMatch(character -> character.role() == role))) {
            scoreEquipe -= 5;
          }
          return scoreEquipe;
        })
        .orElse(0);
  }
}
