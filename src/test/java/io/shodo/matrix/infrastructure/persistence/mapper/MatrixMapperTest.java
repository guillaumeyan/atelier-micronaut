package io.shodo.matrix.infrastructure.persistence.mapper;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.shodo.matrix.domain.model.Competence;
import io.shodo.matrix.domain.model.Personnage;
import io.shodo.matrix.domain.model.PersonnageRole;
import io.shodo.matrix.domain.model.TypeVaisseau;
import io.shodo.matrix.infrastructure.persistence.entity.CompetenceEntity;
import io.shodo.matrix.infrastructure.persistence.entity.PersonnageEntity;
import io.shodo.matrix.infrastructure.persistence.entity.VaisseauEntity;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
class MatrixMapperTest {

  @Inject
  MatrixMapper matrixMapper;

  @Test
  void toDomain_personnage_avecTousLesChamps() {
    // Given
    UUID personnageId = UUID.randomUUID();
    UUID vaisseauId = UUID.randomUUID();
    UUID competenceId = UUID.randomUUID();
    Instant now = Instant.parse("2024-01-01T00:00:00Z");

    VaisseauEntity vaisseauEntity = new VaisseauEntity(
        vaisseauId,
        "Nebuchadnezzar",
        TypeVaisseau.HOVERCRAFT,
        75,
        now,
        now
    );

    CompetenceEntity competenceEntity = new CompetenceEntity(
        competenceId,
        "Kung-fu",
        "Maîtrise des arts martiaux",
        now,
        now
    );

    PersonnageEntity entity = new PersonnageEntity(
        personnageId,
        "Neo",
        "Thomas Anderson",
        PersonnageRole.HUMAIN,
        90,
        vaisseauEntity,
        Set.of(competenceEntity),
        now,
        now
    );

    // When
    Personnage personnage = matrixMapper.toDomain(entity);

    // Then
    assertThat(personnage.id()).isEqualTo(personnageId);
    assertThat(personnage.nom()).isEqualTo("Neo");
    assertThat(personnage.alias()).isEqualTo("Thomas Anderson");
    assertThat(personnage.role()).isEqualTo(PersonnageRole.HUMAIN);
    assertThat(personnage.scoreDeCombat()).isEqualTo(90);
    assertThat(personnage.vaisseau().nom()).isEqualTo("Nebuchadnezzar");
    assertThat(personnage.vaisseau().typeVaisseau()).isEqualTo(TypeVaisseau.HOVERCRAFT);
    assertThat(personnage.vaisseau().scoreDeCombat()).isEqualTo(75);
    assertThat(personnage.competences()).hasSize(1);
    assertThat(personnage.competences()).extracting(Competence::nom).containsExactly("Kung-fu");
  }

  @Test
  void toDomain_personnage_VaisseauEtCompetenceNull() {
    // Given
    UUID personnageId = UUID.randomUUID();
    UUID vaisseauId = UUID.randomUUID();
    UUID competenceId = UUID.randomUUID();
    Instant now = Instant.parse("2024-01-01T00:00:00Z");

    PersonnageEntity entity = new PersonnageEntity(
        personnageId,
        "Neo",
        "Thomas Anderson",
        PersonnageRole.HUMAIN,
        90,
        null,
        null,
        now,
        now
    );

    // When
    Personnage personnage = matrixMapper.toDomain(entity);

    // Then
    assertThat(personnage.id()).isEqualTo(personnageId);
    assertThat(personnage.nom()).isEqualTo("Neo");
    assertThat(personnage.alias()).isEqualTo("Thomas Anderson");
    assertThat(personnage.role()).isEqualTo(PersonnageRole.HUMAIN);
    assertThat(personnage.scoreDeCombat()).isEqualTo(90);
    assertThat(personnage.vaisseau()).isNull();
    assertThat(personnage.competences()).isNull();
  }
}
