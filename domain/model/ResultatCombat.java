package io.shodo.matrix.domain.model;

import io.micronaut.serde.annotation.Serdeable;
import java.util.List;

@Serdeable
public record ResultatCombat(List<NomPersonnage> gagnants, Integer totalPointGagnant, List<NomPersonnage> perdants, Integer totalPointPerdant) {

}
