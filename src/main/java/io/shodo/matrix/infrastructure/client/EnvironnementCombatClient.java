package io.shodo.matrix.infrastructure.client;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;

@Client("${matrix.api.url}")
public interface EnvironnementCombatClient {

  @Get("/matrix/environnement-combat")
  EnvironnementCombatDto recupererEnvironnementCombat();
}

