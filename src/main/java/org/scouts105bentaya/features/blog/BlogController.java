package org.scouts105bentaya.features.blog;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.blog.converter.BlogConverter;
import org.scouts105bentaya.features.blog.converter.BlogInfoConverter;
import org.scouts105bentaya.features.blog.dto.BlogDto;
import org.scouts105bentaya.features.blog.dto.BlogInfoDto;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/blog")
public class BlogController {

    private final BlogService blogService;
    private final BlogConverter blogConverter;
    private final BlogInfoConverter blogInfoConverter;

    public BlogController(
        BlogService blogService,
        BlogConverter blogConverter,
        BlogInfoConverter blogInfoConverter
    ) {
        this.blogService = blogService;
        this.blogConverter = blogConverter;
        this.blogInfoConverter = blogInfoConverter;
    }

    @GetMapping("/public")
    public List<BlogDto> findPublicAll() {
        log.info("METHOD BlogController.findPublicAll");
        return blogConverter.convertEntityCollectionToDtoList(blogService.findAllPublished());
    }

    @GetMapping("/public/home")
    public List<BlogInfoDto> findPublicAllBeforeToday() {
        return blogInfoConverter.convertEntityCollectionToDtoList(blogService.findAllPublishedBeforeToday());
    }

    @PreAuthorize("hasRole('EDITOR')")
    @GetMapping
    public List<BlogDto> findAll() {
        log.info("METHOD BlogController.findAll{}", SecurityUtils.getLoggedUserUsernameForLog());
        return blogConverter.convertEntityCollectionToDtoList(blogService.findAll());
    }

    @PreAuthorize("hasRole('EDITOR')")
    @GetMapping("/{id}")
    public BlogDto findById(@PathVariable Integer id) {
        log.info("METHOD BlogController.findById --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return blogConverter.convertFromEntity(blogService.findById(id));
    }

    @GetMapping("/public/{name}")
    public BlogDto findPublicById(@PathVariable String name) {
        log.info("METHOD BlogController.findPublicById --- PARAMS name: {}", name);
        return blogConverter.convertFromEntity(blogService.findPublishedByName(name));
    }

    @PreAuthorize("hasRole('EDITOR')")
    @PostMapping
    public BlogDto save(@RequestBody BlogDto blog) {
        log.info("METHOD BlogController.save{}", SecurityUtils.getLoggedUserUsernameForLog());
        return blogConverter.convertFromEntity(blogService.save(blogConverter.convertFromDto(blog)));
    }

    @PreAuthorize("hasRole('EDITOR')")
    @PutMapping("/{id}")
    public BlogDto update(@RequestBody BlogDto blog, @PathVariable Integer id) {
        log.info("METHOD BlogController.update --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return blogConverter.convertFromEntity(blogService.update(blogConverter.convertFromDto(blog), id));
    }
}
