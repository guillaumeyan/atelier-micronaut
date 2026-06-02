package io.shodo.matrix.domain.api;

import io.shodo.matrix.domain.model.NomPersonnage;
import io.shodo.matrix.domain.model.ResultatCombat;
import java.util.List;

public interface ZoneCombatApi {

  ResultatCombat combattre(List<NomPersonnage> teamA, List<NomPersonnage> teamB);
}
