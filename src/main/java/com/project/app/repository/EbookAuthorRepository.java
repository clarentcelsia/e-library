package com.project.app.repository;

import com.project.app.entity.EbookAuthor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EbookAuthorRepository extends JpaRepository<EbookAuthor, String> {

    Page<EbookAuthor> findAll(Specification<EbookAuthor> specification, Pageable pageable);
}
