package io.shodo.matrix.infrastructure.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.shodo.matrix.domain.api.RecuperationPersonnagesApi;
import io.shodo.matrix.domain.api.ZoneCombatApi;
import io.shodo.matrix.domain.exception.PersonnageNotFoundException;
import io.shodo.matrix.domain.model.NomPersonnage;
import io.shodo.matrix.infrastructure.controller.dto.Combattant;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@MicronautTest
class ControllerErrorTest {

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
    void error_retourne_500_avec_message_et_lien_self_quand_exception_generique() {
        // Given
        when(recuperationPersonnagesApi.rechercher(any(), any()))
            .thenThrow(new RuntimeException("matrix is broken"));

        // When
        HttpClientResponseException ex = assertThrows(
            HttpClientResponseException.class,
            () -> httpClient.toBlocking().exchange(
                HttpRequest.GET("/matrix/personnages?nom=Neo"), String.class
            )
        );

        // Then
        assertThat(ex.getResponse().getStatus().getCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getCode());

        String body = ex.getResponse().getBody(String.class).orElseThrow();
        DocumentContext doc = JsonPath.parse(body);

        String message = doc.read("$.message");

        assertThat(message).contains("Erreur détectée. Cette version de la réalité");
        assertThat(message).contains("matrix is broken");
    }

    @Test
    void error_retourne_400_quand_violation_de_contrainte() {
        // Given
        when(zoneCombatApi.combattre(any(), any()))
            .thenThrow(new ConstraintViolationException("nom invalide", Set.of()));

        // When
        HttpClientResponseException ex = assertThrows(
            HttpClientResponseException.class,
            () -> httpClient.toBlocking().exchange(
                HttpRequest.POST("/matrix/combat", new Combattant(List.of("Neo"), List.of("Smith"))),
                String.class
            )
        );

        // Then
        assertThat(ex.getResponse().getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());

        String body = ex.getResponse().getBody(String.class).orElseThrow();
        DocumentContext doc = JsonPath.parse(body);

        String message = doc.read("$.message");

        assertThat(message).contains("nom invalide");
    }

    @Test
    void error_retourne_400_quand_personnage_non_trouvee() {
        // Given
        when(zoneCombatApi.combattre(any(), any()))
            .thenThrow(new PersonnageNotFoundException(List.of(new NomPersonnage("Neo"))));

        // When
        HttpClientResponseException ex = assertThrows(
            HttpClientResponseException.class,
            () -> httpClient.toBlocking().exchange(
                HttpRequest.POST("/matrix/combat", new Combattant(List.of("Neo"), List.of("Smith"))),
                String.class
            )
        );

        // Then
        assertThat(ex.getResponse().getStatus().getCode()).isEqualTo(HttpStatus.NOT_FOUND.getCode());

        String body = ex.getResponse().getBody(String.class).orElseThrow();
        DocumentContext doc = JsonPath.parse(body);

        String message = doc.read("$.message");

        assertThat(message).contains("Personnage non trouvés");
    }
}
