package org.scouts105bentaya.service;

import org.apache.commons.lang3.StringUtils;
import org.scouts105bentaya.entity.Blog;
import org.scouts105bentaya.exception.BlogAlreadyExistsException;
import org.scouts105bentaya.exception.BlogNotFoundException;
import org.scouts105bentaya.repository.BlogRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

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
        return blogRepository.findById(id).orElseThrow(BlogNotFoundException::new);
    }

    public Blog findPublishedByName(String name) {
        return blogRepository.findPublishedByName(name).orElseThrow(BlogNotFoundException::new);
    }

    public Blog save(Blog blog) {

        if (blogRepository.findByTitle(blog.getTitle()).isPresent()) {
            throw new BlogAlreadyExistsException("Ya existe una entrada con este nombre");
        }

        blog.setTitle(standardizeStringToURL(blog.getTitle()));
        blog.setModificationDate(ZonedDateTime.now());
        return blogRepository.save(blog);
    }

    public Blog update(Blog blog, Integer id) {

        Blog blogToUpdate = this.findById(id);

        if (!Objects.equals(blogToUpdate.getTitle(), blog.getTitle()) && blogRepository.findByTitle(blog.getTitle()).isPresent()) {
            throw new BlogAlreadyExistsException("Ya existe una entrada con este nombre");
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

    //todo replaceall sonarlint
    private String standardizeStringToURL(String string) {
        return StringUtils.stripAccents(string.replaceAll(" ", "-").toLowerCase());
    }
}
