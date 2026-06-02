package io.shodo.matrix.domain.spi;

import io.shodo.matrix.domain.model.Personnage;
import java.util.List;

public interface ReportingProvider {

  void sendReporting(String idCombat, List<Personnage> gagnants, List<Personnage> perdants);
}
