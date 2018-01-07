package com.ncl.sindhu.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.ncl.sindhu.domain.Issue;
import com.ncl.sindhu.domain.User;
import com.ncl.sindhu.repository.IssueRepository;
import com.ncl.sindhu.repository.UserRepository;
import com.ncl.sindhu.repository.search.IssueSearchRepository;
import com.ncl.sindhu.security.SecurityUtils;
import com.ncl.sindhu.service.MailService;
import com.ncl.sindhu.web.rest.errors.BadRequestAlertException;
import com.ncl.sindhu.web.rest.util.HeaderUtil;
import com.ncl.sindhu.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * REST controller for managing Issue.
 */
@RestController
@RequestMapping("/api")
public class IssueResource {

    private final Logger log = LoggerFactory.getLogger(IssueResource.class);

    private static final String ENTITY_NAME = "issue";

    private final IssueRepository issueRepository;

    private final IssueSearchRepository issueSearchRepository;

    @Autowired
    private MailService mailService;

    private final UserRepository userRepository;

    public IssueResource(IssueRepository issueRepository, IssueSearchRepository issueSearchRepository, UserRepository userRepository) {
        this.issueRepository = issueRepository;
        this.issueSearchRepository = issueSearchRepository;
        this.userRepository=userRepository;
    }

    /**
     * POST  /issues : Create a new issue.
     *
     * @param issue the issue to create
     * @return the ResponseEntity with status 201 (Created) and with body the new issue, or with status 400 (Bad Request) if the issue has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/issues")
    @Timed
    public ResponseEntity<Issue> createIssue(@Valid @RequestBody Issue issue) throws URISyntaxException {
        log.debug("REST request to save Issue : {}", issue);
        if (issue.getId() != null) {
            throw new BadRequestAlertException("A new issue cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Optional<String> userid= SecurityUtils.getCurrentUserLogin();
        System.out.println("test userid"+userid+" useridstring"+userid.get());
        User user=userRepository.findOneByLogin(userid.get()).get();
        issue.setUser(user);
        Issue result = issueRepository.save(issue);
        issueSearchRepository.save(result);
        mailService.sendEmail(user,issue);
        return ResponseEntity.created(new URI("/api/issues/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /issues : Updates an existing issue.
     *
     * @param issue the issue to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated issue,
     * or with status 400 (Bad Request) if the issue is not valid,
     * or with status 500 (Internal Server Error) if the issue couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/issues")
    @Timed
    public ResponseEntity<Issue> updateIssue(@Valid @RequestBody Issue issue) throws URISyntaxException {
        log.debug("REST request to update Issue : {}", issue);
        if (issue.getId() == null) {
            return createIssue(issue);
        }
        Issue result = issueRepository.save(issue);
        issueSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, issue.getId().toString()))
            .body(result);
    }

    /**
     * GET  /issues : get all the issues.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of issues in body
     */
    @GetMapping("/issues")
    @Timed
    public ResponseEntity<List<Issue>> getAllIssues(Pageable pageable) {
        log.debug("REST request to get a page of Issues");
        Page<Issue> page = issueRepository.findByUserIsCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/issues");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /issues/:id : get the "id" issue.
     *
     * @param id the id of the issue to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the issue, or with status 404 (Not Found)
     */
    @GetMapping("/issues/{id}")
    @Timed
    public ResponseEntity<Issue> getIssue(@PathVariable Long id) {
        log.debug("REST request to get Issue : {}", id);
        Issue issue = issueRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(issue));
    }

    /**
     * DELETE  /issues/:id : delete the "id" issue.
     *
     * @param id the id of the issue to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/issues/{id}")
    @Timed
    public ResponseEntity<Void> deleteIssue(@PathVariable Long id) {
        log.debug("REST request to delete Issue : {}", id);
        issueRepository.delete(id);
        issueSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/issues?query=:query : search for the issue corresponding
     * to the query.
     *
     * @param query the query of the issue search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/issues")
    @Timed
    public ResponseEntity<List<Issue>> searchIssues(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Issues for query {}", query);
        Page<Issue> page = issueSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/issues");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
