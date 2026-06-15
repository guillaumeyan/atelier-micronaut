package io.shodo.matrix.infrastructure.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.shodo.matrix.domain.exception.PersonnageNotFoundException;
import io.shodo.matrix.domain.model.NomPersonnage;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ControllerError {

  @Error(global = true)
  public HttpResponse<JsonError> error(HttpRequest<?> request, Throwable e) {
    JsonError error = new JsonError("Erreur détectée. Cette version de la réalité n’est pas supportée.\n " + e.getMessage())
        .link(Link.SELF, Link.of(request.getUri()));
    log.error("Erreur détectée. Cette version de la réalité n’est pas supportée.", e);
    return HttpResponse.<JsonError>serverError()
        .body(error);
  }

  @Error(global = true)
  public HttpResponse<JsonError> error(HttpRequest<?> request, ConstraintViolationException e) {
    JsonError error = new JsonError("Ces paramètres n’existent pas dans cette réalité.\n " + e.getMessage())
        .link(Link.SELF, Link.of(request.getUri()));
    log.error("Ces paramètres n’existent pas dans cette réalité..", e);
    return HttpResponse.<JsonError>serverError()
        .status(HttpStatus.BAD_REQUEST)
        .body(error);
  }
}
