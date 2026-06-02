package io.shodo.matrix.infrastructure.persistence.adapter;

import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.shodo.matrix.domain.model.NomPersonnage;
import io.shodo.matrix.domain.model.PageRequest;
import io.shodo.matrix.domain.model.PageResult;
import io.shodo.matrix.domain.model.Personnage;
import io.shodo.matrix.domain.model.PersonnageFilter;
import io.shodo.matrix.domain.spi.PersonnageRepository;
import jakarta.inject.Singleton;
import java.util.Collections;
import java.util.List;

@Singleton
public class PersonnageRepositoryAdapter implements PersonnageRepository {

    @Override
    public List<Personnage> findAllByName(List<NomPersonnage> names) {
        return Collections.emptyList();
    }

    @Override
    public PageResult<Personnage> rechercher(PersonnageFilter filter, PageRequest pageRequest) {
        Pageable pageable = toPageable(pageRequest);
        return new PageResult<>(
            Collections.emptyList(),
            1,
            100,
            200,
            2
        );
    }

    private Pageable toPageable(PageRequest pageRequest) {
        if (pageRequest.sort() == null || pageRequest.sort()
            .isBlank()) {
            return Pageable.from(pageRequest.page(), pageRequest.size());
        }
        String[] parts = pageRequest.sort()
            .split(",", 2);
        String property = parts[0].trim();
        boolean desc = parts.length > 1 && parts[1].trim()
            .equalsIgnoreCase("desc");
        Sort sort = desc ? Sort.of(Sort.Order.desc(property)) : Sort.of(Sort.Order.asc(property));
        return Pageable.from(pageRequest.page(), pageRequest.size(), sort);
    }

}
