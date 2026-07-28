package org.example.forum.repository;

import java.util.Collection;
import java.util.List;
import org.example.forum.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    Page<Post> findByAuthorId(Long authorId, Pageable pageable);

    List<Post> findTop10ByOrderByCreatedAtDesc();

    List<Post> findByTags_NameIgnoreCase(String tagName);

    List<Post> findDistinctByTags_NameInOrderByCreatedAtDesc(Collection<String> tagNames, Pageable pageable);

    Page<Post> findDistinctByTags_NameIn(Collection<String> tagNames, Pageable pageable);

    @Query("""
            select p from Post p
            left join Comment c on c.post = p
            group by p
            order by count(c) desc
            """)
    List<Post> findMostCommented(Pageable pageable);

    @Query("""
            select p from Post p
            join p.tags t
            left join Comment c on c.post = p
            where t.name in :tagNames
            group by p
            order by count(distinct c) desc
            """)
    List<Post> findMostCommentedByTags(@Param("tagNames") Collection<String> tagNames, Pageable pageable);
}
