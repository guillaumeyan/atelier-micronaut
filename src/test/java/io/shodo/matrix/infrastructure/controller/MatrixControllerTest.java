package io.shodo.matrix.infrastructure.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.shodo.matrix.domain.api.RecuperationPersonnagesApi;
import io.shodo.matrix.domain.api.ZoneCombatApi;
import io.shodo.matrix.domain.model.NomPersonnage;
import io.shodo.matrix.domain.model.PageRequest;
import io.shodo.matrix.domain.model.PageResult;
import io.shodo.matrix.domain.model.Personnage;
import io.shodo.matrix.domain.model.PersonnageFilter;
import io.shodo.matrix.domain.model.PersonnageRole;
import io.shodo.matrix.domain.model.ResultatCombat;
import io.shodo.matrix.infrastructure.controller.dto.Combattant;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MicronautTest
class MatrixControllerTest {

    @Inject
    @Client("/")
    HttpClient httpClient;

    @Inject
    RecuperationPersonnagesApi recuperationPersonnagesApi;

    @MockBean(RecuperationPersonnagesApi.class)
    RecuperationPersonnagesApi mockRecuperationPersonnagesApi() {
        return Mockito.mock(RecuperationPersonnagesApi.class);
    }

    @Inject
    ZoneCombatApi zoneCombatApi;

    @MockBean(ZoneCombatApi.class)
    ZoneCombatApi mockZoneCombatApi() {
        return Mockito.mock(ZoneCombatApi.class);
    }

    @AfterEach
    void resetMocks() {
        Mockito.reset(recuperationPersonnagesApi, zoneCombatApi);
    }

    @Test
    void getAll_retourne_la_page_des_personnages() {
        // Given
        Personnage neo = personnage("Neo", "Thomas Anderson", PersonnageRole.HUMAIN, 90);
        Personnage trinity = personnage("Trinity", "Tiffany", PersonnageRole.HUMAIN, 85);
        when(recuperationPersonnagesApi.rechercher(any(), any()))
            .thenReturn(PageResult.of(List.of(neo, trinity), 0, 10, 2));

        // When
        String body = httpClient.toBlocking().retrieve(HttpRequest.GET("/matrix/personnages"));
        DocumentContext doc = JsonPath.parse(body);

        // Then
        assertThat((Integer) doc.read("$.content.length()")).isEqualTo(2);
        assertThat((String) doc.read("$.content[0].nom")).isEqualTo("Neo");
        assertThat((String) doc.read("$.content[0].alias")).isEqualTo("Thomas Anderson");
        assertThat((String) doc.read("$.content[0].role")).isEqualTo("HUMAIN");
        assertThat((Integer) doc.read("$.content[0].scoreDeCombat")).isEqualTo(90);
        assertThat((String) doc.read("$.content[1].nom")).isEqualTo("Trinity");
        assertThat((Integer) doc.read("$.totalSize")).isEqualTo(2);
        assertThat((Integer) doc.read("$.pageable.number")).isEqualTo(0);
        assertThat((Integer) doc.read("$.pageable.size")).isEqualTo(10);
    }

    @Test
    void getAll_transmet_le_filtre_nom_au_service() {
        // Given
        when(recuperationPersonnagesApi.rechercher(any(), any()))
            .thenReturn(PageResult.of(List.of(), 0, 10, 0));
        ArgumentCaptor<PersonnageFilter> filterCaptor = ArgumentCaptor.forClass(PersonnageFilter.class);

        // When
        httpClient.toBlocking().retrieve(HttpRequest.GET("/matrix/personnages?nom=Neo"));

        // Then
        verify(recuperationPersonnagesApi).rechercher(filterCaptor.capture(), any());
        assertThat(filterCaptor.getValue().nom()).isEqualTo("Neo");
    }

    @Test
    void getAll_transmet_la_pagination_et_le_tri_au_service() {
        // Given
        when(recuperationPersonnagesApi.rechercher(any(), any()))
            .thenReturn(PageResult.of(List.of(), 2, 5, 0));
        ArgumentCaptor<PageRequest> pageRequestCaptor = ArgumentCaptor.forClass(PageRequest.class);

        // When
        httpClient.toBlocking().retrieve(HttpRequest.GET("/matrix/personnages?page=2&size=5&sort=nom,DESC"));

        // Then
        verify(recuperationPersonnagesApi).rechercher(any(), pageRequestCaptor.capture());
        PageRequest pageRequest = pageRequestCaptor.getValue();
        assertThat(pageRequest.page()).isEqualTo(2);
        assertThat(pageRequest.size()).isEqualTo(5);
        assertThat(pageRequest.sort()).isEqualTo("nom,DESC");
    }

    @Test
    void getAll_sans_tri_envoie_un_sort_null() {
        // Given
        when(recuperationPersonnagesApi.rechercher(any(), any()))
            .thenReturn(PageResult.of(List.of(), 0, 10, 0));
        ArgumentCaptor<PageRequest> pageRequestCaptor = ArgumentCaptor.forClass(PageRequest.class);

        // When
        httpClient.toBlocking().retrieve(HttpRequest.GET("/matrix/personnages"));

        // Then
        verify(recuperationPersonnagesApi).rechercher(any(), pageRequestCaptor.capture());
        assertThat(pageRequestCaptor.getValue().sort()).isNull();
    }

    @Test
    void getAll_retourne_une_page_vide_quand_aucun_resultat() {
        // Given
        when(recuperationPersonnagesApi.rechercher(any(), any()))
            .thenReturn(PageResult.of(List.of(), 0, 10, 0));

        // When
        String body = httpClient.toBlocking().retrieve(HttpRequest.GET("/matrix/personnages"));
        DocumentContext doc = JsonPath.parse(body);

        // Then
        assertThat((Integer) doc.read("$.content.length()")).isEqualTo(0);
        assertThat((Integer) doc.read("$.totalSize")).isEqualTo(0);
    }

    // ---- helpers ----

    private Personnage personnage(String nom, String alias, PersonnageRole role, int scoreDeCombat) {
        return new Personnage(UUID.randomUUID(), nom, alias, role, scoreDeCombat, null, Set.of());
    }

    // ==============================
    // combattre
    // ==============================

    @Test
    void combattre_retourne_le_resultat_du_combat() {
        // Given
        ResultatCombat resultat = new ResultatCombat(
            List.of(new NomPersonnage("Neo"), new NomPersonnage("Trinity")), 175,
            List.of(new NomPersonnage("Smith")), 60
        );
        when(zoneCombatApi.combattre(any(), any())).thenReturn(resultat);

        // When
        String body = httpClient.toBlocking().retrieve(
            HttpRequest.POST("/matrix/combat", new Combattant(List.of("Neo", "Trinity"), List.of("Smith")))
        );
        DocumentContext doc = JsonPath.parse(body);

        // Then
        assertThat((List<String>) doc.read("$.gagnants")).containsExactly("Neo", "Trinity");
        assertThat((Integer) doc.read("$.totalPointGagnant")).isEqualTo(175);
        assertThat((List<String>) doc.read("$.perdants")).containsExactly("Smith");
        assertThat((Integer) doc.read("$.totalPointPerdant")).isEqualTo(60);
    }

    @Test
    @SuppressWarnings("unchecked")
    void combattre_transmet_les_noms_convertis_en_NomPersonnage_au_service() {
        // Given
        when(zoneCombatApi.combattre(any(), any()))
            .thenReturn(new ResultatCombat(List.of(), 0, List.of(), 0));
        ArgumentCaptor<List> captorA = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List> captorB = ArgumentCaptor.forClass(List.class);

        // When
        httpClient.toBlocking().retrieve(
            HttpRequest.POST("/matrix/combat", new Combattant(List.of("Neo", "Trinity"), List.of("Smith")))
        );

        // Then
        verify(zoneCombatApi).combattre(captorA.capture(), captorB.capture());
        assertThat((List<NomPersonnage>) captorA.getValue())
            .containsExactly(new NomPersonnage("Neo"), new NomPersonnage("Trinity"));
        assertThat((List<NomPersonnage>) captorB.getValue())
            .containsExactly(new NomPersonnage("Smith"));
    }
}
