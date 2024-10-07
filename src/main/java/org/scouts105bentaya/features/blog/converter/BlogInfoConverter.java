package org.scouts105bentaya.features.blog.converter;

import org.scouts105bentaya.shared.GenericConverter;
import org.scouts105bentaya.features.blog.dto.BlogInfoDto;
import org.scouts105bentaya.features.blog.Blog;
import org.springframework.stereotype.Component;

@Component
public class BlogInfoConverter extends GenericConverter<Blog, BlogInfoDto> {

    @Override
    public Blog convertFromDto(BlogInfoDto dto) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public BlogInfoDto convertFromEntity(Blog blog) {
        return new BlogInfoDto(
            blog.getTitle(),
            blog.getDescription(),
            blog.getImage(),
            blog.isEvent()
        );
    }
}
