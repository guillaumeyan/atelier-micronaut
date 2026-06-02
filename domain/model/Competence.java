package io.shodo.matrix.domain.model;

import java.util.UUID;

public record Competence(
    UUID id,
    String nom,
    String description
) {}