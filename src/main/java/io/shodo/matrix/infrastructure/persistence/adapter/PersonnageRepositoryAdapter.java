package io.shodo.matrix.infrastructure.persistence.adapter;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.shodo.matrix.domain.model.NomPersonnage;
import io.shodo.matrix.domain.model.PageRequest;
import io.shodo.matrix.domain.model.PageResult;
import io.shodo.matrix.domain.model.Personnage;
import io.shodo.matrix.domain.model.PersonnageFilter;
import io.shodo.matrix.domain.spi.PersonnageRepository;
import io.shodo.matrix.infrastructure.persistence.entity.PersonnageEntity;
import io.shodo.matrix.infrastructure.persistence.mapper.MatrixMapper;
import io.shodo.matrix.infrastructure.persistence.repository.JdbcPersonnageRepository;
import jakarta.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class PersonnageRepositoryAdapter implements PersonnageRepository {

    private final JdbcPersonnageRepository jdbcPersonnageRepository;
    private final MatrixMapper matrixMapper;

    @Override
    public List<Personnage> findAllByName(List<NomPersonnage> names) {
        List<String> nomStrings = names.stream()
            .map(NomPersonnage::name)
            .toList();
        Set<PersonnageEntity> entities =
            jdbcPersonnageRepository.findByNomInList(nomStrings);
        return matrixMapper.toDomain(entities);
    }

    @Override
    public PageResult<Personnage> rechercher(PersonnageFilter filter, PageRequest pageRequest) {
        Pageable pageable = toPageable(pageRequest);
        String nomFilter = filter != null && filter.hasNom() ? "%" + filter.nom() + "%" : null;
        final Page<PersonnageEntity> page;
        if (nomFilter != null) {
            page = jdbcPersonnageRepository.findByNomIlike(nomFilter, pageable);
        } else {
            page = jdbcPersonnageRepository.find(pageable);
        }
        List<Personnage> content = matrixMapper.toDomain(page.getContent());
        return new PageResult<>(
            content,
            page.getPageNumber(),
            page.getSize(),
            page.getTotalSize(),
            page.getTotalPages()
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
