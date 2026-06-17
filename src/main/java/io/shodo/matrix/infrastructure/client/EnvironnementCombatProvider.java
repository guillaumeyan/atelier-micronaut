package io.shodo.matrix.infrastructure.client;

import io.shodo.matrix.domain.model.EnvironnementCombat;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class EnvironnementCombatProvider implements io.shodo.matrix.domain.spi.EnvironnementCombatProvider {

  private final EnvironnementCombatClient client;

  @Override
  public EnvironnementCombat recupererEnvironnementCombat() {
    EnvironnementCombatDto environnementCombatDto = client.recupererEnvironnementCombat();
    return new EnvironnementCombat(environnementCombatDto.nom(), environnementCombatDto.rolesBoosted(), environnementCombatDto.rolesAffaiblis());
  }
}
