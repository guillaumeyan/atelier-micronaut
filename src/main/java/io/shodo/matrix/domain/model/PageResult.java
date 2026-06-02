package io.shodo.matrix.domain.model;

import java.util.List;

public record PageResult<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    long totalPages
) {

  public static <T> PageResult<T> of(List<T> content, int page, int size, long totalElements) {
    long totalPages = size == 0 ? 0 : (long) Math.ceil((double) totalElements / (double) size);
    return new PageResult<>(content, page, size, totalElements, totalPages);
  }
}
