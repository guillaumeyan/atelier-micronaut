package io.shodo.matrix.infrastructure.persistence.repository;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import io.shodo.matrix.infrastructure.persistence.entity.PersonnageEntity;
import io.shodo.matrix.infrastructure.persistence.entity.VaisseauEntity;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
class JdbcPersonnageRepositoryTest {

    @Inject
    JdbcPersonnageRepository repository;

    // --- find(Pageable) ---

    @Test
    void find_retourneLaPremierePage_avecTotalCorrect() {
        Page<PersonnageEntity> page = repository.find(Pageable.from(0, 5));

        assertThat(page.getTotalSize()).isEqualTo(7);
        assertThat(page.getContent()).hasSize(5);
    }

    @Test
    void find_retourneLeReste_surLaDeuxiemePage() {
        Page<PersonnageEntity> page = repository.find(Pageable.from(1, 5));

        assertThat(page.getTotalSize()).isEqualTo(7);
        assertThat(page.getContent()).hasSize(2);
    }

    // --- findByNomIlike ---

    @Test
    void findByNomIlike_retournePersonnagesCorrespondants_sansDistinctionDeCasse() {
        Page<PersonnageEntity> page = repository.findByNomIlike("%the%", Pageable.from(0, 10));

        assertThat(page.getContent())
                .hasSize(2)
                .extracting(PersonnageEntity::nom)
                .containsExactlyInAnyOrder("The Oracle", "The Merovingian");
    }

    @Test
    void findByNomIlike_avecPatternEnMajuscule_retourneLaCorrespondance() {
        Page<PersonnageEntity> page = repository.findByNomIlike("%MORPHEUS%", Pageable.from(0, 10));

        assertThat(page.getContent())
                .hasSize(1)
                .first()
                .extracting(PersonnageEntity::nom)
                .isEqualTo("Morpheus");
    }

    @Test
    void findByNomIlike_sansCorrespondance_retournePageVide() {
        Page<PersonnageEntity> page = repository.findByNomIlike("%zzzzzz%", Pageable.from(0, 10));

        assertThat(page.getContent()).isEmpty();
        assertThat(page.getTotalSize()).isZero();
    }

    // --- findByNomInList ---

    @Test
    void findByNomInList_retourneSeulementLesPersonnagesExistants() {
        Set<PersonnageEntity> result = repository.findByNomInList(
                List.of("Morpheus", "Trinity", "Inexistant")
        );

        assertThat(result)
                .hasSize(2)
                .extracting(PersonnageEntity::nom)
                .containsExactlyInAnyOrder("Morpheus", "Trinity");
    }

    @Test
    void findByNomInList_retourneSeulementLesPersonnagesExistantsAvecRelation() {
        Set<PersonnageEntity> result = repository.findByNomInList(
            List.of("Morpheus", "Agent Smith")
        );
        assertThat(result)
            .hasSize(2)
            .extracting(PersonnageEntity::vaisseau)
            .filteredOn(Objects::nonNull)
            .extracting(VaisseauEntity::nom)
            .containsExactlyInAnyOrder("Nebuchadnezzar");
    }

    @Test
    void findByNomInList_sansCorrespondance_retourneEnsembleVide() {
        Set<PersonnageEntity> result = repository.findByNomInList(
                List.of("Inconnu", "Fantome")
        );

        assertThat(result).isEmpty();
    }
}
