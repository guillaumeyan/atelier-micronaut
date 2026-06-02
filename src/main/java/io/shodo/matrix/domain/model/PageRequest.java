package io.shodo.matrix.domain.model;

public record PageRequest(int page, int size, String sort) {

  public PageRequest {
    if (page < 0) {
      throw new IllegalArgumentException("page must be >= 0");
    }
    if (size <= 0) {
      throw new IllegalArgumentException("size must be > 0");
    }
  }

  public static PageRequest of(int page, int size) {
    return new PageRequest(page, size, null);
  }
}
