package org.scouts105bentaya.features.blog.converter;

import org.scouts105bentaya.features.blog.Blog;
import org.scouts105bentaya.features.blog.dto.BlogInfoDto;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

@Component
public class BlogInfoConverter extends GenericConverter<Blog, BlogInfoDto> {

    @Override
    public Blog convertFromDto(BlogInfoDto dto) {
        throw new UnsupportedOperationException(GenericConstants.NOT_IMPLEMENTED);
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
