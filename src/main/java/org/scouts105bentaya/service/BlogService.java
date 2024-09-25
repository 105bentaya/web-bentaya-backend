package org.scouts105bentaya.service;

import org.scouts105bentaya.entity.Blog;

import java.util.List;

public interface BlogService {

    List<Blog> findAll();

    List<Blog> findAllPublished();

    List<Blog> findAllPublishedBeforeToday();

    Blog findById(int id);

    Blog findPublishedByName(String name);

    Blog save(Blog blog);

    Blog update(Blog blog, Integer id);

}
