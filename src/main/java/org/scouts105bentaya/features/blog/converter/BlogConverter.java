package org.scouts105bentaya.features.blog.converter;

import org.scouts105bentaya.features.blog.Blog;
import org.scouts105bentaya.features.blog.dto.BlogDto;
import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

@Component
public class BlogConverter extends GenericConverter<Blog, BlogDto> {
    @Override
    public Blog convertFromDto(BlogDto dto) {
        Blog blog = new Blog();
        blog.setId(dto.id());
        blog.setData(dto.data());
        blog.setDescription(dto.description());
        blog.setEvent(dto.event());
        blog.setImage(dto.image());
        blog.setEndDate(dto.endDate());
        blog.setModificationDate(dto.modificationDate());
        blog.setPublished(dto.published());
        blog.setTitle(dto.title());
        return blog;
    }

    @Override
    public BlogDto convertFromEntity(Blog entity) {
        return new BlogDto(
            entity.getId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getImage(),
            entity.getData(),
            entity.getModificationDate(),
            entity.getEndDate(),
            entity.isEvent(),
            entity.isPublished()
        );
    }
}
