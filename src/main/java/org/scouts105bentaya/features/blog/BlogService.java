package org.scouts105bentaya.features.blog;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BlogService {

    private final BlogRepository blogRepository;

    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    public List<Blog> findAll() {
        return blogRepository.findAll();
    }

    public List<Blog> findAllPublished() {
        return blogRepository.findAllPublished();
    }

    public List<Blog> findAllPublishedBeforeToday() {
        return blogRepository.findAllPublished().stream()
            .filter(blog -> blog.getEndDate().isAfter(ZonedDateTime.now()))
            .toList();
    }

    public Blog findById(int id) {
        return blogRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
    }

    public Blog findPublishedByName(String name) {
        return blogRepository.findPublishedByName(name).orElseThrow(WebBentayaNotFoundException::new);
    }

    public Blog save(Blog blog) {

        if (blogRepository.findByTitle(blog.getTitle()).isPresent()) {
            log.warn("save - Blog with title {} already exists", blog.getTitle());
            throw new WebBentayaBadRequestException("Ya existe una entrada con el título %s".formatted(blog.getTitle()));
        }

        blog.setTitle(standardizeStringToURL(blog.getTitle()));
        blog.setModificationDate(ZonedDateTime.now());
        return blogRepository.save(blog);
    }

    public Blog update(Blog blog, Integer id) {

        Blog blogToUpdate = this.findById(id);

        if (!Objects.equals(blogToUpdate.getTitle(), blog.getTitle()) && blogRepository.findByTitle(blog.getTitle()).isPresent()) {
            log.warn("update - Blog with title {} already exists", blog.getTitle());
            throw new WebBentayaBadRequestException("Ya existe una entrada con el título %s".formatted(blog.getTitle()));
        }

        blogToUpdate.setTitle(standardizeStringToURL(blog.getTitle()));
        blogToUpdate.setDescription(blog.getDescription());
        blogToUpdate.setImage(blog.getImage());
        blogToUpdate.setData(blog.getData());
        blogToUpdate.setPublished(blog.isPublished());
        blogToUpdate.setModificationDate(ZonedDateTime.now());
        blogToUpdate.setEvent(blog.isEvent());
        blogToUpdate.setEndDate(blog.getEndDate());

        return blogRepository.save(blogToUpdate);
    }

    private String standardizeStringToURL(String string) {
        return URLEncoder.encode(string.replaceAll("[, ]+", "-"), StandardCharsets.UTF_8).toLowerCase();
    }
}
