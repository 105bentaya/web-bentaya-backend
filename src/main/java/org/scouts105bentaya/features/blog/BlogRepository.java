package org.scouts105bentaya.features.blog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {
    Optional<Blog> findByTitle(String title);
    @Query("SELECT b from Blog b where b.title = :name AND b.published = true")
    Optional<Blog> findPublishedByName(@Param("name") String name);
    @Query("SELECT b from Blog b where b.published = true")
    List<Blog> findAllPublished();
}
