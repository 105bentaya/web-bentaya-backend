package org.scouts105bentaya.shared;

import io.jsonwebtoken.lang.Collections;
import org.scouts105bentaya.shared.specification.PageDto;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class GenericConverter<E, D> {

    public abstract E convertFromDto(D dto);

    public abstract D convertFromEntity(E entity);

    public List<E> convertDtoCollectionToEntityList(Collection<D> dtoList) {
        return dtoList.stream().map(this::convertFromDto).collect(Collectors.toList());
    }

    public List<D> convertEntityCollectionToDtoList(Collection<E> entityList) {
        return convertEntityCollectionToDtoList(entityList, this::convertFromEntity);
    }

    public PageDto<D> convertEntityPageToPageDto(Page<E> entityPage) {
        return new PageDto<>(entityPage.getTotalElements(), convertEntityCollectionToDtoList(entityPage.getContent()));
    }

    public static <E> PageDto<E> convertListToPageDto(Page<E> entityPage) {
        return new PageDto<>(entityPage.getTotalElements(), entityPage.getContent());
    }

    public static <E, D> PageDto<D> convertListToPageDto(Page<E> entityPage, Function<E, D> converter) {
        return new PageDto<>(entityPage.getTotalElements(), convertEntityCollectionToDtoList(entityPage.getContent(), converter));
    }

    public static <E, D> List<D> convertEntityCollectionToDtoList(Collection<E> entityList, Function<E, D> converter) {
        return Collections.isEmpty(entityList) ? Collections.emptyList() : entityList.stream().map(converter).collect(Collectors.toList());
    }
}
