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
        blog.setId(dto.getId());
        blog.setData(dto.getData());
        blog.setDescription(dto.getDescription());
        blog.setEvent(dto.isEvent());
        blog.setImage(dto.getImage());
        blog.setEndDate(dto.getEndDate());
        blog.setModificationDate(dto.getModificationDate());
        blog.setPublished(dto.isPublished());
        blog.setTitle(dto.getTitle());
        return blog;
    }

    @Override
    public BlogDto convertFromEntity(Blog entity) {
        BlogDto blogDto = new BlogDto();
        blogDto.setId(entity.getId());
        blogDto.setData(entity.getData());
        blogDto.setDescription(entity.getDescription());
        blogDto.setEvent(entity.isEvent());
        blogDto.setImage(entity.getImage());
        blogDto.setEndDate(entity.getEndDate());
        blogDto.setModificationDate(entity.getModificationDate());
        blogDto.setPublished(entity.isPublished());
        blogDto.setTitle(entity.getTitle());
        return blogDto;
    }
}
