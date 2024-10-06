package org.scouts105bentaya.converter.blog;

import org.scouts105bentaya.converter.GenericConverter;
import org.scouts105bentaya.dto.blog.BlogDto;
import org.scouts105bentaya.entity.Blog;
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
