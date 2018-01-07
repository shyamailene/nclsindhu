package com.ncl.sindhu.repository;

import com.ncl.sindhu.domain.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import java.util.List;

/**
 * Spring Data JPA repository for the Issue entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    @Query("select issue from Issue issue where issue.user.login = ?#{principal.username}")
    Page<Issue> findByUserIsCurrentUser(Pageable pageable);
}
