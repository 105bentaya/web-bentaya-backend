package org.scouts105bentaya.shared;

import org.scouts105bentaya.shared.specification.PageDto;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class GenericConverter<E, D> {

    public abstract E convertFromDto(D dto);

    public abstract D convertFromEntity(E entity);

    public List<E> convertDtoCollectionToEntityList(Collection<D> dtoList) {
        return dtoList.stream().map(this::convertFromDto).collect(Collectors.toList());
    }

    public List<D> convertEntityCollectionToDtoList(Collection<E> entityList) {
        return entityList.stream().map(this::convertFromEntity).collect(Collectors.toList());
    }

    public PageDto<D> convertEntityPageToPageDto(Page<E> entityPage) {
        return new PageDto<>(entityPage.getTotalElements(), convertEntityCollectionToDtoList(entityPage.getContent()));
    }

    public static <E> PageDto<E> convertListToPageDto(Page<E> entityPage) {
        return new PageDto<>(entityPage.getTotalElements(), entityPage.getContent());
    }
}
