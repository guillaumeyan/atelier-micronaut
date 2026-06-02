package io.shodo.matrix.infrastructure.kafka;

import java.util.List;

public record ReportingCombat(List<String> personnagesGagnants, List<String> vaisseauxGagnants, List<String> competencesGagnants,
                              List<String> personnagesPerdants, List<String> vaisseauxPerdants, List<String> competencesPerdants) {

}
