package io.shodo.matrix.infrastructure.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;

@Serdeable
public record ResultatCombatDto(List<String> gagnants, Integer totalPointGagnant, List<String> perdants, Integer totalPointPerdant) {

}
