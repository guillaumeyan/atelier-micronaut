package io.shodo.matrix.domain.model;

import java.util.UUID;

public record Vaisseau(
    String nom,
    Integer scoreDeCombat,
    TypeVaisseau typeVaisseau
) {}
