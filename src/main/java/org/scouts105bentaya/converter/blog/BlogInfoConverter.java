package org.scouts105bentaya.converter.blog;

import org.scouts105bentaya.converter.GenericConverter;
import org.scouts105bentaya.dto.blog.BlogInfoDto;
import org.scouts105bentaya.entity.Blog;
import org.springframework.stereotype.Component;

@Component
public class BlogInfoConverter extends GenericConverter<Blog, BlogInfoDto> {

    @Override
    public Blog convertFromDto(BlogInfoDto dto) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public BlogInfoDto convertFromEntity(Blog blog) {
        BlogInfoDto blogInfoDto = new BlogInfoDto();
        blogInfoDto.setDescription(blog.getDescription());
        blogInfoDto.setEvent(blog.isEvent());
        blogInfoDto.setImage(blog.getImage());
        blogInfoDto.setTitle(blog.getTitle());
        return blogInfoDto;
    }
}
