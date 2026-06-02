package io.shodo.matrix.infrastructure.controller.dto;

import io.micronaut.serde.annotation.Serdeable;
import java.util.List;

public record ResultatCombatDto(List<String> gagnants, Integer totalPointGagnant, List<String> perdants, Integer totalPointPerdant) {

}
